using System.ComponentModel.DataAnnotations;

namespace MusicServer.API.DTO
{
    public class UploadMusicDto
    {
        [Required]
        public string Filename { get; set; }

        public string Title { get; set; }
        public string Artist { get; set; }
        public string Album { get; set; }
    }
} // namespace MusicServer.API.DTO
