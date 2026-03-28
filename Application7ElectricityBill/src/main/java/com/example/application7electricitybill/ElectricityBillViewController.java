/**
 * Sample Skeleton for 'ElectricityBillView.fxml' Controller Class
 */

package com.example.application7electricitybill;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;

public class ElectricityBillViewController {

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="imgCurrentReadingValidation"
    private ImageView imgCurrentReadingValidation; // Value injected by FXMLLoader

    @FXML // fx:id="imgDiscountValidation"
    private ImageView imgDiscountValidation; // Value injected by FXMLLoader

    @FXML // fx:id="imgPrevReadingValidation"
    private ImageView imgPrevReadingValidation; // Value injected by FXMLLoader
    @FXML // fx:id="imgNoBillForUnitsValidation"
    private ImageView imgNoBillForUnitsValidation; // Value injected by FXMLLoader

    @FXML // fx:id="txtCurrentReading"
    private TextField txtCurrentReading; // Value injected by FXMLLoader

    @FXML // fx:id="txtDiscount"
    private TextField txtDiscount; // Value injected by FXMLLoader

    @FXML // fx:id="txtNetBill"
    private TextField txtNetBill; // Value injected by FXMLLoader

    @FXML // fx:id="txtPrevReading"
    private TextField txtPrevReading; // Value injected by FXMLLoader

    @FXML // fx:id="txtTotal"
    private TextField txtTotal; // Value injected by FXMLLoader

    @FXML // fx:id="txtNoBillForUnits"
    private TextField txtNoBillForUnits; // Value injected by FXMLLoader

    // FYI, MutableBoolean != AtomicBoolean
    private MutableBoolean isPrevReadingValid; // primitive boolean is pass-by-value inside validateReadingAndShowIcon function, hence we need a wrapper class on it.
    private MutableBoolean isCurrentReadingValid;
    private MutableBoolean isUnitExemptionValid;
    private MutableBoolean isDiscountValid;

    // Preload images for performance. FYI, imageView are different, so this image won't be shared among them (as expected).
    private final Image TICK = new Image(getClass().getResourceAsStream("tick.png"));
    private final Image CROSS = new Image(String.valueOf(getClass().getResource("cross.png")));
    private final String MISSING_VALUE = "Please enter a number for %s";
    private final float RATE_PER_KWH = 10;
    void validationAlert(String context){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Wrong Input");
        alert.setContentText(context);
        alert.show();
    }
    @FXML
    void doBill(ActionEvent event) {
        if(!isPrevReadingValid.get()) {
            validationAlert(String.format(MISSING_VALUE, "Prev. Reading"));
            return;
        }
        if (!isCurrentReadingValid.get()) {
            validationAlert(String.format(MISSING_VALUE, "Current Reading"));
            return;
        }
        if (!isUnitExemptionValid.get()) {
            validationAlert(String.format(MISSING_VALUE, "Unit Exemption"));
            return;
        }
        if (!isDiscountValid.get()) {
            validationAlert(String.format(MISSING_VALUE, "Discount"));
            return;
        }
        // Do NOT use Integer.parseInt()
        float unitsConsumed = Float.parseFloat(txtCurrentReading.getText()) - Float.parseFloat(txtPrevReading.getText());
        if (unitsConsumed < 0) {
            validationAlert("Current reading can't be less than prev. reading. \nEnter valid readings.");
            return;
        }
        float unitsExemptionLimit = Float.parseFloat(txtNoBillForUnits.getText());
        if (unitsConsumed <= unitsExemptionLimit)
            unitsConsumed = 0;
        float totalBill = unitsConsumed * RATE_PER_KWH;
        float netBill = totalBill * (1 - Float.parseFloat(txtDiscount.getText())/100);
        txtTotal.setText(String.valueOf(totalBill));
        txtNetBill.setText(String.valueOf(netBill));
    }

    @FXML
    void doClear(ActionEvent event) {
        // Note: setText() will trigger listener as well as the Key Type event - this will lead to either tick or cross being set
        // Since we want neither, set the image as null explicitly
        txtPrevReading.setText("");
        imgPrevReadingValidation.setImage(null);

        txtCurrentReading.setText("");
        imgCurrentReadingValidation.setImage(null);

        txtNoBillForUnits.setText("0");
         imgNoBillForUnitsValidation.setImage(null);

        txtDiscount.setText("0");
        imgDiscountValidation.setImage(null);

        txtTotal.setText("");
        txtNetBill.setText("");

        // Flags & images get set automatically to true/false because of listener / key type event upon setText()
        // System.out.println(isPrevReadingValid.get() + ":" + isCurrentReadingValid.get() + ":" + isUnitExemptionValid.get() + ":" + isDiscountValid.get());
    }

