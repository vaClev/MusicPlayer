package org.example.vasilev.musicpro.controllers.details;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.vasilev.musicpro.models.ExtraFile;
import org.example.vasilev.musicpro.models.MusicFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TabManager
{

    private static TabManager instance;
    private Stage detailsStage;
    private TabPane tabPane;
    private Map<Long, Tab> openTabs = new HashMap<>();

    private TabManager() {}

    public static TabManager getInstance()
    {
        if (instance == null)
        {
            instance = new TabManager();
        }
        return instance;
    }

    public void showOrCreateTab(MusicFile musicFile)
    {
        // Инициализируем окно если оно еще не создано
        initializeWindowIfNeeded();
        // Показываем окно если оно скрыто
        if (!isWindowOpen())
        {
            detailsStage.show();
            detailsStage.toFront();
        }

        // Проверяем, открыта ли уже вкладка для этого трека
        if (openTabs.containsKey(musicFile.getId()))
        {
            Tab existingTab = openTabs.get(musicFile.getId());
            tabPane.getSelectionModel().select(existingTab);
        }
        else
        {
            // Создаем новую вкладку
            createNewTab(musicFile);
        }
    }

    private void initializeWindowIfNeeded()
    {
        if (detailsStage == null)
        {
            detailsStage = new Stage();
            detailsStage.setTitle("Детали музыки");
            detailsStage.initStyle(StageStyle.DECORATED);

            // Создаем TabPane
            tabPane = new TabPane();
            tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
            tabPane.setId("detailsTabPane");

            // Настройки внешнего вида
            tabPane.setStyle("-fx-background-color: #1e1e1e;");

            // Создаем сцену
            Scene scene = new Scene(tabPane, 900, 700);
            detailsStage.setScene(scene);

            // Обработчик закрытия окна
            detailsStage.setOnCloseRequest(e -> {
                e.consume(); // Не закрывать окно полностью
                detailsStage.hide(); // Только скрыть
            });
        }
    }

    private void createNewTab(MusicFile musicFile)
    {
        try
        {
            // Загружаем FXML
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/vasilev/musicpro/views/details/music-details.fxml")
            );
            BorderPane content = loader.load();
            MusicDetailsController controller = loader.getController();

            // Настраиваем контроллер
            controller.setMusicFile(musicFile);

            // Создаем вкладку
            Tab tab = new Tab();
            tab.setText(getTabTitle(musicFile));
            tab.setContent(content);
            tab.setClosable(true);

            // Иконка вкладки с эмодзи музыки
            Label tabLabel = new Label();
            tabLabel.setGraphic(new Label("🎵"));
            tab.setGraphic(tabLabel);

            // Добавляем обработчик закрытия
            tab.setOnClosed(e -> {
                openTabs.remove(musicFile.getId());
                if (tabPane.getTabs().isEmpty()) {
                    detailsStage.hide(); // Скрываем окно если нет вкладок
                }
            });

            // Добавляем вкладку
            tabPane.getTabs().add(tab);
            tabPane.getSelectionModel().select(tab);

            // Сохраняем ссылку в мапе
            openTabs.put(musicFile.getId(), tab);

        }
        catch (IOException e)
        {
            System.err.println("Error creating details tab: " + e.getMessage());
            showErrorDialog("Cannot open details", e.getMessage());
        }
    }

    private String getTabTitle(MusicFile musicFile)
    {
        String title = String.format("%s - %s", musicFile.getArtist(), musicFile.getTitle());
        if (title.length() > 30)
        {
            title = title.substring(0, 12) + "...";
        }
        return title;
    }

    private void showErrorDialog(String title, String message)
    {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.show();
    }

    public void closeAllTabs()
    {
        if (tabPane != null) {
            tabPane.getTabs().clear();
            openTabs.clear();
        }
    }

    public boolean isWindowOpen()
    {
        return detailsStage != null && detailsStage.isShowing();
    }
}
