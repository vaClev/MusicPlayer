


using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using MusicServer.API.Database;
using MusicServer.API.Models;

namespace MusicServer.API.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class MusicController : ControllerBase
    {
        private readonly AppDbContext _context;

        public MusicController(AppDbContext context)
        {
            _context = context;
        }

        // GET: api/music
        [HttpGet]
        public async Task<ActionResult<IEnumerable<MusicFile>>> GetMusicFiles()
        {
            return await _context.MusicFiles.ToListAsync();
        }

        // GET: api/music/id5 ,где 5 это id сущности "MusicFile"
        [HttpGet("id{id}")]
        public async Task<ActionResult<MusicFile>> GetMusicFile(int id)
        {
            var musicFile = await _context.MusicFiles.FindAsync(id);

            if (musicFile == null)
            {
                return NotFound();
            }

            return musicFile;
        }


        /// Тестовый POST запрос добавления сущности в базу данных.
        /// TODO: Сделать админское приложение на WPF для добавления сущностей.
        [HttpPost("test")]
        public async Task<ActionResult<MusicFile>> PostTestMusic()
        {
            var testMusic = new MusicFile
            {
                filename = "test_song.mp3",
                filepath = "/music/test_song.mp3",
                title = "Test Song",
                artist = "Test Artist",
                album = "Test Album",
                year = 2024,
                genre = "Test Genre",
                filesize = 1024000, // 1MB
                duration = TimeSpan.FromMinutes(3.5)
            };

            _context.MusicFiles.Add(testMusic);
            await _context.SaveChangesAsync();

            return CreatedAtAction("GetMusicFile", new { id = testMusic.id }, testMusic);
        }
    }
}