

using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using MusicServer.API.DTOs;
using MusicServer.API.Models;
using MusicServer.API.Services;

namespace MusicServer.API.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class ExtraFilesController : ControllerBase
    {
        private readonly IExtraFileService m_extraFileService;
        //private readonly ILogger<ExtraFilesController> _logger;

        public ExtraFilesController(IExtraFileService extraFileService)
        {
            m_extraFileService = extraFileService;
        }

        // GET: api/extrafiles/music/5
        [HttpGet("music/id{musicFileId}")]
        public async Task<ActionResult<IEnumerable<ExtraFileDto>>> GetByMusicId(int musicFileId)
        {
            var files = await m_extraFileService.GetExtraFilesByMusicIdAsync(musicFileId);
            if (files.Count() == 0)
                return NotFound($"Music file with id {musicFileId} not found");

            return Ok(files);
        }

        // GET: api/extrafiles/5
        [HttpGet("id{id}")]
        public async Task<ActionResult<ExtraFileDto>> GetExtraFile(int id)
        {
            var extraFile = await m_extraFileService.GetExtraFileAsync(id);
            if (extraFile == null)
                return NotFound();

            extraFile.DownloadExtraUrl = Url.Action("Download", "ExtraFiles", new { id = id }, Request.Scheme);
            return Ok(extraFile);
        }


        // POST: api/extrafiles/upload
        [HttpPost("upload")]
        [RequestSizeLimit(10 * 1024 * 1024)] // 10MB
        public async Task<ActionResult<ExtraFileDto>> UploadExtraFile([FromForm] UploadExtraFileDto uploadDto)
        {
            try
            {
                if (uploadDto.File == null || uploadDto.File.Length == 0)
                    return BadRequest("File is required");

                var extraFile = await m_extraFileService.UploadExtraFileAsync(uploadDto);

                var dto = new ExtraFileDto
                {
                    Id = extraFile.Id,
                    OriginalFileName = extraFile.OriginalFileName,
                    Description = extraFile.Description,
                    FileType = extraFile.FileType,
                    FileSize = extraFile.FileSize,
                    UploadDate = extraFile.UploadDate,
                    DownloadExtraUrl = Url.Action("Download", "ExtraFiles", new { id = extraFile.Id }, Request.Scheme)
                };

                return CreatedAtAction(nameof(GetExtraFile), new { id = extraFile.Id }, dto);
            }
            catch (ArgumentException ex)
            {
                return BadRequest(ex.Message);
            }
            catch (Exception ex)
            {
                return StatusCode(500, "Internal server error");
            }
        }


        // GET: api/extrafiles/download/5
        [HttpGet("download/id{id}")]
        public async Task<IActionResult> Download(int id)
        {
            try
            {
                // TODO подумать над созданием ExtraFileDownloadDTO  и для MusicFile тоже актуально
                // а то тут 2 запроса в БД вместо одного
                string filePath = await m_extraFileService.GetExtraFilePathAsync(id);
                if (!System.IO.File.Exists(filePath))
                    return NotFound();

                var contentType = GetContentType(filePath);

                var extraFile = await m_extraFileService.GetExtraFileEntityAsync(id);
                var downloadName = $"{extraFile?.MusicFile?.artist ?? "Unknown"} - " +
                             $"{extraFile?.MusicFile?.title ?? "Unknown"} - " +
                             $"{extraFile?.OriginalFileName}";

                return PhysicalFile(filePath, contentType, downloadName);
            }
            catch (ArgumentException ex)
            {
                return BadRequest(ex.Message);
            }
            catch
            {
                return StatusCode(500, "Internal server error");
            }
        }

        private string GetContentType(string filePath)
        {
            var extension = Path.GetExtension(filePath).ToLowerInvariant();

            return extension switch
            {
                ".pdf" => "application/pdf",
                ".txt" => "text/plain",
                ".jpg" or ".jpeg" => "image/jpeg",
                ".png" => "image/png",
                ".gif" => "image/gif",
                ".gpx" => "application/xml",
                ".gp" => "application/xml",
                ".gp3" => "application/xml",
                ".gp4" => "application/xml",
                ".gp5" => "application/xml",
                ".doc" => "application/msword",
                ".docx" => "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                _ => "application/octet-stream"
            };
        }
    }
}