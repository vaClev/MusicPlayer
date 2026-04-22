package org.example.vasilev.musicpro.desktop.launcher;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.vasilev.musicpro.desktop.controllers.MainController;
import org.example.vasilev.musicpro.desktop.controllers.PaginationController;
import org.example.vasilev.musicpro.desktop.controllers.PlayerController;
import org.example.vasilev.musicpro.desktop.controllers.SearchController;
import org.example.vasilev.musicpro.desktop.services.AppConfig;
import org.example.vasilev.musicpro.common.services.APIClient;
import org.example.vasilev.musicpro.common.services.ConfigService;
import org.example.vasilev.musicpro.common.services.download.DownloadService;
import org.example.vasilev.musicpro.common.services.download.IDownloadService;
import org.example.vasilev.musicpro.common.services.music.IMusicClientService;
import org.example.vasilev.musicpro.common.services.music.IPageOwner;
import org.example.vasilev.musicpro.common.services.music.MusicClientService;
import org.example.vasilev.musicpro.common.services.music.PageOwner;
import org.example.vasilev.musicpro.desktop.services.player.BasicPlayerService;
import org.example.vasilev.musicpro.common.services.player.IPlayerService;
import org.example.vasilev.musicpro.common.services.player.MusicMetadataExtractService;

import java.io.IOException;
import java.net.URL;

public class MusicProApplication extends Application
{
    private MainController mainController = null;

    private ConfigService configService = null;
    private IDownloadService downloadService = null;
    private IMusicClientService musicClientService = null;
    private IPageOwner pageOwner = null;

    /// Сервис воспроизведения музыки
    private IPlayerService playerService;
    /// Сервис извлечением метаданных из локального файла
    private MusicMetadataExtractService extractService;

    @Override
    public void start(Stage stage)
    {
        try
        {
            // Загружаем главный FXML файл
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/vasilev/musicpro/views/main-view.fxml"));
            Parent root = loader.load();
            this.mainController = loader.getController(); // Вот она, сохраняется в поле класса
            System.out.println("Создан MainController");

            // Создаем сцену
            Scene scene = new Scene(root, 1000, 700);

            // Устанавливаем заголовок
            stage.setTitle("MusicPro v.0.2");

            /// Создать все сервисы и Внедрить зависимости
            startServices();
            loadSearchPanel();
            loadPagination();
            loadPlayer();

            // Обработчик закрытия приложения
            stage.setOnCloseRequest(event ->
            {
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
            } catch (Exception e)
            {
                System.err.println("CSS не загружен: " + e.getMessage());
            }


            // Устанавливаем сцену и показываем окно
            stage.setScene(scene);
            stage.show();
        } catch (Exception e)
        {
            System.err.println("Ошибка загрузки FXML: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /// Запуск сервисов
    private void startServices()
    {
        // Инициализация сервисов
        configService = new ConfigService(AppConfig.getInstance());

        var apiClient = new APIClient(configService.getConfig().getServerUrl(), configService.getConfig().getMaxConcurrentDownloads());
        // один экземпляр apiClient на оба сервиса.
        downloadService = new DownloadService(apiClient, configService.getConfig().getDownloadDir());
        musicClientService = new MusicClientService(apiClient);
        pageOwner = new PageOwner(musicClientService);

        System.out.println("Сервисы запущены");

        // внедрение зависимостей
        mainController.setServices(configService, downloadService, musicClientService, pageOwner);
        System.out.println("Сервисы внедрены в mainController");

        playerService = new BasicPlayerService();
        extractService = new MusicMetadataExtractService();
    }


    /// Загрузка UI контроллера панели поиск. И внедрение его в mainController
    private void loadSearchPanel()
    {
        try
        {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/vasilev/musicpro/views/search-panel.fxml")
            );
            VBox searchPanel = loader.load();

            // Получаем контроллер и внедряем ему IPageOwner
            SearchController searchController = loader.getController();
            searchController.setPageOwner(pageOwner);

            mainController.setSearchPanel(searchPanel);
            System.out.println("SearchPanel внедрен в mainController");
        } catch (IOException e)
        {
            System.err.println("Не удалось загрузить search-panel.fxml: " + e.getMessage());
        }
    }


    /// Загрузка UI контроллера плеера. И внедрение его в mainController
    private void loadPlayer()
    {
        try
        {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/vasilev/musicpro/views/player.fxml")
            );
            VBox player = loader.load();
            PlayerController playerController = loader.getController();

            //внедрение зависимостей
            playerController.setServices(playerService, extractService);
            System.out.println("в PlayerController внедрены в playerService и extractService");

            mainController.setPlayerContainer(player, playerController);
            System.out.println("Player внедрен в mainController");
        } catch (IOException e)
        {
            System.err.println("Не удалось загрузить player.fxml: " + e.getMessage());
        }
    }

    /// Загрузка UI контроллера пагинации. И внедрение его в mainController
    private void loadPagination()
    {
        try
        {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/vasilev/musicpro/views/pagination-view.fxml")
            );
            HBox paginationPanel = loader.load();
            PaginationController paginationController = loader.getController();

            // Внедрение зависимостей
            paginationController.setPageOwner(pageOwner);
            System.out.println("в PaginationController внедрен pageOwner");

            mainController.setPaginationPanel(paginationPanel);
            System.out.println("PaginationPanel внедрен в mainController");
        } catch (IOException e)
        {
            System.err.println("Не удалось загрузить pagination-view.fxml: " + e.getMessage());
        }
    }

    /// Остановка сервисов. Очистка ресурсов где требуется
    private void shutdownServices()
    {
        AutoCloseable downloadNotificator = (AutoCloseable) downloadService;
        try
        {
            downloadNotificator.close();
            // downloadService также закрывает и используемый им apiClient
        } catch (Exception e)
        {
            throw new RuntimeException(e);
        }

        System.out.println("Сервисы остановлены");
    }
}
