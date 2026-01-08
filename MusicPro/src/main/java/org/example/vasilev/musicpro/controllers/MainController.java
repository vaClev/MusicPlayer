package org.example.vasilev.musicpro.controllers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.example.vasilev.musicpro.dto.MusicFileDTO;
import org.example.vasilev.musicpro.models.AppConfig;
import org.example.vasilev.musicpro.models.MusicFile;
import org.example.vasilev.musicpro.services.*;
import org.example.vasilev.musicpro.services.APIClient;
import org.example.vasilev.musicpro.services.music.IMusicClientService;
import org.example.vasilev.musicpro.services.music.MusicClientService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class MainController implements Initializable {
    /// Контейнер UI элементов списков песен
    @FXML
    private VBox songsContainer;

    /// Контейнер UI элементов плеера
    @FXML
    private VBox playerContainer;

    @FXML
    private Label statusLabel;

    //private MockDataService mockDataService;
    //private DownloadService downloadService;
    private ConfigService configService;

    private IMusicClientService musicClientService;

    /// ссылка на контроллер плеера. Будем инжектить ее в карточки песен, чтобы добавлять их в плейлист
    private IPlaylistOwner playlistOwner;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        // Инициализация сервисов //TODO пока тут внедряются зависимости. Отрефакторить
        configService = new ConfigService(AppConfig.getInstance());
        musicClientService = new MusicClientService(new APIClient(configService.getConfig().getServerUrl()));

        // инициализация UI элементов плеера
        loadPlayer();

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


    /// Тестовое получение данных с сервера при запуске
    private void testLoadAllFromServer()
    {
        songsContainer.getChildren().clear();

        musicClientService.getMusicFiles(1,100).thenApplyAsync(
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


    /// Загрузка и отображение в UI содержимого из файла JSON
    private void loadMockSongs()
    {
        try
        {
            // 1. Чтение JSON файла из ресурсов
            List<MusicFile> musicFiles = loadMusicFilesFromJson();

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

    private VBox createSongCard(MusicFile musicFile) throws IOException
    {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/org/example/vasilev/musicpro/views/music-small-card.fxml")
        );

        // Загружаем View
        VBox card = loader.load();

        // Получаем контроллер
        MusicSmallCardController controller = loader.getController();
        controller.setMusicFile(musicFile);
        controller.setPlaylistOwner(playlistOwner);
        controller.setMusicClientService(musicClientService);

        return card;
    }

    /// TODO вынести в тесты
    private List<MusicFile> loadMusicFilesFromJson()
    {
        // Путь к файлу в ресурсах
        String jsonPath = "/org/example/vasilev/musicpro/test.json";
        // Создаем Gson с адаптером для LocalDateTime
        Gson gson = new Gson();

        try (InputStream inputStream = getClass().getResourceAsStream(jsonPath);
             InputStreamReader reader = new InputStreamReader(inputStream))
        {
            // Проверка, что файл найден
            if (inputStream == null) {
                throw new RuntimeException("Файл не найден: " + jsonPath);
            }

            // Читаем весь JSON в строку для отладки
            String json = new BufferedReader(reader).lines().collect(Collectors.joining("\n"));
            System.out.println("JSON содержимое:\n" + json);

            // Десериализация
            List<MusicFileDTO> dtos = gson.fromJson(json,
                    new TypeToken<List<MusicFileDTO>>(){}.getType());

            System.out.println("Успешно прочитано DTO: " + dtos.size());

            return dtos.stream()
                    .map(MusicFileDTO::toDomainModel)
                    .collect(Collectors.toList());
        }
        catch (Exception e)
        {
            throw new RuntimeException("Ошибка при чтении JSON: " + e.getMessage(), e);
        }
    }



    /// Обработчики нажатия кнопок
    @FXML
    private void handleRefresh()
    {
        loadMockSongs();
        statusLabel.setText("Список обновлен из тест JSON. Папка загрузок: " + configService.getConfig().getDownloadDir());
    }

    @FXML
    private void handleGetAll()
    {
        testLoadAllFromServer();
        statusLabel.setText("Список обновлен с сервера. Папка загрузок: " + configService.getConfig().getDownloadDir());
    }

    @FXML
    private void handleSettings()
    {
        // Откроем окно настроек (завтра)
        statusLabel.setText("Настройки пока недоступны. Будет реализовано завтра.");
    }
}