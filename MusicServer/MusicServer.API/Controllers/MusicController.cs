using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
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

        // GET: api/music/
        //  или api/music?pageNumber=2&pageSize=5
        /// Получить список песен с пагинацией
        /// </summary>
        /// <param name="pageNumber">Номер страницы (по умолчанию 1)</param>
        /// <param name="pageSize">Размер страницы (по умолчанию 10, максимум 50)</param>
        /// <returns>Список песен с информацией о пагинации</returns>
        [HttpGet]
        public async Task<ActionResult<PagedResponse<MusicFileResponseDto>>> GetMusicFilesPage(
            [FromQuery] PaginationParams paginationParams)
        {
            try
            {
                // Валидация входных параметров
                var checkResult = paginationParams.Validate();
                if (!checkResult.IsValid)
                    return BadRequest(checkResult.ErrorMessage);

                // Получаем данные из сервиса
                var response = await _musicService.GetMusicFilesPageAsync(paginationParams);

                // Перебираем все элементы и добавляем DownloadUrl'ы
                foreach (var item in response.Items)
                    item.DownloadUrl = Url.Action("Download", "Music", new { id = item.Id }, Request.Scheme);

                // Добавляем информацию о пагинации в заголовки (для удобства)
                Response.Headers.Append("X-Pagination-TotalCount", response.TotalCount.ToString());
                Response.Headers.Append("X-Pagination-PageNumber", response.PageNumber.ToString());
                Response.Headers.Append("X-Pagination-PageSize", response.PageSize.ToString());
                Response.Headers.Append("X-Pagination-TotalPages", response.TotalPages.ToString());
                Response.Headers.Append("X-Pagination-HasNext", response.HasNextPage.ToString());
                Response.Headers.Append("X-Pagination-HasPrevious", response.HasPreviousPage.ToString());

                return Ok(response);
            }
            catch (Exception ex)
            {
                return StatusCode(500, "An error occurred while processing your request");
            }
        }


        // GET: api/music/all
        [HttpGet("all")]
        public async Task<ActionResult<IEnumerable<MusicFileResponseDto>>> GetMusicFiles()
        {
            var musicFiles = (await _musicService.GetAllMusicFilesAsync())
                .Select(musicFile =>
                {
                    musicFile.DownloadUrl = Url.Action("Download", "Music", new { id = musicFile.Id }, Request.Scheme);
                    return musicFile;
                })
                .ToList();

            return Ok(musicFiles);
        }


        // GET: api/music/id5 ,где 5 это id сущности "MusicFile" в таблице БД
        [HttpGet("id{id}")]
        public async Task<ActionResult<MusicFileWithExtrasDto>> GetMusicFile(int id)
        {
            var musicFile = await _musicService.GetMusicFileAsync(id);

            if (musicFile == null)
                return NotFound();

            //Подстановка ссылок для скачивания в DTO
            musicFile.DownloadMusicUrl = Url.Action("Download", "Music", new { id = musicFile.Id }, Request.Scheme);
            musicFile.ExtraFiles.ForEach(extraFile => extraFile.DownloadExtraUrl = Url.Action("Download", "ExtraFiles", new { id = extraFile.Id }, Request.Scheme));

            return Ok(musicFile);
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
                uploadedFile.DownloadUrl = Url.Action("Download", "Music", new { id = uploadedFile.Id }, Request.Scheme);

                return CreatedAtAction(nameof(GetMusicFile), new { id = uploadedFile.Id }, uploadedFile);
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
            DownloadFileDto downloadFileInfo;
            try
            {
                downloadFileInfo = await _musicService.GetMusicFileDownloadDataAsync(id);
            }
            catch (Exception ex)
            {
                return NotFound(ex.Message);// может быть ошибка в запросе, или нет в БД
            }

            if (!System.IO.File.Exists(downloadFileInfo.Filepath))
                return NotFound($"Файла с id={id} нет на диске");

            string extension = downloadFileInfo.Extension.Extension;
            string contentType = downloadFileInfo.Extension.MimeType;

            return PhysicalFile(downloadFileInfo.Filepath, contentType, $"{downloadFileInfo.FilenameForSend}{extension}");
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
                artist = new Artist(),
                album = "Test Album",
                year = 2024,
                genre = new Genre(),
                filesize = 1024000, // 1MB
                duration = TimeSpan.FromMinutes(3.5)
            };

            await _musicService.SaveToDbForTest(testMusic);

            return Ok(testMusic);
        }
    }
}