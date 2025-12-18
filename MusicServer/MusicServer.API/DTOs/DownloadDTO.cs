using System.ComponentModel.DataAnnotations;

namespace MusicServer.API.DTO
{
    // такую сущность сервис вернет контроллеру по запросу скачивания
    public class DownloadFileDto
    {
        [Required]
        public string FilenameForSend { get; set; }

        [Required]
        public string Filepath { get; set; }

    }
} // namespace MusicServer.API.DTO
