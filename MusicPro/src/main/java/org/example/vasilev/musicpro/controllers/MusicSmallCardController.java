package org.example.vasilev.musicpro.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.vasilev.musicpro.controllers.details.TabManager;
import org.example.vasilev.musicpro.models.MusicFile;
import org.example.vasilev.musicpro.services.download.DownloadEvent;
import org.example.vasilev.musicpro.services.download.IDownloadService;
import org.example.vasilev.musicpro.services.music.IMusicClientService;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

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
    private Consumer<DownloadEvent> smallCardSubscriber = null;

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

        subscribeToDownloadEvent();
        updateUI();
    }

    private void subscribeToDownloadEvent()
    {
        if(downloadService== null)
            return;

        // Подписка на события сервиса скачивания (реализация IObservable)
        smallCardSubscriber = event -> {
            if(event.getFileId() != musicFile.getId())
                return;

            switch (event.getType()) {
                case PROGRESS:
                    Platform.runLater(()->{
                        progressBar.setProgress(event.getProgress());
                    });
                    break;
                case COMPLETED:
                    musicFile.setDownloaded(true);
                    Platform.runLater(this::updateDownloadStatusUI);
                    //showNotification("Файл загружен", event.getFileName());
                    break;
                case ERROR:
                    //showErrorAlert(event.getMessage());
                    break;
            }
        };

        downloadService.subscribe(smallCardSubscriber);
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
            progressBar.setVisible(false);
            downloadButton.setDisable(true);
            downloadButton.setText("Скачано");

            //отписка от событий скачивания
            downloadService.unsubscribe(smallCardSubscriber);
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

        // Формируем полный путь к создаваемому файлу
        Path filepathToSave = Paths.get(
                downloadService.getDefaultDownloadsFolder(),
                String.format("%s - %s%s",
                        musicFile.getArtist(),
                        musicFile.getTitle(),
                        musicFile.getExtension())
        );

        // Запускаем загрузку
        CompletableFuture<File> downloadFuture = downloadService.downloadMusicFile(musicFile.getId(), filepathToSave.toString());
        downloadFuture.thenAccept(file -> {
            Platform.runLater(() -> {
                musicFile.setDownloaded(true);
                musicFile.setLocalFilePath(file.getPath());
            });
        }).exceptionally(throwable -> {
            Platform.runLater(() -> {
                progressBar.setVisible(false);
                downloadButton.setDisable(false);
                statusLabel.setText("Ошибка");
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
                            tabManager.showOrCreateTab(musicFileFullInfo, downloadService, playlistOwner);

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

    public void cleanup()
    {
        downloadService.unsubscribe(smallCardSubscriber);
    }
}
