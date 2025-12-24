package org.example.vasilev.musicpro.models;

import javafx.beans.property.*;

import java.time.LocalDateTime;

public class ExtraFile
{
    private final LongProperty id;
    private final StringProperty originalFileName;
    private final StringProperty description;
    private final ObjectProperty<ExtraFileType> fileType;
    private final LongProperty fileSize;
    private final ObjectProperty<LocalDateTime> uploadDate;
    private final StringProperty downloadExtraUrl;
    private final LongProperty musicFileId;
    private MusicFile musicFile;


    /// Конструктор для создания объекта-карточки доп файла из DTO
    public ExtraFile(Long id, String originalFileName, String description,
                     ExtraFileType fileType, Long fileSize,
                     LocalDateTime uploadDate, String downloadExtraUrl, Long musicFileId)
    {
        this.id = new SimpleLongProperty(id);
        this.originalFileName = new SimpleStringProperty(originalFileName);
        this.description = new SimpleStringProperty(description);
        this.fileType = new SimpleObjectProperty<ExtraFileType>(fileType);
        this.fileSize = new SimpleLongProperty(fileSize);
        this.uploadDate = new SimpleObjectProperty<LocalDateTime>(uploadDate);
        this.downloadExtraUrl = new SimpleStringProperty(downloadExtraUrl);
        this.musicFileId = new SimpleLongProperty(musicFileId);
        musicFile = null;
    }

    // Геттеры/сеттеры для свойств JavaFX

    // id
    public long getId()
    {
        return id.get();
    }

    public LongProperty idProperty()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id.set(id != null ? id : 0L);
    }

    // originalFileName
    public String getOriginalFileName()
    {
        return originalFileName.get();
    }

    public StringProperty originalFileNameProperty()
    {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName)
    {
        this.originalFileName.set(originalFileName);
    }

    // description
    public String getDescription()
    {
        return description.get();
    }

    public StringProperty descriptionProperty()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description.set(description);
    }

    // fileType
    public ExtraFileType getFileType()
    {
        return fileType.get();
    }

    public ObjectProperty<ExtraFileType> fileTypeProperty()
    {
        return fileType;
    }

    public void setFileType(ExtraFileType fileType)
    {
        this.fileType.set(fileType);
    }

    // fileTypeName
    public String getFileTypeName()
    {
        return fileType.get().getDisplayName();
    }

    // fileSize
    public long getFileSize()
    {
        return fileSize.get();
    }

    public LongProperty fileSizeProperty()
    {
        return fileSize;
    }

    public void setFileSize(Long fileSize)
    {
        this.fileSize.set(fileSize != null ? fileSize : 0L);
    }

    // uploadDate
    public LocalDateTime getUploadDate()
    {
        return uploadDate.get();
    }

    public ObjectProperty<LocalDateTime> uploadDateProperty()
    {
        return uploadDate;
    }

    // downloadExtraUrl
    public String getDownloadExtraUrl()
    {
        return downloadExtraUrl.get();
    }

    public StringProperty downloadExtraUrlProperty()
    {
        return downloadExtraUrl;
    }

    public void setDownloadExtraUrl(String downloadExtraUrl)
    {
        this.downloadExtraUrl.set(downloadExtraUrl);
    }

    // musicFileId
    public long getMusicFileId()
    {
        return musicFileId.get();
    }

    public LongProperty musicFileIdProperty()
    {
        return musicFileId;
    }

    public void setMusicFileId(Long musicFileId)
    {
        this.musicFileId.set(musicFileId != null ? musicFileId : 0L);
    }


    /// Вспомогательные методы
    public String getFormattedFileSize()
    {
        long size = getFileSize();
        if (size == 0) return "0 B";

        if (size < 1024) return size + " B";

        int exp = (int) (Math.log(size) / Math.log(1024));
        char unit = "KMG".charAt(exp - 1);
        return String.format("%.1f %sB", size / Math.pow(1024, exp), unit);
    }

    public String getDisplayName()
    {
        String desc = getDescription();
        if (desc != null && !desc.trim().isEmpty()) {
            return desc;
        }
        String fileName = getOriginalFileName();
        return fileName != null ? fileName : "Без названия";
    }

    public boolean isDownloadable() {
        String url = getDownloadExtraUrl();
        return url != null && !url.trim().isEmpty();
    }

    public String getIcon()
    {
        ExtraFileType type = getFileType();
        return switch (type) {
            case SHEET_MUSIC -> "🎼";
            case TABS -> "🎸";
            case LYRICS -> "📝";
            case CHORDS -> "🎹";
            case IMAGE -> "🖼️";
            case OTHER -> "📎";
        };
    }

    @Override
    public String toString() {
        return getDisplayName() + " (" + getFileTypeName() + ")";
    }
}
