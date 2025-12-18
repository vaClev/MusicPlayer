module org.example.vasilev.musicpro {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.vasilev.musicpro to javafx.fxml;
    exports org.example.vasilev.musicpro;
}