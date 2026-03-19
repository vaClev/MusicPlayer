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


		// Поиск
		public async Task<(IEnumerable<MusicFile> items, int totalCount)> SearchAsync(SearchParams searchParams)
		{
			// Начинаем с базового запроса
			var query = _context.MusicFiles
				.Include(m => m.artist)
				.Include(m => m.genre)
				.Include(m => m.FileExtension)
				.AsQueryable();

			// ПРИМЕНЯЕМ ВСЕ ФИЛЬТРЫ
			query = ApplySearchFilters(query, searchParams);

			// Получаем общее количество ДО пагинации
			var totalCount = await query.CountAsync();

			// ПРИМЕНЯЕМ СОРТИРОВКУ
			query = ApplySorting(query, searchParams);

			// ПРИМЕНЯЕМ ПАГИНАЦИЮ
			var items = await query
				.Skip((searchParams.PageNumber - 1) * searchParams.PageSize)
				.Take(searchParams.PageSize)
				.ToListAsync();

			return (items, totalCount);
		}

		/// Применение всех фильтров поиска
		private IQueryable<MusicFile> ApplySearchFilters(IQueryable<MusicFile> query, SearchParams searchParams)
		{
			// 1. Поиск по общему запросу (ищет в названии и исполнителе)
			if (!string.IsNullOrWhiteSpace(searchParams.Query))
			{
				var searchTerm = searchParams.Query.Trim().ToLower();
				query = query.Where(m =>
					m.title.ToLower().Contains(searchTerm) ||
					(m.artist != null && m.artist.Name.ToLower().Contains(searchTerm)));
			}

			// 2. Поиск по названию песни
			if (!string.IsNullOrWhiteSpace(searchParams.Title))
			{
				if (searchParams.ExactMatch)
				{
					query = query.Where(m => m.title == searchParams.Title);
				}
				else
				{
					var searchTerm = searchParams.Title.Trim().ToLower();
					query = query.Where(m => m.title.ToLower().Contains(searchTerm));
				}
			}

			// 3. Поиск по исполнителю (по имени)
			if (!string.IsNullOrWhiteSpace(searchParams.Artist))
			{
				if (searchParams.ExactMatch)
				{
					query = query.Where(m =>
						m.artist != null && m.artist.Name == searchParams.Artist);
				}
				else
				{
					var searchTerm = searchParams.Artist.Trim().ToLower();
					query = query.Where(m =>
						m.artist != null && m.artist.Name.ToLower().Contains(searchTerm));
				}
			}

			// 4. Поиск по ID исполнителя (точное совпадение)
			if (searchParams.ArtistId.HasValue)
			{
				query = query.Where(m => m.ArtistId == searchParams.ArtistId);
			}

			// 5. Поиск по жанру (по имени)
			if (!string.IsNullOrWhiteSpace(searchParams.Genre))
			{
				if (searchParams.ExactMatch)
				{
					query = query.Where(m =>
						m.genre != null && m.genre.Name == searchParams.Genre);
				}
				else
				{
					var searchTerm = searchParams.Genre.Trim().ToLower();
					query = query.Where(m =>
						m.genre != null && m.genre.Name.ToLower().Contains(searchTerm));
				}
			}

			// 6. Поиск по ID жанра (точное совпадение)
			if (searchParams.GenreId.HasValue)
			{
				query = query.Where(m => m.GenreId == searchParams.GenreId);
			}

			// 7. Поиск по году
			if (searchParams.Year.HasValue)
			{
				query = query.Where(m => m.year == searchParams.Year);
			}

			return query;
		}

		private IQueryable<MusicFile> ApplySorting(IQueryable<MusicFile> query, SearchParams searchParams)
		{
			if (string.IsNullOrWhiteSpace(searchParams.SortBy))
				searchParams.SortBy = "Title";

			return searchParams.SortBy.ToLower() switch
			{
				"title" => searchParams.SortDesc
					? query.OrderByDescending(m => m.title)
					: query.OrderBy(m => m.title),

				"artist" => searchParams.SortDesc
					? query.OrderByDescending(m => m.artist.Name)
					: query.OrderBy(m => m.artist.Name),

				"album" => searchParams.SortDesc
					? query.OrderByDescending(m => m.album)
					: query.OrderBy(m => m.album),

				"genre" => searchParams.SortDesc
					? query.OrderByDescending(m => m.genre.Name)
					: query.OrderBy(m => m.genre.Name),

				"year" => searchParams.SortDesc
					? query.OrderByDescending(m => m.year)
					: query.OrderBy(m => m.year),

				"duration" => searchParams.SortDesc
					? query.OrderByDescending(m => m.duration)
					: query.OrderBy(m => m.duration),

				"uploaddate" => searchParams.SortDesc
					? query.OrderByDescending(m => m.uploadDate)
					: query.OrderBy(m => m.uploadDate),

				_ => query.OrderBy(m => m.title) // По умолчанию по названию
			};
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