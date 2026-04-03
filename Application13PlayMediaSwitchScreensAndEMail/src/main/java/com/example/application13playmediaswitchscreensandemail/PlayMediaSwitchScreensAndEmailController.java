/**
 * Sample Skeleton for 'PlayMediaSwitchScreensAndEmailView.fxml' Controller Class
 */

package com.example.application13playmediaswitchscreensandemail;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.media.MediaView;

public class PlayMediaSwitchScreensAndEmailController {

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="mdVideoPlayer"
    private MediaView mdVideoPlayer; // Value injected by FXMLLoader

    @FXML
    void navigateToEmailPage(MouseEvent event) {
        // if a new window is not needed, then switch scenes on the same stage using txtBody.getScene()
        try {
            Parent emailRoot = FXMLLoader.load(getClass().getResource("EmailView.fxml"));
            Scene emailScene = new Scene(emailRoot);
            Stage emailStage = new Stage();
            emailStage.setTitle("Email Orbit");
            emailStage.setScene(emailScene);
            emailStage.initModality(Modality.APPLICATION_MODAL); // doesn't let you open another email window
            emailStage.showAndWait();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    

    // Tip: singleton audioClip can optimise this event
    @FXML
    void playAudioClip(MouseEvent event) {
        // playing audio using MediaPlayer(heavy library) and not AudioClip (light-weight)
        URL audioUrl = getClass().getResource("media/audioclip.mp3");
        Media audioMedia = new Media(audioUrl.toString());
        MediaPlayer mediaPlayer = new MediaPlayer(audioMedia);
        mediaPlayer.play();
    }

    // Tip: singleton audioClip can optimise this event
    @FXML
    void playNotification(MouseEvent event) {
        URL audioUrl = getClass().getResource("media/notification.mp3");
        AudioClip audioClip = new AudioClip(audioUrl.toString());
        audioClip.play();
    }

    @FXML
    void playVideoClip(MouseEvent event) {
        URL videoUrl = getClass().getResource("media/nosoundvideoclip.mp4");
        Media videoMedia = new Media(videoUrl.toString());
        MediaPlayer mediaPlayer = new MediaPlayer(videoMedia);
        mdVideoPlayer.setMediaPlayer(mediaPlayer);
        mediaPlayer.play();
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert mdVideoPlayer != null : "fx:id=\"mdVideoPlayer\" was not injected: check your FXML file 'PlayMediaSwitchScreensAndEmailView.fxml'.";

        // PTR:
        // javafx-media maven dependency from org.openjfx is required here for AudioClip, MediaPlayer and MediaView.
        // * add it in VM Options too along with other modules
        //      --add-modules javafx.controls,javafx.fxml,javafx.media
        // * to remove warning add
        //  --enable-native-access=javafx.media

        // Future development ideas:
        // 1. add button to stop audio
        // 2. add buttons to stop, resume or reset video
    }
}