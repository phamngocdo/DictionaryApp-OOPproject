package app.controller;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Objects;

import app.api.GoogleFormFeedback;
import app.main.App;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

public class Settings {
    
    @FXML
    private Button changeThemeButton, viButton, engButton;

    @FXML
    private TextArea feedbackArea;

    @FXML
    private Button feedbackSendButton;

    private boolean isDarkTheme;
    private boolean isEnglish;

    @FXML
    private void initialize(){
        isDarkTheme = true;
        try (FileInputStream fis = new FileInputStream("src/main/resources/bundle/language_choosed.txt");
            ObjectInputStream ois = new ObjectInputStream(fis)) {
            isEnglish = ois.readBoolean();
        } catch (IOException e) {
            throw new RuntimeException();
        }
        if(isEnglish){
            engButton.getStyleClass().remove("button-choose-language");
            engButton.getStyleClass().add("button-choosed-language");
            viButton.getStyleClass().remove("button-choosed-language");
            viButton.getStyleClass().add("button-choose-language");
        }
        else{
            engButton.getStyleClass().remove("button-choosed-language");
            engButton.getStyleClass().add("button-choose-language");
            viButton.getStyleClass().remove("button-choose-language");
            viButton.getStyleClass().add("button-choosed-language");
        }
    }
    @FXML
    private void changeTheme(ActionEvent event){
        isDarkTheme = !isDarkTheme;
        String cssPath;
        if(isDarkTheme){
            cssPath = Objects.requireNonNull(Settings.class.getResource("/graphic/dark_style/dark_style.css")).toExternalForm();
        }
        else{
            cssPath = Objects.requireNonNull(Settings.class.getResource("/graphic/light_style/light_style.css")).toExternalForm();
        }
        App.setSceneStyle(cssPath);
    }

    @FXML
    private void changeToEng(ActionEvent event){
        App.setBundle("english");
        setEnglish(true);
    }

    @FXML
    private void changeToVi(ActionEvent event){
        App.setBundle("vietnamese");
        isEnglish = false;
        setEnglish(false);
    }

    @FXML
    private void sendFeedback(ActionEvent event){
        String userFeedback = feedbackArea.getText();
        if(!Objects.equals(userFeedback, "")){
            System.out.println("Feedback: " + userFeedback);
            try {
                GoogleFormFeedback.sendFeedback(userFeedback);
                String title = "";
                String message = App.getBundle().getString("message.sendfeedbacksuccess");
                AlertScreen.showAlert(Alert.AlertType.INFORMATION,title,message);
            } catch (IOException e) {
                String title = "";
                String message = App.getBundle().getString("message.nointernet");
                AlertScreen.showAlert(Alert.AlertType.WARNING,title,message);
            }
        }
    }

    private void setEnglish(boolean choosed){
        try (FileOutputStream fos = new FileOutputStream("src/main/resources/bundle/language_choosed.txt");
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeBoolean(choosed);
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }
}
