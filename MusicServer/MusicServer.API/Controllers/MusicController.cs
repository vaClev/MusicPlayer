using Microsoft.AspNetCore.Mvc;
using MusicServer.API.DTO;
using MusicServer.API.DTOs;
using MusicServer.API.Models;
using MusicServer.API.Services;

namespace MusicServer.API.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class MusicController : ControllerBase
    {
        private readonly IMusicService _musicService;
        private readonly IWebHostEnvironment _environment;

        // Конструктор для dependency injection
        public MusicController(IMusicService musicService, IWebHostEnvironment environment)
        {
            _musicService = musicService;
            _environment = environment;
        }

        #region GetDTO
        // GET: api/music
        [HttpGet]
        public async Task<ActionResult<IEnumerable<MusicFileResponseDto>>> GetMusicFiles()
        {
            var musicFiles = await _musicService.GetAllMusicFilesAsync();
            return Ok(musicFiles.Select(musicFile => new MusicFileResponseDto
            {
                Id = musicFile.id,
                Title = musicFile.title,
                Artist = musicFile.artist,
                Album = musicFile.album,
                Genre = musicFile.genre,
                Year = musicFile.year,
                FileSize = musicFile.filesize,
                Duration = musicFile.duration,
                UploadDate = musicFile.uploadDate,
                DownloadUrl = Url.Action("Download", "Music", new { id = musicFile.id }, Request.Scheme)
            }));
        }


        // GET: api/music/id5 ,где 5 это id сущности "MusicFile" в таблице БД
        [HttpGet("id{id}")]
        public async Task<ActionResult<MusicFileResponseDto>> GetMusicFile(int id)
        {
            var musicFile = await _musicService.GetMusicFileAsync(id);

            if (musicFile == null)
            {
                return NotFound();
            }

            var dto = new MusicFileWithExtrasDto //TODO создать class helper для создания MusicFileWithExtrasDto 
            {
                Id = musicFile.id,
                Title = musicFile.title,
                Artist = musicFile.artist,
                Album = musicFile.album,
                Genre = musicFile.genre,
                Year = musicFile.year,
                Duration = musicFile.duration,
                UploadDate = musicFile.uploadDate,
                DownloadMusicUrl = Url.Action("Download", "Music", new { id = musicFile.id }, Request.Scheme),
                ExtraFiles = musicFile.ExtraFiles.Select(ef => new ExtraFileDto
                {
                    Id = ef.Id,
                    OriginalFileName = ef.OriginalFileName,
                    Description = ef.Description,
                    FileType = ef.FileType,
                    FileSize = ef.FileSize,
                    UploadDate = ef.UploadDate,
                    DownloadExtraUrl = Url.Action("Download", "ExtraFiles", new { id = ef.Id }, Request.Scheme),
                    MusicFileId = ef.MusicFileId
                }).ToList()
            };
            return Ok(dto);
        }
        #endregion

        #region UploadFile
        // Загрузка файла на сервер. Прототип.
        // TODO: сделать возможным только для авторизованных админов.
        // POST: api/music/upload
        [HttpPost("upload")]
        [RequestSizeLimit(52428800)] // 50MB
        public async Task<ActionResult<MusicFileResponseDto>> UploadMusic(IFormFile file)
        {
            if (file == null || file.Length == 0)
                return BadRequest("Файл отсутсвует");

            try
            {
                var uploadedFile = await _musicService.UploadMusicAsync(file);

                return CreatedAtAction(nameof(GetMusicFile), new { id = uploadedFile.id },
                    new MusicFileResponseDto //TODO создать class helper для создания MusicFileResponseDto 
                    {
                        Id = uploadedFile.id,
                        Title = uploadedFile.title,
                        Artist = uploadedFile.artist,
                        Album = uploadedFile.album,
                        Genre = uploadedFile.genre,
                        Year = uploadedFile.year,
                        FileSize = uploadedFile.filesize,
                        Duration = uploadedFile.duration,
                        UploadDate = uploadedFile.uploadDate,
                        DownloadUrl = Url.Action("Download", "Music", new { id = uploadedFile.id }, Request.Scheme)
                    });
            }
            catch (Exception ex)
            {
                return BadRequest($"Ошибка загрузки: {ex.Message}");
            }
        }
        #endregion

        #region Download
        // запрос скачивания файла
        // GET: api/music/download/5
        [HttpGet("download/id{id}")]
        public async Task<IActionResult> Download(int id)
        {
            var filePath = await _musicService.GetMusicFilePathAsync(id);

            if (string.IsNullOrEmpty(filePath) || !System.IO.File.Exists(filePath))
                return NotFound();// может быть ошибка в запросе. Или в БД есть запись а файла нет в папке.

            string extension = Path.GetExtension(filePath).ToLower();
            string contentType = GetContentType(extension);

            MusicFile? musicFile = await _musicService.GetMusicFileAsync(id);
            string artist = musicFile != null ? musicFile.artist : string.Empty;
            string title = musicFile != null ? musicFile.title : string.Empty;

            return PhysicalFile(filePath, contentType, $"{artist} - {title}{extension}");
        }

        private string GetContentType(string extension)
        {
            return extension switch
            {
                ".mp3" => "audio/mpeg",
                ".wav" => "audio/wav",
                ".flac" => "audio/flac",
                ".ogg" => "audio/ogg",
                _ => "application/octet-stream"
            };
        }
        #endregion


        // DELETE: api/music/5
        [HttpDelete("{id}")]
        public async Task<IActionResult> DeleteMusicFile(int id)
        {
            var result = await _musicService.DeleteMusicFileAsync(id);
            if (!result)
            {
                return NotFound();
            }

            return NoContent();
        }

        /// Тестовый POST запрос добавления сущности в базу данных.
        /// TODO: Сделать админское приложение на WPF для добавления сущностей.
        [HttpPost("test")]
        public async Task<ActionResult<MusicFile>> PostTestMusic()
        {
            var testFilePAth = Path.Combine(_environment.WebRootPath, "test.mp3");
            var testMusic = new MusicFile
            {
                filename = "test_song.mp3",
                filepath = "/music/test_song.mp3",
                title = "Test Song",
                artist = "Test Artist",
                album = "Test Album",
                year = 2024,
                genre = "Test Genre",
                filesize = 1024000, // 1MB
                duration = TimeSpan.FromMinutes(3.5)
            };

            await _musicService.SaveToDbForTest(testMusic);

            return Ok(testMusic);
        }
    }
}