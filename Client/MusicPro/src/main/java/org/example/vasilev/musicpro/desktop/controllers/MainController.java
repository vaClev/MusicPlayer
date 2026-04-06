package org.example.vasilev.musicpro.desktop.controllers;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.vasilev.musicpro.common.dto.MusicPageDTO;
import org.example.vasilev.musicpro.common.models.MusicFileCore;
import org.example.vasilev.musicpro.common.services.*;
import org.example.vasilev.musicpro.common.services.download.IDownloadService;
import org.example.vasilev.musicpro.common.services.music.IMusicClientService;
import org.example.vasilev.musicpro.common.services.music.IPageOwner;
import org.example.vasilev.musicpro.common.services.music.PageChangeEvent;
import org.example.vasilev.musicpro.desktop.models.MusicFileFX;
import org.example.vasilev.musicpro.desktop.utils.FolderOpener;
import org.example.vasilev.musicpro.common.utils.Tests;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;

public class MainController implements Initializable
{
    public VBox root;

    @FXML
    /// Контейнер для элементов поиска
    private VBox searchContainer;

    @FXML
    /// Контейнер для кнопок пагинации
    private HBox paginationContainer;

    @FXML
    public SplitPane splitPane;

    @FXML
    /// Контейнер UI элементов списков песен
    private VBox songsContainer;

    @FXML
    /// Контейнер UI элементов плеера
    private VBox playerContainer;


    @FXML
    private Label statusLabel;

    private ConfigService configService = null;
    private IDownloadService downloadService = null;
    private IMusicClientService musicClientService = null;
    private IPageOwner pageOwner = null;
    private final Consumer<PageChangeEvent> cardCreatorSubscriber = this::onPageChange;

    /// Ссылка на контроллер плеера. Будем инжектить ее в карточки песен, чтобы добавлять их в плейлист
    private IPlaylistOwner playlistOwner;

    /// //////////////////////////////////////////////////////
    ///  Инициализация и внедрение зависимостей
    /// //////////////////////////////////////////////////////
    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        // Слушатель очистки songsContainer. Чтобы карточки отписались от событий сервисов.
        initSongsContainerClearListener();
    }

    // Добавляем слушатель изменений в songsContainer. Чтобы карточки при удалении из UI отписались от событий сервисов.
    private void initSongsContainerClearListener()
    {
        Consumer<Node> cleanupNode = node ->
        {
            if (node instanceof VBox)
            {
                Object controller = node.getProperties().get("controller");
                if (controller instanceof MusicSmallCardController)
                {
                    ((MusicSmallCardController) controller).cleanup();
                }
            }
        };

        songsContainer.getChildren().addListener((ListChangeListener<Node>) change ->
        {
            while (change.next())
            {
                if (change.wasRemoved())
                {
                    change.getRemoved().forEach(cleanupNode);
                }
                if (change.wasReplaced())
                {
                    change.getRemoved().forEach(cleanupNode);
                }
            }
        });
    }

    /// Внедрение зависимостей - Сервисы
    public void setServices(ConfigService configService, IDownloadService downloadService, IMusicClientService musicClientService,
                            IPageOwner pageOwner)
    {
        this.configService = configService;
        this.downloadService = downloadService;
        this.musicClientService = musicClientService;

        this.pageOwner = pageOwner;
        pageOwner.subscribe(cardCreatorSubscriber);  // подпишемся на события

        // Показываем путь к папке загрузок
        statusLabel.setText("Папка загрузок: " + configService.getConfig().getDownloadDir());
    }

    /// Внедрение UI элементов плеера
    public void setPlayerContainer(VBox player, PlayerController playerController)
    {
        playerContainer.getChildren().add(player);
        playerController.setSplitPane(splitPane);

        this.playlistOwner = playerController; /// сохраняем ссылку на владельца плейлиста.
    }

    /// Внедрение UI элементов панели поиск
    public void setSearchPanel(VBox searchPanel)
    {
        searchContainer.getChildren().add(searchPanel);
    }

    /// Внедрение UI элементов пагинации
    public void setPaginationPanel(HBox paginationPanel)
    {
        paginationContainer.getChildren().add(paginationPanel);
    }

    /// //////////////////////////////////////////////////////
    ///  Собственные Методы MainController
    /// //////////////////////////////////////////////////////
    @FXML
    private void handleGetPage()
    {
        int page = 1;
        pageOwner.loadPage(page);
    }

    /// //////////////////////////////////////////////////////
    ///  Получение карточек песен
    /// //////////////////////////////////////////////////////
    /// handler которым подписываемся на события IPageOwner'a
    private void onPageChange(PageChangeEvent event)
    {
        if (event.isError())
        {
            showAlert("Ошибка загрузки", event.getError().getMessage());
            return;
        }

        if (event.isSearch() && event.getSearchParams().isEmpty()) /// Поиск без параметров = сигнал об очистке результатов.
        {
            Platform.runLater(() ->
            {
                songsContainer.getChildren().clear();
            });
            return;
        }

        List<VBox> cards = createCardsFromPage(event.getData());
        displayCards(cards);
    }

    /// Создание списка карточек из страницы
    private List<VBox> createCardsFromPage(MusicPageDTO page)
    {
        List<VBox> cards = new ArrayList<>();

        for (MusicFileCore musicFileCore : page.toCardsList())
        {
            try
            {
                var musicFile = new MusicFileFX(musicFileCore); /// TODO конвертация
                VBox card = createSongCard(musicFile);
                cards.add(card);
            } catch (IOException e)
            {
                System.err.println("Ошибка создания карточки: " + e.getMessage());
            }
        }

        return cards;
    }

    ///  Создание одной карточки конкретного musicFile
    private VBox createSongCard(MusicFileFX musicFile) throws IOException
    {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/org/example/vasilev/musicpro/views/music-small-card.fxml")
        );

        // Загружаем View
        VBox card = loader.load();

        // Получаем контроллер
        MusicSmallCardController controller = loader.getController();

        // сохраняем контроллер в свойствах карточки -- чтобы cleanListener при очистке смог все понять и почистить
        card.getProperties().put("controller", controller);

        // Внедряем ему зависимости
        controller.setMusicFile(musicFile, downloadService);
        controller.setPlaylistOwner(playlistOwner);
        controller.setMusicClientService(musicClientService);

        return card;
    }

    /// Отображение карточек в UI
    private void displayCards(List<VBox> cards)
    {
        Platform.runLater(() ->
        {
            songsContainer.getChildren().clear();
            if (cards.isEmpty())
            {
                showAlert("Ошибка", "Пустая страница. Ничего не нашлось по вашим параметрам поиска");
                return;
            }

            songsContainer.getChildren().addAll(cards);
            statusLabel.setText("Список обновлен с сервера. Папка загрузок: " +
                    configService.getConfig().getDownloadDir());
        });
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
            List<MusicFileFX> musicFiles = util.loadMusicFilesFromJson();

            // 2. Очищаем контейнер
            songsContainer.getChildren().clear();

            // 3. Создаем карточки для каждого трека
            for (MusicFileFX musicFile : musicFiles)
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
    public void handleOpenFolder(ActionEvent actionEvent)
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