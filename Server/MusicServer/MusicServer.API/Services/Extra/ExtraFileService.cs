

using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Configuration;
using MusicServer.API.Database;
using MusicServer.API.DTO;
using MusicServer.API.DTOs;
using MusicServer.API.Models;
using MusicServer.API.Services.Upload;

namespace MusicServer.API.Services
{
    public class ExtraFileService : IExtraFileService
    {
        private readonly IConfiguration _configuration;
        private readonly IMusicFileRepository _repository;
        private readonly IUploadServiceFactory _uploadServiceFactory;

        private IUploadService? _uploadService;
        private readonly string _pathPrefix;
        private readonly string _folderName;

        public ExtraFileService(
            IConfiguration configuration,
            IUploadServiceFactory uploadServiceFactory,
            IMusicFileRepository repository)
        {
            _configuration = configuration;
            _uploadServiceFactory = uploadServiceFactory;
            _repository = repository;

            _pathPrefix = configuration.GetSection("ExtraStorage:PrefixPath").Get<string>() ?? string.Empty;
            _folderName = configuration.GetSection("ExtraStorage:Path").Value ?? string.Empty;
        }

        private void LazyInitUploadService()
        {
            if (_uploadService == null)
            {
                string musicFolderFullSystemPath = _configuration.GetSection("ExtraStorage:FullPath").Get<string>() ?? string.Empty;
                var allowedExtensions = _configuration.GetSection("ExtraStorage:AllowedExtensions").Get<string[]>() ?? new[] { string.Empty };
                _uploadService = _uploadServiceFactory.Create(musicFolderFullSystemPath, allowedExtensions);
            }
        }

        #region Upload
        public async Task<ExtraFileDto> UploadExtraFileAsync(UploadExtraFileDto uploadDto)
        {
           // 0.Проверка на пустой файл
            if (uploadDto.File == null || uploadDto.File.Length == 0)
                throw new ArgumentException("Файл пустой");

            // 1. Проверяем существование музыкального файла
            var musicFileExists = await _repository.GetByIdAsync(uploadDto.MusicFileId) != null;
            if (!musicFileExists)
                throw new ArgumentException($"MusicFile c id {uploadDto.MusicFileId} не найден");

            LazyInitUploadService();
            if (_uploadService == null)
                throw new Exception("Не удалось инициализировать Upload Service");

            // 2. Проверяем расширение файла
            var extension = _uploadService.GetExtensionWithCheck(uploadDto.File);
            var fileExtension = await _repository.GetFileExtensionAsync(extension);
            if (fileExtension == null)
                throw new ArgumentException($"Неизвестное расширение файла: {extension}");

            // 3. Создаем уникальное имя файла и сохраняем
            var fileName = Guid.NewGuid().ToString() + extension;
            var filePath = _uploadService.CreateFilePath(fileName);
            await _uploadService.SaveFile(uploadDto.File, filePath);

            // 4. Создаем сущность ExtraFile
            var extraFile = new ExtraFile
            {
                OriginalFileName = uploadDto.File.FileName,
                StoredFileName = fileName,
                FilePath = Path.Combine(_folderName, fileName),
                Description = uploadDto.Description,
                FileType = uploadDto.FileType,
                FileSize = uploadDto.File.Length,
                UploadDate = DateTime.UtcNow,
                MusicFileId = uploadDto.MusicFileId,
                FileExtensionId = fileExtension.Id
            };

            // 5. Сохраняем в БД через репозиторий
            await _repository.AddExtraFileAsync(extraFile);
            await _repository.SaveChangesAsync();

            return MapToDto(extraFile, fileExtension);
        }


        public async Task<IEnumerable<ExtraFileDto>> GetExtraFilesByMusicIdAsync(int musicFileId)
        {
            var extraFiles = await _repository.GetExtraFilesByMusicIdAsync(musicFileId);
            return extraFiles.Select(ef => MapToDto(ef, ef.FileExtension));
        }

        public async Task<ExtraFileDto> GetExtraFileAsync(int extraFileId)
        {
            var extraFile = await GetExtraFileEntityAsync(extraFileId);
            return MapToDto(extraFile, extraFile.FileExtension);
        }

        public async Task<DownloadFileDto> GetExtraFileDownloadDataAsync(int id)
        {
            var extraFile = await GetExtraFileEntityAsync(id);
            return ToDownloadDto(extraFile);
        }

        public async Task<bool> DeleteExtraFileAsync(int extraFileId)
        {
            LazyInitUploadService();

            var extraFile = await _repository.GetExtraFileByIdAsync(extraFileId);
            if (extraFile == null)
                return false;

            // Удаляем физический файл
            string filepath = Path.Combine(_pathPrefix, extraFile.FilePath);
            _uploadService?.DeleteFile(filepath);

            // Удаляем запись из БД
            var deleted = await _repository.DeleteExtraFileAsync(extraFileId);
            if (deleted)
                await _repository.SaveChangesAsync();

            return deleted;
        }

        #endregion
        #region "Приватные методы"

        private async Task<ExtraFile> GetExtraFileEntityAsync(int extraFileId)
        {
            var extraFile = await _repository.GetExtraFileByIdWithDetailsAsync(extraFileId);
            if (extraFile == null)
                throw new ArgumentException($"Файл с id={extraFileId} не найден");

            // Формируем полный путь для доступа к файлу
            extraFile.FilePath = Path.Combine(_pathPrefix, extraFile.FilePath);

            return extraFile;
        }

        #endregion

        #region "Методы маппинга"
        private ExtraFileDto MapToDto(ExtraFile extraFile, FileExtension? fileExtension = null)
        {
            var ext = fileExtension ?? extraFile.FileExtension;

            return new ExtraFileDto
            {
                Id = extraFile.Id,
                OriginalFileName = extraFile.OriginalFileName,
                Extension = ext?.Extension ?? string.Empty,
                Description = extraFile.Description,
                FileType = extraFile.FileType,
                FileSize = extraFile.FileSize,
                UploadDate = extraFile.UploadDate,
                DownloadExtraUrl = string.Empty, // Заполняется в контроллере
                MusicFileId = extraFile.MusicFileId
            };
        }

        private DownloadFileDto ToDownloadDto(ExtraFile extraFile)
        {
            var artistName = extraFile.MusicFile?.artist?.Name ?? "Unknown";
            var title = extraFile.MusicFile?.title ?? "Unknown";

            return new DownloadFileDto
            {
                FilenameForSend = $"{artistName}-{title} - {extraFile.OriginalFileName}",
                Filepath = extraFile.FilePath,
                Extension = extraFile.FileExtension
            };
        }

        #endregion

    }
}
