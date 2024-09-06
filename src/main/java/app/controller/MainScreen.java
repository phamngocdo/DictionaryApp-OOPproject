package app.controller;

import java.io.IOException;
import java.util.ResourceBundle;

import app.api.TextToSpeech;
import app.main.App;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;

public class MainScreen {

    @FXML
    private Button homeButton, dictionaryButton, translateButton, addButton, bookmarkButton, gamesButton, settingsButton;

    @FXML
    private Pane functionPane, mainPane, controllPane;

    
    private Button currentClickedButton;

    @FXML
    private void initialize(){
        currentClickedButton = homeButton;
        goToFunction("Home", homeButton);
    }

    @FXML
    private void goToHomePage(ActionEvent event) {
        goToFunction("Home", homeButton);
        currentClickedButton = homeButton;
    }

    @FXML
    private void goToDictionary(ActionEvent event) {
        goToFunction("Dictionary", dictionaryButton);
        currentClickedButton = dictionaryButton;
    }

    @FXML
    private void goToTranslate(ActionEvent event) {
        goToFunction("Translate", translateButton);
        currentClickedButton = translateButton;
    }

    @FXML
    private void goToAddWord(ActionEvent event) {
        EditWord.setWordToEdit(null);
        goToFunction("EditWord", addButton);
        currentClickedButton = addButton;
    }

    @FXML
    private void goToBookmark(ActionEvent event) {
        goToFunction("Bookmark", bookmarkButton);
        currentClickedButton = bookmarkButton;
    }

    @FXML
    private void goToGames(ActionEvent event) {
        goToFunction("Games", gamesButton);
        currentClickedButton = gamesButton;
    }

    @FXML
    private void goToSettings(ActionEvent event) {
        goToFunction("Settings", settingsButton);
        currentClickedButton = settingsButton;
    }

    private void goToFunction(String name, Button button) {
        TextToSpeech.stopSpeaking();
        Parent page;
        try {
            ResourceBundle bundle = App.getBundle();
            page = FXMLLoader.load(App.class.getResource("/controller/" + name + ".fxml"), bundle);
            functionPane.getChildren().clear();
            functionPane.getChildren().add(page);
            currentClickedButton.getStyleClass().remove("controll-button-clicked");
            currentClickedButton.getStyleClass().add("controll-button");
            button.getStyleClass().remove("controll-button");
            button.getStyleClass().add("controll-button-clicked");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void goToEditFunction() {
        TextToSpeech.stopSpeaking();
        Parent page;
        ResourceBundle bundle = App.getBundle();
        try {
            page = FXMLLoader.load(App.class.getResource("/controller/EditWord.fxml"), bundle);
            functionPane.getChildren().clear();
            functionPane.getChildren().add(page);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void turnOffEditFunction(String previousFunction) {
        Parent page;
        ResourceBundle bundle = App.getBundle();
        try {
            page = FXMLLoader.load(App.class.getResource("/controller/" + previousFunction + ".fxml"), bundle);
            functionPane.getChildren().clear();
            functionPane.getChildren().add(page);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
