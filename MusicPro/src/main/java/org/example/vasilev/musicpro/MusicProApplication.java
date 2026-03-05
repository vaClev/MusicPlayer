package org.example.vasilev.musicpro;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.vasilev.musicpro.controllers.MainController;
import org.example.vasilev.musicpro.models.AppConfig;
import org.example.vasilev.musicpro.services.APIClient;
import org.example.vasilev.musicpro.services.ConfigService;
import org.example.vasilev.musicpro.services.download.DownloadService;
import org.example.vasilev.musicpro.services.download.IDownloadService;
import org.example.vasilev.musicpro.services.music.IMusicClientService;
import org.example.vasilev.musicpro.services.music.MusicClientService;

import java.net.URL;

public class MusicProApplication extends Application
{
    private MainController mainController = null;

    private ConfigService configService = null;
    private IDownloadService downloadService = null;
    private IMusicClientService musicClientService = null;

    @Override
    public void start(Stage stage)
    {
        try
        {
            // Загружаем главный FXML файл
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/vasilev/musicpro/views/main-view.fxml"));
            Parent root = loader.load();
            this.mainController = loader.getController(); // Вот она, сохраняется в поле класса

            // Создаем сцену
            Scene scene = new Scene(root, 1000, 700);

            // Устанавливаем заголовок
            stage.setTitle("MusicPro v.0.2");

            /// TODO Создать все сервисы  и Внедрить зависимости
            startServices();

            // Обработчик закрытия приложения
            stage.setOnCloseRequest(event -> {
                shutdownServices();
            });


            /// Загружаем CSS стили напрямую в сцену
            try
            {
                /// Основные стили
                URL cssUrl = getClass().getResource("/org/example/vasilev/musicpro/styles.css");
                if (cssUrl != null)
                    scene.getStylesheets().add(cssUrl.toExternalForm());

                /// Стили панели поиска
                cssUrl = getClass().getResource("/org/example/vasilev/musicpro/search-panel.css");
                if (cssUrl != null)
                    scene.getStylesheets().add(cssUrl.toExternalForm());

                /// Стили кнопок плеера
                cssUrl = getClass().getResource("/org/example/vasilev/musicpro/player.css");
                if (cssUrl != null)
                    scene.getStylesheets().add(cssUrl.toExternalForm());
            }
            catch (Exception e)
            {
                System.err.println("CSS не загружен: " + e.getMessage());
            }

            
            // Устанавливаем сцену и показываем окно
            stage.setScene(scene);
            stage.show();
        }
        catch (Exception e)
        {
            System.err.println("Ошибка загрузки FXML: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void startServices()
    {
        // Инициализация сервисов
        configService = new ConfigService(AppConfig.getInstance());

        var apiClient = new APIClient(configService.getConfig().getServerUrl(), configService.getConfig().getMaxConcurrentDownloads());
        // один экземпляр apiClient на оба сервиса.
        downloadService = new DownloadService(apiClient, configService.getConfig().getDownloadDir());
        musicClientService = new MusicClientService(apiClient);

        System.out.println("Сервисы запущены");

        // внедрение зависимостей
        mainController.setServices(configService, downloadService, musicClientService);
        System.out.println("Сервисы внедрены в mainController");
    }

    private void shutdownServices()
    {
        AutoCloseable downloadNotificator = (AutoCloseable) downloadService;
        try
        {
            downloadNotificator.close();
            // downloadService также закрывает и используемый им apiClient
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }

        System.out.println("Сервисы остановлены");
    }
}
