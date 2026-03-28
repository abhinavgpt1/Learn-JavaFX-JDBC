/**
 * Sample Skeleton for 'ShopItemsView.fxml' Controller Class
 */

package com.example.application8shopitems;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

import javax.swing.JOptionPane;

import static javax.swing.JOptionPane.QUESTION_MESSAGE;
import static javax.swing.JOptionPane.YES_NO_CANCEL_OPTION;

public class ShopItemsController {

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="comboType"
    private ComboBox<String> comboType; // Value injected by FXMLLoader

    @FXML // fx:id="lblBill"
    private Label lblBill; // Value injected by FXMLLoader

    @FXML // fx:id="lstItems"
    private ListView<String> lstItems; // Value injected by FXMLLoader

    @FXML // fx:id="lstItemsPrice"
    private ListView<Double> lstItemsPrice; // Value injected by FXMLLoader

    @FXML // fx:id="lstSelectedItems"
    private ListView<String> lstSelectedItems; // Value injected by FXMLLoader

    @FXML // fx:id="lstSelectedItemsPrice"
    private ListView<Double> lstSelectedItemsPrice; // Value injected by FXMLLoader

    @FXML // fx:id="rad10"
    private RadioButton rad10; // Value injected by FXMLLoader

    @FXML // fx:id="rad20"
    private RadioButton rad20; // Value injected by FXMLLoader

    @FXML // fx:id="radCustomDiscount"
    private RadioButton radCustomDiscount; // Value injected by FXMLLoader

    @FXML // fx:id="toggleDiscount"
    private ToggleGroup toggleDiscount; // Value injected by FXMLLoader

    @FXML // fx:id="txtCustomDiscount"
    private TextField txtCustomDiscount; // Value injected by FXMLLoader

    private final Map<String, List<Item>> DEVICE_DATA = getDeviceData(); //can use initializer block too for the initialization.
    private Map<String, List<Item>> getDeviceData() {
        Map<String, List<Item>> itemMap = new HashMap<>();
        itemMap.put("Phone", Phone.getPhoneList());
        itemMap.put("Laptop", Laptop.getLaptopList());
        return itemMap;
    }
    
    @FXML
    void doAdd(ActionEvent event) {
        // Rule for this app: single purchase of particular item

        // Objective of this function:
        // 1. Add all selected items in lstItems (not lstItemsPrice)
        // 2. Do not repeat same item
        // 3. Alert user to not select already selected item
        // no need for ObservableList ref since it won't change inside this function, and doesn't need to be observed.
        // FYI, this is read-only list
        List<Integer> selectedItemsIndices = lstItems.getSelectionModel().getSelectedIndices();

        // Addition to lstSelectedItems and lstSelectedItemsPrice can be done using DEVICE_DATA too, but here we're taking the long route.
        // Efficient DS for DEVICE_DATA would be map of map.
        List<String> allRepeatedItems = new ArrayList<>();
        for(Integer selectedItemIndex: selectedItemsIndices){
            String item = lstItems.getItems().get(selectedItemIndex);
            Double price = lstItemsPrice.getItems().get(selectedItemIndex);
            if (lstSelectedItems.getItems().contains(item))
                allRepeatedItems.add(item);
            else {
                lstSelectedItems.getItems().add(item);
                lstSelectedItemsPrice.getItems().add(price);
            }
        }
        if (!allRepeatedItems.isEmpty())
            showAlert("Multiple Quantity For Item Not Allowed", "Not adding these items since they are already selected: \n" + allRepeatedItems, Alert.AlertType.INFORMATION);
    }

    @FXML
    void doBill(ActionEvent event) {
        if (lstSelectedItems.getItems().size() != lstSelectedItemsPrice.getItems().size()) { // would rarely happen
            showAlert("Item-Price mismatch", "Item count doesn't match with Price count", Alert.AlertType.ERROR);
            return;
        }
        double total = lstSelectedItemsPrice.getItems().stream().reduce(Double::sum).orElse(0.0);
        double discountPercentage = 0;
        if(toggleDiscount.getSelectedToggle() != null) { // Check if any radio is selected
            if (toggleDiscount.getSelectedToggle() != radCustomDiscount)
                discountPercentage = (Double) toggleDiscount.getSelectedToggle().getUserData();
            else {
                // if userData = double, this else can be avoided by setting up OutOfScope event on txtCustomDiscount (not key event or listener since we don't want to throw alert as user types)
                // if userData = string, then Key Type event is fine on txtCustomDiscount. But still, isNumber validation is necessary during billing.
                String customDiscountStr = txtCustomDiscount.getText();
                try {
                    discountPercentage = Double.parseDouble(customDiscountStr);
                } catch (NumberFormatException | NullPointerException ex) {
                    System.out.println("ERROR: Invalid discount input: " + txtCustomDiscount.getText());
                    showAlert("Invalid Discount", customDiscountStr + "is not a valid number", Alert.AlertType.ERROR);
                    lblBill.setText("");
                    return;
                }
            }
        }
        total -= total * discountPercentage / 100;

        // blocking call i.e. no interaction with app unless dialog closed
        int optionSelected = JOptionPane.showConfirmDialog(null, "Proceed with billing?", "Billing Confirmation", YES_NO_CANCEL_OPTION, QUESTION_MESSAGE);
        System.out.println("Option selected on billing confirmation: " + optionSelected);
        if (optionSelected == 0) { // Yes
            lblBill.setText(String.format("$ %.2f", total));
        }
    }

