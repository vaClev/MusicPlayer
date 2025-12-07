
using Microsoft.AspNetCore.Http.Features;
using MusicServer.API.Services;

namespace MusicServer.API
{
    public class AppConfigUtils
    {
        public static String? InitMusicFolder(string configPath) //относительный путь из файла appsettings.json
        {
            // Папку создаем в директории расположения приложения папку MusicFiles.
            // TODO: подумать как лучше
            var musicFolder = Path.Combine(Directory.GetCurrentDirectory(), configPath);
            if (!Directory.Exists(musicFolder))
            {
                Directory.CreateDirectory(musicFolder);
            }
            return musicFolder;
        }

        public static void RegistrateMusicService(WebApplicationBuilder builder)
        {
            builder.Services.AddScoped<IMusicService, MusicService>();

            // Настройка максимального размера файла
            builder.Services.Configure<IISServerOptions>(options =>
            {
                options.MaxRequestBodySize = 52428800; // 50MB
            });

            builder.Services.Configure<FormOptions>(options =>
            {
                options.ValueLengthLimit = int.MaxValue;
                options.MultipartBodyLengthLimit = 52428800; // 50MB
                options.MemoryBufferThreshold = 52428800;
            });
        }
    }
}