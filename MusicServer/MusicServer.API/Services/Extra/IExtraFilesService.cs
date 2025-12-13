using MusicServer.API.DTOs;
using MusicServer.API.Models;

namespace MusicServer.API.Services
{
    public interface IExtraFileService
    {
        Task<ExtraFile> UploadExtraFileAsync(UploadExtraFileDto uploadDto);
        Task<IEnumerable<ExtraFileDto>> GetExtraFilesByMusicIdAsync(int musicFileId);
        Task<ExtraFileDto> GetExtraFileAsync(int extraFileId);
        Task<string> GetExtraFilePathAsync(int extraFileId);
        Task<bool> DeleteExtraFileAsync(int extraFileId);
    }
}
