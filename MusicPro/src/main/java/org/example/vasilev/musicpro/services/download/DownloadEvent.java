package org.example.vasilev.musicpro.services.download;

import java.time.LocalDateTime;

public class DownloadEvent
{
    public enum EventType
    {
        STARTED,    // Начало загрузки
        PROGRESS,   // Прогресс загрузки
        COMPLETED,  // Загрузка завершена
        ERROR       // Ошибка загрузки
    }

    private final EventType type;
    private final long fileId;
    private final String fileType; // "music" или "extra"
    private final String fileName;
    private final String filePath;
    private final double progress; // 0-100
    private final String message;
    private final LocalDateTime timestamp;

    private DownloadEvent(EventType type, long fileId, String fileType,
                          String fileName, String filePath,
                          double progress, String message) {
        this.type = type;
        this.fileId = fileId;
        this.fileType = fileType;
        this.fileName = fileName;
        this.filePath = filePath;
        this.progress = progress;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    // Фабричные методы
    public static DownloadEvent started(long fileId, String fileType, String message) {
        return new DownloadEvent(EventType.STARTED, fileId, fileType,
                null, null, 0, message);
    }

    public static DownloadEvent progress(long fileId, String fileType,
                                         String fileName, Double progress) {
        return new DownloadEvent(EventType.PROGRESS, fileId, fileType,
                fileName, null, progress, null);
    }

    public static DownloadEvent completed(long fileId, String fileType,
                                          String fileName, String filePath) {
        return new DownloadEvent(EventType.COMPLETED, fileId, fileType,
                fileName, filePath, 100, "Загрузка завершена");
    }

    public static DownloadEvent error(long fileId, String fileType, String errorMessage) {
        return new DownloadEvent(EventType.ERROR, fileId, fileType,
                null, null, 0, errorMessage);
    }

    // Геттеры
    public EventType getType() { return type; }
    public long getFileId() { return fileId; }
    public String getFileType() { return fileType; }
    public String getFileName() { return fileName; }
    public String getFilePath() { return filePath; }
    public double getProgress() { return progress; }
    public String getMessage() { return message; }
    public LocalDateTime getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        return String.format("[%s] %s %d: %s (прогресс: %d%%)",
                timestamp, type, fileId,
                message != null ? message : fileName != null ? fileName : "",
                progress);
    }
}
