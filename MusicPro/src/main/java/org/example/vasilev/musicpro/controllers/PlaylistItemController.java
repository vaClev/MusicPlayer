package org.example.vasilev.musicpro.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.example.vasilev.musicpro.models.MusicFile;

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

    private MusicFile musicFile;

    private Runnable onRemoveListener;

    /// TODO можно по индексу
    public void handleRemove(ActionEvent actionEvent)
    {
        if(onRemoveListener!=null)
            onRemoveListener.run();
    }

    public void setPlaylistItem(MusicFile item)
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
