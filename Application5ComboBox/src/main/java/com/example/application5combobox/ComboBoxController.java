/**
 * Sample Skeleton for 'ComboBoxView.fxml' Controller Class
 */

package com.example.application5combobox;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;

public class ComboBoxController {

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="comboItems"
    private ComboBox<String> comboItems; // Value injected by FXMLLoader

    @FXML // fx:id="lblIndex"
    private Label lblIndex; // Value injected by FXMLLoader

    @FXML // fx:id="lblItem"
    private Label lblItem; // Value injected by FXMLLoader

    @FXML
    void doAdd(ActionEvent event) {
        TextInputDialog inputDialog = new TextInputDialog();
        inputDialog.setTitle("Add Item");
        inputDialog.setContentText("Enter value to add to list");
        Optional<String> valueOpt = inputDialog.showAndWait();
        // add at index is available too.
        valueOpt.ifPresent(s -> comboItems.getItems().add(s));

        // above code is same as
        // if(valueOpt.isPresent()){
        //     comboItems.getItems().add(valueOpt.get()); // add at index is available too.
        // }
    }

    @FXML
    void doDeleteAll(ActionEvent event) {
        comboItems.getItems().clear();
        lblIndex.setText("");
        lblItem.setText("");
    }

    @FXML
    void doShow(ActionEvent event) {
        // PTR: Following code checks
        // - if comboBox list empty -> show alert and return
        // - if value in comboBox search bar is not found -> show alert and return
        //
        // PTR: As you type in combo box search, the getSelectionModel updates item and index dynamically:
        // - getSelectedIndex() changes from -1 to +ve (if found).
        // - getSelectedItem() changes from empty string to the value typed.

        // PTR: if the value typed doesn't exist in comboBox, index = -1, item = value typed

        System.out.println("doShow(): getSelectedIndex: " + comboItems.getSelectionModel().getSelectedIndex() + ", getSelectedItem: " + comboItems.getSelectionModel().getSelectedItem() + ", itemLength: " + comboItems.getSelectionModel().getSelectedItem().length());

        if (comboItems.getItems().isEmpty()) {
            validationAlert(Alert.AlertType.ERROR, "Empty List", "Empty list! Add something before clicking Show");
            System.out.println("ERROR: SHOW selected when list was empty.");
            return;
        }
        if (comboItems.getSelectionModel().getSelectedIndex() == -1) {
            validationAlert(Alert.AlertType.WARNING, "Value Not Found", "Either select something or typed value wasn't found");
            System.out.println("WARN: SHOW selected, but value not found.");
            return;
        }
        String item = comboItems.getSelectionModel().getSelectedItem();
        int index = comboItems.getSelectionModel().getSelectedIndex();
        lblItem.setText(item);
        lblIndex.setText(String.valueOf(index));
    }

    @FXML
    void doShowSelectedItem(ActionEvent event) {
        // This function has same agenda as doShow()
        // Editable ComboBox executes doShowSelectedItem() with a lot of glitches
        // - when Delete All is clicked - getItems is cleared, so maybe that's why
        // - when Show is clicked after value is entered in ComboBox textField
        // This function works very well without validation for non-editable ComboBox.

        /**
         * Theory on how it actually works:
         * In JavaFX, the onAction event for a ComboBox (specifically ComboBoxBase) is triggered whenever the value property of the ComboBox is changed.
         * This can happen in several ways, and is not limited to direct user interaction.
         * - User selection from the dropdown list:
         *  When a user selects an item from the pop-up list using a mouse click or the Enter key.
         * - User input in an editable ComboBox:
         *  If the ComboBox is editable, the action is triggered when the user types a value into the text field and "submits" it, typically by pressing the Enter key.
         * - Programmatic value changes:
         * The event is triggered even when the value property is changed programmatically using methods like setValue() or by a binding.
         */

        System.out.println("doShowSelectedItem(): getSelectedIndex: " + comboItems.getSelectionModel().getSelectedIndex() + ", getSelectedItem: " + comboItems.getSelectionModel().getSelectedItem() + ", itemLength: " + comboItems.getSelectionModel().getSelectedItem().length());

        // An easy way to check if list is empty, or if typed value is not found => comboItems.getSelectionModel().isEmpty()
        if (comboItems.getSelectionModel().isEmpty()) {
            validationAlert(Alert.AlertType.WARNING, "Value Not Found (Default)", "Value Not Found (Default)");
            System.out.println("WARN: Value not found (by default) in ComboBox");
            return;
        }
        String item = comboItems.getSelectionModel().getSelectedItem();
        int index = comboItems.getSelectionModel().getSelectedIndex();
        lblItem.setText(item + " (Default selection)");
        lblIndex.setText(index + " (Default selection)");
    }

    void validationAlert(Alert.AlertType alertType, String title, String context){
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(context);
        alert.showAndWait();
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert comboItems != null : "fx:id=\"comboItems\" was not injected: check your FXML file 'ComboBoxView.fxml'.";
        assert lblIndex != null : "fx:id=\"lblIndex\" was not injected: check your FXML file 'ComboBoxView.fxml'.";
        assert lblItem != null : "fx:id=\"lblItem\" was not injected: check your FXML file 'ComboBoxView.fxml'.";

        // Note: Don't forget to resolve ComboBox<?>

        // Way 1: populate ComboBox
        List<String> items = Arrays.asList("Laptop", "Mobile", "Mouse");
        comboItems.setItems(FXCollections.observableArrayList(items));

        // Way 2: populate ComboBox
        // String [] items = {"Laptop", "Mobile", "Mouse"};
        // comboItems.getItems().addAll(items);

        // PTR: Editable comboBox is used only for searching after/during typing instead of scroll selecting.
    }
}