using Microsoft.EntityFrameworkCore;
using MusicServer.API.Database;
using MusicServer.API.DTO;
using MusicServer.API.Models;

namespace MusicServer.API.Database.Repositories
{
	public class MusicFileRepository : IMusicFileRepository
	{
		private readonly AppDbContext _context;

		public MusicFileRepository(AppDbContext context)
		{
			_context = context;
		}

		#region "Базовые CRUD операции"

		public async Task<MusicFile?> GetByIdAsync(int id)
		{
			return await _context.MusicFiles
				.FirstOrDefaultAsync(mf => mf.id == id);
		}

		public async Task<MusicFile> AddAsync(MusicFile musicFile)
		{
			await _context.MusicFiles.AddAsync(musicFile);
			return musicFile;
		}

		public async Task<bool> DeleteAsync(int id)
		{
			var musicFile = await GetByIdAsync(id);
			if (musicFile == null)
				return false;

			_context.MusicFiles.Remove(musicFile);
			return true;
		}

		public async Task<bool> UpdateAsync(MusicFile musicFile)
		{
			_context.MusicFiles.Update(musicFile);
			return true;
		}

		public async Task<int> GetTotalCount()
		{
			return await _context.MusicFiles.CountAsync();
		}

		#endregion

		#region "Специализированные запросы с деталями"
		public async Task<MusicFile?> GetByIdWithExtensionAndArtistAsync(int id)
		{
			return await _context.MusicFiles
				.Include(mf => mf.FileExtension)
				.Include(mf => mf.artist)
				.FirstOrDefaultAsync(mf => mf.id == id);
			//минимальная информация для скачивания файла
		}


		public async Task<MusicFile?> GetByIdWithDetailsAsync(int id)
		{
			return await _context.MusicFiles
				.Include(mf => mf.FileExtension)
				.Include(mf => mf.artist)
				.Include(mf => mf.genre)
				.Include(mf => mf.ExtraFiles)
				.FirstOrDefaultAsync(mf => mf.id == id);
		}

		#endregion

		#region "Пагинация"

		public async Task<List<MusicFile>> GetPagedAsync(PaginationParams pageParams)
		{
			// Базовый запрос с включением связанных данных
			var query = _context.MusicFiles
				.Include(mf => mf.FileExtension)
				.Include(mf => mf.artist)
				.Include(mf => mf.genre)
				.OrderByDescending(m => m.uploadDate)
				.AsNoTracking();

			// Применяем пагинацию и возвращаем список
			return await query
				.Skip((pageParams.PageNumber - 1) * pageParams.PageSize)
				.Take(pageParams.PageSize)
				.ToListAsync();
		}
		#endregion



		#region "Работа со связанными сущностями" //TODO рефакторить в другие классы
		public async Task<Artist> GetOrCreateArtistAsync(string artistName)
		{
			if (string.IsNullOrWhiteSpace(artistName))
				artistName = "Unknown Artist";

			// Ищем существующего исполнителя
			// TODO проверять альтернативное написание - не плодить дубликаты
			var artist = await _context.Artists
				.FirstOrDefaultAsync(a => a.Name == artistName);

			if (artist == null)
			{
				artist = new Artist
				{
					Name = artistName,
					Bio = "",
					Country = ""
				};
				await _context.Artists.AddAsync(artist);
				await SaveChangesAsync(); // Сохраняем чтобы получить ID
			}

			return artist;
		}

		public async Task<Genre> GetOrCreateGenreAsync(string genreName)
		{
			if (string.IsNullOrWhiteSpace(genreName))
				genreName = "Unknown Genre";

			// Ищем существующий жанр
			var genre = await _context.Genres
				.FirstOrDefaultAsync(g => g.Name == genreName);

			if (genre == null)
			{
				genre = new Genre
				{
					Name = genreName,
					Description = ""
				};
				await _context.Genres.AddAsync(genre);
				await SaveChangesAsync(); // Сохраняем чтобы получить ID
			}

			return genre;
		}

		public async Task<FileExtension?> GetFileExtensionAsync(string extension)
		{
			return await _context.FileExtensions
				.FirstOrDefaultAsync(e => e.Extension == extension);
		}

		#endregion

		#region "Сохранение изменений"
		public async Task<int> SaveChangesAsync()
		{
			return await _context.SaveChangesAsync();
		}

		#endregion
	}
}