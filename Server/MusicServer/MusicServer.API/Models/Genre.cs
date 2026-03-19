using System.ComponentModel.DataAnnotations;

namespace MusicServer.API.Models
{
    public class Genre
    {
        [Key]
        public int Id { get; set; }

        [Required]
        [MaxLength(100)]
        public string Name { get; set; }

        [MaxLength(500)]
        public string? Description { get; set; }

        // Навигационное свойство
        public ICollection<MusicFile> MusicFiles { get; set; }
    }
}