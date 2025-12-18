using System.ComponentModel.DataAnnotations;
using MusicServer.API.DTOs;

namespace MusicServer.API.DTO
{
    //Отдаем информацию в таком виде при запросе списка песен.
	//условно GetAll. Get50. GetMyList
    public class MusicFileResponseDto
	{
		public int Id { get; set; }
		public string Title { get; set; }
		public string Artist { get; set; }
		public string? Album { get; set; }
		public string? Genre { get; set; }
		public int? Year { get; set; }
		public long FileSize { get; set; }
		public TimeSpan Duration { get; set; }
		public DateTime UploadDate { get; set; }
		public string? DownloadUrl { get; set; }
	}
}
