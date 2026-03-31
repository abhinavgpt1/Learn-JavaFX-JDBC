package com.example.application9studentrecordsusingjdbc;

import com.example.dbconnectionutil.DBConnectionFactory;
import com.example.dbconnectionutil.Database;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;

public class Main extends Application {

    // Connection is shared with Controller for CRUD
    // stop() closes connection gracefully on both app exit or app crash.
    private Connection connection;
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("StudentRecordsView.fxml"));
        connection = DBConnectionFactory.getConnection(Database.MYSQL);
        StudentRecordsController controller = new StudentRecordsController(connection);
        // You need to remove FXML-Controller linking to not get error on .load()
        // Demerit: linking/reference breaks between fields and methods (while developing).
        fxmlLoader.setController(controller);

        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Student Details (CRUD)");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        if (connection != null) {
            System.out.println("INFO: Closing DB connection gracefully...");
            connection.close();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}