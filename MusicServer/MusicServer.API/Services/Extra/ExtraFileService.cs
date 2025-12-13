

using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Configuration;
using MusicServer.API.Database;
using MusicServer.API.DTOs;
using MusicServer.API.Models;
using MusicServer.API.Services.Upload;

namespace MusicServer.API.Services
{
    public class ExtraFileService : IExtraFileService
    {
        private readonly AppDbContext _context;
        private readonly IConfiguration _configuration; //TODO проверить можно без нее?

        private readonly IUploadService m_uploadService;
        private readonly string m_pathPrefix;
        private readonly string m_folderName;
        //private readonly ILogger<ExtraFileService> _logger; TODO обдумать возможно стоит добавить

        public ExtraFileService(
            AppDbContext context,
            IConfiguration configuration,
            IUploadServiceFactory uploadServiceFactory)
        {
            _context = context;
            _configuration = configuration;

            string musicFolderFullSystemPath = configuration.GetSection("ExtraStorage:FullPath").Get<string>() ?? string.Empty; ;
            var allowedExtensions = configuration.GetSection("ExtraStorage:AllowedExtensions").Get<string[]>() ?? new[] { string.Empty };
            m_uploadService = uploadServiceFactory.Create(musicFolderFullSystemPath, allowedExtensions);

            m_pathPrefix = configuration.GetSection("ExtraStorage:PrefixPath").Get<string>() ?? string.Empty;
            m_folderName = configuration.GetSection("ExtraStorage:Path").Value ?? string.Empty;
        }

        #region Upload
        public async Task<ExtraFile> UploadExtraFileAsync(UploadExtraFileDto uploadDto)
        {
            bool musicFileExists = await MusicFileExistsAsync(uploadDto.MusicFileId);
            if (!musicFileExists)
                throw new ArgumentException($"MusicFile with id {uploadDto.MusicFileId} not found");

            // 1. Проверяем расширение файла.
            var extension = m_uploadService.GetExtensionWithCheck(uploadDto.File);

            // 2. Создаем уникальное имя файла
            var fileName = Guid.NewGuid().ToString() + extension;
            var filePath = m_uploadService.CreateFilePath(fileName);

            // 3. Сохраняем файл
            await m_uploadService.SaveFile(uploadDto.File, filePath);
            // TODO пункты 1. 2 и 3 можно инкапсулировать внутрь m_uploadService
            // - нужен только FilePath на выходе

            // 4. Сохранить в БД
            string fileNameForSaveDb = Path.GetFileName(filePath) ?? "";
            string filePathForSaveDb = Path.Combine(m_folderName, fileNameForSaveDb); //в базу пишем только от папки ExtraFiles, для кросплатформенности Linux
            var extraFile = new ExtraFile
            {
                OriginalFileName = uploadDto.File.FileName,
                StoredFileName = fileNameForSaveDb,
                FilePath = filePathForSaveDb,
                Description = uploadDto.Description,
                FileType = uploadDto.FileType,
                FileSize = uploadDto.File.Length,
                MusicFileId = uploadDto.MusicFileId,
                UploadDate = DateTime.UtcNow
            };
            _context.ExtraFiles.Add(extraFile);
            await _context.SaveChangesAsync();

            return extraFile;
        }


        private async Task<bool> MusicFileExistsAsync(int musicFileId)
        {
            return await _context.MusicFiles.AnyAsync(mf => mf.id == musicFileId);
        }
        #endregion

        #region GetExtraFiles for MyMusicFile
        public async Task<IEnumerable<ExtraFileDto>> GetExtraFilesByMusicIdAsync(int musicFileId)
        {
            var extraFiles = await _context.ExtraFiles
                .Where(ef => ef.MusicFileId == musicFileId)
                .OrderByDescending(ef => ef.UploadDate)
                .ToListAsync();

            return extraFiles.Select(ef => MapToDto(ef));
        }
        private ExtraFileDto MapToDto(ExtraFile extraFile)
        {
            //TODO //Сделать аналогичную функцию для musicFile. Применять в контроллере.
            return new ExtraFileDto
            {
                Id = extraFile.Id,
                OriginalFileName = extraFile.OriginalFileName,
                Description = extraFile.Description,
                FileType = extraFile.FileType,
                FileSize = extraFile.FileSize,
                UploadDate = extraFile.UploadDate,
                DownloadExtraUrl = $"/api/extrafiles/download/id{extraFile.Id}",
                MusicFileId = extraFile.MusicFileId
            };
        }
        #endregion

        #region GetExtraFile GetPathTofile
        public async Task<ExtraFileDto> GetExtraFileAsync(int extraFileId)
        {
            var extraFile = await GetExtraFileEntityAsync(extraFileId);
            if (extraFile == null)
                throw new ArgumentException($"Файла c id={extraFileId} не найдено");

            return MapToDto(extraFile);
        }


        private async Task<ExtraFile?> GetExtraFileEntityAsync(int extraFileId)
        {
            return await _context.ExtraFiles.FindAsync(extraFileId);
        }


        public async Task<string> GetExtraFilePathAsync(int extraFileId)
        {
            var extraFile = await GetExtraFileEntityAsync(extraFileId);
            if (extraFile == null)
                throw new ArgumentException($"Файла c id={extraFileId} не найдено");

            return Path.Combine(m_pathPrefix, extraFile.FilePath);
        }
        #endregion


        public async Task<bool> DeleteExtraFileAsync(int extraFileId)
        {
            var extraFile = await _context.ExtraFiles.FindAsync(extraFileId);
            if (extraFile == null) return false;

            // Удаляем физический файл c диска.
            string filepath = Path.Combine(m_pathPrefix, extraFile.FilePath);
            m_uploadService.DeleteFile(extraFile.FilePath);

            // Удаляем запись из БД.
            _context.ExtraFiles.Remove(extraFile);
            await _context.SaveChangesAsync();

            return true;
        }
    }
}
