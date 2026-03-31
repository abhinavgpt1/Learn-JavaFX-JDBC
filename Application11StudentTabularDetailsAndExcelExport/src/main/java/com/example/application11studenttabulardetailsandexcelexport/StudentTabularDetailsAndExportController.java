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
import java.sql.Date;
import java.util.ResourceBundle;

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

    public void showAlert(String title, String message, Alert.AlertType alertType){
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.show();
    }

    public String getDefaultExcelDumpLocation() {
        String osName = System.getProperty("os.name").toLowerCase();
        String defaultExcelDumpLocation;
        if (osName.contains("win")) {
            defaultExcelDumpLocation = System.getProperty("user.home") + "\\Desktop";
        } else {
            defaultExcelDumpLocation = System.getProperty("user.home");
        }
        return defaultExcelDumpLocation;
    }
    @FXML
    void doExportCSV(ActionEvent event) {
        String desktopPath = getDefaultExcelDumpLocation();

        FileChooser fileChooserWindow = new FileChooser();
        fileChooserWindow.setInitialDirectory(new File(desktopPath));
        fileChooserWindow.setTitle("Save As");
        fileChooserWindow.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Excel (.csv)", "*.csv"),
                new FileChooser.ExtensionFilter("All Files", "*.*") // not necessary to provide to user. Instead, we'll have to do validation on extension.
        );


        File excelDumpFile = fileChooserWindow.showSaveDialog(null); // showOpenDialog / showOpenMultipleDialog are also available.
        if (excelDumpFile == null) { // if user closes the dialog box using cross or Cancel button.
            System.out.println("INFO: Looks like user doesn't want to save the excel yet");
            return;
        }

        String excelDumpFilePath = excelDumpFile.getAbsolutePath();
        if(!excelDumpFilePath.toLowerCase().endsWith(".csv")) {
            excelDumpFilePath = excelDumpFilePath + ".csv";
        }
        // qq - why not append .csv in existing file object?
        // ans - File objects are immutable; their abstract pathname cannot be changed after creation.
        excelDumpFile = new File(excelDumpFilePath);

        // Recall: if a file with same name at same path is open, then FileNotFoundException comes: The process cannot access the file because it is being used by another process.
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(excelDumpFile))) {
            // PTR: adding space anywhere will reflect in cell.
            writer.write("Roll Number,Name,Percentage,Date of Admission\n"); // \n is IMP. so that data is not in single line
            for(Student student: tblStudents.getItems()) {
                // [IMP]: \n is important to add at the end
                writer.write(String.format("%s,%s,%s,%s\n", student.getRollNumber(), student.getName(), student.getPercentage(), student.getDateOfAdmission())); // doa's toString is called, and by default it's in ISO standard (yyyy-mm-dd)
            }
            writer.flush(); // flush anything which isn't written yet off buffer.

            System.out.println("INFO: File downloaded successfully at path: " + excelDumpFilePath);
            // Display location to user and successful operation
            showAlert("Downloaded", "File downloaded: " + excelDumpFilePath, Alert.AlertType.INFORMATION);
        } catch (IOException e) {
            System.out.println("ERROR: File couldn't be downloaded at path: " + excelDumpFilePath + " due to error: " + e.getMessage());
            showAlert("Unknown Error", "File couldn't be downloaded", Alert.AlertType.ERROR);
            throw new RuntimeException(e);
        }
    }

    @FXML
    void doFetchAll(ActionEvent event) {
        String fetchAllStudents = "SELECT * FROM students";
        try {
            PreparedStatement ps = connection.prepareStatement(fetchAllStudents);
            ResultSet studentDetails = ps.executeQuery();
            int rowCount = 0;
            ObservableList<Student> studentList = FXCollections.observableArrayList(); // for setting in TableView
            while(studentDetails.next()) {
                int rollNumber = studentDetails.getInt("roll_number");
                String name = studentDetails.getString("name");
                String percentage = studentDetails.getString("percentage"); // null percentage will show up as 0.0
                Date dateOfAdmission = studentDetails.getDate("date_of_admission");
                studentList.add(new Student(rollNumber, name, percentage, dateOfAdmission.toLocalDate()));
                rowCount++;
            }

            if(rowCount == 0) {
                System.out.println("INFO: No student found in database");
                showAlert("No student found", "Database has no record of students", Alert.AlertType.INFORMATION);
            } else {
                System.out.println("INFO: " + rowCount + " students found in database");
                tblStudents.setItems(studentList); // sets new data and cleans up prev. one.
                // tblStudents.getItems().addAll(studentList); would append as you click.
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert tblStudents != null : "fx:id=\"tblStudents\" was not injected: check your FXML file 'StudentTabularDetailsAndExportView.fxml'.";

        // Note: Add DBConnectionUtil package dependency in pom

        // IMP: Since connection closure is needed on app exit,
        // - created Connection in Main,
        // - created parameterized constructor of this Controller with connection, and linked it to Main
        // - Overridden stop() lifecycle function of app inside Main to close this connection.
        //      * FYI, stop() is called on both, app crash and successful exit.
        // - Removed fx:controller in FXML, since .load() gives error
        //      * fxmlLoader in Main is associated to controller twice.

        try {
            if (connection == null || connection.isClosed()) {
                System.out.println("ERROR: Database connection is null or closed.");
                showAlert("Database Connection Failed", "Failed to establish database connection. Please check with the team.", Alert.AlertType.ERROR);
            } else {
                System.out.println("INFO: Database connection established successfully.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        // Create table if it doesn't exist
        createStudentsTableIfNotExists();

        // Setup TableView
        // ---------------
        TableColumn<Student, Integer> rollNumberTableColumn = new TableColumn<>("Roll Number"); // how the column looks in UI.
        rollNumberTableColumn.setCellValueFactory(new PropertyValueFactory<>("rollNumber")); // maps to property in Student.class

        // PTR: Naming the getter as getRollnumber and not getRollNumber threw java.lang.IllegalStateException: Cannot read from unreadable property rollNumber.
        // Understand: property name is deciphered automatically by removing "get" and converting first letter to lowercase.
        // getRoll_number/getroll_number -> roll_number

        TableColumn<Student, String> nameTableColumn = new TableColumn<>("Name");
        nameTableColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<Student, Float> percentageTableColumn = new TableColumn<>("Percentage");
        percentageTableColumn.setCellValueFactory(new PropertyValueFactory<>("percentage"));
        TableColumn<Student, String> dateOfAdmissionTableColumn = new TableColumn<>("Date of Admission");
        dateOfAdmissionTableColumn.setCellValueFactory(new PropertyValueFactory<>("dateOfAdmission"));

        // Same will be the order at UI.
        tblStudents.getColumns().addAll(rollNumberTableColumn, nameTableColumn, percentageTableColumn, dateOfAdmissionTableColumn);

        /**
         * Further enhancements:
         * 1. adjust column width of DOA. I have to extend it always to see the heading completely.
         * 2. remove extra column right side of DOA
         * 3. add filter + sorting functionality.
         * 4. Editable column fields.
         * 5. Alert box with big error needs width adjustment - I think we can do that with setMinWidth(REGION.USE_PREF_SIZE
         * 6. Export in different format (at least .pdf is a real life use case).
         */
    }
}