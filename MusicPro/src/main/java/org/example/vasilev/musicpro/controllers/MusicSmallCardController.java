package org.example.vasilev.musicpro.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.vasilev.musicpro.controllers.details.TabManager;
import org.example.vasilev.musicpro.models.MusicFile;
import org.example.vasilev.musicpro.services.download.IDownloadService;
import org.example.vasilev.musicpro.services.music.IMusicClientService;

import java.io.File;
import java.util.concurrent.CompletableFuture;

public class MusicSmallCardController
{
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

    private MusicFile musicFile = null;
    private IPlaylistOwner playlistOwner = null;
    private IMusicClientService musicClientService = null;
    private IDownloadService downloadService = null;

    public MusicSmallCardController()
    {
    }

    // Инициализация после загрузки FXML
    @FXML
    private void initialize()
    {
    }

    // Метод установки MusicFile и внедрение сервиса скачивания
    public void setMusicFile(MusicFile musicFile, IDownloadService downloadService)
    {
        this.musicFile = musicFile;
        this.downloadService = downloadService;

        updateUI();
    }

    /// Метод установки списка воспроизведения
    public void setPlaylistOwner(IPlaylistOwner owner)
    {
        this.playlistOwner = owner;
    }

    /// Метод установки сервиса обращения к серверу
    public void setMusicClientService(IMusicClientService musicClientService)
    {
        this.musicClientService = musicClientService;
    }


    /// обновление отображения
    private void updateUI()
    {
        if (musicFile == null) return;

        titleLabel.setText(musicFile.getTitle());
        artistLabel.setText(musicFile.getArtist());
        albumLabel.setText("Альбом: " + musicFile.getAlbum());

        // Форматированная длительность
        durationLabel.setText("Длительность: " + musicFile.getFormattedDuration());

        // Статус скачивания
        updateDownloadStatusUI();
    }
    private void updateDownloadStatusUI()
    {
        if (musicFile.isDownloaded())
        {
            statusLabel.setText("✓ Скачано");
            downloadButton.setDisable(true);
            downloadButton.setText("Скачано");
        }
        else
        {
            statusLabel.setText("Не скачано");
            downloadButton.setDisable(false);
            downloadButton.setText("Скачать");
        }
    }

    @FXML
    private void handleDownload()
    {
        if (musicFile == null || downloadService == null)
            return;

        // Проверяем, не загружен ли файл уже
        if (musicFile.isDownloaded())
            return;

        // Блокируем кнопку, чтобы избежать двойного нажатия
        downloadButton.setDisable(true);
        progressBar.setVisible(true);
        statusLabel.setText("Скачивание...");

        //Consumer<DownloadEvent> progressListener = event ->
        // Временно подписываем прогресс бар
        // downloadService.subscribe(progressListener);

        //String filepath = "норм имя с расширением"??
        // Запускаем загрузку
        CompletableFuture<File> downloadFuture = downloadService.downloadMusicFile(musicFile.getId());
        downloadFuture.thenAccept(file -> {
            Platform.runLater(() -> {
                progressBar.setVisible(false);
                downloadButton.setDisable(false);
                musicFile.setDownloaded(true);
                musicFile.setLocalFilePath(file.getPath());
                // Обновляем UI с информацией о локальном файле
                updateDownloadStatusUI();

                // Отписываемся от событий
                //downloadService.unsubscribe(progressListener);
            });

        }).exceptionally(throwable -> {
            Platform.runLater(() -> {
                progressBar.setVisible(false);
                downloadButton.setDisable(false);
                statusLabel.setText("Ошибка");

                // Отписываемся от событий
                //downloadService.unsubscribe(progressListener);
            });
            return null;
        });
    }

    @FXML
    private void handleDetails()
    {
        //TODO оптимизировать. Перед тем как обращаться на сервер, проверить может он уже есть в открытых вкладках.

        musicClientService.getMusicFileDetails(musicFile.getId())
                .thenAcceptAsync(musicFileFullInfo -> {
                    if (musicFileFullInfo != null)
                    {
                        Platform.runLater(() ->
                        {
                            System.out.println("Детали загружены успешно");

                            boolean downloadStatus = musicFile.isDownloaded();
                            musicFile = musicFileFullInfo;
                            musicFileFullInfo.setDownloaded(downloadStatus);

                            TabManager tabManager = TabManager.getInstance();
                            tabManager.showOrCreateTab(musicFileFullInfo);

                            /// TODO создать UI форму для просмотра доп файлов и всех полей musicFile
//                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
//                            alert.setTitle("Информация");
//                            alert.setHeaderText("Детальная информация");
//                            alert.setContentText(musicFile.toString());
//                            alert.showAndWait();
                        });
                    }
                    else
                    {
                        Platform.runLater(() -> {
                            System.err.println("Сервис вернул null");
                        });
                    }
                })
                .exceptionally(throwable -> {
                    System.err.println("Ошибка: " + throwable.getMessage());
                    return null;
                });



    }

    // Геттер для получения MusicFile. Будем использовать при переходе в "Подробнее..."
    public MusicFile getMusicFile()
    {
        return musicFile;
    }

    @FXML
    public void handleAddToPlaylist(ActionEvent actionEvent)
    {
        if (musicFile == null || !musicFile.isDownloaded() || playlistOwner == null)
            return;

        playlistOwner.addMusicFileToPlaylist(musicFile);
    }
}
