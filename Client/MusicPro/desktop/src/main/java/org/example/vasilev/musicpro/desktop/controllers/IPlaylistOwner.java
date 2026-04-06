package org.example.vasilev.musicpro.desktop.controllers;

import org.example.vasilev.musicpro.common.models.MusicFile;

public interface IPlaylistOwner
{
    void addMusicFileToPlaylist(MusicFile musicFile);
    void removeMusicFile(long id);

    void setAsCurrent(long id);
}
