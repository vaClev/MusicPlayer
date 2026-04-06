module org.example.vasilev.musicpro
{
    // === Модули, от которых зависит весь проект ===
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires com.google.gson;
    requires jaudiotagger;
    requires java.desktop;

    // === Экспортируем "запускатель"
    exports org.example.vasilev.musicpro.launcher;

    // === Экспортируем common пакеты (для использования в desktop и будущем Android) ===
    exports org.example.vasilev.musicpro.common.models;
    exports org.example.vasilev.musicpro.common.dto;
    exports org.example.vasilev.musicpro.common.services;
    exports org.example.vasilev.musicpro.common.services.music;
    exports org.example.vasilev.musicpro.common.services.player;
    exports org.example.vasilev.musicpro.common.services.download;
    // === Открываем для Gson (сериализация/десериализация) ===
    opens org.example.vasilev.musicpro.common.dto to com.google.gson;

    // === Экспортируем desktop пакеты (только то, что нужно внешнему миру) ===
    exports org.example.vasilev.musicpro.desktop.controllers to javafx.fxml;
    exports org.example.vasilev.musicpro.desktop.controllers.details to javafx.fxml;

    // === Открываем для FXML загрузчика (только контроллеры и UI) ===
    opens org.example.vasilev.musicpro.desktop.controllers to javafx.fxml;
    opens org.example.vasilev.musicpro.desktop.controllers.details to javafx.fxml;
    opens org.example.vasilev.musicpro.views to javafx.fxml;
    opens org.example.vasilev.musicpro.views.details to javafx.fxml;
    opens org.example.vasilev.musicpro.desktop.services.player to javafx.fxml;
}