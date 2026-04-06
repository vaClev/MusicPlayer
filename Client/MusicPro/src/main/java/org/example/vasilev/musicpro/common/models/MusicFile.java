package org.example.vasilev.musicpro.common.models;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Платформонезависимая модель музыкального файла.
 * Использует только стандартные Java-типы (без JavaFX).
 * Подходит для Desktop, Android и iOS.
 */
public class MusicFile
{
    // === Информация с сервера ===
    private final Long id;
    private String title;
    private String artist;
    private final String extension;
    private String album;
    private String genre;
    private Integer year;
    private Long fileSize;          // в байтах
    private String duration;        // формат "00:03:56"
    private LocalDateTime uploadDate;
    private String downloadUrl;

    // === Информация клиентского приложения ===
    private boolean downloaded;
    private String localFilePath;

    // === Связанные файлы (ноты, табы и т.д.) ===
    private final List<ExtraFile> extraFiles = new ArrayList<>();

    // === Конструкторы ===

    /**
     * Конструктор для данных с сервера (список песен)
     */
    public MusicFile(Long id, String title, String artist, String extension,
                     String album, String genre, Integer year, Long fileSize,
                     String duration, LocalDateTime uploadDate, String downloadUrl)
    {
        this.id = id;
        this.title = title != null ? title : "";
        this.artist = artist != null ? artist : "Unknown Artist";
        this.extension = extension != null ? extension : "";
        this.album = album != null ? album : "Unknown Album";
        this.genre = genre != null ? genre : "Unknown";
        this.year = year != null ? year : 0;
        this.fileSize = fileSize != null ? fileSize : 0L;
        this.duration = duration != null ? duration : "00:00";
        this.uploadDate = uploadDate;
        this.downloadUrl = downloadUrl;
        this.downloaded = false;
        this.localFilePath = null;
    }

    /**
     * Конструктор для детальной информации о песне (с дополнительными файлами)
     */
    public MusicFile(Long id, String title, String artist, String extension,
                     String album, String genre, Integer year, Long fileSize,
                     String duration, LocalDateTime uploadDate, String downloadUrl,
                     List<ExtraFile> extraFiles)
    {
        this(id, title, artist, extension, album, genre, year, fileSize, duration, uploadDate, downloadUrl);
        if (extraFiles != null)
        {
            this.extraFiles.addAll(extraFiles);
        }
    }


    // === Геттеры ===
    public Long getId()
    {
        return id;
    }

    public String getTitle()
    {
        return title;
    }

    public String getArtist()
    {
        return artist;
    }

    public String getExtension()
    {
        return extension;
    }

    public String getAlbum()
    {
        return album;
    }

    public String getGenre()
    {
        return genre;
    }

    public Integer getYear()
    {
        return year;
    }

    public Long getFileSize()
    {
        return fileSize;
    }

    public String getDuration()
    {
        return duration;
    }

    public LocalDateTime getUploadDate()
    {
        return uploadDate;
    }

    public String getDownloadUrl()
    {
        return downloadUrl;
    }

    public boolean isDownloaded()
    {
        return downloaded;
    }

    public String getLocalFilePath()
    {
        return localFilePath;
    }

    public List<ExtraFile> getExtraFiles()
    {
        return extraFiles;
    }

    // === Сеттеры ===

    public void setTitle(String title)
    {
        this.title = title;
    }

    public void setArtist(String artist)
    {
        this.artist = artist;
    }

    public void setAlbum(String album)
    {
        this.album = album;
    }

    public void setGenre(String genre)
    {
        this.genre = genre;
    }

    public void setYear(Integer year)
    {
        this.year = year;
    }

    public void setFileSize(Long fileSize)
    {
        this.fileSize = fileSize;
    }

    /**
     * Установка длительности в формате "00:03:56"
     */
    public void setDuration(String duration)
    {
        this.duration = duration;
    }

