package org.example.vasilev.musicpro.desktop.models;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.vasilev.musicpro.common.models.ExtraFileCore;
import org.example.vasilev.musicpro.common.models.MusicFileCore;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * JavaFX-обёртка для MusicFileCore.
 * Используется в Desktop UI для поддержки binding.
 */
public class MusicFileFX
{
    /// common модель муз файла
    private final MusicFileCore core;

    // === JavaFX свойства для binding ===
    private final LongProperty id;
    private final StringProperty title;
    private final StringProperty artist;
    private final StringProperty extension;
    private final StringProperty album;
    private final StringProperty genre;
    private final IntegerProperty year;
    private final LongProperty fileSize;
    private final StringProperty duration;
    private final ObjectProperty<LocalDateTime> uploadDate;
    private final StringProperty downloadUrl;
    private final BooleanProperty downloaded;
    private final StringProperty localFilePath;
    private final ObservableList<ExtraFileFX> extraFiles = FXCollections.observableArrayList();

    /**
     * Конструктор из Core-модели
     */
    public MusicFileFX(MusicFileCore core)
    {
        this.core = core;
        this.id = new SimpleLongProperty(core.getId());
        this.title = new SimpleStringProperty(core.getTitle());
        this.artist = new SimpleStringProperty(core.getArtist());
        this.extension = new SimpleStringProperty(core.getExtension());
        this.album = new SimpleStringProperty(core.getAlbum());
        this.genre = new SimpleStringProperty(core.getGenre());
        this.year = new SimpleIntegerProperty(core.getYear());
        this.fileSize = new SimpleLongProperty(core.getFileSize());
        this.duration = new SimpleStringProperty(core.getDuration());
        this.uploadDate = new SimpleObjectProperty<>(core.getUploadDate());
        this.downloadUrl = new SimpleStringProperty(core.getDownloadUrl());
        this.downloaded = new SimpleBooleanProperty(core.isDownloaded());
        this.localFilePath = new SimpleStringProperty(core.getLocalFilePath());

        // Конвертируем ExtraFileCore в ExtraFileFX
        if (core.getExtraFiles() != null)
        {
            for (ExtraFileCore extraCore : core.getExtraFiles())
            {
                extraFiles.add(new ExtraFileFX(extraCore));
            }
        }
    }

    /**
     * Конструктор для создания нового объекта (без Core)
     */
    public MusicFileFX(Long id, String title, String artist, String extension,
                       String album, String genre, Integer year, Long fileSize,
                       String duration, LocalDateTime uploadDate, String downloadUrl)
    {
        this.core = new MusicFileCore(id, title, artist, extension, album, genre,
                year, fileSize, duration, uploadDate, downloadUrl);
        this.id = new SimpleLongProperty(id);
        this.title = new SimpleStringProperty(title);
        this.artist = new SimpleStringProperty(artist);
        this.extension = new SimpleStringProperty(extension);
        this.album = new SimpleStringProperty(album);
        this.genre = new SimpleStringProperty(genre);
        this.year = new SimpleIntegerProperty(year);
        this.fileSize = new SimpleLongProperty(fileSize);
        this.duration = new SimpleStringProperty(duration);
        this.uploadDate = new SimpleObjectProperty<>(uploadDate);
        this.downloadUrl = new SimpleStringProperty(downloadUrl);
        this.downloaded = new SimpleBooleanProperty(false);
        this.localFilePath = new SimpleStringProperty();
    }

    public void updateFromCore(MusicFileCore newCore)
    {
        setTitle(newCore.getTitle());
        setArtist(newCore.getArtist());
        setAlbum(newCore.getAlbum());
        setGenre(newCore.getGenre());
        setYear(newCore.getYear());
        setFileSize(newCore.getFileSize());
        setDuration(newCore.getDuration());
        setUploadDate(newCore.getUploadDate());
        setDownloadUrl(newCore.getDownloadUrl());

        // Обновляем ExtraFiles
        extraFiles.clear();
        for (ExtraFileCore extraCore : newCore.getExtraFiles()) {
            extraFiles.add(new ExtraFileFX(extraCore));
        }
    }

