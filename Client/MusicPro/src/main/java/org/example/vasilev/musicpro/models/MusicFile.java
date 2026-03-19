package org.example.vasilev.musicpro.models;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Duration;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class MusicFile
{
    /// Информация получаемая с сервера
    private final LongProperty id;
    private final StringProperty title;
    private final StringProperty artist;
    private final StringProperty extension;
    private final StringProperty album;
    private final StringProperty genre;
    private final IntegerProperty year;
    private final LongProperty fileSize; // в байтах
    private final StringProperty duration; // строка "00:03:56.9880000"
    private final ObjectProperty<LocalDateTime> uploadDate;
    private final StringProperty downloadUrl;
    // Список дополнительных файлов
    private final ObservableList<ExtraFile> extraFiles = FXCollections.observableArrayList();

    /// Информация используемая только в клиентском приложении
    /// Скачан или еще нет?
    private final BooleanProperty downloaded;
    /// Путь к локальному файлу (если скачан)
    private final StringProperty localFilePath;



    /// Конструктор для данных с сервера элемента "Списка песен"
    public MusicFile(Long id, String title, String artist, String extension, String album,
                     String genre, Integer year, Long fileSize,
                     String duration, LocalDateTime uploadDate,
                     String downloadUrl)
    {
        this.id = new SimpleLongProperty(id);
        this.title = new SimpleStringProperty(title);
        this.artist = new SimpleStringProperty(artist);
        this.extension = new SimpleStringProperty(extension);
        this.album = new SimpleStringProperty(album != null ? album : "Unknown Album");
        this.genre = new SimpleStringProperty(genre != null ? genre : "Unknown");
        this.year = new SimpleIntegerProperty(year != null ? year : 0);
        this.fileSize = new SimpleLongProperty(fileSize != null ? fileSize : 0);
        this.duration = new SimpleStringProperty(duration != null ? duration : "00:00");
        this.uploadDate = new SimpleObjectProperty<>(uploadDate);
        this.downloadUrl = new SimpleStringProperty(downloadUrl);
        this.downloaded = new SimpleBooleanProperty(false);
        this.localFilePath = new SimpleStringProperty();
    }

    /// Конструктор для данных с сервера "Конкретная песня"
    public MusicFile(Long id, String title, String artist, String extension, String album, String genre,
                     Integer year, Long fileSize, String duration, LocalDateTime uploadDate,
                     String downloadMusicUrl, List<ExtraFile> extraFiles)
    {
        this(id, title, artist, extension, album, genre, year, fileSize, duration, uploadDate, downloadMusicUrl);
        if (extraFiles != null)
            this.extraFiles.setAll(extraFiles);
    }

    /// JavaFX свойства (для binding)
    public LongProperty idProperty() { return id; }
    public StringProperty titleProperty() { return title; }
    public StringProperty artistProperty() { return artist; }
    public StringProperty albumProperty() { return album; }
    public StringProperty genreProperty() { return genre; }
    public IntegerProperty yearProperty() { return year; }
    public LongProperty fileSizeProperty() { return fileSize; }
    public StringProperty durationProperty() { return duration; }
    public ObjectProperty<LocalDateTime> uploadDateProperty() { return uploadDate; }
    public StringProperty downloadUrlProperty() { return downloadUrl; }
    public BooleanProperty downloadedProperty() { return downloaded; }
    public StringProperty localFilePathProperty() { return localFilePath; }

    /// Геттеры
    public Long getId() { return id.get(); }
    public String getTitle() { return title.get(); }
    public String getArtist() { return artist.get(); }
    public String getExtension() { return extension.get(); }
    public String getAlbum() { return album.get(); }
    public String getGenre() { return genre.get(); }
    public Integer getYear() { return year.get(); }
    public Long getFileSize() { return fileSize.get(); }
    public String getDuration() { return duration.get(); }
    public LocalDateTime getUploadDate() { return uploadDate.get(); }
    public String getDownloadUrl() { return downloadUrl.get(); }
    public boolean isDownloaded() { return downloaded.get(); }
    public String getLocalFilePath() { return localFilePath.get(); }

    /// Сеттеры
    public void setDownloaded(boolean downloaded) { this.downloaded.set(downloaded); }
    public void setLocalFilePath(String localFilePath) { this.localFilePath.set(localFilePath); }
    public void setTitle(String newTitle) {this.title.set(newTitle);}
    public void setArtist(String newArtist) {this.artist.set(newArtist);}
    public void setDuration(Duration measuredDuration)
    {
        if (measuredDuration == null)
        {
            this.duration.set("00:00:00.0000000");
            return;
        }

        long totalMillis = (long) measuredDuration.toMillis();

        long hours = totalMillis / 3_600_000;
        long minutes = (totalMillis % 3_600_000) / 60_000;
        long seconds = (totalMillis % 60_000) / 1000;
        long millis = totalMillis % 1000;

        // Форматируем с точкой, гарантируя 7 знаков после запятой
        this.duration.set(String.format(Locale.US,
                "%02d:%02d:%02d.%04d%03d",
                hours, minutes, seconds, millis, 0));
        /// TODO подумать о смене формата ответа с сервера. Было бы удобно получать mm:ss сразу.
    }
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

    /// Вспомогательные методы для работы с файлом (после скачивания)
    /// Получить File объект для локального файла
    public File getLocalFile()
    {
        String path = getLocalFilePath();
        return (path != null && !path.isEmpty()) ? new File(path) : null;
    }
    /// Установить локальный файл
    public void setLocalFile(File file)
    {
        setLocalFilePath(file != null ? file.getAbsolutePath() : null);
    }


    @Override
    public String toString()
    {
        return String.format("%s - %s [%s] {%s}", artist.get(), title.get(), getFormattedDuration(), extraFiles.toString());
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
