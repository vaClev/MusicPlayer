using MusicServer.API.DTO;
using MusicServer.API.DTOs;
using MusicServer.API.Models;

namespace MusicServer.API.Services
{
    public interface IExtraFileService
    {
        // Загрузить новый лоп файл (связанный с муз-файлом)
        Task<ExtraFileDto> UploadExtraFileAsync(UploadExtraFileDto uploadDto);

        // Получить список карточек доп файлов связанных с указанным муз файлом
        Task<IEnumerable<ExtraFileDto>> GetExtraFilesByMusicIdAsync(int musicFileId);

        // Получить карточку доп файла
        Task<ExtraFileDto> GetExtraFileAsync(int extraFileId);

        // Получить данные для скачивания ExtraFile
        Task<DownloadFileDto> GetExtraFileDownloadDataAsync(int id);

        // Удалить доп файл из базы и с диска
        Task<bool> DeleteExtraFileAsync(int extraFileId);
    }
}
