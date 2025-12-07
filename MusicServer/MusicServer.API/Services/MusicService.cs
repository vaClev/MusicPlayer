
using Microsoft.EntityFrameworkCore;
using MusicServer.API.Database;
using MusicServer.API.Models;
using TagLib;

namespace MusicServer.API.Services
{
    public class MusicService : IMusicService
    {
        private readonly AppDbContext _context;
        private readonly IWebHostEnvironment _environment;
        private readonly IConfiguration _configuration;

        public MusicService(
            AppDbContext context,
            IWebHostEnvironment environment,
            IConfiguration configuration)
        {
            _context = context;
            _environment = environment;
            _configuration = configuration;
        }

        #region "Реализация интерфейса IMusicService"
        //Загрузка файла на сервер
        public async Task<MusicFile> UploadMusicAsync(IFormFile file)
        {
            // 1. Проверяем расширение файла
            var allowedExtensions = _configuration.GetSection("MusicStorage:AllowedExtensions").Get<string[]>() 
                                    ?? new[] { ".mp3" }; // ?? если null то харкоженные mp3 толко
            var extension = Path.GetExtension(file.FileName).ToLower();

            if (!allowedExtensions.Contains(extension))
            {
                throw new ArgumentException($"Недопустимый формат файла. Разрешены: {string.Join(", ", allowedExtensions)}");
            }

            // 2. Создаем уникальное имя файла
            var fileName = Guid.NewGuid().ToString() + extension;
            var musicFolder = _configuration["MusicStoragePath"]
                ?? Path.Combine(_environment.WebRootPath, "MusicFiles");

            if (!Directory.Exists(musicFolder))
            {
                Directory.CreateDirectory(musicFolder);
            }

            var filePath = Path.Combine(musicFolder, fileName);

            // 3. Сохраняем файл
            using (var stream = new FileStream(filePath, FileMode.Create))
            {
                await file.CopyToAsync(stream);
            }

            // 4. Извлекаем метаданные
            // TODO создать класс обертку для этой функции.
            var musicFile = await ExtractMetadataAsync(filePath, fileName, file);

            // 5. Сохраняем в БД 
            // TODO вынести в отдельный класс MusicFileDBHelper 
            _context.MusicFiles.Add(musicFile);
            await _context.SaveChangesAsync();

            return musicFile;
        }

        // TODO создать класс обертку для этой функции.
        // зависит от библиотеки TagLib
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
        public async Task<MusicFile> GetMusicFileAsync(int id)
        {
            return await _context.MusicFiles.FindAsync(id);
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
            return (musicFile != null) ? musicFile.filepath : "";
        }

        // Удаление карточки и файла
        public async Task<bool> DeleteMusicFileAsync(int id)
        {
            var musicFile = await GetMusicFileAsync(id);
            if (musicFile == null)
                return false;

            // Удаляем физический файл из папки
            // TODO вынести в Utils
            if (System.IO.File.Exists(musicFile.filepath))
            {
                System.IO.File.Delete(musicFile.filepath);
            }

            // Удаляем запись из БД
            // TODO ВЫнести 
            _context.MusicFiles.Remove(musicFile);
            await _context.SaveChangesAsync();

            return true;
        }
        #endregion


    }
} //namespace MusicServer.API.Services