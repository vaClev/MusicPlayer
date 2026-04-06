package org.example.vasilev.musicpro.common.models;

import java.time.LocalDateTime;

/**
 * Платформонезависимая модель дополнительного файла (ноты, табы, текст и т.д.).
 * Использует только стандартные Java-типы (без JavaFX).
 */
public class ExtraFile
{
    private final Long id;
    private String originalFileName;
    private String description;
    private ExtraFileType fileType;
    private Long fileSize;
    private LocalDateTime uploadDate;
    private String downloadExtraUrl;
    private Long musicFileId;

    /**
     * Конструктор для создания объекта дополнительного файла из DTO
     */
    public ExtraFile(Long id, String originalFileName, String description,
                     ExtraFileType fileType, Long fileSize,
                     LocalDateTime uploadDate, String downloadExtraUrl, Long musicFileId)
    {
        this.id = id;
        this.originalFileName = originalFileName;
        this.description = description;
        this.fileType = fileType;
        this.fileSize = fileSize != null ? fileSize : 0L;
        this.uploadDate = uploadDate;
        this.downloadExtraUrl = downloadExtraUrl;
        this.musicFileId = musicFileId;
    }

    // === Геттеры ===

    public Long getId()
    {
        return id;
    }

    public String getOriginalFileName()
    {
        return originalFileName;
    }

    public String getDescription()
    {
        return description;
    }

    public ExtraFileType getFileType()
    {
        return fileType;
    }

    public Long getFileSize()
    {
        return fileSize;
    }

    public LocalDateTime getUploadDate()
    {
        return uploadDate;
    }

    public String getDownloadExtraUrl()
    {
        return downloadExtraUrl;
    }

    public Long getMusicFileId()
    {
        return musicFileId;
    }

    // === Сеттеры ===

    public void setOriginalFileName(String originalFileName)
    {
        this.originalFileName = originalFileName;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public void setFileType(ExtraFileType fileType)
    {
        this.fileType = fileType;
    }

    public void setFileSize(Long fileSize)
    {
        this.fileSize = fileSize != null ? fileSize : 0L;
    }

    public void setUploadDate(LocalDateTime uploadDate)
    {
        this.uploadDate = uploadDate;
    }

    public void setDownloadExtraUrl(String downloadExtraUrl)
    {
        this.downloadExtraUrl = downloadExtraUrl;
    }

    public void setMusicFileId(Long musicFileId)
    {
        this.musicFileId = musicFileId;
    }

    // === Вспомогательные методы ===

    /**
     * Название типа файла для отображения
     */
    public String getFileTypeName()
    {
        return fileType != null ? fileType.getDisplayName() : "Неизвестно";
    }

    /**
     * Форматированный размер файла (B, KB, MB, GB)
     */
    public String getFormattedFileSize()
    {
        long size = fileSize;
        if (size == 0) return "0 B";

        if (size < 1024) return size + " B";

        int exp = (int) (Math.log(size) / Math.log(1024));
        char unit = "KMG".charAt(exp - 1);
        return String.format("%.1f %sB", size / Math.pow(1024, exp), unit);
    }

    /**
     * Отображаемое имя (описание или имя файла)
     */
    public String getDisplayName()
    {
        if (description != null && !description.trim().isEmpty())
        {
            return description;
        }
        return originalFileName != null ? originalFileName : "Без названия";
    }

    /**
     * Проверяет, доступен ли файл для скачивания
     */
    public boolean isDownloadable()
    {
        return downloadExtraUrl != null && !downloadExtraUrl.trim().isEmpty();
    }

    /**
     * Иконка для отображения в зависимости от типа файла
     */
    public String getIcon()
    {
        if (fileType == null) return "📎";
        return switch (fileType)
        {
            case SHEET_MUSIC -> "🎼";
            case TABS -> "🎸";
            case LYRICS -> "📝";
            case CHORDS -> "🎹";
            case IMAGE -> "🖼️";
            case OTHER -> "📎";
        };
    }
}