    @FXML
    void doClearCart(ActionEvent event) {
        lstSelectedItems.getItems().clear();
        lstSelectedItemsPrice.getItems().clear();
        lblBill.setText("");
    }

    @FXML
    void doDeleteSelectedItems(ActionEvent event) {
        // [IMP] - using loop with indices. Don't simply loop and remove since the Lists (lstSelectedItems, lstSelectedItemsPrice) changes dynamically meanwhile the indices being same (during multiple selection).
        // [IMP] - getSelectedItems/Indices return read-only, hence sort() throws UnsupportedOperationException.
        List<Integer> selectedItemIndices = new ArrayList<>(lstSelectedItems.getSelectionModel().getSelectedIndices());
        Collections.sort(selectedItemIndices);
        int elementsDeleted = 0;
        for (Integer selectedItemIndex: selectedItemIndices) {
            lstSelectedItems.getItems().remove(selectedItemIndex - elementsDeleted); // remove by index and not by value
            lstSelectedItemsPrice.getItems().remove(selectedItemIndex - elementsDeleted);
            elementsDeleted++;
        }
        // PTR: this loop logic is because lstSelectedItems is SelectionMode.MULTIPLE

        // Not helpful here - use removalAll()
        // lstSelectedItems.getItems().removeAll(lstSelectedItems.getSelectionModel().getSelectedItems());
        // - there's no direct way to pick the Double values in lstSelectedItemsPrice corresponding to items selected => use same loop logic as above
    }

    @FXML
    void doPopulateTypeItems(ActionEvent event) {
        String type = comboType.getSelectionModel().getSelectedItem();
        if (!DEVICE_DATA.containsKey(type)) {
            showAlert("Limited Inventory", "Items for " + type + " not available", Alert.AlertType.INFORMATION);
            return;
        }
        lstItems.getItems().clear(); // IMP.
        lstItemsPrice.getItems().clear(); // IMP.
        for (Item i : DEVICE_DATA.get(type)){
            lstItems.getItems().add(i.getModel());
            lstItemsPrice.getItems().add(i.getPrice());
        }
    }

    void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.show();
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert comboType != null : "fx:id=\"comboType\" was not injected: check your FXML file 'ShopItemsView.fxml'.";
        assert lblBill != null : "fx:id=\"lblBill\" was not injected: check your FXML file 'ShopItemsView.fxml'.";
        assert lstItems != null : "fx:id=\"lstItems\" was not injected: check your FXML file 'ShopItemsView.fxml'.";
        assert lstItemsPrice != null : "fx:id=\"lstItemsPrice\" was not injected: check your FXML file 'ShopItemsView.fxml'.";
        assert lstSelectedItems != null : "fx:id=\"lstSelectedItems\" was not injected: check your FXML file 'ShopItemsView.fxml'.";
        assert lstSelectedItemsPrice != null : "fx:id=\"lstSelectedItemsPrice\" was not injected: check your FXML file 'ShopItemsView.fxml'.";
        assert rad10 != null : "fx:id=\"rad10\" was not injected: check your FXML file 'ShopItemsView.fxml'.";
        assert rad20 != null : "fx:id=\"rad20\" was not injected: check your FXML file 'ShopItemsView.fxml'.";
        assert radCustomDiscount != null : "fx:id=\"radCustomDiscount\" was not injected: check your FXML file 'ShopItemsView.fxml'.";
        assert toggleDiscount != null : "fx:id=\"toggleDiscount\" was not injected: check your FXML file 'ShopItemsView.fxml'.";
        assert txtCustomDiscount != null : "fx:id=\"txtCustomDiscount\" was not injected: check your FXML file 'ShopItemsView.fxml'.";

        lstItems.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        // lstItemsPrice.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE); // not needed since we allow item addition based on lstItems and not lstItemPrice.
        lstSelectedItems.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        // lstSelectedItemsPrice.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE); // not needed since we allow item deletion based on lstSelectedItems and not lstSelectedItemsPrice.

        rad10.setUserData(10.0);
        rad20.setUserData(20.0);
        // redundant - defined in fxml: turns active only when it's radio is selected using listener below.
        // txtCustomDiscount.setDisable(true);
        // txtCustomDiscount.setEditable(true); // should be true always
        // FYI, onAction event on radCustomDiscount would enable the textField but won't disable it if any other radio is selected.
        // Hence, we need to either listen on toggle group.
        // Listening on radCustomDiscount won't help becoz if I switch from CustomRad to 10, customRad won't know about this change.

        toggleDiscount.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            boolean isCustomDiscountRadSelected = newToggle == radCustomDiscount;
            txtCustomDiscount.setDisable(!isCustomDiscountRadSelected);
            if (isCustomDiscountRadSelected) {
                txtCustomDiscount.requestFocus();
            }
        });

        List<String> deviceTypes = new ArrayList<>(DEVICE_DATA.keySet());
        // Or use "var" - a reserved type name != keyword ~= auto (c++)
        // https://stackoverflow.com/questions/63073153/var-keyword-in-java
        comboType.setItems(FXCollections.observableList(deviceTypes));

        // IMP: getSelectedItems() / Indices() return read-only list.
    }
}