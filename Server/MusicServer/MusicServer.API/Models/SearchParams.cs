namespace MusicServer.API.Models
{
    public class SearchParams : PaginationParams
    {
        /// <summary>
        /// Поисковый запрос (ищет по названию песни и имени исполнителя)
        /// </summary>
        public string? Query { get; set; }

        /// <summary>
        /// Точное название песни (для Cases 1)
        /// </summary>
        public string? Title { get; set; }

        /// <summary>
        /// Имя исполнителя (для Cases 2)
        /// </summary>
        public string? Artist { get; set; }

        /// <summary>
        /// Название жанра (для Cases 3)
        /// </summary>
        public string? Genre { get; set; }

        /// <summary>
        /// ID исполнителя (если известен)
        /// </summary>
        public int? ArtistId { get; set; }

        /// <summary>
        /// ID жанра (если известен)
        /// </summary>
        public int? GenreId { get; set; }

        /// <summary>
        /// Год выпуска
        /// </summary>
        public int? Year { get; set; }

        /// <summary>
        /// Флаг - искать точное совпадение или частичное
        /// </summary>
        public bool ExactMatch { get; set; } = false;

        /// <summary>
        /// Сортировка (по умолчанию по названию)
        /// </summary>
        public string SortBy { get; set; } = "Title";

        /// <summary>
        /// Направление сортировки
        /// </summary>
        public bool SortDesc { get; set; } = false;

        /// <summary>
        /// Проверка, есть ли хоть один критерий поиска
        /// </summary>
        public bool HasAnyCriteria =>
            !string.IsNullOrWhiteSpace(Query) ||
            !string.IsNullOrWhiteSpace(Title) ||
            !string.IsNullOrWhiteSpace(Artist) ||
            !string.IsNullOrWhiteSpace(Genre) ||
            ArtistId.HasValue ||
            GenreId.HasValue ||
            Year.HasValue;
    }
}