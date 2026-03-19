using System;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace MusicServer.API.Models
{
    public class MusicFile
    {
        [Key]
        public int id { get; set; }

        [Required]
        public string filename { get; set; }

        [Required]
        public string filepath { get; set; }

        public string title { get; set; }

        // Внешний ключ на Artist
        public int ArtistId { get; set; }
        [ForeignKey("ArtistId")]
        public virtual Artist artist { get; set; }

        public string? album { get; set; }
        public int? year { get; set; }

        // Внешний ключ на Genre
        public int GenreId { get; set; }
        [ForeignKey("GenreId")]
        public virtual Genre genre { get; set; }

        public long filesize { get; set; }
        public TimeSpan duration { get; set; }

        public DateTime uploadDate { get; set; } = DateTime.UtcNow;

        // Навигационное свойство для ExtraFiles
        public virtual ICollection<ExtraFile> ExtraFiles { get; set; } = new List<ExtraFile>();

        // Любой файл имеет конкретное расширение
        // Внешний ключ для расширения файла
        public int? FileExtensionId { get; set; }
        // Навигационное свойство
        [ForeignKey("FileExtensionId")]
        public virtual FileExtension FileExtension { get; set; }
    }
}