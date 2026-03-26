/**
 * Sample Skeleton for 'MarksCalculatorView.fxml' Controller Class
 */

package com.example.application3markscalculator;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.Region;

public class MarksCalculatorController {

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="txtCpp"
    private TextField txtCpp; // Value injected by FXMLLoader

    @FXML // fx:id="txtJava"
    private TextField txtJava; // Value injected by FXMLLoader

    @FXML // fx:id="txtMaxTotal"
    private TextField txtMaxTotal; // Value injected by FXMLLoader

    @FXML // fx:id="txtPercentage"
    private TextField txtPercentage; // Value injected by FXMLLoader

    @FXML // fx:id="txtTotal"
    private TextField txtTotal; // Value injected by FXMLLoader

    @FXML
    void doNew(ActionEvent event) {
        txtCpp.setText("");
        txtJava.setText("");
        txtTotal.setText("");
        txtMaxTotal.setText("");
        txtPercentage.setText("");
    }

    @FXML
    void doPercentage(ActionEvent event) {
        if(!isNumber(txtTotal.getText())){
            validationAlert("Calculate total marks first by entering marks scored in each subject.");
            return;
        }
        TextInputDialog totalMaxMarksInput = new TextInputDialog("200");
        totalMaxMarksInput.setTitle("Enter total marks");
        totalMaxMarksInput.setContentText("Enter total marks");
        Optional<String> totalMaxMarksOpt = totalMaxMarksInput.showAndWait(); // it is different from show() since it blocks the execution code until the displayed dialog box isn't closed.
        if(totalMaxMarksOpt.isPresent() && isNumber(totalMaxMarksOpt.get())){
            Float totalMaxMarks = Float.parseFloat(totalMaxMarksOpt.get());
            txtMaxTotal.setText(String.valueOf(totalMaxMarks));
            txtPercentage.setText(String.valueOf(Float.parseFloat(txtTotal.getText()) / totalMaxMarks * 100));
            System.out.println("INFO: Percentage calculation successful.");
        } else {
            validationAlert("Enter sum of max. possible score in each exam.");
            System.out.println("ERROR: Unsuccessful percentage calculation.");
        }
    }

    @FXML
    void doTotal(ActionEvent event) {
        String cppMarks = txtCpp.getText();
        String javaMarks = txtJava.getText();
        if(!isNumber(cppMarks)){ // handles empty or null cases
            validationAlert("Please fill C++ marks.");
            return;
        }
        if(!isNumber(javaMarks)){
            validationAlert("Please fill Java marks.");
            return;
        }

        txtTotal.setText(String.valueOf(Float.parseFloat(cppMarks) + Float.parseFloat(javaMarks)));
        System.out.println("INFO: Total calculated successfully.");
    }

    void validationAlert(String context){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Wrong Input");
        alert.setContentText(context);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE); // for lengthy messages
        alert.show();
    }

    boolean isNumber(String value){
        try {
            Float.parseFloat(value); // Not Integer.parseInt()
            return true;
        } catch(NumberFormatException | NullPointerException n){
            System.out.println("Incorrect input: " + value);
            return false;
        }
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        // assert 1 == 2; // NO FAILURE
        // qq: why doesn't assert 1 == 2; throw AssertionError as expected?
        /*
          The assert 1 == 2 statement is not throwing an AssertionError because assertions are disabled by default in the Java Virtual Machine (JVM)
          To make assertions work (throw an error when false), you must explicitly enable them at runtime using a specific command-line flag
          You need to pass the -enableassertions (or the shorter -ea) flag

          javac YourProgramName.java
          java -ea YourProgramName
         */
        // to make assert 1==2 working, pass -ea in VM options

        assert txtCpp != null : "fx:id=\"txtCpp\" was not injected: check your FXML file 'MarksCalculatorView.fxml'.";
        assert txtJava != null : "fx:id=\"txtJava\" was not injected: check your FXML file 'MarksCalculatorView.fxml'.";
        assert txtMaxTotal != null : "fx:id=\"txtMaxTotal\" was not injected: check your FXML file 'MarksCalculatorView.fxml'.";
        assert txtPercentage != null : "fx:id=\"txtPercentage\" was not injected: check your FXML file 'MarksCalculatorView.fxml'.";
        assert txtTotal != null : "fx:id=\"txtTotal\" was not injected: check your FXML file 'MarksCalculatorView.fxml'.";

        // app init
        txtTotal.setDisable(true);
        txtMaxTotal.setDisable(true);
        txtPercentage.setDisable(true);
    }
}