/**
 * Sample Skeleton for 'StudentTabularDetailsAndExportView.fxml' Controller Class
 */

package com.example.application11studenttabulardetailsandexcelexport;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import com.example.dbconnectionutil.DBConnectionFactory;
import com.example.dbconnectionutil.Database;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;

public class StudentTabularDetailsAndExportController {
    // Connection isn't static since there can be multiple instances of this app.
    private final Connection connection;
    StudentTabularDetailsAndExportController(Connection connection) {
        this.connection = connection;
    }

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="tblStudents"
    private TableView<Student> tblStudents; // Value injected by FXMLLoader

    /**
     * Although SQL is case-insensitive, but there's a convention for naming in DBMS:
     * - database: pascal, eg. learn_javafx
     * - table: pascal (plural), eg. trainees, product_prices
     * - column: pascal, eg. first_name
     * - SQL commands: ALL CAPS eg. SELECT, INSERT, ROW_NUMBER
     *
     * CREATE TABLE IF NOT EXISTS students (
     *  roll_number int PRIMARY key,
     *  name varchar(40) NOT NULL,
     *  percentage float DEFAULT NULL, --be aware while fetching/updating
     *  date_of_admission DATE NOT NULL DEFAULT CURRENT_TIMESTAMP
     * )
     *
     * NOTE: if percentage in db = null, then
     * - getFloat("percentage") returns 0.0
     * - getString("percentage") returns null
     *
     * Rule: For null db column/value,
     *     - resultSet.get<DataType> function -> default value eg. 0, 0.0, false
     *     - resultSet.getString -> null
     * Note: In db, nullable float is either null, or a valid float.
     * - preparedStatement.setString("") will give error. So, convert empty txtPercentage.getText() to null before saving.
     */
    public void createStudentsTableIfNotExists(){
        String createStudentsTableIfNotExists = "CREATE TABLE IF NOT EXISTS students (roll_number int PRIMARY key, name varchar(40) NOT NULL, percentage float DEFAULT NULL, date_of_admission DATE NOT NULL DEFAULT CURRENT_TIMESTAMP)";
        try {
            int rowsAffected = connection.prepareStatement(createStudentsTableIfNotExists).executeUpdate();
            if (rowsAffected == 0) {
                System.out.println("INFO: Student table exists, or is created");
            } else {
                showAlert("Database Table Error", "Unknown issue in database. Please check with the team.", Alert.AlertType.ERROR);
                throw new SQLException("ERROR: Unknown: Table couldn't be created. Please check the issue.");
            }
        } catch (SQLException e) {
            System.out.println("ERROR: Couldn't create students table" + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType alertType){
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.show();
    }

    private String getDefaultExcelDumpLocation() {
        String osName = System.getProperty("os.name").toLowerCase();
        String initialDir;
        if (osName.contains("win")) {
            initialDir = System.getProperty("user.home") + "\\Desktop";
        } else {
            initialDir = System.getProperty("user.home");
        }
        return initialDir;
    }
    @FXML
    void doExportCSV(ActionEvent event) {
        FileChooser excelDumpLocation = new FileChooser();
        String desktopPath = getDefaultExcelDumpLocation();
        excelDumpLocation.setInitialDirectory(new File(desktopPath));
        excelDumpLocation.setTitle("Save As");
        excelDumpLocation.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Excel (.csv)", "*.csv"),
                new FileChooser.ExtensionFilter("All Files", "*.*") // not necessary to provide to user. Instead, we'll have to do validation on extension.
        );


        File excelDumpFile = excelDumpLocation.showSaveDialog(null);
        if (excelDumpFile == null) { // if user closes the Save As dialog box using cross or Cancel button.
            System.out.println("INFO: Looks like user doesn't want to save the excel yet");
            return;
        }
        String excelDumpFilePath = excelDumpFile.getAbsolutePath();
        if(!excelDumpFilePath.toLowerCase().endsWith(".csv")) {
            excelDumpFilePath = excelDumpFilePath + ".csv";
        }
        File excelFile = new File(excelDumpFilePath);
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(excelFile));
            writer.write("Roll Number, Name, Percentage, Date of Admission\n"); //\n is Super IMP.
            for(Student student: tblStudents.getItems()) {
                // PTR: newline \n is important to add
                writer.write(String.format("%s,%s,%s,%s\n", student.getRollNumber(), student.getName(), student.getPercentage(), student.getDateOfAdmission()));
            }
            writer.flush(); // flush anything which isn't written yet off buffer.

            // Alert location and success
            showAlert("Downloaded", "File exported at " + excelDumpFilePath, Alert.AlertType.INFORMATION);
        } catch (IOException e) {
            showAlert("Error", "File couldn't be downloaded", Alert.AlertType.ERROR);
            throw new RuntimeException(e);
        } finally {
            try {
                writer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        // PTR: file can be replaced if you select an existing file
        // PTR: if that file is opened, then it can't be written, and probably you'll face exception = Caused by: java.lang.NullPointerException: Cannot invoke "java.io.BufferedWriter.close()" because "writer" is null.
    }

    @FXML
    void doFetchAll(ActionEvent event) {
        String fetchAllStudents = "SELECT * from Students";
        try {
            PreparedStatement ps = connection.prepareStatement(fetchAllStudents);
            ResultSet studentDetails = ps.executeQuery();
            int rowCount = 0;
            ObservableList<Student> studentList = FXCollections.observableArrayList();
            while(studentDetails.next()) {
                int roll_number = studentDetails.getInt("roll_number");
                String name = studentDetails.getString("name");
                float percentage = studentDetails.getFloat("percentage");
                String dateOfAdmission = studentDetails.getString("date_of_admission"); // or use .getDate(), and then convert to String and store in Student object.
                studentList.add(new Student(roll_number, name, percentage, dateOfAdmission));
                rowCount++;
            }

            if(rowCount == 0) {
                showAlert("No student found", "Database has no record of students", Alert.AlertType.INFORMATION);
            } else {
                tblStudents.setItems(studentList); // does auto-cleaning of table, therefore no append in table.
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert tblStudents != null : "fx:id=\"tblStudents\" was not injected: check your FXML file 'StudentTabularDetailsAndExportView.fxml'.";

        createStudentsTableIfNotExists();

        // setup table view
        TableColumn<Student, Integer> roll_numberColumn = new TableColumn<>("Roll Number");
        roll_numberColumn.setCellValueFactory(new PropertyValueFactory<>("roll_number")); // maps to property in Student.class

        // Found a bug in developement - naming the getter as getroll_number and not as getroll_number threw exception = java.lang.IllegalStateException: Cannot read from unreadable property roll_number

        TableColumn<Student, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Student, Float> percentageColumn = new TableColumn<>("Percentage");
        percentageColumn.setCellValueFactory(new PropertyValueFactory<>("percentage"));

        TableColumn<Student, String> dateOfAdmissionColumn = new TableColumn<>("Date of Admission");
        dateOfAdmissionColumn.setCellValueFactory(new PropertyValueFactory<>("dateOfAdmission"));

        tblStudents.getColumns().clear(); // clearing C1, C2 which are created on scene builder by default. Although it is deleted in FXML for now.
        tblStudents.getColumns().addAll(roll_numberColumn, nameColumn, percentageColumn, dateOfAdmissionColumn); // same is the order in UI.
    }
}