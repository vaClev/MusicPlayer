package org.example.vasilev.musicpro.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.example.vasilev.musicpro.models.MusicFile;
import org.example.vasilev.musicpro.services.*;
import org.example.vasilev.musicpro.services.download.IDownloadService;
import org.example.vasilev.musicpro.services.music.IMusicClientService;
import org.example.vasilev.musicpro.utils.FolderOpener;
import org.example.vasilev.musicpro.utils.Tests;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    public VBox root;
    /// Контейнер UI элементов списков песен
    @FXML
    private VBox songsContainer;

    /// Контейнер UI элементов плеера
    @FXML
    private VBox playerContainer;

    @FXML
    private Label statusLabel;

    private ConfigService configService = null;
    private IDownloadService downloadService = null;
    private IMusicClientService musicClientService = null;

    /// ссылка на контроллер плеера. Будем инжектить ее в карточки песен, чтобы добавлять их в плейлист
    private IPlaylistOwner playlistOwner;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        // инициализация UI элементов плеера
        loadPlayer();
    }

    public void setServices(ConfigService configService, IDownloadService downloadService,  IMusicClientService musicClientService)
    {
       this.configService = configService;
       this.downloadService = downloadService;
       this.musicClientService = musicClientService;

        // Показываем путь к папке загрузок
        statusLabel.setText("Папка загрузок: " + configService.getConfig().getDownloadDir());
    }

    /// Загрузка UI элементов кнопок плеера
    private void loadPlayer()
    {
        try
        {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/vasilev/musicpro/views/player.fxml")
            );
            VBox player = loader.load();
            playerContainer.getChildren().add(player);

            /// сохраняем ссылку на владельца плейлиста.
            this.playlistOwner = loader.getController();
        }
        catch (IOException e)
        {
            System.err.println("Не удалось загрузить player.fxml: " + e.getMessage());
        }
    }


    /// Тестовое получение данных с сервера OLD API GetALL
    private void testLoadAllFromServer()
    {
        songsContainer.getChildren().clear();

        musicClientService.getMusicFiles().thenApplyAsync(
                musicFiles->
                {
                    List<VBox> cards = new ArrayList<>();
                    for (MusicFile musicFile : musicFiles) {
                        try
                        {
                            VBox card = createSongCard(musicFile);
                            cards.add(card);
                        }
                        catch (IOException e)
                        {
                            // Логируем ошибку, но продолжаем создание других карточек
                            System.err.println("Ошибка создания карточки: " + e.getMessage());
                        }
                    }
                    return cards;
                })
                .thenAcceptAsync(cards -> {
                    // Обновление UI только после создания всех карточек
                    Platform.runLater(() -> {
                        songsContainer.getChildren().addAll(cards);
                        //showLoadingIndicator(false);
                        //updateStatus("Загружено " + cards.size() + " песен");
                    });
                }, Platform::runLater) // Исполнять в UI потоке

                .exceptionally(throwable -> {
                    Platform.runLater(() -> {
                        //showError("Ошибка загрузки", throwable.getMessage());
                        //showLoadingIndicator(false);
                    });
                    return null;
                });
    }

    /// Тестовое получение данных с сервера NEW API с пагинацией
    private void testLoadPageFromServer()
    {
        songsContainer.getChildren().clear();

        musicClientService.getMusicFiles(1,10).thenApplyAsync(
                        page->
                        {
                            List<VBox> cards = new ArrayList<>();
                            for (MusicFile musicFile : page.toCardsList()) {
                                try
                                {
                                    VBox card = createSongCard(musicFile);
                                    cards.add(card);
                                }
                                catch (IOException e)
                                {
                                    // Логируем ошибку, но продолжаем создание других карточек
                                    System.err.println("Ошибка создания карточки: " + e.getMessage());
                                }
                            }
                            return cards;
                        })
                .thenAcceptAsync(cards -> {
                    // Обновление UI только после создания всех карточек
                    Platform.runLater(() -> {
                        songsContainer.getChildren().addAll(cards);
                        //showLoadingIndicator(false);
                        //updateStatus("Загружено " + cards.size() + " песен");
                    });
                }, Platform::runLater) // Исполнять в UI потоке

                .exceptionally(throwable -> {
                    Platform.runLater(() -> {
                        showAlert("Ошибка загрузки", throwable.getMessage());
                        //showLoadingIndicator(false);
                    });
                    return null;
                });
    }


    private VBox createSongCard(MusicFile musicFile) throws IOException
    {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/org/example/vasilev/musicpro/views/music-small-card.fxml")
        );

        // Загружаем View
        VBox card = loader.load();

        // Получаем контроллер
        MusicSmallCardController controller = loader.getController();
        // Внедряем ему зависимости
        controller.setMusicFile(musicFile, downloadService);
        controller.setPlaylistOwner(playlistOwner);
        controller.setMusicClientService(musicClientService);

        return card;
    }


    /// Обработчики нажатия кнопок
    @FXML
    private void handleGetAll()
    {
        testLoadAllFromServer();
        statusLabel.setText("Список обновлен с сервера. Папка загрузок: " + configService.getConfig().getDownloadDir());
    }

    @FXML
    private void handleGetPage()
    {
        testLoadPageFromServer();
        statusLabel.setText("Список обновлен с сервера. Папка загрузок: " + configService.getConfig().getDownloadDir());
    }


    /// //////////////////////////////////////////////////////
    /// //////////////////////////////////////////////////////
    ///  отладка UI
    /// //////////////////////////////////////////////////////
    @FXML
    private void handleRefresh()
    {
        loadMockSongs();
        statusLabel.setText("Список обновлен из тест JSON. Папка загрузок: " + configService.getConfig().getDownloadDir());
    }
    /// Загрузка и отображение в UI содержимого из файла JSON
    private void loadMockSongs()
    {
        try
        {
            Tests util = new Tests();
            // 1. Чтение JSON файла из ресурсов
            List<MusicFile> musicFiles = util.loadMusicFilesFromJson();

            // 2. Очищаем контейнер
            songsContainer.getChildren().clear();

            // 3. Создаем карточки для каждого трека
            for (MusicFile musicFile : musicFiles)
            {
                // Создаем карточку
                VBox card = createSongCard(musicFile);
                /// Показываем где лежит псевдо-скаченный файл
                musicFile.setLocalFilePath("C:\\Users\\Олег\\MusicPlayer\\downloads\\kiss-detroit-rock-city.mp3");

                // Добавляем View (VBox) в контейнер
                songsContainer.getChildren().add(card);
            }

            // 4. Обновляем статус
            statusLabel.setText("Загружено " + musicFiles.size() + " треков из test.json");

        } catch (Exception e)
        {
            statusLabel.setText("Ошибка загрузки: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void openFolder(ActionEvent actionEvent)
    {
        boolean success = FolderOpener.openFolder(
                downloadService.getDefaultDownloadsFolder()
        );

        if (!success)
            // Показываем путь для ручного открытия
            showAlert("Не удалось открыть проводник",
                    "откройте папку"+ downloadService.getDefaultDownloadsFolder());
    }
    private void showAlert(String title, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }
}