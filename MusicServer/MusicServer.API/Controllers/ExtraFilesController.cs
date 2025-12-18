

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
            var files = (await m_extraFileService.GetExtraFilesByMusicIdAsync(musicFileId))
                .Select(extraFile =>
                { 
                  extraFile.DownloadExtraUrl = Url.Action("Download", "ExtraFiles", new { id = extraFile.Id }, Request.Scheme);
                  return extraFile;
                })
                .ToList();

            if (files.Count() == 0)
                return NotFound($"Music file with id {musicFileId} have not extras");

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
                extraFile.DownloadExtraUrl = Url.Action("Download", "ExtraFiles", new { id = extraFile.Id }, Request.Scheme);

                return CreatedAtAction(nameof(GetExtraFile), new { id = extraFile.Id }, extraFile);
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


        // GET: api/extrafiles/download/5
        [HttpGet("download/id{id}")]
        public async Task<IActionResult> Download(int id)
        {
            try
            {
                var extraFile = await m_extraFileService.GetExtraFileDownloadDataAsync(id);
                if (!System.IO.File.Exists(extraFile.Filepath))
                    return NotFound($"файл id={id} не найден на диске");

                var contentType = GetContentType(extraFile.Filepath);

                return PhysicalFile(extraFile.Filepath, contentType, extraFile.FilenameForSend);
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
                ".gpx" or ".gp" or ".gp3" or ".gp4" or ".gp5" => "application/xml",
                ".doc" => "application/msword",
                ".docx" => "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                _ => "application/octet-stream"
            };
        }
    }
}