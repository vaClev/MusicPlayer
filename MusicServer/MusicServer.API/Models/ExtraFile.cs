using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace MusicServer.API.Models
{
    public enum ExtraFileType
    {
        SheetMusic,     // Ноты
        Tabs,           // Табулатуры
        Lyrics,         // Текст песни
        Chords,         // Аккорды
        Image,          // Обложка/фото
        Other           // Прочее
    }

    public class ExtraFile
    {
        [Key]
        public int Id { get; set; }

        [Required]
        public string OriginalFileName { get; set; }

        [Required]
        public string StoredFileName { get; set; }

        [Required]
        public string FilePath { get; set; }

        public string Description { get; set; }

        [Required]
        public ExtraFileType FileType { get; set; }

        public long FileSize { get; set; }

        public DateTime UploadDate { get; set; } = DateTime.UtcNow;

        // Внешний ключ для MusicFile (с какой песней связан)
        public int MusicFileId { get; set; }

        // Навигационное свойство
        [ForeignKey("MusicFileId")]
        public virtual MusicFile MusicFile { get; set; }
    }
}