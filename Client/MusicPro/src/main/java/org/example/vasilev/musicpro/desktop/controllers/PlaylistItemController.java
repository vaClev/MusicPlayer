package org.example.vasilev.musicpro.desktop.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.example.vasilev.musicpro.common.models.MusicFileCore;

public class PlaylistItemController
{
    @FXML
    public Label currentIndicator;
    @FXML
    private Label titleLabel;
    @FXML
    private Label artistLabel;
    @FXML
    private Label durationLabel;

    private MusicFileCore musicFile;

    private Runnable onRemoveListener;

    /// TODO можно по индексу
    public void handleRemove(ActionEvent actionEvent)
    {
        if(onRemoveListener!=null)
            onRemoveListener.run();
    }

    public void setPlaylistItem(MusicFileCore item)
    {
        this.musicFile = item;
        titleLabel.setText(item.getTitle());
        artistLabel.setText(item.getArtist());
        durationLabel.setText(item.getFormattedDuration());
    }

    public void setAsCurrent(boolean show)
    {
        currentIndicator.setVisible(show);
    }

    /// Установка callback для самоудаления из плейлиста
    public void setOnRemoveListener(Runnable listener)
    {
        this.onRemoveListener = listener;
    }
}
