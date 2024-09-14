package app.controller;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;

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
    private TextArea userText, translateText;

    @FXML
    private Button viUserOption, engUserOption, viTransOption,  engTransOption;

    @FXML
    private Button swapButton, userTextSound, translateTextSound, imageButton;

    @FXML
    private Label characterLimit, loadingTranslate;

    private String targetLanguage, sourceLanguage;
    private Button userChoosedButton, userNotChooseButton, transChoosedButton, transNotChooseButton;

    private final int CHARACTER_LIMIT = 2000;
    private final PauseTransition PAUSE = new PauseTransition(Duration.millis(700));

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
            PAUSE.setOnFinished(event -> translate());
            PAUSE.playFromStart();
        }
        );
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
        try {
            TextToSpeech.speakText(userText.getText(), voice(sourceLanguage));
        } catch (IOException e) {
            String title = App.getBundle().getString("title.warning");
            String message = App.getBundle().getString("message.nointernet");
            AlertScreen.showAlert(AlertType.WARNING,title,message);
        }
    }

    @FXML
    private void playTransTextSound(ActionEvent event) {
        try {
            TextToSpeech.speakText(translateText.getText(), voice(targetLanguage));
        } catch (IOException e) {
            String title = App.getBundle().getString("title.warning");
            String message = App.getBundle().getString("message.nointernet");
            AlertScreen.showAlert(AlertType.WARNING,title,message);
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
                System.out.println("The URL for the image file is malformed.");
            }
            catch (URISyntaxException e) {
                System.out.println("An error occurred while processing the image.");
            }
        }
    }

    private void translate() {
        if (userText.getLength() > CHARACTER_LIMIT) {
            String title = App.getBundle().getString("title.warning");
            String message = App.getBundle().getString("message.characterlimit");
            message += " " + CHARACTER_LIMIT + " " + App.getBundle().getString("characters");
            AlertScreen.showAlert(AlertType.WARNING,title,message);
        }
        else if (!userText.getText().isEmpty()) {
            translateText.setText("");
            loadingTranslate.setVisible(true);
            Task<String> translateTask = new Task<>() {
                @Override
                protected String call() throws IOException {
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
                        String title = App.getBundle().getString("title.warning");
                        String message = App.getBundle().getString("message.nointernet");
                        AlertScreen.showAlert(AlertType.WARNING,title,message);
                        loadingTranslate.setVisible(false);
                    });
                }
            };
            new Thread(translateTask).start();
        }
    }

    private String voice(String language){
        if (Objects.equals(language, "en")){
            return "en-us";
        }
        else {
            return "vi-vn";
        }
    }
}
