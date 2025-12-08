using System;
using System.ComponentModel.DataAnnotations;

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
        public string artist { get; set; }
        public string? album { get; set; }
        public int? year { get; set; }
        public string? genre { get; set; }

        public long filesize { get; set; }
        public TimeSpan duration { get; set; }

        public DateTime uploadDate { get; set; } = DateTime.UtcNow;

        // Для будущих окружений, концертов (типо плейлистов)
        //public virtual ICollection<Tracklist> tracklists { get; set; }
    }
}