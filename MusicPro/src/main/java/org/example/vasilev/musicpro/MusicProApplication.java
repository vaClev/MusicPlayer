package org.example.vasilev.musicpro;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class MusicProApplication extends Application
{

    @Override
    public void start(Stage stage)
    {
        try
        {
            // Загружаем главный FXML файл
            Parent root = FXMLLoader.load(
                    getClass().getResource("/org/example/vasilev/musicpro/views/main-view.fxml")
            );

            // Создаем сцену
            Scene scene = new Scene(root, 1000, 700);

            // Устанавливаем заголовок
            stage.setTitle("MusicPro v.0.1");


            /// Загружаем CSS стили напрямую в сцену
            try
            {
                /// Основные стили
                URL cssUrl = getClass().getResource("/org/example/vasilev/musicpro/styles.css");
                if (cssUrl != null)
                {
                    scene.getStylesheets().add(cssUrl.toExternalForm());
                }

                /// Стили кнопок плеера
                cssUrl = getClass().getResource("/org/example/vasilev/musicpro/player.css");
                if (cssUrl != null)
                {
                    scene.getStylesheets().add(cssUrl.toExternalForm());
                }
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
}
