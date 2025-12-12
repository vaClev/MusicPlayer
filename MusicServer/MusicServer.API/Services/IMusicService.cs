using MusicServer.API.Models;

namespace MusicServer.API.Services
{
    public interface IMusicService
    {
        Task<MusicFile> UploadMusicAsync(IFormFile file);
        Task<MusicFile?> GetMusicFileAsync(int id);
        Task<IEnumerable<MusicFile>> GetAllMusicFilesAsync();
        Task<string> GetMusicFilePathAsync(int id);
        Task<bool> DeleteMusicFileAsync(int id);

        Task<MusicFile> SaveToDbForTest(MusicFile musicFile);
    }
}