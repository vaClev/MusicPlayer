package org.example.vasilev.musicpro.models;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class MusicFile
{
    private final LongProperty id;
    private final StringProperty title;
    private final StringProperty artist;
    private final StringProperty album;
    private final StringProperty genre;
    private final IntegerProperty year;
    private final LongProperty fileSize; // в байтах
    private final StringProperty duration; // строка "00:03:56.9880000"
    private final ObjectProperty<LocalDateTime> uploadDate;
    private final StringProperty downloadUrl;
    private final BooleanProperty downloaded;

    // Список дополнительных файлов
    private final ObservableList<ExtraFile> extraFiles = FXCollections.observableArrayList();

    /// Конструктор для данных с сервера элемента "Списка песен"
    public MusicFile(Long id, String title, String artist, String album,
                     String genre, Integer year, Long fileSize,
                     String duration, LocalDateTime uploadDate,
                     String downloadUrl)
    {
        this.id = new SimpleLongProperty(id);
        this.title = new SimpleStringProperty(title);
        this.artist = new SimpleStringProperty(artist);
        this.album = new SimpleStringProperty(album != null ? album : "Unknown Album");
        this.genre = new SimpleStringProperty(genre != null ? genre : "Unknown");
        this.year = new SimpleIntegerProperty(year != null ? year : 0);
        this.fileSize = new SimpleLongProperty(fileSize != null ? fileSize : 0);
        this.duration = new SimpleStringProperty(duration != null ? duration : "00:00");
        this.uploadDate = new SimpleObjectProperty<>(uploadDate);
        this.downloadUrl = new SimpleStringProperty(downloadUrl);
        this.downloaded = new SimpleBooleanProperty(false);
    }

    /// Конструктор для данных с сервера "Конкретная песня"
    public MusicFile(Long id, String title, String artist, String album, String genre,
                     Integer year, Long fileSize, String duration, LocalDateTime uploadDate,
                     String downloadMusicUrl, List<ExtraFile> extraFiles)
    {
        this(id, title, artist, album, genre, year, fileSize, duration, uploadDate, downloadMusicUrl);
        if (extraFiles != null)
            this.extraFiles.setAll(extraFiles);
    }

    /// JavaFX свойства (для binding)
    public LongProperty idProperty() { return id; }
    public StringProperty titleProperty() { return title; }
    public StringProperty artistProperty() { return title; }
    public StringProperty albumProperty() { return album; }
    public StringProperty genreProperty() { return genre; }
    public IntegerProperty yearProperty() { return year; }
    public LongProperty fileSizeProperty() { return fileSize; }
    public StringProperty durationProperty() { return duration; }
    public ObjectProperty<LocalDateTime> uploadDateProperty() { return uploadDate; }
    public StringProperty downloadUrlProperty() { return downloadUrl; }
    public BooleanProperty downloadedProperty() { return downloaded; }


    /// Геттеры
    public Long getId() { return id.get(); }
    public String getTitle() { return title.get(); }
    public String getArtist() { return artist.get(); }
    public String getAlbum() { return album.get(); }
    public String getGenre() { return genre.get(); }
    public Integer getYear() { return year.get(); }
    public Long getFileSize() { return fileSize.get(); }
    public String getDuration() { return duration.get(); }
    public LocalDateTime getUploadDate() { return uploadDate.get(); }
    public String getDownloadUrl() { return downloadUrl.get(); }
    public boolean isDownloaded() { return downloaded.get(); }

    /// Сеттеры
    public void setDownloaded(boolean downloaded) { this.downloaded.set(downloaded); }

    /// ////////////////////////////////////////////
    /// Геттеры для UI отображения - начало
    /// Форматированная строка продолжительности песни
    public String getFormattedDuration()
    {
        return formatDuration(duration.get());
    }
    private static String formatDuration(String duration)
    {
        try
        {
            // Формат: "00:03:56.9880000" -> "3:56"
            String[] parts = duration.split("\\.");
            String timePart = parts[0]; // "00:03:56"
            String[] timeParts = timePart.split(":");

            int hours = Integer.parseInt(timeParts[0]);
            int minutes = Integer.parseInt(timeParts[1]);
            int seconds = Integer.parseInt(timeParts[2]);

            if (hours > 0) {
                return String.format("%d:%02d:%02d", hours, minutes, seconds);
            } else {
                return String.format("%d:%02d", minutes, seconds);
            }
        } catch (Exception e)
        {
            return duration; // Возвращаем как есть при ошибке
        }
    }

    /// Форматированная строка размера файла
    public String getFormattedFileSize()
    {
        return formatFileSize(fileSize.get());
    }
    private static String formatFileSize(long bytes)
    {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
        return String.format("%.1f GB", bytes / (1024.0 * 1024.0 * 1024.0));
    }

    /// Форматированная строка даты появления песни на сервере
    public String getFormattedUploadDate()
    {
        if (uploadDate.get() == null) return "Неизвестно";
        return uploadDate.get().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
    }
    /// Геттеры для UI отображения - конец
    /// ////////////////////////////////////////////

    @Override
    public String toString()
    {
        return String.format("%s - %s [%s]", artist.get(), title.get(), getFormattedDuration());
    }

    /// ////////////////////////////////////////////
    /// Работа со списком ExtraFiles
    public ObservableList<ExtraFile> getExtraFiles()
    {
        return extraFiles;
    }

    public List<ExtraFile> getSheetMusic()
    {
        return extraFiles.stream()
                .filter(ef -> ef.getFileType() == ExtraFileType.SHEET_MUSIC)
                .collect(Collectors.toList());
    }

    public List<ExtraFile> getTabs()
    {
        return extraFiles.stream()
                .filter(ef -> ef.getFileType() == ExtraFileType.TABS)
                .collect(Collectors.toList());
    }

    public List<ExtraFile> getLyrics()
    {
        return extraFiles.stream()
                .filter(ef -> ef.getFileType() == ExtraFileType.LYRICS)
                .collect(Collectors.toList());
    }

    public List<ExtraFile> getChords()
    {
        return extraFiles.stream()
                .filter(ef -> ef.getFileType() == ExtraFileType.CHORDS)
                .collect(Collectors.toList());
    }

    public List<ExtraFile> getImages()
    {
        return extraFiles.stream()
                .filter(ef -> ef.getFileType() == ExtraFileType.IMAGE)
                .collect(Collectors.toList());
    }

    public List<ExtraFile> getOtherFiles()
    {
        return extraFiles.stream()
                .filter(ef -> ef.getFileType() == ExtraFileType.OTHER)
                .collect(Collectors.toList());
    }
}
