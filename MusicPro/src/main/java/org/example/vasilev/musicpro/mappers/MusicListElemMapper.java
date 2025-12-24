package org.example.vasilev.musicpro.mappers;

import org.example.vasilev.musicpro.dto.MusicFileDTO;
import org.example.vasilev.musicpro.models.MusicFile;

public class MusicListElemMapper {

    /*public static MusicFileDTO toDTO(MusicFile musicFile) {
        return new MusicFileDTO(
                musicFile.getId(),
                musicFile.getTitle(),
                musicFile.getArtist(),
                musicFile.getAlbum(),
                musicFile.getGenre(),
                musicFile.getYear(),
                musicFile.getFileSize(),
                musicFile.getDuration(),
                musicFile.getUploadDate(),
                musicFile.getDownloadUrl()
        );
    }*/

    public static MusicFile toDomain(MusicFileDTO dto)
    {
        return dto.toDomainModel();
    }

    public static MusicFile toDomain(MusicFileDTO dto, boolean downloaded)
    {
        MusicFile musicFile = dto.toDomainModel();
        musicFile.setDownloaded(downloaded);
        return musicFile;
    }
}