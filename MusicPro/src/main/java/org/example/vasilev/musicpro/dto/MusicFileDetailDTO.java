package org.example.vasilev.musicpro.dto;

import com.google.gson.annotations.SerializedName;
import org.example.vasilev.musicpro.models.ExtraFile;
import org.example.vasilev.musicpro.models.MusicFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class MusicFileDetailDTO
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

    @SerializedName("duration")
    private String duration;

    @SerializedName("uploadDate")
    private String uploadDate;

    @SerializedName("downloadMusicUrl")
    private String downloadMusicUrl;

    @SerializedName("extraFiles")
    private List<ExtraFileDTO> extraFiles;

    // Конструкторы
    public MusicFileDetailDTO() {
    }

    public MusicFileDetailDTO(Long id, String title, String artist, String album, String genre,
                              Integer year, String duration, String uploadDate,
                              String downloadMusicUrl, List<ExtraFileDTO> extraFiles) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.genre = genre;
        this.year = year;
        this.duration = duration;
        this.uploadDate = uploadDate;
        this.downloadMusicUrl = downloadMusicUrl;
        this.extraFiles = extraFiles;
    }

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(String uploadDate) {
        this.uploadDate = uploadDate;
    }

    public String getDownloadMusicUrl() {
        return downloadMusicUrl;
    }

    public void setDownloadMusicUrl(String downloadMusicUrl) {
        this.downloadMusicUrl = downloadMusicUrl;
    }

    public List<ExtraFileDTO> getExtraFiles() {
        return extraFiles;
    }

    public void setExtraFiles(List<ExtraFileDTO> extraFiles) {
        this.extraFiles = extraFiles;
    }

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

        List<ExtraFile> extractedExtraFiles = null;
        if (extraFiles != null && !extraFiles.isEmpty())
        {
            extractedExtraFiles = getExtraFiles().stream().map(ExtraFileDTO::toDomainModel).toList();
        }

        return new MusicFile(
                id, title, artist, album, genre, year,
                100L/*заглушка решить на сервере ошибку*/, duration, parsedDate, downloadMusicUrl,extractedExtraFiles);
    }

    @Override
    public String toString() {
        return "MusicFileDetailDTO{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                ", album='" + album + '\'' +
                ", genre='" + genre + '\'' +
                ", year=" + year +
                ", duration='" + duration + '\'' +
                ", uploadDate=" + uploadDate +
                ", downloadMusicUrl='" + downloadMusicUrl + '\'' +
                ", extraFiles=" + extraFiles +
                '}';
    }
}
