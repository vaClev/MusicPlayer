module musicpro.common {
    // Экспортируем все пакеты, которые нужны desktop
    exports org.example.vasilev.musicpro.common.models;
    exports org.example.vasilev.musicpro.common.dto;
    exports org.example.vasilev.musicpro.common.services;
    exports org.example.vasilev.musicpro.common.services.music;
    exports org.example.vasilev.musicpro.common.services.player;
    exports org.example.vasilev.musicpro.common.services.download;

    // Открываем для Gson (рефлексия)
    opens org.example.vasilev.musicpro.common.dto to com.google.gson;
    exports org.example.vasilev.musicpro.common.utils;

    // Зависимости модуля
    requires com.google.gson;
    requires jaudiotagger;
}