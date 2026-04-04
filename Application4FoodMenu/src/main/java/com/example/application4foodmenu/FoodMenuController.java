/**
 * Sample Skeleton for 'FoodMenuView.fxml' Controller Class
 */

package com.example.application4foodmenu;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;

import javax.swing.JOptionPane;

import static javax.swing.JOptionPane.QUESTION_MESSAGE;
import static javax.swing.JOptionPane.YES_NO_CANCEL_OPTION;

public class FoodMenuController {

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="chkAll"
    private CheckBox chkAll; // Value injected by FXMLLoader

    @FXML // fx:id="chkBurger"
    private CheckBox chkBurger; // Value injected by FXMLLoader

    @FXML // fx:id="chkPizza"
    private CheckBox chkPizza; // Value injected by FXMLLoader

    @FXML // fx:id="rad10"
    private RadioButton rad10; // Value injected by FXMLLoader

    @FXML // fx:id="rad20"
    private RadioButton rad20; // Value injected by FXMLLoader

    @FXML // fx:id="toggleDiscount"
    private ToggleGroup toggleDiscount; // Value injected by FXMLLoader

    @FXML // fx:id="txtBill"
    private Label txtBill; // Value injected by FXMLLoader

    @FXML // fx:id="txtPostBillingMessage"
    private TextArea txtPostBillingMessage; // Value injected by FXMLLoader

    private final String CONGRATULATIONS_MSG = "Congratulations! You've received a discount of %s%%. You saved Rs. %s.";
    @FXML
    void doBill(ActionEvent event) {
        float bill = 0;
        if(chkBurger.isSelected()){
            bill += 50;
        }
        if(chkPizza.isSelected()) {
            bill += 100;
        }

        if(bill == 0) {
            validationAlert("Select something on menu before billing");
            return;
        }
        // int discountPer = rad10.isSelected() ? 10 : rad20.isSelected() ? 20: 0; // say the list of radio is long, it would be tedious to get discount.
        // assign userData to the radio at initialization
        int discountPer = 0;
        if (toggleDiscount.getSelectedToggle() != null) {
            discountPer = (int)toggleDiscount.getSelectedToggle().getUserData();
        }
        float discount = bill * discountPer / 100;
        bill = bill - discount;

        /**
         * JOptionPane = used to create standard dialog boxes for displaying
         * - information (showMessageDialog),
         * - soliciting input (showInputDialog),
         * - asking for confirmation (showConfirmDialog)
         *
         * ParentComponent = determines the Frame in which the dialog is displayed; 
         * if null, or if the parentComponent has no Frame, a default Frame is used, and
         * dialog appears on center of the screen.
         *
         * Available MessageType
         * - ERROR_MESSAGE
         * - INFORMATION_MESSAGE
         * - WARNING_MESSAGE
         * - QUESTION_MESSAGE
         * - PLAIN_MESSAGE
         *
         * Available OptionType
         * - DEFAULT_OPTION : Ok button maps to value 0
         * - YES_NO_OPTION : mapping: Yes = 0, No = 1, Cross button = -1
         * - YES_NO_CANCEL_OPTION : mapping: Yes = 0, No = 1, Cancel = 2, Cross button = -1
         * - OK_CANCEL_OPTION : mapping: Ok = 0, Cancel = 2
         * - OK_OPTION ~ YES_NO_OPTION
         * - NO_OPTION ~ YES_NO_CANCEL_OPTION
         *
         * Result:
         * -------
         * YES -> 0, OK -> 0
         * NO -> 1
         * Cancel -> 2
         * Cross button -> -1
         */
        // blocking call i.e. no interaction with app unless dialog closed
        int optionSelected = JOptionPane.showConfirmDialog(null, "Proceed with billing?", "Billing Confirmation", YES_NO_CANCEL_OPTION, QUESTION_MESSAGE);
        System.out.println("Option selected on billing confirmation: " + optionSelected);
        if (optionSelected == 0) { // OK or Yes
            txtBill.setText("Rs. " + bill);
            txtPostBillingMessage.setText(String.format(CONGRATULATIONS_MSG, discountPer, discount));
        }
    }

    @FXML
    void doChkAll(ActionEvent event) {
        // IMP: can't just set it to true since "All" checkbox can be either selected or deselected.
        chkBurger.setSelected(chkAll.isSelected());
        chkPizza.setSelected(chkAll.isSelected());
    }

    @FXML
    void doClearDiscount(ActionEvent event) {
        // Set false to only the one which is selected. This is better instead of iterating over all toggles and marking them false.
        // FYI, toggleGroup ensures all radio are tied up, and after doClearDiscount() no radio will be selected.
        if(toggleDiscount.getToggles().stream().anyMatch(Toggle::isSelected)) { // = anyMatch(radio -> radio.isSelected())
            toggleDiscount.getSelectedToggle().setSelected(false);
        }
    }

    @FXML
    void doNew(ActionEvent event) {
        chkAll.setSelected(false);
        // PTR: passing event has no relevance. Setting chkAll as false doesn't call doChkAll
        doChkAll(event); // this function sees value set as false already, and performs clearance of all checkboxes as per chkAll().

        doClearDiscount(event); // passing null won't work. Passing event has no relevance either, but function works.

        txtBill.setText("");
        txtPostBillingMessage.setText("");
    }

    @FXML
    void doRad10(ActionEvent event) {
        // PTR:
        // This function comes in action when radio button is unselected, and we click on it.
        // Radio button calls nothing if clicked twice.
        // To make a decision on multiple clicks, use Event Listener.
        System.out.println("This function works for rad10 only when it is unselected. Don't use it for deselection.");
    }

    void validationAlert(String context){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Wrong Input");
        alert.setContentText(context);
        alert.show();
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert chkAll != null : "fx:id=\"chkAll\" was not injected: check your FXML file 'FoodMenuView.fxml'.";
        assert chkBurger != null : "fx:id=\"chkBurger\" was not injected: check your FXML file 'FoodMenuView.fxml'.";
        assert chkPizza != null : "fx:id=\"chkPizza\" was not injected: check your FXML file 'FoodMenuView.fxml'.";
        assert rad10 != null : "fx:id=\"rad10\" was not injected: check your FXML file 'FoodMenuView.fxml'.";
        assert rad20 != null : "fx:id=\"rad20\" was not injected: check your FXML file 'FoodMenuView.fxml'.";
        assert toggleDiscount != null : "fx:id=\"toggleDiscount\" was not injected: check your FXML file 'FoodMenuView.fxml'.";
        assert txtBill != null : "fx:id=\"txtBill\" was not injected: check your FXML file 'FoodMenuView.fxml'.";
        assert txtPostBillingMessage != null : "fx:id=\"txtPostBillingMessage\" was not injected: check your FXML file 'FoodMenuView.fxml'.";

        // Assign values to radio buttons to apply discount efficiently
        rad10.setUserData(10);
        rad20.setUserData(20);

        // redundant here since it's set in fxml. But it's imp to know Disable != Editable != Visible
        txtPostBillingMessage.setEditable(false);
    }
}
