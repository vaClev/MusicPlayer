package org.example.vasilev.musicpro.services.player;

import javafx.util.Duration;
import org.example.vasilev.musicpro.models.MusicFile;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

import java.io.File;
import java.time.LocalDateTime;

public class MusicMetadataExtractService
{
    /// Создать MusicFile из локального аудиофайла с извлечением метаданных
    public MusicFile createMusicFileFromLocalFile(File audioFile)
    {
        if (audioFile == null || !audioFile.exists() || !audioFile.canRead())
        {
            throw new IllegalArgumentException("Невозможно прочитать файл: " +
                    (audioFile != null ? audioFile.getPath() : "null"));
        }

        // Генерируем уникальный ID на основе пути файла
        long fileId = generateFileId(audioFile);

        // Извлекаем базовую информацию из имени файла
        String fileName = audioFile.getName();
        String baseTitle = extractTitleFromFileName(fileName);

        // Создаем MusicFile с минимальной информацией
        MusicFile musicFile = new MusicFile(
                fileId,
                baseTitle,
                "Неизвестный исполнитель",  // Временное значение
                ".mp3",
                "",                    // Альбом
                "",                          // Жанр
                0,                           // Год
                audioFile.length(),          // размер файла
                "00:00",                     // Длительность
                LocalDateTime.now(),         // Дата загрузки (текущая)
                null                         // downloadMusicUrl нет для локальных файлов
        );

        // Устанавливаем путь к локальному файлу
        musicFile.setLocalFilePath(audioFile.getAbsolutePath());

        // Пытаемся извлечь метаданные из файла //TODO
        extractMetadataFromFile(audioFile, musicFile);

        return musicFile;
    }


    private long generateFileId(File audioFile)
    {
        // Используем хэш абсолютного пути файла *-1 локальный id<0. Постоянные id присваивает сервер.
        String absolutePath = audioFile.getAbsolutePath();

        return Math.abs(absolutePath.hashCode()) * -1;
    }


    private String extractTitleFromFileName(String fileName)
    {
        if (fileName == null || fileName.isEmpty())
        {
            return "Без названия";
        }

        // Убираем расширение файла
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0)
        {
            fileName = fileName.substring(0, dotIndex);
        }

        // Убираем лишние пробелы
        return fileName.trim();
    }


    private void extractMetadataFromFile(File audioFile, MusicFile musicFile)
    {
        extractMetadataWithJAudioTagger(musicFile.getLocalFilePath(), musicFile);
    }

    private void extractMetadataWithJAudioTagger(String filePath, MusicFile musicFile)
    {
        try
        {
            File audioFile = new File(filePath);
            AudioFile audioFileObj = AudioFileIO.read(audioFile);
            Tag tag = audioFileObj.getTag();

            if (tag != null) {
                // Получаем метаданные
                musicFile.setTitle(tag.getFirst(FieldKey.TITLE));
                musicFile.setArtist(tag.getFirst(FieldKey.ARTIST));
                //musicFile.setAlbum(tag.getFirst(FieldKey.ALBUM));
                //musicFile.setGenre(tag.getFirst(FieldKey.GENRE));
                //musicFile.setYear(tag.getFirst(FieldKey.YEAR));

                // Длительность
                AudioHeader header = audioFileObj.getAudioHeader();
                if (header != null)
                {
                    int trackLength = header.getTrackLength(); // в секундах
                    Duration duration = Duration.seconds(trackLength);
                    musicFile.setDuration(duration);
                }

                System.out.println("[SUCCESS] Metadata extracted with JAudioTagger");
            }
        }
        catch (Exception e)
        {
            System.err.println("[ERROR] JAudioTagger failed: " + e.getMessage());
        }
    }
}
