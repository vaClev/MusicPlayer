package org.example.vasilev.musicpro.controllers;

import org.example.vasilev.musicpro.models.MusicFile;

public interface IPlaylistOwner
{
    void addMusicFileToPlaylist(MusicFile musicFile);
    void removeMusicFile(long id);
}
