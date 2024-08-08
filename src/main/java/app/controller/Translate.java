package app.controller;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import app.api.GoogleTranslate;
import app.api.ImageToText;
import app.api.TextToSpeech;
import app.main.App;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Translate {

    @FXML
    private TextArea userText;

    @FXML
    private TextArea translateText;

    private String targetLanguage, sourceLanguage;

    @FXML
    private Button viUserOption;
    
    @FXML
    private Button engUserOption; 
    
    @FXML
    private Button viTransOption;
    
    @FXML
    private Button engTransOption;

    private Button userChoosedButton, userNotChooseButton, transChoosedButton, transNotChooseButton;

    @FXML
    private Button swap;

    @FXML
    private Button userTextSound;

    @FXML
    private Button translateTextSound;

    @FXML
    private Button addImage;

    @FXML
    private Label characterLimit;

    private final int CHARACTER_LIMIT = 2000;

    private final PauseTransition pause = new PauseTransition(Duration.millis(700));

    @FXML
    private Label loadingTranslate;

    @FXML
    void initialize(){
        characterLimit.setText("0/" +CHARACTER_LIMIT);
        sourceLanguage = "en";
        userChoosedButton = engUserOption;
        userNotChooseButton = viUserOption;
        transChoosedButton = viTransOption;
        transNotChooseButton = engTransOption;
        targetLanguage = "vi";
        userText.textProperty().addListener((observable,oldValue, newValue)->{
            characterLimit.setText(newValue.length() + "/" + CHARACTER_LIMIT);
            pause.setOnFinished(event -> translate());
            pause.playFromStart();
        }
        );
    }

    @FXML
    private void translate() {
        if (userText.getLength() > CHARACTER_LIMIT) {
            boolean isEnglish = App.getLanguage() == "english";
            String title = isEnglish ? "Character Limit Exceeded" : "Số Kí Tự Vượt Quá Giới Hạn";
            String message = isEnglish ? "Please type no more than " + CHARACTER_LIMIT + " characters" : "Vui lòng nhập không quá " + CHARACTER_LIMIT + " kí tự";
            AlertScreen.showAlert(AlertType.WARNING,title,message);
        } 
        else if (!userText.getText().isEmpty()) {
            translateText.setText("");
            loadingTranslate.setVisible(true);
            Task<String> translateTask = new Task<>() {
                @Override
                protected String call() throws Exception {
                    return GoogleTranslate.translate(userText.getText(), sourceLanguage, targetLanguage);
                }
   
                @Override
                protected void succeeded() {
                    super.succeeded();
                    Platform.runLater(() -> {
                        translateText.setText(getValue());
                        loadingTranslate.setVisible(false);
                    });
                }
   
                @Override
                protected void failed() {
                    super.failed();
                    Platform.runLater(() -> {
                    translateText.setText("Failed");
                    loadingTranslate.setVisible(false);
                    });
                }
            };
          new Thread(translateTask).start();
        }
    }

    @FXML
    private void chooseEngForUser(ActionEvent event){
        sourceLanguage = "en";
        targetLanguage = "vi";
        userChoosedButton = engUserOption;
        userNotChooseButton = viUserOption;
        transChoosedButton = viTransOption;
        transNotChooseButton = engTransOption;
        userText.setText("");
        translateText.setText("");
        changeStyleLanguageButton();
    }

    @FXML
    private void chooseViForUser(ActionEvent event){
        sourceLanguage = "vi";
        targetLanguage = "en";
        userChoosedButton = viUserOption;
        userNotChooseButton = engUserOption;
        transChoosedButton = engTransOption;
        transNotChooseButton = viTransOption;
        userText.setText("");
        translateText.setText("");
        changeStyleLanguageButton();
    }

    @FXML
    private void chooseEngForTrans(ActionEvent event){
        sourceLanguage = "vi";
        targetLanguage = "en";
        userChoosedButton = viUserOption;
        userNotChooseButton = engUserOption;
        transChoosedButton = engTransOption;
        transNotChooseButton = viTransOption;
        userText.setText("");
        translateText.setText("");
        changeStyleLanguageButton();
    }

    @FXML
    private void chooseViForTrans(ActionEvent event){
        sourceLanguage = "en";
        targetLanguage = "vi";
        userChoosedButton = engUserOption;
        userNotChooseButton = viUserOption;
        transChoosedButton = viTransOption;
        transNotChooseButton = engTransOption;
        userText.setText("");
        translateText.setText("");
        changeStyleLanguageButton();
    }

    private void changeStyleLanguageButton(){
        TextToSpeech.stopSpeaking();
        userChoosedButton.getStyleClass().remove("button-choose-language");
        userChoosedButton.getStyleClass().add("button-choosed-language");
        userNotChooseButton.getStyleClass().remove("button-choosed-language");
        userNotChooseButton.getStyleClass().add("button-choose-language");
        transChoosedButton.getStyleClass().remove("button-choose-language");
        transChoosedButton.getStyleClass().add("button-choosed-language");
        transNotChooseButton.getStyleClass().remove("button-choosed-language");
        transNotChooseButton.getStyleClass().add("button-choose-language");

    }

    @FXML
    private void swapLanguage(ActionEvent event){
        String temp = sourceLanguage;
        sourceLanguage = targetLanguage;
        targetLanguage = temp;
        temp = userText.getText();
        userText.setText(translateText.getText());
        translateText.setText(temp);
        Button temp2 = transChoosedButton;
        transChoosedButton = transNotChooseButton;
        transNotChooseButton = temp2;
        temp2 = userChoosedButton;
        userChoosedButton = userNotChooseButton;
        userNotChooseButton = temp2;
        TextToSpeech.stopSpeaking();
        changeStyleLanguageButton();
    }

    @FXML
    private void playUserTextSound(ActionEvent event) {
        TextToSpeech.speakText(userText.getText(), voice(sourceLanguage));
    }

    @FXML
    private void playTransTextSound(ActionEvent event) {
        TextToSpeech.speakText(translateText.getText(), voice(targetLanguage));
    }

    private String voice(String language){
        if (language == "en"){
            return "en-us";
        }
        else {
            return "vi-vn";
        }
    }
    
    @FXML
    private void selectImage(ActionEvent event){
        TextToSpeech.stopSpeaking();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Image");
        fileChooser.getExtensionFilters().add(new ExtensionFilter("ImageFile", "*.png", "*.jpg", "*.bmp"));
        File imageFile = fileChooser.showOpenDialog(new Stage());
        if (imageFile != null){
            URL imageUrl;
            try {
                imageUrl = imageFile.toURI().toURL();
                String text = ImageToText.getText(imageUrl, sourceLanguage);
                userText.setText(text);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                System.out.println("The URL for the image file is malformed.");
            }
            catch (URISyntaxException e) {
                e.printStackTrace();
                System.out.println("An error occurred while processing the image.");
            }
        }
    }
}
