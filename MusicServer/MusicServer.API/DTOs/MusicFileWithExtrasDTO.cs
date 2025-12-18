using MusicServer.API.Models;
using System.ComponentModel.DataAnnotations;

namespace MusicServer.API.DTOs
{
    // Отдаем информацию в таком виде при запросе конкретного музыкальног файла по id.
    // условно GetById
    public class MusicFileWithExtrasDto
    {
        public int Id { get; set; }
        public string Title { get; set; }
        public string Artist { get; set; }
        public string? Album { get; set; }
        public string? Genre { get; set; }
        public int? Year { get; set; }
        public TimeSpan Duration { get; set; }
        public DateTime UploadDate { get; set; }
        public string? DownloadMusicUrl { get; set; }
        public List<ExtraFileDto> ExtraFiles { get; set; } = new();
    }
}