package org.example.vasilev.musicpro;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class HelloController {
    @FXML
    private Label welcomeText;
    //@FXML private Label welcomeText - ссылка на элемент с fx:id="welcomeText"

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}
