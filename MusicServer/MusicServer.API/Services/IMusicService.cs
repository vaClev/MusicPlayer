using MusicServer.API.DTO;
using MusicServer.API.DTOs;
using MusicServer.API.Models;

namespace MusicServer.API.Services
{
    public interface IMusicService
    {
        Task<MusicFileResponseDto> UploadMusicAsync(IFormFile file);

        // Получить MusicFile(карточку)
        Task<MusicFileWithExtrasDto> GetMusicFileAsync(int id);

        // Получить все музыкальные файлы(карточки) из библиотки
        Task<IEnumerable<MusicFileResponseDto>> GetAllMusicFilesAsync();

        // Получить данные для скачивания MusicFile
        Task<DownloadFileDto> GetMusicFileDownloadDataAsync(int id);

        // Удаление карточки и файла
        Task<bool> DeleteMusicFileAsync(int id);

        Task<MusicFile> SaveToDbForTest(MusicFile musicFile);
    }
}