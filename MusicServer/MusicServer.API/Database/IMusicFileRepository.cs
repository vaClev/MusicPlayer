using MusicServer.API.DTO;
using MusicServer.API.Models;

namespace MusicServer.API.Database
{
    public interface IMusicFileRepository
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
        Task<List<MusicFile>> GetPagedAsync(PaginationParams pageParams);

        
        // Работа со связанными сущностями TODO подумать о разделении ответственности
        Task<Artist> GetOrCreateArtistAsync(string artistName);
        Task<Genre> GetOrCreateGenreAsync(string genreName);
        Task<FileExtension?> GetFileExtensionAsync(string extension);

        // Сохранение изменений
        Task<int> SaveChangesAsync();
    }
}