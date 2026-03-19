using System.ComponentModel.DataAnnotations;

namespace MusicServer.API.Models
{
    public class Artist
    {
        [Key]
        public int Id { get; set; }

        [Required]
        [MaxLength(200)]
        public string Name { get; set; }

        [MaxLength(1000)]
        public string? Bio { get; set; }

        [MaxLength(100)]
        public string? Country { get; set; }


        // Навигационное свойство
        public ICollection<MusicFile> MusicFiles { get; set; }
    }
}