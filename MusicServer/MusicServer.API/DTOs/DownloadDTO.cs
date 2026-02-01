using System.ComponentModel.DataAnnotations;
using MusicServer.API.Models;

namespace MusicServer.API.DTO
{
    // такую сущность сервис вернет контроллеру по запросу скачивания
    public class DownloadFileDto
    {
        [Required]
        public string FilenameForSend { get; set; }

        [Required]
        public string Filepath { get; set; }

        [Required]
        public FileExtension Extension { get; set; }

    }
} // namespace MusicServer.API.DTO
