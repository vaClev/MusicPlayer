package org.example.vasilev.musicpro.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.vasilev.musicpro.services.player.BasicPlayerService;
import org.example.vasilev.musicpro.services.player.IPlayerService;

import java.io.File;

public class PlayerController
{
    @FXML
    private Button selectFileButton;
    @FXML
    private Button playPauseButton;
    @FXML
    private Label playerStatusLabel;


    private IPlayerService playerService;

    public PlayerController()
    {
        // Используем базовую реализацию
        this.playerService = new BasicPlayerService();

        // Добавляем слушателей для реактивного обновления UI ///TODO
        //setupServiceListeners();
    }

    @FXML
    public void initialize()
    {

    }

    public void handleSelectFile(ActionEvent actionEvent)
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выберите аудиофайл");

        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                "Аудио файлы", "*.mp3", "*.wav", "*.flac", "*.aac", "*.ogg", "*.m4a"
        );
        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Все файлы", "*.*"));

        Stage stage = (Stage) selectFileButton.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null)
        {
            loadAndPlayFile(selectedFile);
        }
    }

    private void loadAndPlayFile(File selectedFile)
    {
        new Thread(() -> {
            boolean success = playerService.loadFile(selectedFile);

            Platform.runLater(() -> {
                //showLoading(false);//можно отобразить загрузку но кажется это очень быстро, так что не требуется

                if (success)
                {
                    //updateTrackInfo(selectedFile);
                    playerService.play();
                    playerStatusLabel.setText("Воспроизведение: " + selectedFile.getName());
                }
                else
                {
                    playerStatusLabel.setText("Ошибка загрузки файла");
                }
            });
        }).start();
    }



    public void handlePlayPause(ActionEvent actionEvent)
    {
        if (!playerService.isFileLoaded())
        {
            playerStatusLabel.setText("Сначала выберите файл");
            return;
        }

        playerService.togglePlayPause();
    }


    public void handlePrev(ActionEvent actionEvent)
    {
    }

    public void handleNext(ActionEvent actionEvent)
    {
    }

    public void handlePlaylist(ActionEvent actionEvent)
    {
    }
}