    /**
     * Установка длительности из Duration в секундах
     */
    public void setDurationFromSeconds(int seconds)
    {
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        int secs = seconds % 60;
        if (hours > 0)
        {
            this.duration = String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, secs);
        } else
        {
            this.duration = String.format(Locale.US, "%02d:%02d", minutes, secs);
        }
    }

    public void setUploadDate(LocalDateTime uploadDate)
    {
        this.uploadDate = uploadDate;
    }

    public void setDownloadUrl(String downloadUrl)
    {
        this.downloadUrl = downloadUrl;
    }

    public void setDownloaded(boolean downloaded)
    {
        this.downloaded = downloaded;
    }

    public void setLocalFilePath(String localFilePath)
    {
        this.localFilePath = localFilePath;
    }

    // === Вспомогательные методы ===

    /**
     * Форматированная строка длительности (без часов, если 0)
     */
    public String getFormattedDuration()
    {
        return formatDuration(duration);
    }

    /**
     * Форматированная строка размера файла (KB, MB, GB)
     */
    public String getFormattedFileSize()
    {
        return formatFileSize(fileSize);
    }

    /**
     * Форматированная дата загрузки на сервер
     */
    public String getFormattedUploadDate()
    {
        if (uploadDate == null) return "Неизвестно";
        return uploadDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
    }

    /**
     * Полное имя файла (с расширением)
     */
    public String getFullFileName()
    {
        return title + extension;
    }

    /**
     * Отображаемое название в формате "Исполнитель — Название"
     */
    public String getDisplayName()
    {
        return artist + " — " + title;
    }

    // === Статические методы форматирования ===

    private static String formatDuration(String duration)
    {
        if (duration == null || duration.isEmpty()) return "00:00";
        try
        {
            // Поддерживаем форматы: "03:56", "00:03:56", "00:03:56.123"
            String timePart = duration.split("\\.")[0];
            String[] parts = timePart.split(":");

            if (parts.length == 2)
            {
                // формат "03:56"
                return String.format("%d:%02d",
                        Integer.parseInt(parts[0]),
                        Integer.parseInt(parts[1]));
            } else if (parts.length == 3)
            {
                int hours = Integer.parseInt(parts[0]);
                int minutes = Integer.parseInt(parts[1]);
                int seconds = Integer.parseInt(parts[2]);
                if (hours > 0)
                {
                    return String.format("%d:%02d:%02d", hours, minutes, seconds);
                } else
                {
                    return String.format("%d:%02d", minutes, seconds);
                }
            }
        } catch (Exception e)
        {
            // ignore
        }
        return duration;
    }

    private static String formatFileSize(long bytes)
    {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
        return String.format("%.1f GB", bytes / (1024.0 * 1024.0 * 1024.0));
    }

    // === Работа с дополнительными файлами ===

    public void addExtraFile(ExtraFile extraFile)
    {
        extraFiles.add(extraFile);
    }

    public void addAllExtraFiles(List<ExtraFile> files)
    {
        extraFiles.addAll(files);
    }

    public List<ExtraFile> getSheetMusic()
    {
        List<ExtraFile> result = new ArrayList<>();
        for (ExtraFile ef : extraFiles)
        {
            if (ef.getFileType() == ExtraFileType.SHEET_MUSIC)
            {
                result.add(ef);
            }
        }
        return result;
    }

    public List<ExtraFile> getTabs()
    {
        List<ExtraFile> result = new ArrayList<>();
        for (ExtraFile ef : extraFiles)
        {
            if (ef.getFileType() == ExtraFileType.TABS)
            {
                result.add(ef);
            }
        }
        return result;
    }

    public List<ExtraFile> getLyrics()
    {
        List<ExtraFile> result = new ArrayList<>();
        for (ExtraFile ef : extraFiles)
        {
            if (ef.getFileType() == ExtraFileType.LYRICS)
            {
                result.add(ef);
            }
        }
        return result;
    }

    public List<ExtraFile> getChords()
    {
        List<ExtraFile> result = new ArrayList<>();
        for (ExtraFile ef : extraFiles)
        {
            if (ef.getFileType() == ExtraFileType.CHORDS)
            {
                result.add(ef);
            }
        }
        return result;
    }

    public List<ExtraFile> getImages()
    {
        List<ExtraFile> result = new ArrayList<>();
        for (ExtraFile ef : extraFiles)
        {
            if (ef.getFileType() == ExtraFileType.IMAGE)
            {
                result.add(ef);
            }
        }
        return result;
    }

    public List<ExtraFile> getOtherFiles()
    {
        List<ExtraFile> result = new ArrayList<>();
        for (ExtraFile ef : extraFiles)
        {
            if (ef.getFileType() == ExtraFileType.OTHER)
            {
                result.add(ef);
            }
        }
        return result;
    }

    public File getLocalFile()
    {
        String path = getLocalFilePath();
        return (path != null && !path.isEmpty()) ? new File(path) : null;
    }
}