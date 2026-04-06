package org.example.vasilev.musicpro.desktop.controllers;

import org.example.vasilev.musicpro.common.models.MusicFileCore;

public interface IPlaylistOwner
{
    void addMusicFileToPlaylist(MusicFileCore musicFile);
    void removeMusicFile(long id);

    void setAsCurrent(long id);
}
