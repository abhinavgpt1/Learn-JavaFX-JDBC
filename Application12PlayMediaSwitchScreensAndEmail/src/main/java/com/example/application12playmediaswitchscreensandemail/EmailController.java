/**
 * Sample Skeleton for 'EmailView.fxml' Controller Class
 */

package com.example.application12playmediaswitchscreensandemail;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class EmailController {

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="txtAppPassword"
    private TextArea txtAppPassword; // Value injected by FXMLLoader

    @FXML // fx:id="txtBody"
    private TextArea txtBody; // Value injected by FXMLLoader

    @FXML // fx:id="txtFromMailAddress"
    private TextField txtFromMailAddress; // Value injected by FXMLLoader

    @FXML // fx:id="txtSubject"
    private TextArea txtSubject; // Value injected by FXMLLoader

    @FXML // fx:id="txtToMailAddress"
    private TextField txtToMailAddress; // Value injected by FXMLLoader

    private static void showAlert(String title, String message, Alert.AlertType alertType){
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.show();
    }

    private static boolean validateEmailAddress(String emailAddress) {
        Pattern GMAIL_ADDRESS_REGEX =
                Pattern.compile("^[A-Z0-9._%+-]+@gmail\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        // General regex
        // Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = GMAIL_ADDRESS_REGEX.matcher(emailAddress);
        return matcher.matches();
    }

    @FXML
    void doSendMail(ActionEvent event) {
        // PTR: use maven dependency com.sun.mail > javax.mail > 1.6.2
        String fromMailAddress = txtFromMailAddress.getText();
        String appPassword = txtAppPassword.getText();
        String toMailAddress = txtToMailAddress.getText();
        String body = txtBody.getText();
        String subject = txtSubject.getText();
        if(!validateEmailAddress(fromMailAddress)) {
            showAlert("Invalid mail address", "Please fill correct Gmail address for sender.", Alert.AlertType.ERROR);
            return;
        }
        if(!validateEmailAddress(toMailAddress)) {
            showAlert("Invalid mail address", "Please fill correct Gmail address for receiver.", Alert.AlertType.ERROR);
            return;
        }
        if(subject == null || subject.isEmpty()) {
            showAlert("Missing subject", "Please fill subject for the mail.", Alert.AlertType.ERROR);
            return;
        }
        if(appPassword == null || appPassword.isEmpty()) {
            showAlert("Missing password", "Please fill app password for authentication purposes.", Alert.AlertType.ERROR);
            return;
        }

        CompletableFuture
                .supplyAsync(() -> EmailService.sendMail(fromMailAddress, appPassword, toMailAddress, subject, body))
                .thenAccept(errorList -> Platform.runLater(() -> { 
                    // need to wrap in Platform to show alert (only possible if it's on main thread)
                    if (errorList.isEmpty()) {
                        String successfulEmailAlertContext = 
                            "Email sent successfully\n" +
                            "from: " + txtFromMailAddress.getText() + "\n" +
                            "to: " + txtToMailAddress.getText();
                        showAlert("Email Sent", successfulEmailAlertContext, Alert.AlertType.INFORMATION);
                    } else {
                        showAlert("Failed to send email", errorList.stream().reduce((s1, s2) -> (s1 + "\n" + s2)).get(), Alert.AlertType.ERROR);
                    }
                }));
    }

    @FXML
    void learnAboutAppPassword(MouseEvent event) {
        URI appPasswordInfoLink;
        try {
            appPasswordInfoLink = new URI("https://support.google.com/accounts/answer/185833?hl=en");
            Desktop.getDesktop().browse(appPasswordInfoLink);
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void navigateToMainPage(MouseEvent event) {
        Stage emailStage = (Stage) txtBody.getScene().getWindow();
        emailStage.close();
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert txtAppPassword != null : "fx:id=\"txtAppPassword\" was not injected: check your FXML file 'EmailView.fxml'.";
        assert txtBody != null : "fx:id=\"txtBody\" was not injected: check your FXML file 'EmailView.fxml'.";
        assert txtFromMailAddress != null : "fx:id=\"txtFromMailAddress\" was not injected: check your FXML file 'EmailView.fxml'.";
        assert txtSubject != null : "fx:id=\"txtSubject\" was not injected: check your FXML file 'EmailView.fxml'.";
        assert txtToMailAddress != null : "fx:id=\"txtToMailAddress\" was not injected: check your FXML file 'EmailView.fxml'.";
    }
}