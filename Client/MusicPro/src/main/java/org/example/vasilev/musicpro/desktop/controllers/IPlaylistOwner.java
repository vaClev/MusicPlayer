package org.example.vasilev.musicpro.desktop.controllers;

import org.example.vasilev.musicpro.desktop.models.MusicFileFX;

public interface IPlaylistOwner
{
    void addMusicFileToPlaylist(MusicFileFX musicFile);
    void removeMusicFile(long id);

    void setAsCurrent(long id);
}
