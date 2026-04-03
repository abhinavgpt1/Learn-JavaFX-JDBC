package com.example.application12playmediaswitchscreensandemail;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // startup screen for us is the PlayMediaView.fxml. Later a button redirects to EmailView.
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("PlayMediaView.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Play Audio, Video and Email");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        // 1. Can perform resource cleanups here. eg. db connection closure
        // 2. if stage.close() is called by a function, then too stop() gets called.
        System.out.println("Closing the application...");
    }

    public static void main(String[] args) {
        launch();
    }
}