    // === Property геттеры для JavaFX binding ===

    public LongProperty idProperty()
    {
        return id;
    }

    public StringProperty titleProperty()
    {
        return title;
    }

    public StringProperty artistProperty()
    {
        return artist;
    }

    public StringProperty extensionProperty()
    {
        return extension;
    }

    public StringProperty albumProperty()
    {
        return album;
    }

    public StringProperty genreProperty()
    {
        return genre;
    }

    public IntegerProperty yearProperty()
    {
        return year;
    }

    public LongProperty fileSizeProperty()
    {
        return fileSize;
    }

    public StringProperty durationProperty()
    {
        return duration;
    }

    public ObjectProperty<LocalDateTime> uploadDateProperty()
    {
        return uploadDate;
    }

    public StringProperty downloadUrlProperty()
    {
        return downloadUrl;
    }

    public BooleanProperty downloadedProperty()
    {
        return downloaded;
    }

    public StringProperty localFilePathProperty()
    {
        return localFilePath;
    }

    public ObservableList<ExtraFileFX> extraFilesProperty()
    {
        return extraFiles;
    }

    // === Геттеры ===

    public Long getId()
    {
        return id.get();
    }

    public String getTitle()
    {
        return title.get();
    }

    public String getArtist()
    {
        return artist.get();
    }

    public String getExtension()
    {
        return extension.get();
    }

    public String getAlbum()
    {
        return album.get();
    }

    public String getGenre()
    {
        return genre.get();
    }

    public Integer getYear()
    {
        return year.get();
    }

    public Long getFileSize()
    {
        return fileSize.get();
    }

    public String getDuration()
    {
        return duration.get();
    }

    public LocalDateTime getUploadDate()
    {
        return uploadDate.get();
    }

    public String getDownloadUrl()
    {
        return downloadUrl.get();
    }

    public boolean isDownloaded()
    {
        return downloaded.get();
    }

    public String getLocalFilePath()
    {
        return localFilePath.get();
    }

    public ObservableList<ExtraFileFX> getExtraFiles()
    {
        return extraFiles;
    }

    // === Сеттеры (обновляют и Property, и Core) ===

    public void setTitle(String title)
    {
        this.title.set(title);
        core.setTitle(title);
    }

    public void setArtist(String artist)
    {
        this.artist.set(artist);
        core.setArtist(artist);
    }

    public void setAlbum(String album)
    {
        this.album.set(album);
        core.setAlbum(album);
    }

    public void setGenre(String genre)
    {
        this.genre.set(genre);
        core.setGenre(genre);
    }

    public void setYear(Integer year)
    {
        this.year.set(year);
        core.setYear(year);
    }

    public void setFileSize(Long fileSize)
    {
        this.fileSize.set(fileSize);
        core.setFileSize(fileSize);
    }

    public void setDuration(String duration)
    {
        this.duration.set(duration);
        core.setDuration(duration);
    }

    public void setUploadDate(LocalDateTime uploadDate)
    {
        this.uploadDate.set(uploadDate);
        core.setUploadDate(uploadDate);
    }

    public void setDownloadUrl(String downloadUrl)
    {
        this.downloadUrl.set(downloadUrl);
        core.setDownloadUrl(downloadUrl);
    }

    public void setDownloaded(boolean downloaded)
    {
        this.downloaded.set(downloaded);
        core.setDownloaded(downloaded);
    }

    public void setLocalFilePath(String localFilePath)
    {
        this.localFilePath.set(localFilePath);
        core.setLocalFilePath(localFilePath);
    }

    // === Вспомогательные методы (делегируют Core) ===

    public String getFormattedDuration()
    {
        return core.getFormattedDuration();
    }

    public String getFormattedFileSize()
    {
        return core.getFormattedFileSize();
    }

    public String getFormattedUploadDate()
    {
        return core.getFormattedUploadDate();
    }

    public String getFullFileName()
    {
        return core.getFullFileName();
    }

    public String getDisplayName()
    {
        return core.getDisplayName();
    }

