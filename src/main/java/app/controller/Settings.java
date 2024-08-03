package app.controller;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import app.api.GoogleFormFeedback;
import app.main.App;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

public class Settings {
    
    @FXML
    private Button changeThemeButton;

    private boolean isDarkTheme;

    @FXML
    private Button viButton;

    @FXML
    private Button engButton;

    private boolean chooseEng;

    @FXML
    private TextArea feedbackArea;

    @FXML
    private Button feedbackSendButton;

    @FXML
    private void initialize(){
        isDarkTheme = true;
        try (FileInputStream fis = new FileInputStream("src/main/resources/app/bundle/language_choosed.txt");
            ObjectInputStream ois = new ObjectInputStream(fis)) {
            chooseEng = ois.readBoolean();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(chooseEng){
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
            cssPath = getClass().getResource("/graphic/dark_style/dark_style.css").toExternalForm();
        }
        else{
            cssPath = getClass().getResource("/graphic/light_style/light_style.css").toExternalForm();
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
        chooseEng = false;
        setEnglish(false);
    }

    @FXML
    private void sendFeedback(ActionEvent event){
        String userFeedback = feedbackArea.getText();
        if(userFeedback != ""){
            System.out.println("Feedback: " + userFeedback);
            GoogleFormFeedback.sendFeedback(userFeedback);
        }
    }

    private void setEnglish(boolean choosed){
        try (FileOutputStream fos = new FileOutputStream("src/main/resources/app/bundle/language_choosed.txt");
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeBoolean(choosed);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
