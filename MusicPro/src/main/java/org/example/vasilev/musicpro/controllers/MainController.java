package org.example.vasilev.musicpro.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.VBox;
import org.example.vasilev.musicpro.dto.MusicPageDTO;
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
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

public class MainController implements Initializable, SearchController.SearchCallback
{
    public VBox root;

    @FXML
    private VBox searchContainer;
    /// Контейнер для элементов поиска

    @FXML
    public SplitPane splitPane;
    @FXML
    private VBox songsContainer;
    /// Контейнер UI элементов списков песен
    @FXML
    private VBox playerContainer;
    /// Контейнер UI элементов плеера

    @FXML
    private Label statusLabel;

    private ConfigService configService = null;
    private IDownloadService downloadService = null;
    private IMusicClientService musicClientService = null;

    /// ссылка на контроллер плеера. Будем инжектить ее в карточки песен, чтобы добавлять их в плейлист
    private IPlaylistOwner playlistOwner;

    /// контроллер поиска
    private SearchController searchController; //TODO интерфейс



    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        // инициализация UI элементов плеера
        loadPlayer();

        // инициализация панели поиска
        loadSearchPanel();
    }

    public void setServices(ConfigService configService, IDownloadService downloadService, IMusicClientService musicClientService)
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

            PlayerController playerController = loader.getController();
            playerController.setSplitPane(splitPane);
            /// сохраняем ссылку на владельца плейлиста.
            this.playlistOwner = playerController;
        } catch (IOException e)
        {
            System.err.println("Не удалось загрузить player.fxml: " + e.getMessage());
        }
    }

    /// Загрузка UI элементов панели поиск
    private void loadSearchPanel()
    {
        try
        {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/vasilev/musicpro/views/search-panel.fxml")
            );
            VBox searchPanel = loader.load();
            searchContainer.getChildren().add(searchPanel);

            // Получаем контроллер и устанавливаем колбэк
            searchController = loader.getController();
            searchController.setCallback(this);
        }
        catch (IOException e)
        {
            System.err.println("Не удалось загрузить search-panel.fxml: " + e.getMessage());
        }
    }

    /// //////////////////////////////////////////////////////
    ///  Методы
    /// //////////////////////////////////////////////////////

    ///  Реализация SearchCallback
    @Override
    public void onSearch(Map<String, String> searchParams, int page, int pageSize)
    {
        loadAndDisplayCards(musicClientService.searchMusicFiles(searchParams, page, pageSize));
    }

    @FXML
    private void handleGetPage()
    {
        loadPagesFromServer();
    }

    /// Получение всех песен с сервера NEW API с пагинацией
    private void loadPagesFromServer()
    {
        loadAndDisplayCards(musicClientService.getMusicFiles(1, 10));
    }

    /// //////////////////////////////////////////////////////
    ///  Получение карточек песен
    /// //////////////////////////////////////////////////////
    /**
     * Загрузка и отображения карточек песен
     *
     * @param futurePage CompletableFuture с данными страницы
     */
    private void loadAndDisplayCards(CompletableFuture<MusicPageDTO> futurePage)
    {
        futurePage
                .thenApplyAsync(this::createCardsFromPage)
                .thenAcceptAsync(this::displayCards, Platform::runLater)
                .exceptionally(throwable ->
                {
                    Platform.runLater(() ->
                    {
                        showAlert("Ошибка загрузки", throwable.getMessage());
                    });
                    return null;
                });
    }

    /**
     * Создание списка карточек из страницы
     *
     * @param page страница с данными
     * @return список VBox карточек
     */
    private List<VBox> createCardsFromPage(MusicPageDTO page)
    {
        List<VBox> cards = new ArrayList<>();

        // Обновляем информацию о пагинации в SearchController
        if (searchController != null)
            searchController.updatePagination(page.getPageNumber(), page.getTotalPages());

        for (MusicFile musicFile : page.toCardsList())
        {
            try
            {
                VBox card = createSongCard(musicFile);
                cards.add(card);
            } catch (IOException e)
            {
                System.err.println("Ошибка создания карточки: " + e.getMessage());
            }
        }

        return cards;
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

    /**
     * Отображение карточек в UI
     *
     * @param cards список карточек для отображения
     */
    private void displayCards(List<VBox> cards)
    {
        songsContainer.getChildren().clear();
        if(cards.isEmpty())
            throw new RuntimeException("Пустая страница. Ничего не нашлось по вашим параметрам поиска");

        songsContainer.getChildren().addAll(cards);
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


    /// //////////////////////////////////////////////////////
    /// Открыть папку "Загрузки"
    /// //////////////////////////////////////////////////////
    @FXML
    public void openFolder(ActionEvent actionEvent)
    {
        boolean success = FolderOpener.openFolder(
                downloadService.getDefaultDownloadsFolder()
        );

        if (!success)
            // Показываем путь для ручного открытия
            showAlert("Не удалось открыть проводник",
                    "откройте папку" + downloadService.getDefaultDownloadsFolder());
    }

    /// Показ окна с ошибкой
    private void showAlert(String title, String content)
    {
        Platform.runLater(() ->
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }

    /// Показать/скрыть панель поиска
    @FXML
    public void handleShowHide(ActionEvent actionEvent)
    {
        if (searchContainer == null) return;

        boolean isVisible = searchContainer.isVisible();

        // Переключаем видимость
        searchContainer.setVisible(!isVisible);
        searchContainer.setManaged(!isVisible); // managed влияет на то, занимает ли элемент место в layout

        // Меняем текст кнопки (если нужно)
        if (actionEvent.getSource() instanceof Button sourceButton)
        {
            if (!isVisible)
            {
                sourceButton.setText("Скрыть поиск");
            } else
            {
                sourceButton.setText("Показать поиск");
            }
        }

        // Обновляем статус
        if (statusLabel != null)
        {
            statusLabel.setText("Панель поиска " + (searchContainer.isVisible() ? "показана" : "скрыта") +
                    " | Папка загрузок: " + configService.getConfig().getDownloadDir());
        }
    }
}