    /**
     * Получить Core-модель (для передачи в сервисы)
     */
    public MusicFileCore toCore()
    {
        // Обновляем Core перед возвратом
        core.setTitle(getTitle());
        core.setArtist(getArtist());
        core.setAlbum(getAlbum());
        core.setGenre(getGenre());
        core.setYear(getYear());
        core.setFileSize(getFileSize());
        core.setDuration(getDuration());
        core.setUploadDate(getUploadDate());
        core.setDownloadUrl(getDownloadUrl());
        core.setDownloaded(isDownloaded());
        core.setLocalFilePath(getLocalFilePath());

        // Обновляем ExtraFiles
        core.getExtraFiles().clear();
        for (ExtraFileFX efx : extraFiles)
        {
            core.addExtraFile(efx.toCore());
        }

        return core;
    }

    // === Работа с ExtraFiles ===

    public void addExtraFile(ExtraFileFX extraFile)
    {
        extraFiles.add(extraFile);
        core.addExtraFile(extraFile.toCore());
    }

    public void addAllExtraFiles(List<ExtraFileFX> files)
    {
        extraFiles.addAll(files);
        for (ExtraFileFX efx : files)
        {
            core.addExtraFile(efx.toCore());
        }
    }

    public void removeExtraFile(ExtraFileFX extraFile)
    {
        extraFiles.remove(extraFile);
        core.getExtraFiles().remove(extraFile.toCore());
    }

    public void clearExtraFiles()
    {
        extraFiles.clear();
        core.getExtraFiles().clear();
    }

    // === Работа с файловой системой ===

    /**
     * Получить File объект для локального файла
     */
    public File getLocalFile()
    {
        String path = getLocalFilePath();
        return (path != null && !path.isEmpty()) ? new File(path) : null;
    }

    /**
     * Установить локальный файл
     */
    public void setLocalFile(File file)
    {
        setLocalFilePath(file != null ? file.getAbsolutePath() : null);
    }

    // === Фильтрация ExtraFiles по типам ===

    public ObservableList<ExtraFileFX> getSheetMusic()
    {
        return FXCollections.observableArrayList(
                extraFiles.stream()
                        .filter(ef -> ef.getFileType() == org.example.vasilev.musicpro.common.models.ExtraFileType.SHEET_MUSIC)
                        .collect(Collectors.toList())
        );
    }

    public ObservableList<ExtraFileFX> getTabs()
    {
        return FXCollections.observableArrayList(
                extraFiles.stream()
                        .filter(ef -> ef.getFileType() == org.example.vasilev.musicpro.common.models.ExtraFileType.TABS)
                        .collect(Collectors.toList())
        );
    }

    public ObservableList<ExtraFileFX> getLyrics()
    {
        return FXCollections.observableArrayList(
                extraFiles.stream()
                        .filter(ef -> ef.getFileType() == org.example.vasilev.musicpro.common.models.ExtraFileType.LYRICS)
                        .collect(Collectors.toList())
        );
    }

    public ObservableList<ExtraFileFX> getChords()
    {
        return FXCollections.observableArrayList(
                extraFiles.stream()
                        .filter(ef -> ef.getFileType() == org.example.vasilev.musicpro.common.models.ExtraFileType.CHORDS)
                        .collect(Collectors.toList())
        );
    }

    public ObservableList<ExtraFileFX> getImages()
    {
        return FXCollections.observableArrayList(
                extraFiles.stream()
                        .filter(ef -> ef.getFileType() == org.example.vasilev.musicpro.common.models.ExtraFileType.IMAGE)
                        .collect(Collectors.toList())
        );
    }

    public ObservableList<ExtraFileFX> getOtherFiles()
    {
        return FXCollections.observableArrayList(
                extraFiles.stream()
                        .filter(ef -> ef.getFileType() == org.example.vasilev.musicpro.common.models.ExtraFileType.OTHER)
                        .collect(Collectors.toList())
        );
    }

    @Override
    public String toString()
    {
        return core.toString();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MusicFileFX that = (MusicFileFX) o;
        return core.equals(that.core);
    }

    @Override
    public int hashCode()
    {
        return core.hashCode();
    }
}