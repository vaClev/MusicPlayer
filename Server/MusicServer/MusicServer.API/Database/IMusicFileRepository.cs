using MusicServer.API.DTO;
using MusicServer.API.Models;

namespace MusicServer.API.Database
{
    public interface IMusicFileRepository : IExtraFileRepository
    {
        // Базовые CRUD операции
        Task<MusicFile?> GetByIdAsync(int id);
        Task<MusicFile> AddAsync(MusicFile musicFile);
        Task<bool> DeleteAsync(int id);
        Task<bool> UpdateAsync(MusicFile musicFile);
        Task<int> GetTotalCount();// TODO потом расширим для поиска

        // Специализированные запросы с деталями
        Task<MusicFile?> GetByIdWithExtensionAndArtistAsync(int id);
        Task<MusicFile?> GetByIdWithDetailsAsync(int id);

        // Пагинация с использованием PagedResponse
        Task<List<MusicFile>> GetPagedAsync(PaginationParams pageParams); // TODO тоже передавать int totalCount в возврате

        // Поиск
        Task<(IEnumerable<MusicFile> items, int totalCount)> SearchAsync(SearchParams searchParams);


        // Работа со связанными сущностями TODO подумать о разделении ответственности
        Task<Artist> GetOrCreateArtistAsync(string artistName);
        Task<Genre> GetOrCreateGenreAsync(string genreName);
        Task<FileExtension?> GetFileExtensionAsync(string extension);

        // Сохранение изменений
        Task<int> SaveChangesAsync();

    }


    //Задел на возможность разделения репозиториев
    public interface IExtraFileRepository
    {
        // ===  МЕТОДЫ ДЛЯ ExtraFile ===
        /// Получить все дополнительные файлы для музыкального файла
        Task<IEnumerable<ExtraFile>> GetExtraFilesByMusicIdAsync(int musicFileId);

        /// Получить дополнительный файл по ID
        Task<ExtraFile?> GetExtraFileByIdAsync(int extraFileId);

        /// Получить дополнительный файл по ID со всеми связями
        Task<ExtraFile?> GetExtraFileByIdWithDetailsAsync(int extraFileId);

        /// Добавить дополнительный файл
        Task AddExtraFileAsync(ExtraFile extraFile);

        /// Удалить дополнительный файл
        Task<bool> DeleteExtraFileAsync(int extraFileId);
    }
}