using MusicServer.API.Models;
using System.ComponentModel.DataAnnotations;

namespace MusicServer.API.DTOs
{
    //Используем как "карточку" доп файла.
    public class ExtraFileDto
    {
        public int Id { get; set; }
        public string OriginalFileName { get; set; }
        public string Description { get; set; }
        public ExtraFileType FileType { get; set; }
        public string FileTypeName => FileType.ToString();
        public long FileSize { get; set; }
        public DateTime UploadDate { get; set; }
        public string? DownloadExtraUrl { get; set; }
        public int MusicFileId { get; set; }
    }

    public class UploadExtraFileDto
    {
        [Required]
        public int MusicFileId { get; set; }

        [Required]
        public ExtraFileType FileType { get; set; }

        [Required]
        public string Description { get; set; }

        [Required]
        public IFormFile File { get; set; }
    }
}