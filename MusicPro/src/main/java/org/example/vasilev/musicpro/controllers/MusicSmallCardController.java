package org.example.vasilev.musicpro.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.example.vasilev.musicpro.models.MusicFile;
import org.example.vasilev.musicpro.services.MusicFileDetailsService;

import java.io.IOException;
import java.net.URL;

public class MusicSmallCardController
{
    @FXML
    private VBox root;           // Корневой элемент из FXML
    @FXML
    private Label titleLabel;
    @FXML
    private Label artistLabel;
    @FXML
    private Label albumLabel;
    @FXML
    private Label durationLabel;
    @FXML
    private Label statusLabel;
    @FXML
    private Button downloadButton;
    @FXML
    private ProgressBar progressBar;

    private MusicFile musicFile;

    public MusicSmallCardController()  {}

    // Метод для получения View (VBox)
    public VBox getView() {
        return root;
    }

    // Инициализация после загрузки FXML
    @FXML
    private void initialize()
    {
        // Автоматически вызывается после загрузки FXML
        // Загружаем CSS программно
        try {
            URL cssUrl = getClass().getResource("/org/example/vasilev/musicpro/styles.css");
            if (cssUrl != null) {
                root.getStylesheets().add(cssUrl.toExternalForm());
            }
        } catch (Exception e) {
            // Игнорируем ошибку CSS
            System.err.println("CSS не загружен, продолжаем без стилей");
        }
    }


    // Метод установки MusicFile
    public void setMusicFile(MusicFile musicFile /*DownloadService downloadService*/)
    {
        this.musicFile = musicFile;
        //this.downloadService = downloadService;
        // TODO добавить возможность обращаться к сервису для загрузки указанного файла
        updateUI();
    }

    /// обновление отображения
    private void updateUI()
    {
        if (musicFile == null) return;

        titleLabel.setText(musicFile.getTitle());
        artistLabel.setText("Исполнитель: " + musicFile.getArtist());
        albumLabel.setText("Альбом: " + musicFile.getAlbum());

        // Форматированная длительность
        durationLabel.setText("Длительность: " + musicFile.getFormattedDuration());

        // Статус скачивания
        if (musicFile.isDownloaded())
        {
            statusLabel.setText("✓ Скачано");
            downloadButton.setDisable(true);
            downloadButton.setText("Скачано");
        } else
        {
            statusLabel.setText("Не скачано");
            downloadButton.setDisable(false);
            downloadButton.setText("Скачать");
        }
    }

    @FXML
    private void handleDownload()
    {
        if (musicFile == null /*|| downloadService == null*/) return;

        downloadButton.setDisable(true);
        progressBar.setVisible(true);
        statusLabel.setText("Скачивание...");

        /// Имитация скачивания в отдельном не UI потоке/// TODO реализовать
        new Thread(() -> {
            try {
                // Здесь будет реальное скачивание через downloadService
                // downloadService.download(musicFile);

                Thread.sleep(2000); // Имитация задержки

                // Обновляем UI в UI-потоке
                javafx.application.Platform.runLater(() -> {
                    musicFile.setDownloaded(true);
                    updateUI();
                    progressBar.setVisible(false);

                    // Показать уведомление
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Скачивание завершено");
                    alert.setHeaderText(null);
                    alert.setContentText("Песня \"" + musicFile.getTitle() + "\" успешно скачана!");
                    alert.showAndWait();
                });
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }).start();
    }

    @FXML
    private void handleDetails()
    {
        MusicFileDetailsService extraInfoService = new MusicFileDetailsService();
        extraInfoService.getMusicFileDetails(musicFile.getId());
    }

    // Геттер для получения MusicFile. Будем использовать при переходе в "Подробнее..."
    public MusicFile getMusicFile()
    {
        return musicFile;
    }
}
