package com.example.application4foodmenu;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * We learnt:
 * 1. CheckBoxes and Radio buttons.
 * 2. Grouping radio buttons and forming a toggle group.
 * 3. Deselecting radio button using button and Java streams (It's futile to keep a click count check using Event listener, or using an On Action event method).
 * 4. Escape % in String.format().
 * 5. Utilise FXML methods internally with each other.
 * 6. TextArea editable != TextArea disable != TextArea visible
 */
public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("FoodMenuView.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Order food!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}