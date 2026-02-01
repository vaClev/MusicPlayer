using System.ComponentModel.DataAnnotations;

namespace MusicServer.API.Models
{
    public class FileExtension
    {
        [Key]
        public int Id { get; set; }

        [Required]
        [MaxLength(10)]
        public string Extension { get; set; } // Например: ".mp3", ".jpg", ".pdf"

        [Required]
        [MaxLength(100)]
        public string MimeType { get; set; } // Например: "audio/mpeg", "image/jpeg"

        [Required]
        [MaxLength(20)]
        public string Category { get; set; } // "audio", "image", "document", "other"

        public string Description { get; set; }

        // Навигационные свойства
        public virtual ICollection<MusicFile> MusicFiles { get; set; }
        public virtual ICollection<ExtraFile> ExtraFiles { get; set; }
    }
}