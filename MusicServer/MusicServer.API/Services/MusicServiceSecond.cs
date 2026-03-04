using MusicServer.API.Database;
using MusicServer.API.DTO;
using MusicServer.API.DTOs;
using MusicServer.API.Models;
using MusicServer.API.Services.Upload;

namespace MusicServer.API.Services
{
    public class MusicServiceSecond : IMusicService
    {
        private readonly IConfiguration _configuration;
        private readonly IMusicFileRepository _repository;
        private readonly IUploadServiceFactory _uploadServiceFactory;

        private IUploadService? _uploadService;
        private readonly string _pathPrefix;
        private readonly string _folderName;

        public MusicServiceSecond(
            IConfiguration configuration,
            IUploadServiceFactory uploadServiceFactory,
            IMusicFileRepository repository)
        {
            _configuration = configuration;
            _uploadServiceFactory = uploadServiceFactory;
            _repository = repository;

            _pathPrefix = _configuration.GetSection("MusicStorage:PrefixPath").Get<string>() ?? string.Empty;
            _folderName = _configuration.GetSection("MusicStorage:Path").Value ?? string.Empty;
        }

        private void LazyInitUploadService()
        {
            if (_uploadService == null)
            {
                string musicFolderFullSystemPath = _configuration.GetSection("MusicStorage:FullPath").Get<string>() ?? string.Empty;
                var allowedExtensions = _configuration.GetSection("MusicStorage:AllowedExtensions").Get<string[]>() ?? new[] { string.Empty };
                _uploadService = _uploadServiceFactory.Create(musicFolderFullSystemPath, allowedExtensions);
            }
        }

        #region "Реализация интерфейса IMusicService"

        public async Task<MusicFileResponseDto> UploadMusicAsync(IFormFile file)
        {
            // 0. проверка на пустой файл
            if (file.Length == 0)
                throw new ArgumentException("Файл пустой");

            LazyInitUploadService();
            if (_uploadService == null)
                throw new Exception("Не удалось инициализировать Upload Service");

            // 1. Проверяем расширение файла.
            var extension = _uploadService.GetExtensionWithCheck(file);  //может выбросить исключение
            var fileExtension = await _repository.GetFileExtensionAsync(extension);
            // 1.1 Проверка расширения. Что оно есть в таблице сервера. 
            if (fileExtension == null)
                throw new ArgumentException($"Неизвестное расширение файла: {extension}");

            // 2. Создаем уникальное имя файла
            var fileName = Guid.NewGuid().ToString() + extension;
            var filePath = _uploadService.CreateFilePath(fileName);

            // 3. Сохраняем файл
            await _uploadService.SaveFile(file, filePath);

            // 4. Извлекаем метаданные из mp3
            MusicFile musicFile = await ExtractMetadataAsync(filePath, fileName, file);
            musicFile.FileExtensionId = fileExtension.Id;

            // 5. Сохраняем в БД через репозиторий
            musicFile.filepath = Path.Combine(_folderName, fileName);
            await _repository.AddAsync(musicFile);
            await _repository.SaveChangesAsync();

            return ToResponseDto(musicFile);
        }

        private async Task<MusicFile> ExtractMetadataAsync(string filePath, string fileName, IFormFile originalFile)
        {
            var musicFile = new MusicFile
            {
                filename = fileName,
                filepath = filePath,
                filesize = originalFile.Length,
                uploadDate = DateTime.UtcNow
            };

            // Используем TagLibSharp для извлечения метаданных
            try
            {
                using (var tagFile = TagLib.File.Create(filePath))
                {
                    var artist = await _repository.GetOrCreateArtistAsync(
                    tagFile.Tag.FirstPerformer ?? "Unknown Artist");

                    var genre = await _repository.GetOrCreateGenreAsync(
                        tagFile.Tag.FirstGenre ?? "Unknown Genre");

                    musicFile.title = tagFile.Tag.Title ?? Path.GetFileNameWithoutExtension(originalFile.FileName);
                    musicFile.artist = artist;
                    musicFile.album = tagFile.Tag.Album ?? "Unknown Album";
                    musicFile.genre = genre;
                    musicFile.year = (int?)tagFile.Tag.Year;
                    musicFile.duration = tagFile.Properties.Duration;
                }
            }
            catch (Exception ex)
            {
                // Если не удалось извлечь метаданные, используем информацию из имени файла
                Console.WriteLine($"Ошибка извлечения метаданных: {ex.Message}");
                musicFile.title = Path.GetFileNameWithoutExtension(originalFile.FileName);
                musicFile.artist = await _repository.GetOrCreateArtistAsync("Unknown Artist");
                musicFile.album = "Unknown Album";
                musicFile.genre = await _repository.GetOrCreateGenreAsync("Unknown Genre");
                musicFile.duration = TimeSpan.Zero;
            }

            return musicFile;
        }

