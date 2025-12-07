using Microsoft.EntityFrameworkCore;
using MusicServer.API.Models;

namespace MusicServer.API.Database
{
    public class AppDbContext : DbContext
    {
        /// Конструктор передающий опции базовому классу DbContext (EntityFrameworkCore) 
        public AppDbContext(DbContextOptions<AppDbContext> options)
            : base(options)
        {
        }

        /// Набор сущностей MusicServer.API.Models:::::MusicFile
        public DbSet<MusicFile> MusicFiles
        {
            get; set;
        }
        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            base.OnModelCreating(modelBuilder);

            // TODO: Настройка отношений (когда будут окружения-треклисты)  1 песня:много треклистов
            //modelBuilder.Entity<MusicFile>()
            //    .HasMany(m => m.Playlists)
            //    .WithMany(p => p.MusicFiles);

            // TODO: Настройка отношений (когда будут вложения в карточку песни)  1 песня : несколько вложенных файлов с нотами и т.п
        }
    }
}
