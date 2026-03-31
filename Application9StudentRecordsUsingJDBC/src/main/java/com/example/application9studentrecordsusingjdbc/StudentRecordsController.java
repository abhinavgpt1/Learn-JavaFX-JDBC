/**
 * Sample Skeleton for 'StudentRecordsView.fxml' Controller Class
 */

package com.example.application9studentrecordsusingjdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class StudentRecordsController {

    // Connection isn't static since there can be multiple instances of this app.
    private final Connection connection;
    StudentRecordsController(Connection connection) {
        this.connection = connection;
    }

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="lblResult"
    private Label lblResult; // Value injected by FXMLLoader

    @FXML // fx:id="txtName"
    private TextField txtName; // Value injected by FXMLLoader

    @FXML // fx:id="txtPercentage"
    private TextField txtPercentage; // Value injected by FXMLLoader

    @FXML // fx:id="txtRollNumber"
    private TextField txtRollNumber; // Value injected by FXMLLoader

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

    public boolean isNumber(String value){
        try {
            Float.parseFloat(value); // Not Integer.parseInt() since it can be decimal
            return true;
        } catch(NumberFormatException | NullPointerException n){
            System.out.println(value + " isn't a number");
            return false;
        }
    }

    public boolean isRollNumberElseAlert(){
        if(!isNumber(txtRollNumber.getText())) {
            showAlert("Invalid Roll Number", "Please enter a valid number for Roll number", Alert.AlertType.ERROR);
            return false;
        }
        return true;
    }
    public boolean isNameElseAlert(){
        // 1. string with all spaces is not allowed - used isBlank()
        // (optional) 2. Can check english letters only using regx - or identify a way to check if chars are human-readable lang, say hindi, french.
        if(txtName.getText() == null || txtName.getText().isBlank()) {
            showAlert("Invalid Name", "Please fill a valid name", Alert.AlertType.ERROR);
            return false;
        }
        return true;
    }
    public boolean isValidPercentage(){
        // As per table schema, percentage = null by default. So, if it's empty, or just spaces / blank, or null, it's fine.
        if (txtPercentage.getText() == null || txtPercentage.getText().isBlank())
            return true;

        if(!isNumber(txtPercentage.getText())) {
            showAlert("Invalid percentage", "Please fill a valid percentage containing digits and decimal", Alert.AlertType.ERROR);
            return false;
        }
        return true;
    }
    public void showAlert(String title, String message, Alert.AlertType alertType){
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.show();
    }

    @FXML
    void doConsoleAllStudents(ActionEvent event) {
        String getAllStudents = "SELECT * FROM students";
        try {
            PreparedStatement ps = connection.prepareStatement(getAllStudents);
            ResultSet studentDetails = ps.executeQuery();
            int rowCount = 0;
            while(studentDetails.next()) {
                int rollNumber = studentDetails.getInt("roll_number");
                String name = studentDetails.getString("name");
                String percentage = studentDetails.getString("percentage"); // nullable column
                String dateOfAdmission = studentDetails.getString("date_of_admission");
                rowCount++;
                System.out.printf("%s. (%s, %s, %s, %s)\n", rowCount, rollNumber, name, percentage, dateOfAdmission);
            }

            if(rowCount == 0) {
                showAlert("No Student Found", "Database has no student record", Alert.AlertType.INFORMATION);
                lblResult.setText("No student record found");
                System.out.println("No student record found to be logged");
            } else {
                showAlert("Student Details Logged", "Student details logged on console", Alert.AlertType.INFORMATION);
                lblResult.setText("Student details logged");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void doDelete(ActionEvent event) {
        if (!isRollNumberElseAlert())
            return;
        int rollNumber = Integer.parseInt(txtRollNumber.getText());

        // JOptionPane is old, use Alert Confirmation.
        // FYI, it returns Optional<ButtonType> unlike TextInputDialog which returns Optional<String>
        Alert confirmDeletion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDeletion.setTitle("Deletion Confirmation");
        confirmDeletion.setContentText("Sure about deleting this student record?");
        Optional<ButtonType> buttonType = confirmDeletion.showAndWait();
        if(buttonType.isEmpty() || buttonType.get() != ButtonType.OK) {
            System.out.println("INFO: User cancelled deleting student record with roll number " + rollNumber);
            return;
        }
        String deleteStudent = "DELETE FROM students WHERE roll_number = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(deleteStudent);
            ps.setInt(1, rollNumber);
            int rowsAffected = ps.executeUpdate();
            if(rowsAffected == 1) {
                System.out.println("INFO: Student record deleted with roll number " + rollNumber);
                txtName.setText("");
                txtPercentage.setText("");
                lblResult.setText("Student record deleted");
                showAlert("Student Record Deleted", "Student record deleted for Roll number " + rollNumber, Alert.AlertType.INFORMATION);
            } else {
                showAlert("Student Not Found", "Cannot delete: Roll number " + rollNumber + " not found.", Alert.AlertType.ERROR);
                lblResult.setText("No student record found to delete");
            }
        } catch (SQLException e) {
            System.out.println("ERROR: Student " + rollNumber + " not found for deletion." + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @FXML
    void doFetch(ActionEvent event) {
        if(!isRollNumberElseAlert())
            return;

        String getStudentDetails = "SELECT * FROM students WHERE roll_number = ?";
        int rollNumber = Integer.parseInt(txtRollNumber.getText());
        try {
            PreparedStatement ps = connection.prepareStatement(getStudentDetails);
            ps.setInt(1, rollNumber);
            ResultSet studentDetails = ps.executeQuery();
            int numRows = 0;
            while(studentDetails.next()) {
                String name = studentDetails.getString("name");
                String percentage = studentDetails.getString("percentage"); // getFloat returns 0.0, and Update btn would update DB column from null to 0.0
                numRows++;
                txtName.setText(name);
                txtPercentage.setText(percentage == null ? "" : percentage);
            }

            if (numRows == 0) {
                txtName.setText("");
                txtPercentage.setText("");
                lblResult.setText("Student record not found");
                showAlert("Student Not Found", "No student record found for Roll Number: " + rollNumber, Alert.AlertType.ERROR);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void doSave(ActionEvent event) {
        if (!isRollNumberElseAlert() || !isNameElseAlert() || !isValidPercentage())
            return;

        String insertStudentDetails = "INSERT INTO students (roll_number, name, percentage, date_of_admission) values(?, ?, ?, CURRENT_DATE)";
        int rollNumber = Integer.parseInt(txtRollNumber.getText());
        String name = txtName.getText();
        String percentage = !isNumber(txtPercentage.getText()) ? null : txtPercentage.getText(); // till here perc. is either null, empty, blank, or valid number

        try {
            PreparedStatement ps = connection.prepareStatement(insertStudentDetails);
            ps.setInt(1, rollNumber);
            ps.setString(2, name);
            ps.setString(3, percentage);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 1) {
                System.out.printf("INFO: Student record created: (%s, %s, %s)", rollNumber, name, percentage);
                lblResult.setText("Student record created for roll number " + rollNumber);
                showAlert("Student Record Created", "Student record created for Roll number " + rollNumber, Alert.AlertType.INFORMATION);
            } else {
                System.out.println("ERROR: Unknown: Couldn't create student record. Please check the issue.");
                lblResult.setText("Error creating student record");
                showAlert("Student Save Failed", "Student record failed to save for Roll number " + rollNumber, Alert.AlertType.ERROR);
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("WARN: Student with this roll number exists:" + rollNumber);
            lblResult.setText("Student with this roll number exists.");
            showAlert("Student Found", "Roll number " + rollNumber + " already exists. Please use a different one to save.", Alert.AlertType.ERROR);
        } catch (SQLException e) {
            System.out.printf("ERROR: Couldn't create student record. (%s, %s, %s): %s", rollNumber, name, percentage, e.getMessage());
            lblResult.setText("Error creating record");
            showAlert("Unknown Error", "SQLException error, please investigate", Alert.AlertType.ERROR);
            throw new RuntimeException(e);
        }
    }

    @FXML
    void doUpdate(ActionEvent event) {
        if (!isRollNumberElseAlert() || !isNameElseAlert() || !isValidPercentage())
            return;
        String updateStudentDetails = "UPDATE students SET name = ?, percentage = ? WHERE roll_number = ?";
        String name = txtName.getText();
        String percentage = !isNumber(txtPercentage.getText()) ? null : txtPercentage.getText();
        int rollNumber = Integer.parseInt(txtRollNumber.getText());
        try {
            PreparedStatement ps = connection.prepareStatement(updateStudentDetails);
            ps.setString(1, name);
            ps.setString(2, percentage);
            ps.setInt(3, rollNumber);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 1) {
                System.out.printf("INFO: Student record updated: (%s, %s, %s)", rollNumber, name, percentage);
                lblResult.setText("Student record updated!");
                showAlert("Student Record Updated", "Student record updated for Roll number " + rollNumber, Alert.AlertType.INFORMATION);
            } else {
                System.out.println("INFO: No student with Roll number " + rollNumber + " found.");
                lblResult.setText(rollNumber + " student not found to update");
                showAlert("Student Not Found", "Cannot update: Student record not found for Roll number " + rollNumber, Alert.AlertType.ERROR);
            }
        } catch (SQLException e) {
            // No SQLIntegrityConstraintViolationException since no PK / index violation happens.
            showAlert("Unknown Error", "SQLException error, please investigate" + rollNumber, Alert.AlertType.ERROR);
            throw new RuntimeException(e);
        }
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert lblResult != null : "fx:id=\"lblResult\" was not injected: check your FXML file 'StudentRecordsView.fxml'.";
        assert txtName != null : "fx:id=\"txtName\" was not injected: check your FXML file 'StudentRecordsView.fxml'.";
        assert txtPercentage != null : "fx:id=\"txtPercentage\" was not injected: check your FXML file 'StudentRecordsView.fxml'.";
        assert txtRollNumber != null : "fx:id=\"txtRollNumber\" was not injected: check your FXML file 'StudentRecordsView.fxml'.";

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
                showAlert("Database Connection Failed", "Failed to establish database connection. Please check with the team.", Alert.AlertType.ERROR);
                System.out.println("ERROR: Database connection is null or closed.");
            } else {
                System.out.println("INFO: Database connection established successfully.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        // Create table if it doesn't exist
        createStudentsTableIfNotExists();
    }
}