module org.example.vasilev.musicpro {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;

    // РАЗРЕШАЕМ доступ к контроллерам
    exports org.example.vasilev.musicpro.controllers to javafx.fxml;

    // Если нужно, экспортируем и другие пакеты
    exports org.example.vasilev.musicpro;
    exports org.example.vasilev.musicpro.models;
    exports org.example.vasilev.musicpro.services;

    // Открываем доступ для FXML загрузчика
    opens org.example.vasilev.musicpro to javafx.fxml;
    opens org.example.vasilev.musicpro.controllers to javafx.fxml;
    opens org.example.vasilev.musicpro.views to javafx.fxml;

    // Открываем пакет dto для Gson (и для javafx.fxml)
    opens org.example.vasilev.musicpro.dto to com.google.gson, javafx.fxml;
}