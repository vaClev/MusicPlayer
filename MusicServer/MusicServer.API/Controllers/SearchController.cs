using Microsoft.AspNetCore.Mvc;
using MusicServer.API.DTO;
using MusicServer.API.DTOs;
using MusicServer.API.Models;
using MusicServer.API.Services;

namespace MusicServer.API.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class SearchController : ControllerBase
    {
        private readonly IMusicService _musicService;
        private readonly ILogger<SearchController> _logger;

        public SearchController(
            IMusicService musicService,
            ILogger<SearchController> logger)
        {
            _musicService = musicService;
            _logger = logger;
        }

        /// <summary>
        /// Универсальный поиск по музыкальным файлам
        /// </summary>
        /// <param name="searchParams">Параметры поиска</param>
        /// <returns>Страница с результатами поиска</returns>
        /// <remarks>
        /// Примеры запросов:
        /// 
        /// 1. Простой поиск по названию/исполнителю:
        ///    GET /api/search?query=blur
        /// 
        /// 2. Поиск по точному названию песни:
        ///    GET /api/search?title=Coffee And TV
        /// 
        /// 3. Поиск по исполнителю:
        ///    GET /api/search?artist=Blur
        /// 
        /// 4. Поиск по жанру:
        ///    GET /api/search?genre=Rock
        /// 
        /// 5. Комбинированный поиск с пагинацией:
        ///    GET /api/search?artist=Blur&amp;year=2000&amp;pageNumber=2&amp;pageSize=5
        /// 
        /// 6. Поиск с сортировкой:
        ///    GET /api/search?genre=Rock&amp;sortBy=year&amp;sortDesc=true
        /// </remarks>
        [HttpGet]
        public async Task<ActionResult<PagedResponse<MusicFileResponseDto>>> Search(
            [FromQuery] SearchParams searchParams)
        {
            try
            {
                // Если нет критериев поиска, возвращаем пустой результат
                if (!searchParams.HasAnyCriteria)
                {
                    return Ok(new PagedResponse<MusicFileResponseDto>(
                        new List<MusicFileResponseDto>(), 0, searchParams));
                }

                var results = await _musicService.SearchMusicFilesAsync(searchParams);

                // Добавляем URL для скачивания
                foreach (var item in results.Items)
                {
                    item.DownloadUrl = Url.Action("Download", "Music",
                        new { id = item.Id }, Request.Scheme);
                }

                return Ok(results);
            }
            catch (Exception ex)
            {
                return StatusCode(500, new { error = "Internal server error during search" });
            }
        }

        /// <summary>
        /// Быстрый поиск по одному параметру (упрощенный вариант)
        /// </summary>
        [HttpGet("simple")]
        public async Task<ActionResult<PagedResponse<MusicFileResponseDto>>> SimpleSearch(
            [FromQuery] string q,
            [FromQuery] int pageNumber = 1,
            [FromQuery] int pageSize = 10)
        {
            var searchParams = new SearchParams
            {
                Query = q,
                PageNumber = pageNumber,
                PageSize = pageSize
            };

            return await Search(searchParams);
        }
    }
}