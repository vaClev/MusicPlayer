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
