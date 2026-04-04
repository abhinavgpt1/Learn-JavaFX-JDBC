/**
 * Sample Skeleton for 'StudentRecordsViewV2.fxml' Controller Class
 */

package com.example.application10studentrecordscrudwithcomboanddatepicker;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class StudentRecordsControllerV2 {

    // Connection isn't static since there can be multiple instances of this app.
    private final Connection connection;
    StudentRecordsControllerV2(Connection connection) {
        this.connection = connection;
    }

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="comboRollNumber"
    private ComboBox<String> comboRollNumber; // Value injected by FXMLLoader

    @FXML // fx:id="dtpDateOfAdmission"
    private DatePicker dtpDateOfAdmission; // Value injected by FXMLLoader

    @FXML // fx:id="lblResult"
    private Label lblResult; // Value injected by FXMLLoader

    @FXML // fx:id="txtName"
    private TextField txtName; // Value injected by FXMLLoader

    @FXML // fx:id="txtPercentage"
    private TextField txtPercentage; // Value injected by FXMLLoader

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

    public void populateComboBoxWithAvailableRollNumbers() {
        String getAllRollNumbers = "SELECT roll_number FROM students";
        try {
            PreparedStatement ps = connection.prepareStatement(getAllRollNumbers);
            ResultSet rs = ps.executeQuery();
            List<String> studentRollNumberList = new ArrayList<>();
            while(rs.next()) {
                studentRollNumberList.add(rs.getString("roll_number"));
            }
            comboRollNumber.getItems().addAll(studentRollNumberList); // or use setItems(FXCollections)
            System.out.println("INFO: Populated all Roll numbers in combo box from database");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isNumber(String value) {
        try {
            Float.parseFloat(value); // Not Integer.parseInt() since it can be decimal
            return true;
        } catch(NumberFormatException | NullPointerException n){
            System.out.println(value + " isn't a number");
            return false;
        }
    }

    public boolean isRollNumberElseAlert() {
        // Note: value typed in editable ComboBox's search field is treated as a selected item, even if not found.
        if(!isNumber(comboRollNumber.getSelectionModel().getSelectedItem())) {
            showAlert("Invalid Roll Number", "Please enter a valid number for Roll number", Alert.AlertType.ERROR);
            return false;
        }
        return true;
    }

    public boolean isNameElseAlert() {
        // 1. string with all spaces is not allowed - used isBlank()
        // (optional) 2. Can check english letters only using regx - or identify a way to check if chars are human-readable lang, say hindi, french.
        if(txtName.getText() == null || txtName.getText().isBlank()) {
            showAlert("Invalid Name", "Please fill a valid name", Alert.AlertType.ERROR);
            return false;
        }
        return true;
    }

    public boolean isPercentageElseAlert() {
        // As per table schema, percentage = null by default. So, if it's empty, or just spaces / blank, or null, it's fine.
        if (txtPercentage.getText() == null || txtPercentage.getText().isBlank())
            return true;

        if(!isNumber(txtPercentage.getText())) {
            showAlert("Invalid percentage", "Please fill a valid percentage containing digits and decimal", Alert.AlertType.ERROR);
            return false;
        }
        return true;
    }

    public boolean isDate_NotNeeded(String date) {
        // DatePicker displays date in dd/mm/yyyy format, hence this validation is done accordingly.
        // [Update] dtpDateOfAdmission.getValue() returns null if date is invalid. So it is sufficient to test date.
        try {
            LocalDate.parse(date, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            return true;
        } catch (DateTimeParseException | NullPointerException e) {
            System.out.println(date + " isn't a valid date");
            return false;
        }
    }

    public boolean isDateElseAlert(){
        // Tip: Better have non-editable DatePicker, else everytime validation like this is needed.
        // - doSave() code reduces to just this - Date.valueOf(dtpDateOfAdmission.getValue())

        // As per table schema, doa = CURRENT_TIMESTAMP by default. So, if it's empty / blank (just spaces) / null, it's fine.
        if (dtpDateOfAdmission.getEditor().getText() == null || dtpDateOfAdmission.getEditor().getText().isBlank())
            return true;

        // dtpDateOfAdmission.getValue() is null if date is null/empty/blank/invalid
        if (dtpDateOfAdmission.getValue() == null) {
            showAlert("Invalid date", "Please pick a valid date or fill in format dd/mm/yyyy", Alert.AlertType.ERROR);
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

    private
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
                lblResult.setText("No student record found");
                System.out.println("No student record found to be logged");
                showAlert("No Student Found", "Database has no student record", Alert.AlertType.INFORMATION);
            } else {
                lblResult.setText("Student details logged");
                showAlert("Student Details Logged", "Student details logged on console", Alert.AlertType.INFORMATION);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void doDelete(ActionEvent event) {
        if (!isRollNumberElseAlert())
            return;
        int rollNumber = Integer.parseInt(comboRollNumber.getSelectionModel().getSelectedItem());

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
                dtpDateOfAdmission.getEditor().setText("");
                lblResult.setText("Student record deleted");
                showAlert("Student Record Deleted", "Student record deleted for Roll number " + rollNumber, Alert.AlertType.INFORMATION);
            } else {
                lblResult.setText("No student record found to delete");
                showAlert("Student Not Found", "Cannot delete: Roll number " + rollNumber + " not found.", Alert.AlertType.ERROR);
            }
        } catch (SQLException e) {
            System.out.println("ERROR: Unknown SQLException on deletion of student: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @FXML
    void doFetch(ActionEvent event) {
        if(!isRollNumberElseAlert())
            return;

        String getStudentDetails = "SELECT * FROM students where roll_number = ?";
        int rollNumber = Integer.parseInt(comboRollNumber.getSelectionModel().getSelectedItem());
        try {
            PreparedStatement ps = connection.prepareStatement(getStudentDetails);
            ps.setInt(1, rollNumber);
            ResultSet studentDetails = ps.executeQuery();
            int numRows = 0;
            while(studentDetails.next()) {
                String name = studentDetails.getString("name");
                String percentage = studentDetails.getString("percentage"); // getFloat returns 0.0, and Update btn would update DB column from null to 0.0
                Date dateOfAdmission = studentDetails.getDate("date_of_admission");
                numRows++;
                txtName.setText(name);
                txtPercentage.setText(percentage);
                dtpDateOfAdmission.setValue(dateOfAdmission.toLocalDate());
                // dtpDateOfAdmission.getEditor().setText("abc"); // getEditor() returns TextField, so literally we can setText anything - which is WRONG from UI/UX perspective
                // A valid date won't be selected on the datepicker otherwise.
            }

            if (numRows == 0) {
                txtName.setText("");
                txtPercentage.setText("");
                dtpDateOfAdmission.getEditor().setText("");
                lblResult.setText("Student record not found");
                showAlert("Student Not Found", "No student record found for Roll Number: " + rollNumber, Alert.AlertType.ERROR);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void doSave(ActionEvent event) {
        if (!isRollNumberElseAlert() || !isNameElseAlert() || !isPercentageElseAlert() || !isDateElseAlert())
            return;

        String insertStudentDetails = "INSERT INTO students (roll_number, name, percentage, date_of_admission) values(?, ?, ?, ?)";
        int rollNumber = Integer.parseInt(comboRollNumber.getSelectionModel().getSelectedItem());
        String name = txtName.getText();
        String percentage = isNumber(txtPercentage.getText()) ? txtPercentage.getText() : null; // till here perc. is null/empty/blank/valid number
        LocalDate dateOfAdmissionLocalDate = dtpDateOfAdmission.getValue() == null ? LocalDate.now(): dtpDateOfAdmission.getValue(); // till here date is either null/empty/blank/valid date
        Date dateOfAdmission = Date.valueOf(dateOfAdmissionLocalDate); // java.sql.Date

        // Summary: DTP returns java.util.LocalDate, shows date in format dd/mm/yyyy. java.sql.Date in MySQL accepts LocalDate.

        try {
            PreparedStatement ps = connection.prepareStatement(insertStudentDetails);
            ps.setInt(1, rollNumber);
            ps.setString(2, name);
            ps.setString(3, percentage);
            ps.setDate(4, dateOfAdmission);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 1) {
                System.out.printf("INFO: Student record created: (%s, %s, %s, %s)\n", rollNumber, name, percentage, dateOfAdmission);
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
            showAlert("Student Already Exists", "Student with roll number " + rollNumber + " already exists. Please use a different one to save.", Alert.AlertType.ERROR);
        } catch (SQLException e) {
            System.out.printf("ERROR: Couldn't create Student record. (%s, %s, %s, %s): %s\n", rollNumber, name, percentage, dateOfAdmission, e.getMessage());
            lblResult.setText("Error creating record");
            showAlert("Unknown Error", "SQLException error, please investigate", Alert.AlertType.ERROR);
            throw new RuntimeException(e);
        }
    }

    @FXML
    void doUpdate(ActionEvent event) {
        if (!isRollNumberElseAlert() || !isNameElseAlert() || !isPercentageElseAlert() || !isDateElseAlert())
            return;

        // Since dateOfAdmission is not nullable in schema, we can't update it to null
        String updateStudentDetails = "UPDATE students SET name = ?, percentage = ?, date_of_admission = ? WHERE roll_number = ?";
        String name = txtName.getText();
        String percentage = isNumber(txtPercentage.getText()) ? txtPercentage.getText() : null;
        if (dtpDateOfAdmission.getValue() == null) {
            showAlert("Invalid Date for Update", "Please pick a valid date for update in format dd/mm/yyyy", Alert.AlertType.ERROR);
            return;
        }
        Date dateOfAdmission = Date.valueOf(dtpDateOfAdmission.getValue());
        int rollNumber = Integer.parseInt(comboRollNumber.getSelectionModel().getSelectedItem());
        try {
            PreparedStatement ps = connection.prepareStatement(updateStudentDetails);
            ps.setString(1, name);
            ps.setString(2, percentage);
            ps.setDate(3, dateOfAdmission);
            ps.setInt(4, rollNumber);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 1) {
                System.out.printf("INFO: Student record updated: (%s, %s, %s, %s)\n", rollNumber, name, percentage, dateOfAdmission);
                lblResult.setText("Student record updated!");
                showAlert("Student Record Updated", "Student record updated for Roll number " + rollNumber, Alert.AlertType.INFORMATION);
            } else {
                System.out.println("INFO: No student with Roll number " + rollNumber + " found to update");
                lblResult.setText("No student with Roll number " + rollNumber + " found to update");
                showAlert("Student Not Found", "Cannot update: Student record not found for Roll number " + rollNumber, Alert.AlertType.ERROR);
            }
        } catch (SQLException e) {
            // No SQLIntegrityConstraintViolationException occurs since we're updating a record not inserting new.
            // So, no PK / index violation, therefore no SQLIntegrityConstraintViolationException.
            throw new RuntimeException(e);
        }
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert comboRollNumber != null : "fx:id=\"comboRollNumber\" was not injected: check your FXML file 'StudentRecordsViewV2.fxml'.";
        assert dtpDateOfAdmission != null : "fx:id=\"dtpDateOfAdmission\" was not injected: check your FXML file 'StudentRecordsViewV2.fxml'.";
        assert lblResult != null : "fx:id=\"lblResult\" was not injected: check your FXML file 'StudentRecordsViewV2.fxml'.";
        assert txtName != null : "fx:id=\"txtName\" was not injected: check your FXML file 'StudentRecordsViewV2.fxml'.";
        assert txtPercentage != null : "fx:id=\"txtPercentage\" was not injected: check your FXML file 'StudentRecordsViewV2.fxml'.";

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
        populateComboBoxWithAvailableRollNumbers();
        /**
         * QQ - Why getSelectedIndex() = -1 ALWAYS for any selection I make in the ComboBox<Integer>, but not for ComboBox<String>?
         * Ans - This behavior occurs because JavaFX's ComboBox uses the equals() method to determine the selected index.
         * When you use a ComboBox<String>, the string you type or select is compared to the items in the list using String.equals(), which works as expected.
         * However, with a ComboBox<Integer>, if you type a number into the EDITABLE ComboBox, the value is a String, not an Integer.
         * - getSelectionModel().getSelectedItem returns String even if ComboBox<Integer>
         * - getSelectionModel().getSelectedIndex(), JavaFX tries to find an Integer in the list that equals the typed String, which always fails, so it returns -1.
         *
         * Reason: When a JavaFX ComboBox<T> is set to setEditable(true), the internal editor is a TextField. When a user types or selects a value, the editor's content is treated as a String. Unless you tell the ComboBox how to convert that text back into your data type (Integer), it defaults to returning the raw String from the editor.
         * Fix: comboBox.setConverter(new IntegerStringConverter());
         *
         * Summary:
         * ComboBox<String> is a safer option when combobox is editable.
         */
    }
}