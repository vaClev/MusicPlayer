module org.example.vasilev.musicpro
{
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires javafx.media;
    requires jaudiotagger;
    requires java.desktop;

    // РАЗРЕШАЕМ доступ к контроллерам
    exports org.example.vasilev.musicpro.controllers to javafx.fxml;

    // Если нужно, экспортируем и другие пакеты
    exports org.example.vasilev.musicpro;
    exports org.example.vasilev.musicpro.models;
    exports org.example.vasilev.musicpro.dto;
    exports org.example.vasilev.musicpro.services;
    exports org.example.vasilev.musicpro.services.music;
    exports org.example.vasilev.musicpro.services.player;

    // Открываем доступ для FXML загрузчика
    opens org.example.vasilev.musicpro to javafx.fxml;
    opens org.example.vasilev.musicpro.controllers to javafx.fxml;
    opens org.example.vasilev.musicpro.controllers.details to javafx.fxml;

    opens org.example.vasilev.musicpro.views to javafx.fxml;
    opens org.example.vasilev.musicpro.views.details to javafx.fxml;
    opens org.example.vasilev.musicpro.models to javafx.fxml; // ДЛЯ BINDING СВОЙСТВ

    // Открываем пакет dto для Gson (и для javafx.fxml)
    opens org.example.vasilev.musicpro.dto to com.google.gson, javafx.fxml;
    exports org.example.vasilev.musicpro.services.download;
}