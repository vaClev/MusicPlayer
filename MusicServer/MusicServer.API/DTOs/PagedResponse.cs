using System.ComponentModel.DataAnnotations;
using MusicServer.API.Models;

namespace MusicServer.API.DTO
{
    // такую сущность сервис вернет контроллеру по запросу списка песен
    // на всякий случай список шаблонный, но пока пользуемся только MusicFiles
    public class PagedResponse<T>
    {
        public int PageNumber { get; set; }
        public int PageSize { get; set; }
        public int TotalPages { get; set; }
        public int TotalCount { get; set; }
        public bool HasPreviousPage => PageNumber > 1;
        public bool HasNextPage => PageNumber < TotalPages;
        public List<T> Items { get; set; }

        public PagedResponse()
        {
            Items = new List<T>();
        }

        public PagedResponse(List<T> items, int count, PaginationParams pageParams)
        {
            PageNumber = pageParams.PageNumber;
            PageSize = pageParams.PageSize;
            TotalCount = count;
            TotalPages = (int)Math.Ceiling(count / (double)pageParams.PageSize);
            Items = items;
        }
    }
} // namespace MusicServer.API.DTO