    @FXML
    void doClose(ActionEvent event) {
        System.out.println("Closing the application...");
        System.exit(0);
    }

    @FXML
    void validatePrevReadingOnTheFly(KeyEvent event) {
        validateReadingAndShowIcon(txtPrevReading.getText(), imgPrevReadingValidation, isPrevReadingValid);
    }

    @FXML
    void validateCurrentReadingOnTheFly(KeyEvent event) {
        validateReadingAndShowIcon(txtCurrentReading.getText(), imgCurrentReadingValidation, isCurrentReadingValid);
    }

    private void validateReadingAndShowIcon(String value, ImageView img, MutableBoolean flag) {
        try {
            Float.parseFloat(value); // Not Integer.parseInt() since reading can be in decimal
            img.setImage(TICK);
            flag.set(true);
        } catch(NumberFormatException n){
            // System.out.println(value);
            flag.set(false);
            if (value == null || value.isEmpty())
                img.setImage(null);
            else
                img.setImage(CROSS);
        }
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert imgCurrentReadingValidation != null : "fx:id=\"imgCurrentReadingValidation\" was not injected: check your FXML file 'ElectricityBillView.fxml'.";
        assert imgDiscountValidation != null : "fx:id=\"imgDiscountValidation\" was not injected: check your FXML file 'ElectricityBillView.fxml'.";
        assert imgPrevReadingValidation != null : "fx:id=\"imgPrevReadingValidation\" was not injected: check your FXML file 'ElectricityBillView.fxml'.";
        assert imgNoBillForUnitsValidation != null : "fx:id=\"imgNoBillForUnitsValidation\" was not injected: check your FXML file 'ElectricityBillView.fxml'.";
        assert txtCurrentReading != null : "fx:id=\"txtCurrentReading\" was not injected: check your FXML file 'ElectricityBillView.fxml'.";
        assert txtDiscount != null : "fx:id=\"txtDiscount\" was not injected: check your FXML file 'ElectricityBillView.fxml'.";
        assert txtNetBill != null : "fx:id=\"txtNetBill\" was not injected: check your FXML file 'ElectricityBillView.fxml'.";
        assert txtPrevReading != null : "fx:id=\"txtPrevReading\" was not injected: check your FXML file 'ElectricityBillView.fxml'.";
        assert txtTotal != null : "fx:id=\"txtTotal\" was not injected: check your FXML file 'ElectricityBillView.fxml'.";
        assert txtNoBillForUnits != null : "fx:id=\"txtNoBillForUnits\" was not injected: check your FXML file 'ElectricityBillView.fxml'.";

        // initial interface settings - redundant, since it's present in fxml
        // txtNoBillForUnits.setText("0");
        // txtDiscount.setText("0");
        // txtTotal.setDisable(true);
        // txtTotal.setEditable(false);
        // txtNetBill.setDisable(true);
        // txtNetBill.setEditable(false);
        isPrevReadingValid = new MutableBoolean(false);
        isCurrentReadingValid = new MutableBoolean(false);
        isUnitExemptionValid = new MutableBoolean(true);
        isDiscountValid = new MutableBoolean(true);

        // No need for following explicit listener since we have action on Key Typed
        // txtPrevReading.textProperty().addListener(
        //         (observable, oldValue, newValue) -> validateReadingAndShowIcon(newValue, imgPrevReadingValidation, isPrevReadingValid));
        // txtCurrentReading.textProperty().addListener(
        //         (observable, oldValue, newValue) -> validateReadingAndShowIcon(newValue, imgCurrentReadingValidation, isCurrentReadingValid));

        // Here's manual listener as well
        txtNoBillForUnits.textProperty().addListener(
                (observable, oldValue, newValue) -> validateReadingAndShowIcon(newValue, imgNoBillForUnitsValidation, isUnitExemptionValid));
        txtDiscount.textProperty().addListener(
                (observable, oldValue, newValue) -> validateReadingAndShowIcon(newValue, imgDiscountValidation, isDiscountValid));
    }
}