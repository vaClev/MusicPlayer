package org.example.vasilev.musicpro.dto;

import com.google.gson.annotations.SerializedName;
import org.example.vasilev.musicpro.models.ExtraFile;
import org.example.vasilev.musicpro.models.ExtraFileType;

import java.time.LocalDateTime;

public class ExtraFileDTO
{
    @SerializedName("id")
    private Long id;

    @SerializedName("originalFileName")
    private String originalFileName;

    @SerializedName("description")
    private String description;

    @SerializedName("fileType")
    private int fileTypeValue;  // int из JSON

    @SerializedName("fileTypeName")
    private String fileTypeName;

    @SerializedName("fileSize")
    private Long fileSize;

    @SerializedName("uploadDate")
    private String uploadDate; // конвертируем если потребуется в

    @SerializedName("downloadExtraUrl")
    private String downloadExtraUrl;

    @SerializedName("musicFileId")
    private Long musicFileId;

    /// Конструкторы
    public ExtraFileDTO()
    {
    }

    public ExtraFileDTO(Long id, String originalFileName, String description, int fileType,
                        String fileTypeName, Long fileSize, String uploadDate,
                        String downloadExtraUrl, Long musicFileId)
    {
        this.id = id;
        this.originalFileName = originalFileName;
        this.description = description;
        this.fileTypeValue = fileType;
        this.fileTypeName = fileTypeName;
        this.fileSize = fileSize;
        this.uploadDate = uploadDate;
        this.downloadExtraUrl = downloadExtraUrl;
        this.musicFileId = musicFileId;
    }

    ///  Геттеры и сеттеры для всех полей DTO
    /// /////////////////////////////////////
    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getOriginalFileName()
    {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName)
    {
        this.originalFileName = originalFileName;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public int getFileTypeValue()
    {
        return fileTypeValue;
    }

    public void setFileTypeValue(int fileType)
    {
        this.fileTypeValue = fileType;
    }

    public String getFileTypeName()
    {
        return fileTypeName;
    }

    public void setFileTypeName(String fileTypeName)
    {
        this.fileTypeName = fileTypeName;
    }

    public Long getFileSize()
    {
        return fileSize;
    }

    public void setFileSize(Long fileSize)
    {
        this.fileSize = fileSize;
    }

    public String getUploadDate()
    {
        return uploadDate;
    }

    public void setUploadDate(String uploadDate)
    {
        this.uploadDate = uploadDate;
    }

    public String getDownloadExtraUrl()
    {
        return downloadExtraUrl;
    }

    public void setDownloadExtraUrl(String downloadExtraUrl)
    {
        this.downloadExtraUrl = downloadExtraUrl;
    }

    public Long getMusicFileId()
    {
        return musicFileId;
    }

    public void setMusicFileId(Long musicFileId)
    {
        this.musicFileId = musicFileId;
    }
    ///  Геттеры и сеттеры для всех полей DTO
    /// /////////////////////////////////////
    ///
    // Метод для преобразования в доменную модель
    public ExtraFile toDomainModel()
    {
        LocalDateTime parsedDate = null;
        try
        {
            if (uploadDate != null && !uploadDate.isEmpty())
            {
                // Парсим строку в LocalDateTime
                parsedDate = LocalDateTime.parse(uploadDate.replace("Z", ""));
            }
        }
        catch (Exception e)
        {
            System.err.println("Ошибка парсинга даты: " + uploadDate);
        }

        ExtraFileType filetype = ExtraFileType.fromValue(fileTypeValue);
        return new ExtraFile(
                id, originalFileName, description, filetype, fileSize,
                parsedDate, downloadExtraUrl, musicFileId
        );
    }

    /// Для отладки и проверки данных.
    @Override
    public String toString() {
        return "ExtraFileDTO{" +
                "id=" + id +
                ", originalFileName='" + originalFileName + '\'' +
                ", description='" + description + '\'' +
                ", fileType=" + fileTypeValue +
                ", fileTypeName='" + fileTypeName + '\'' +
                ", fileSize=" + fileSize +
                ", uploadDate=" + uploadDate +
                ", downloadExtraUrl='" + downloadExtraUrl + '\'' +
                ", musicFileId=" + musicFileId +
                '}';
    }
}
