namespace MusicServer.API.Models
{
    public class PaginationParams
    {
        private const int MaxPageSize = 50;
        private int _pageSize = 10;

        private string _errorText = string.Empty;

        /// Номер страницы (начиная с 1)
        public int PageNumber { get; set; } = 1;

        /// Количество элементов на странице
        public int PageSize
        {
            get => _pageSize;
            set => _pageSize = value > MaxPageSize ? MaxPageSize : value;
        }

        public (bool IsValid, string ErrorMessage) Validate()
        {
            if (PageNumber < 1)
            {
                return (false, "Page number must be greater than or equal to 1");
            }

            if (PageSize < 1)
            {
                return (false, "Page size must be greater than 0");
            }

            if (PageSize > MaxPageSize)
            {
                return (false, $"Page size cannot exceed {MaxPageSize}");
            }

            return (true, string.Empty);
        }
        /// Формирует строку запроса для ссылок
        public string ToQueryString()
        {
            return $"pageNumber={PageNumber}&pageSize={PageSize}";
        }
    }
}