        public async Task<MusicFileWithExtrasDto> GetMusicFileAsync(int id)
        {
            var musicFile = await _repository.GetByIdWithDetailsAsync(id);
            if (musicFile == null)
                throw new ArgumentException($"Файл с id={id} не найден");

            musicFile.filepath = Path.Combine(_pathPrefix, musicFile.filepath);

            return ToWithExtrasDto(musicFile);
        }

        //TODO по идее можно отказаться от него OLD
        public async Task<IEnumerable<MusicFileResponseDto>> GetAllMusicFilesAsync()
        {
            // Для получения всех файлов используем пагинацию с большим размером страницы
            // или можно добавить метод GetAllWithDetails в репозиторий
            var pageParams = new PaginationParams
            {
                PageNumber = 1,
                PageSize = 1000 // достаточно большое число
            };

            var files = await _repository.GetPagedAsync(pageParams);
            return files.Select(ToResponseDto);
        }

        public async Task<PagedResponse<MusicFileResponseDto>> GetMusicFilesPageAsync(PaginationParams pageParams)
        {
            // Получаем список файлов из репозитория
            var items = await _repository.GetPagedAsync(pageParams);

            // Получаем общее количество
            var totalCount = await _repository.GetTotalCount();

            // Маппим в DTO
            var itemsDto = items.Select(ToResponseDto).ToList();

            // Возвращаем PagedResponse
            return new PagedResponse<MusicFileResponseDto>(itemsDto, totalCount, pageParams);
        }

        public async Task<DownloadFileDto> GetMusicFileDownloadDataAsync(int id)
        {
            var musicFile = await _repository.GetByIdWithExtensionAndArtistAsync(id);
            if (musicFile == null)
                throw new ArgumentException($"Файл с id={id} не найден");

            musicFile.filepath = Path.Combine(_pathPrefix, musicFile.filepath);
            return ToDownloadDto(musicFile);
        }

        public async Task<bool> DeleteMusicFileAsync(int id)
        {
            LazyInitUploadService();

            var musicFile = await _repository.GetByIdAsync(id);
            if (musicFile == null)
                return false;

            // Удаляем физический файл
            string filepath = Path.Combine(_pathPrefix, musicFile.filepath);
            _uploadService?.DeleteFile(filepath);

            // Удаляем запись из БД
            var deleted = await _repository.DeleteAsync(id);
            if (deleted)
                await _repository.SaveChangesAsync();

            return deleted;
        }

        #endregion

        #region "Методы маппинга"

        private MusicFileResponseDto ToResponseDto(MusicFile musicFile)
        {
            return new MusicFileResponseDto
            {
                Id = musicFile.id,
                Title = musicFile.title,
                Artist = musicFile.artist.Name,
                Extension = musicFile.FileExtension.Extension,
                Album = musicFile.album,
                Genre = musicFile.genre.Name,
                Year = musicFile.year,
                FileSize = musicFile.filesize,
                Duration = musicFile.duration,
                UploadDate = musicFile.uploadDate,
                DownloadUrl = string.Empty //определяется контроллером // TODO маппером.
            };
        }

        private MusicFileWithExtrasDto ToWithExtrasDto(MusicFile musicFile)
        {
            return new MusicFileWithExtrasDto
            {
                Id = musicFile.id,
                Title = musicFile.title,
                Artist = musicFile.artist.Name,
                Extension = musicFile.FileExtension.Extension,
                Album = musicFile.album,
                Genre = musicFile.genre.Name,
                Year = musicFile.year,
                Duration = musicFile.duration,
                UploadDate = musicFile.uploadDate,
                DownloadMusicUrl = string.Empty, //определяется контроллером // TODO маппером.
                ExtraFiles = musicFile.ExtraFiles.Select(ef => new ExtraFileDto
                {
                    Id = ef.Id,
                    OriginalFileName = ef.OriginalFileName,
                    Description = ef.Description,
                    FileType = ef.FileType,
                    FileSize = ef.FileSize,
                    UploadDate = ef.UploadDate,
                    DownloadExtraUrl = string.Empty, //определяется контроллером // TODO маппером.
                    MusicFileId = ef.MusicFileId
                }).ToList()
            };
        }

        private DownloadFileDto ToDownloadDto(MusicFile musicFile)
        {
            return new DownloadFileDto
            {
                FilenameForSend = $"{musicFile.artist.Name}-{musicFile.title}",
                Filepath = musicFile.filepath,
                Extension = musicFile.FileExtension
            };
        }
        #endregion
    }
}//namespace MusicServer.API.Services