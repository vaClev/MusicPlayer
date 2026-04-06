module org.example.vasilev.musicpro
{
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires javafx.media;
    requires jaudiotagger;
    requires java.desktop;

    // РАЗРЕШАЕМ доступ к контроллерам
    exports org.example.vasilev.musicpro.desktop.controllers to javafx.fxml;

    // Если нужно, экспортируем и другие пакеты
    exports org.example.vasilev.musicpro;
    exports org.example.vasilev.musicpro.common.models;
    exports org.example.vasilev.musicpro.common.dto;
    exports org.example.vasilev.musicpro.common.services;
    exports org.example.vasilev.musicpro.common.services.music;
    exports org.example.vasilev.musicpro.common.services.player;

    // Открываем доступ для FXML загрузчика
    opens org.example.vasilev.musicpro to javafx.fxml;
    opens org.example.vasilev.musicpro.desktop.controllers to javafx.fxml;
    opens org.example.vasilev.musicpro.desktop.controllers.details to javafx.fxml;

    opens org.example.vasilev.musicpro.views to javafx.fxml;
    opens org.example.vasilev.musicpro.views.details to javafx.fxml;
    opens org.example.vasilev.musicpro.common.models to javafx.fxml; // ДЛЯ BINDING СВОЙСТВ

    // Открываем пакет dto для Gson (и для javafx.fxml)
    opens org.example.vasilev.musicpro.common.dto to com.google.gson, javafx.fxml;
    exports org.example.vasilev.musicpro.common.services.download;
    opens org.example.vasilev.musicpro.common.services to javafx.fxml;
    exports org.example.vasilev.musicpro.desktop.services;
    opens org.example.vasilev.musicpro.desktop.services to javafx.fxml;
    exports org.example.vasilev.musicpro.desktop.services.player;
    exports org.example.vasilev.musicpro.desktop.models;
    opens org.example.vasilev.musicpro.desktop.models to javafx.fxml;
}