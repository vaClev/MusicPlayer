package org.example.vasilev.musicpro.dto;

import com.google.gson.annotations.SerializedName;
import org.example.vasilev.musicpro.models.MusicFile;

import java.time.LocalDateTime;

public class MusicFileDTO
{
    @SerializedName("id")
    private Long id;

    @SerializedName("title")
    private String title;

    @SerializedName("artist")
    private String artist;

    @SerializedName("album")
    private String album;

    @SerializedName("genre")
    private String genre;

    @SerializedName("year")
    private Integer year;

    @SerializedName("fileSize")
    private Long fileSize;

    @SerializedName("duration")
    private String duration;

    @SerializedName("uploadDate")
    private String uploadDate;

    @SerializedName("downloadUrl")
    private String downloadUrl;

    /// Конструктор
    public MusicFileDTO() {}

    /// Конструктор как в получаемом DTO элемента в списке
    public MusicFileDTO(Long id, String title, String artist, String album,
                        String genre, Integer year, Long fileSize,
                        String duration, String uploadDate, String downloadUrl)
    {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.genre = genre;
        this.year = year;
        this.fileSize = fileSize;
        this.duration = duration;
        this.uploadDate = uploadDate;
        this.downloadUrl = downloadUrl;
    }

    ///  Геттеры и сеттеры для всех полей DTO
    /// /////////////////////////////////////

    // id
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    // title
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    // artist
    public String getArtist() { return artist; }
    public void setArtist(String artist) { this.artist = artist; }

    // album
    public String getAlbum() { return album; }
    public void setAlbum(String album) { this.album = album; }

    // genre
    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    // year
    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    // fileSize
    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }

    // duration
    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }

    // uploadDate
    public String getUploadDate() { return uploadDate; }
    public void setUploadDate(String uploadDate) { this.uploadDate = uploadDate; }

    // downloadUrl
    public String getDownloadUrl() { return downloadUrl; }
    public void setDownloadUrl(String downloadUrl) { this.downloadUrl = downloadUrl;}
    ///  Геттеры и сеттеры для всех полей DTO
    /// /////////////////////////////////////


    // Метод для преобразования в доменную модель
    public MusicFile toDomainModel()
    {
        LocalDateTime parsedDate = null;
        try {
            if (uploadDate != null && !uploadDate.isEmpty()) {
                // Парсим строку в LocalDateTime
                parsedDate = LocalDateTime.parse(uploadDate.replace("Z", ""));
            }
        } catch (Exception e) {
            System.err.println("Ошибка парсинга даты: " + uploadDate);
        }
        return new MusicFile(
                id, title, artist, album, genre, year,
                fileSize, duration, parsedDate, downloadUrl
        );
    }
}
