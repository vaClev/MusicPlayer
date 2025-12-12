using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Configuration;
using MusicServer.API.Models;

namespace MusicServer.API.Services.Upload
{
    public class UploadService : IUploadService
    {
        private string m_FolderFullSystemPath;
        private string[] m_allowedExtensions;

        public UploadService(string fullSystemPath, string[] allowedExtensions)
        {
            m_FolderFullSystemPath = fullSystemPath;
            m_allowedExtensions = allowedExtensions;

            //При необходимости создаем папку.
            if (!Directory.Exists(m_FolderFullSystemPath))
            {
                Directory.CreateDirectory(m_FolderFullSystemPath);
            }
        }
        // Извлекаем расширение файла. С проверкой допустипого формата.
        public string GetExtensionWithCheck(IFormFile file)
        {
            string extension = Path.GetExtension(file.FileName).ToLower();

            if (!m_allowedExtensions.Contains(extension))
            {
                throw new ArgumentException($"Недопустимый формат файла. Разрешены: {string.Join(", ", m_allowedExtensions)}");
            }
            return extension;
        }

        // Создаем полный путь к файлу
        public string CreateFilePath(string fileName)
        {
            return Path.Combine(m_FolderFullSystemPath, fileName);
        }

        // Сохраняем файл на диск в указанный путь
        public async Task SaveFile(IFormFile file, string saveToFilepath)
        {
            using (var stream = new FileStream(saveToFilepath, FileMode.Create))
            {
                await file.CopyToAsync(stream);
            }
        }

        // Удаляем файл
        public void DeleteFile(string filepath)
        {
            if (System.IO.File.Exists(filepath))
            {
                System.IO.File.Delete(filepath);
            }
        }
    }
}


/*//Загрузка файла на сервер
public async Task<MusicFile> UploadMusicAsync(IFormFile file)
{
    //Init UploadService для работы с музыкальными файлами.
    var allowedExtensions = _configuration.GetSection("MusicStorage:AllowedExtensions").Get<string[]>()
                ?? new[] { ".mp3" };
    var musicFolderFullSystemPath = _configuration.GetSection("MusicStorage:FullPath").Get<string>() ?? "~/";
    UploadService m_uploadService = new UploadService(allowedExtensions, musicFolderFullSystemPath);

    // 1. Определяем расширение с проверкой
    var extension = m_uploadService.GetExtension(file);
    // 2. Создаем уникальное имя файла и путь
    var fileName = Guid.NewGuid().ToString() + extension;
    var filePath = m_uploadService.CreateFilePath(fileName);
    // 3. Сохраняем файл
    await m_uploadService.SaveFile(file, filePath);


    // 4. Извлекаем метаданные
    // TODO создать класс обертку для этой функции.
    var musicFile = await ExtractMetadataAsync(filePath, fileName, file);

    // 5. Сохраняем в БД 
    // TODO вынести в отдельный класс MusicFileDBHelper 
    //Подмена пути на короткий для сохранения в БД
    string shortFolderName = _configuration.GetSection("MusicStorage:Path").Value ?? string.Empty;
    musicFile.filepath = Path.Combine(shortFolderName, fileName) ?? ""; //в базу пишем только от папки MusicFiles, для кросплатформенности Linux
    _context.MusicFiles.Add(musicFile);
    await _context.SaveChangesAsync();

    return musicFile;
}*/