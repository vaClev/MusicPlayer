
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using MusicServer.API.Database;
using MusicServer.API.Models;
using MusicServer.API.Services.Upload;
using TagLib;

namespace MusicServer.API.Services
{
    public class MusicService : IMusicService
    {
        private readonly AppDbContext _context;
        private readonly IConfiguration _configuration; //TODO проверить можно без нее?

        private readonly IUploadService m_uploadService;
        private readonly string m_pathPrefix;
        private readonly string m_folderName;
        //private readonly ILogger<ExtraFileService> _logger; TODO обдумать возможно стоит добавить

        public MusicService(
            AppDbContext context,
            IWebHostEnvironment environment,
            IConfiguration configuration,
            IUploadServiceFactory uploadServiceFactory)
        {
            _context = context;
            _configuration = configuration;

            string musicFolderFullSystemPath = _configuration.GetSection("MusicStorage:FullPath").Get<string>() ?? string.Empty; ;
            var allowedExtensions = _configuration.GetSection("MusicStorage:AllowedExtensions").Get<string[]>() ?? new[] { string.Empty };
            m_uploadService = uploadServiceFactory.Create(musicFolderFullSystemPath, allowedExtensions);

            m_pathPrefix = _configuration.GetSection("MusicStorage:PrefixPath").Get<string>() ?? string.Empty;
            m_folderName = _configuration.GetSection("MusicStorage:Path").Value ?? string.Empty;
        }


        #region "Реализация интерфейса IMusicService"
        //Загрузка файла на сервер
        public async Task<MusicFile> UploadMusicAsync([FromForm] IFormFile file)
        {
            // 1. Проверяем расширение файла.
            var extension = m_uploadService.GetExtensionWithCheck(file);

            // 2. Создаем уникальное имя файла
            var fileName = Guid.NewGuid().ToString() + extension;
            var filePath = m_uploadService.CreateFilePath(fileName);

            // 3. Сохраняем файл
            await m_uploadService.SaveFile(file, filePath);

            // 4. Извлекаем метаданные из mp3
            var musicFile = ExtractMetadataAsync(filePath, fileName, file);

            // 5. Сохраняем в БД 
            // TODO вынести в отдельный класс MusicFileDBHelper 
            //Подмена пути на короткий для сохранения в БД
            musicFile.filepath = Path.Combine(m_folderName, fileName) ?? ""; //в базу пишем только от папки MusicFiles, для кросплатформенности Linux
            _context.MusicFiles.Add(musicFile);
            await _context.SaveChangesAsync();

            return musicFile;
        }

        // TODO создать класс обертку для этой функции.
        // зависит от библиотеки TagLib
        private MusicFile ExtractMetadataAsync(string filePath, string fileName, IFormFile originalFile)
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
                    musicFile.title = tagFile.Tag.Title ?? Path.GetFileNameWithoutExtension(originalFile.FileName);
                    musicFile.artist = tagFile.Tag.FirstPerformer ?? "Unknown Artist";
                    musicFile.album = tagFile.Tag.Album ?? "Unknown Album";
                    musicFile.genre = tagFile.Tag.FirstGenre;
                    musicFile.year = (int?)tagFile.Tag.Year;
                    musicFile.duration = tagFile.Properties.Duration;
                }
            }
            catch (Exception ex)
            {
                // Если не удалось извлечь метаданные, используем информацию из имени файла
                Console.WriteLine($"Ошибка извлечения метаданных: {ex.Message}");
                musicFile.title = Path.GetFileNameWithoutExtension(originalFile.FileName);
                musicFile.artist = "Unknown Artist";
                musicFile.album = "Unknown Album";
                musicFile.duration = TimeSpan.Zero;
            }

            return musicFile;
        }

        // Получить MusicFile(карточку) из БД
        public async Task<MusicFile?> GetMusicFileAsync(int id)
        {
            return await _context.MusicFiles
                .Include(mf => mf.ExtraFiles)// Включить связанные допфайлы
                .FirstOrDefaultAsync(mf => mf.id == id);
        }


        // Получить все музыкальные файлы(карточки) из библиотки
        // TODO продумать с учетом пагинации по 20 песен
        public async Task<IEnumerable<MusicFile>> GetAllMusicFilesAsync()
        {
            return await _context.MusicFiles
               .OrderByDescending(m => m.uploadDate)
               .ToListAsync();
        }


        // Получить путь к файлу
        public async Task<string> GetMusicFilePathAsync(int id)
        {
            var musicFile = await GetMusicFileAsync(id);
            if (musicFile == null)
                throw new ArgumentException($"Файла c id={id} не найдено");

            return Path.Combine(m_pathPrefix, musicFile.filepath);
        }

        // Удаление карточки и файла
        public async Task<bool> DeleteMusicFileAsync(int id)
        {
            var musicFile = await GetMusicFileAsync(id);
            if (musicFile == null)
                return false;

            // Удаляем физический файл из папки
            string filepath = Path.Combine(m_pathPrefix, musicFile.filepath);
            m_uploadService.DeleteFile(filepath);

            // Удаляем запись из БД
            _context.MusicFiles.Remove(musicFile);
            await _context.SaveChangesAsync();

            return true;
        }

        public async Task<MusicFile> SaveToDbForTest(MusicFile musicFile)
        {
            _context.MusicFiles.Add(musicFile);
            await _context.SaveChangesAsync();
            return musicFile;
        }
        #endregion
    }
} //namespace MusicServer.API.Services
