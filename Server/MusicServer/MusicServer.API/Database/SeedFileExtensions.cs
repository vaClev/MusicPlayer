using MusicServer.API.Database;
using MusicServer.API.Models;

public static class SeedFileExtensions
{
    public static void Seed(AppDbContext context)
    {
        if (!context.FileExtensions.Any())
        {
            var extensions = new List<FileExtension>
            {
                // Аудио файлы
                new() { Extension = ".mp3", MimeType = "audio/mpeg", Category = "audio", Description = "MP3 Audio" },
                new() { Extension = ".wav", MimeType = "audio/wav", Category = "audio", Description = "WAV Audio" },
                new() { Extension = ".flac", MimeType = "audio/flac", Category = "audio", Description = "FLAC Audio" },
                new() { Extension = ".ogg", MimeType = "audio/ogg", Category = "audio", Description = "OGG Audio" },
                new() { Extension = ".m4a", MimeType = "audio/mp4", Category = "audio", Description = "MPEG-4 Audio" },
                new() { Extension = ".aac", MimeType = "audio/aac", Category = "audio", Description = "AAC Audio" },
    
                // Изображения
                new() { Extension = ".jpg", MimeType = "image/jpeg", Category = "image", Description = "JPEG Image" },
                new() { Extension = ".jpeg", MimeType = "image/jpeg", Category = "image", Description = "JPEG Image" },
                new() { Extension = ".png", MimeType = "image/png", Category = "image", Description = "PNG Image" },
                new() { Extension = ".gif", MimeType = "image/gif", Category = "image", Description = "GIF Image" },
                new() { Extension = ".bmp", MimeType = "image/bmp", Category = "image", Description = "Bitmap Image" },
                new() { Extension = ".svg", MimeType = "image/svg+xml", Category = "image", Description = "SVG Vector Image" },
                new() { Extension = ".webp", MimeType = "image/webp", Category = "image", Description = "WebP Image" },
    
                // Документы и текстовые файлы
                new() { Extension = ".pdf", MimeType = "application/pdf", Category = "document", Description = "PDF Document" },
                new() { Extension = ".txt", MimeType = "text/plain", Category = "document", Description = "Plain Text File" },
                new() { Extension = ".doc", MimeType = "application/msword", Category = "document", Description = "Microsoft Word Document" },
                new() { Extension = ".docx", MimeType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document", Category = "document", Description = "Microsoft Word (OpenXML)" },
    
                // Табулатуры и ноты (особенно важно для вашего проекта)
                new() { Extension = ".gpx", MimeType = "application/xml", Category = "sheetmusic", Description = "Guitar Pro 6+ File" },
                new() { Extension = ".gp", MimeType = "application/xml", Category = "sheetmusic", Description = "Guitar Pro File" },
                new() { Extension = ".gp3", MimeType = "application/xml", Category = "sheetmusic", Description = "Guitar Pro 3 File" },
                new() { Extension = ".gp4", MimeType = "application/xml", Category = "sheetmusic", Description = "Guitar Pro 4 File" },
                new() { Extension = ".gp5", MimeType = "application/xml", Category = "sheetmusic", Description = "Guitar Pro 5 File" },
                new() { Extension = ".gp6", MimeType = "application/xml", Category = "sheetmusic", Description = "Guitar Pro 6 File" },
                new() { Extension = ".gp7", MimeType = "application/xml", Category = "sheetmusic", Description = "Guitar Pro 7 File" },
                new() { Extension = ".gp8", MimeType = "application/xml", Category = "sheetmusic", Description = "Guitar Pro 8 File" },
                new() { Extension = ".ptb", MimeType = "application/xml", Category = "sheetmusic", Description = "Power Tab Editor File" },
                new() { Extension = ".ptx", MimeType = "application/xml", Category = "sheetmusic", Description = "Power Tab XML File" },
                new() { Extension = ".tux", MimeType = "application/xml", Category = "sheetmusic", Description = "TuxGuitar File" },
                new() { Extension = ".musicxml", MimeType = "application/vnd.recordare.musicxml+xml", Category = "sheetmusic", Description = "MusicXML File" },
                new() { Extension = ".mxl", MimeType = "application/vnd.recordare.musicxml", Category = "sheetmusic", Description = "Compressed MusicXML" },
                new() { Extension = ".mid", MimeType = "audio/midi", Category = "sheetmusic", Description = "MIDI File" },
                new() { Extension = ".midi", MimeType = "audio/midi", Category = "sheetmusic", Description = "MIDI File" },
                new() { Extension = ".sib", MimeType = "application/octet-stream", Category = "sheetmusic", Description = "Sibelius Score" },
                new() { Extension = ".mscz", MimeType = "application/octet-stream", Category = "sheetmusic", Description = "MuseScore 2+ File" },
                new() { Extension = ".mscx", MimeType = "application/xml", Category = "sheetmusic", Description = "MuseScore Uncompressed" },
            };

            context.FileExtensions.AddRange(extensions);
            context.SaveChanges();
        }
    }
}