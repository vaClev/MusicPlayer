package org.example.vasilev.musicpro.desktop.models;

import javafx.beans.property.*;
import org.example.vasilev.musicpro.common.models.ExtraFileCore;
import org.example.vasilev.musicpro.common.models.ExtraFileType;

import java.time.LocalDateTime;

/**
 * JavaFX-обёртка для ExtraFileCore.
 * Используется в Desktop UI для поддержки binding.
 */
public class ExtraFileFX
{
    private final ExtraFileCore core;

    // JavaFX свойства для binding
    private final LongProperty id;
    private final StringProperty originalFileName;
    private final StringProperty description;
    private final ObjectProperty<ExtraFileType> fileType;
    private final LongProperty fileSize;
    private final ObjectProperty<LocalDateTime> uploadDate;
    private final StringProperty downloadExtraUrl;
    private final LongProperty musicFileId;

    /**
     * Конструктор из Core-модели
     */
    public ExtraFileFX(ExtraFileCore core)
    {
        this.core = core;
        this.id = new SimpleLongProperty(core.getId());
        this.originalFileName = new SimpleStringProperty(core.getOriginalFileName());
        this.description = new SimpleStringProperty(core.getDescription());
        this.fileType = new SimpleObjectProperty<>(core.getFileType());
        this.fileSize = new SimpleLongProperty(core.getFileSize());
        this.uploadDate = new SimpleObjectProperty<>(core.getUploadDate());
        this.downloadExtraUrl = new SimpleStringProperty(core.getDownloadExtraUrl());
        this.musicFileId = new SimpleLongProperty(core.getMusicFileId());
    }

    /**
     * Конструктор для создания нового объекта (без Core)
     */
    public ExtraFileFX(Long id, String originalFileName, String description,
                       ExtraFileType fileType, Long fileSize,
                       LocalDateTime uploadDate, String downloadExtraUrl, Long musicFileId)
    {
        this.core = new ExtraFileCore(id, originalFileName, description, fileType, fileSize, uploadDate, downloadExtraUrl, musicFileId);
        this.id = new SimpleLongProperty(id);
        this.originalFileName = new SimpleStringProperty(originalFileName);
        this.description = new SimpleStringProperty(description);
        this.fileType = new SimpleObjectProperty<>(fileType);
        this.fileSize = new SimpleLongProperty(fileSize != null ? fileSize : 0L);
        this.uploadDate = new SimpleObjectProperty<>(uploadDate);
        this.downloadExtraUrl = new SimpleStringProperty(downloadExtraUrl);
        this.musicFileId = new SimpleLongProperty(musicFileId != null ? musicFileId : 0L);
    }

    // === Property геттеры для JavaFX binding ===

    /*public LongProperty idProperty()
    {
        return id;
    }

    public StringProperty originalFileNameProperty()
    {
        return originalFileName;
    }

    public StringProperty descriptionProperty()
    {
        return description;
    }

    public ObjectProperty<ExtraFileType> fileTypeProperty()
    {
        return fileType;
    }

    public LongProperty fileSizeProperty()
    {
        return fileSize;
    }

    public ObjectProperty<LocalDateTime> uploadDateProperty()
    {
        return uploadDate;
    }

    public StringProperty downloadExtraUrlProperty()
    {
        return downloadExtraUrl;
    }

    public LongProperty musicFileIdProperty()
    {
        return musicFileId;
    }*/

    // === Геттеры ===

    public Long getId()
    {
        return id.get();
    }

    public String getOriginalFileName()
    {
        return originalFileName.get();
    }

    public String getDescription()
    {
        return description.get();
    }

    public ExtraFileType getFileType()
    {
        return fileType.get();
    }

    public Long getFileSize()
    {
        return fileSize.get();
    }

    public LocalDateTime getUploadDate()
    {
        return uploadDate.get();
    }

    public String getDownloadExtraUrl()
    {
        return downloadExtraUrl.get();
    }

    public Long getMusicFileId()
    {
        return musicFileId.get();
    }

    // === Сеттеры (обновляют и Property, и Core) ===

    public void setOriginalFileName(String originalFileName)
    {
        this.originalFileName.set(originalFileName);
        core.setOriginalFileName(originalFileName);
    }

    public void setDescription(String description)
    {
        this.description.set(description);
        core.setDescription(description);
    }

    public void setFileType(ExtraFileType fileType)
    {
        this.fileType.set(fileType);
        core.setFileType(fileType);
    }

    public void setFileSize(Long fileSize)
    {
        long size = fileSize != null ? fileSize : 0L;
        this.fileSize.set(size);
        core.setFileSize(size);
    }

    public void setUploadDate(LocalDateTime uploadDate)
    {
        this.uploadDate.set(uploadDate);
        core.setUploadDate(uploadDate);
    }

    public void setDownloadExtraUrl(String downloadExtraUrl)
    {
        this.downloadExtraUrl.set(downloadExtraUrl);
        core.setDownloadExtraUrl(downloadExtraUrl);
    }

    public void setMusicFileId(Long musicFileId)
    {
        long id = musicFileId != null ? musicFileId : 0L;
        this.musicFileId.set(id);
        core.setMusicFileId(id);
    }

    // === Вспомогательные методы (делегируют Core) ===

    public String getFileTypeName()
    {
        return core.getFileTypeName();
    }

    public String getFormattedFileSize()
    {
        return core.getFormattedFileSize();
    }

    public String getDisplayName()
    {
        return core.getDisplayName();
    }

    public boolean isDownloadable()
    {
        return core.isDownloadable();
    }

    public String getIcon()
    {
        return core.getIcon();
    }

    /**
     * Получить Core-модель (для передачи в сервисы)
     */
    public ExtraFileCore toCore()
    {
        // Обновляем Core перед возвратом
        core.setOriginalFileName(getOriginalFileName());
        core.setDescription(getDescription());
        core.setFileType(getFileType());
        core.setFileSize(getFileSize());
        core.setUploadDate(getUploadDate());
        core.setDownloadExtraUrl(getDownloadExtraUrl());
        core.setMusicFileId(getMusicFileId());
        return core;
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
        ExtraFileFX that = (ExtraFileFX) o;
        return core.equals(that.core);
    }

    @Override
    public int hashCode()
    {
        return core.hashCode();
    }
}