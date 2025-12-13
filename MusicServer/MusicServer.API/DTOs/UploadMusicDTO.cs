using System.ComponentModel.DataAnnotations;

namespace MusicServer.API.DTO
{
    // Сейчас UploadMusicDto не используется,
    // но по идее в таком виде мог бы загружать файлы администратор.
    // для которых не удается извлечь MetaData
    // TODO при разработке клиента попробовать wav flac и другие.
    // скорее всего пригодится. И нужно будет создать еще один POST music/upload/new
    public class UploadMusicDto
    {
        [Required]
        public string Filename { get; set; }

        public string Title { get; set; }
        public string Artist { get; set; }
        public string Album { get; set; }
    }
} // namespace MusicServer.API.DTO
