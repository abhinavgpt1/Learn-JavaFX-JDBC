/**
 * Sample Skeleton for 'StudentRecordsView.fxml' Controller Class
 */

package com.example.application9studentrecordsusingjdbc;

import java.net.URL;
import java.sql.*;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class StudentRecordsController {

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

    private Connection connection;

    private static boolean isNumber(String value, Class<? extends Number> numberImpl) { // simply Class numberImpl could have been written only to get raw parameterized warning. Better is to specify what types we're expecting
        try {
            if (numberImpl == Integer.class) {
                Integer.parseInt(value);
            } else if (numberImpl == Float.class) {
                Float.parseFloat(value);
            } else {
                throw new IllegalArgumentException("Unsupported number type: " + numberImpl);
            }
            return true;
        } catch (NumberFormatException e) {
            System.out.println("ERROR: Value " + value + " isn't a valid " + numberImpl.getSimpleName() + ".");
            return false;
        }
    }
    private boolean isValidRollNumber(){
        if(!isNumber(txtRollNumber.getText(), Integer.class)) {
            showAlert("Invalid rollnumber", "Please fill a valid number for rollnumber.", Alert.AlertType.ERROR);
            return false;
        }
        return true;
    }
    private boolean isValidName(){
        if(txtName.getText() == null || txtName.getText().isEmpty()) {
            showAlert("Invalid Name", "Please fill a name.", Alert.AlertType.ERROR);
            return false;
        }
        return true;
    }
    private boolean isValidPercentage(){
        // Default percentage = null as per table schema, but I'm still making it compulsory from UI since the query to fetch/update will need if-else check on txtPercentage.
        if(!isNumber(txtPercentage.getText(), Float.class)) {
            showAlert("Invalid percentage", "Please fill a valid percentage excluding %.", Alert.AlertType.ERROR);
            return false;
        }
        return true;
    }
    private void showAlert(String title, String message, Alert.AlertType alertType){
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.show();
    }

    @FXML
    void doConsoleAllStudents(ActionEvent event) {
        String getAllStudents = "SELECT * from Students";
        try {
            PreparedStatement ps = connection.prepareStatement(getAllStudents);
            ResultSet studentDetails = ps.executeQuery();
            int rowCount = 0;
            while(studentDetails.next()) {
                int rollNumber = studentDetails.getInt("rollnumber");
                String name = studentDetails.getString("name");
                float percentage = studentDetails.getFloat("percentage");
                String dateOfAdmission = studentDetails.getString("dateofadmission");
                rowCount++;
                System.out.println(String.format("%s. (%s,%s,%s,%s)", rowCount, rollNumber, name, percentage, dateOfAdmission));
            }
            if(rowCount == 0) {
                showAlert("No student found", "Database has no record of students", Alert.AlertType.INFORMATION);
                lblResult.setText("No student found");
            } else {
                lblResult.setText("Logged student details");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void doDelete(ActionEvent event) {
        if (!isValidRollNumber())
            return;
        int rollNumber = Integer.parseInt(txtRollNumber.getText());
        Alert confirmDeletion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDeletion.setTitle("Confirmation");
        confirmDeletion.setContentText("Are you sure about deleting student record?");
        Optional<ButtonType> verdict = confirmDeletion.showAndWait();
        if(!verdict.isPresent() || verdict.get() != ButtonType.OK) {
            System.out.println("INFO: Backed off from deleting student record with roll number " + rollNumber);
            return;
        }
        String deleteStudent = "DELETE from Students where rollnumber = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(deleteStudent);
            ps.setInt(1, rollNumber);
            int rowsAffected = ps.executeUpdate();
            if(rowsAffected == 1) {
                System.out.println("INFO: Student record deleted with roll number " + rollNumber);
                lblResult.setText("Student record deleted");
                txtName.setText("");
                txtPercentage.setText("");
            } else {
                showAlert("Delete Unsuccessful", "No student record found to delete", Alert.AlertType.INFORMATION);
                lblResult.setText("No student record found to delete");
            }
        } catch (SQLException e) {
            System.out.println("ERROR: Couldn't delete student record " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @FXML
    void doFetch(ActionEvent event) {
        if(!isValidRollNumber())
            return;

        String getStudentDetails = "SELECT * from Students where rollnumber = ?";
        int rollNumber = Integer.parseInt(txtRollNumber.getText());
        try {
            PreparedStatement ps = connection.prepareStatement(getStudentDetails);
            ps.setInt(1, rollNumber);
            ResultSet studentDetails = ps.executeQuery();
            int numRows = 0;
            while(studentDetails.next()) {
                String name = studentDetails.getString("name");
                float percentage = studentDetails.getFloat("percentage");
                numRows++;
                txtName.setText(name);
                txtPercentage.setText(String.valueOf(percentage));
            }
            if (numRows == 0) {
                showAlert("Student not found", "No student found with roll number: " + rollNumber, Alert.AlertType.INFORMATION);
                lblResult.setText("Student not found");
                txtName.setText("");
                txtPercentage.setText("");
                return;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void doSave(ActionEvent event) {
        if (!isValidRollNumber() || !isValidName() || !isValidPercentage())
            return;

        String insertStudentDetails = "INSERT into Students (rollnumber, name, percentage, dateofadmission) values(?,?,?,CURRENT_DATE)";
        int rollNumber = Integer.parseInt(txtRollNumber.getText());
        String name = txtName.getText();
        float percentage = Float.parseFloat(txtPercentage.getText());

        try {
            PreparedStatement ps = connection.prepareStatement(insertStudentDetails);
            ps.setInt(1, rollNumber);
            ps.setString(2, name);
            ps.setFloat(3, percentage);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 1) {
                System.out.println(String.format("INFO: Student record created: (%s,%s,%s)", rollNumber, name, percentage));
                lblResult.setText("Student record created!");
            } else {
                System.out.println("ERROR: Unknown: Couldn't create Student. Please check the issue.");
                lblResult.setText("Error creating record");
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("WARN: Student with this roll number exists:" + rollNumber);
            lblResult.setText("Student with this roll number exists.");
        } catch (SQLException e) {
            System.out.println(String.format("ERROR: Couldn't create Student record. (%s,%s,%s,%s).", rollNumber, name, percentage, e.getMessage()));
            lblResult.setText("Error creating record");
            throw new RuntimeException(e);
        }
    }

    @FXML
    void doUpdate(ActionEvent event) {
        if (!isValidRollNumber() || !isValidName() || !isValidPercentage())
            return;
        String updateStudentDetails = "UPDATE Students set name=?, percentage=? where rollnumber=?";
        String name = txtName.getText();
        float percentage = Float.parseFloat(txtPercentage.getText());
        int rollNumber = Integer.parseInt(txtRollNumber.getText());
        try {
            PreparedStatement ps = connection.prepareStatement(updateStudentDetails);
            ps.setString(1, name);
            ps.setFloat(2, percentage);
            ps.setInt(3, rollNumber);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 1) {
                System.out.println(String.format("INFO: Student record updated: (%s,%s,%s)", rollNumber, name, percentage));
                lblResult.setText("Student record updated!");
            } else {
                System.out.println("INFO: No student with rollnumber " + rollNumber + " found.");
                lblResult.setText(rollNumber + " student not found to update");
            }
        } catch (SQLException e) { // no SQLIntegrityConstraintViolationException since update flow doesn't create duplicate rollNo student, but updates it.
            throw new RuntimeException(e);
        }
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert lblResult != null : "fx:id=\"lblResult\" was not injected: check your FXML file 'StudentRecordsView.fxml'.";
        assert txtName != null : "fx:id=\"txtName\" was not injected: check your FXML file 'StudentRecordsView.fxml'.";
        assert txtPercentage != null : "fx:id=\"txtPercentage\" was not injected: check your FXML file 'StudentRecordsView.fxml'.";
        assert txtRollNumber != null : "fx:id=\"txtRollNumber\" was not injected: check your FXML file 'StudentRecordsView.fxml'.";

//        // app and db init
//        // connect to db and create table if not created
//        connection = DBConnection.doConnect();
//        DBConnection.createStudentsTableIfNotExists(connection);
    }
}