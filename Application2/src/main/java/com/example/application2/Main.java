package com.example.application2;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

// Application 1, but without fxml and controller
public class Main extends Application {
    public static Parent getComponents(){
        AnchorPane root = new AnchorPane();
        root.setPrefSize(371, 157);

        Label helloWorldText = new Label();
        helloWorldText.setLayoutX(95.0);
        helloWorldText.setLayoutY(98.0);
        helloWorldText.setPrefSize(181.0, 17.0);
        helloWorldText.setAlignment(javafx.geometry.Pos.CENTER);

        Separator separator = new Separator();
        separator.setLayoutX(86.0);
        separator.setLayoutY(95.0);
        separator.setPrefWidth(200.0);

        Button closeButton = new Button("close");
        closeButton.setLayoutX(327.0);
        closeButton.setLayoutY(1.0);
        closeButton.setOnAction(e -> System.exit(1));

        Button helloButton = new Button("Hello World");
        helloButton.setPrefSize(98.0, 37.0);
        helloButton.setLayoutX(136.0);
        helloButton.setLayoutY(60.0);
        helloButton.setOnAction(e -> {
            System.out.println("Hello world printed on console from application 2.");
            helloWorldText.setText("Hello World Again!");
        });


        root.getChildren().addAll(closeButton, helloButton, helloWorldText, separator);
        return root;
    }

    @Override
    public void start(Stage stage) {
        Parent root = getComponents();
        Scene scene = new Scene(root);
        stage.setTitle("Hello World Again!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}