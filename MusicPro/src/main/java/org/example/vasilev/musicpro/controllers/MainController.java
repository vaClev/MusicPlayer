package org.example.vasilev.musicpro.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import org.example.vasilev.musicpro.dto.MusicFileDTO;
import org.example.vasilev.musicpro.models.AppConfig;
import org.example.vasilev.musicpro.models.MusicFile;
import org.example.vasilev.musicpro.services.*;
import org.example.vasilev.musicpro.utils.LocalDateTimeAdapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class MainController implements Initializable {
    @FXML private FlowPane songsContainer;
    @FXML private Label statusLabel;

    //private MockDataService mockDataService;
    //private DownloadService downloadService;
    private ConfigService configService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Инициализация сервисов //TODO пока тут внедряются зависимости. Отрефакторить
        configService = new ConfigService(AppConfig.getInstance());
        //mockDataService = new MockDataService();
        //downloadService = new DownloadService(configService);

        // Загрузка тестовых данных
        loadMockSongs();

        // Показываем путь к папке загрузок
        statusLabel.setText("Папка загрузок: " + configService.getConfig().getDownloadDir());
    }

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

        return card;
    }

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



    @FXML
    private void handleRefresh() {
        loadMockSongs();
        statusLabel.setText("Список обновлен. Папка загрузок: " + configService.getConfig().getDownloadDir());
    }

    @FXML
    private void handleSettings() {
        // Откроем окно настроек (завтра)
        statusLabel.setText("Настройки пока недоступны. Будет реализовано завтра.");
    }
}