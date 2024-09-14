package app.controller;

import java.io.IOException;
import java.util.Objects;
import java.util.ResourceBundle;

import app.main.App;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

public class Games {

    @FXML
    private Pane functionPane;
    
    @FXML
    private Pane quizzGame;

    @FXML
    private Pane wordHunt;

    @FXML
    private Pane hangman;

    @FXML
    private void goToQuizzGame(MouseEvent event) {
        goToGame("QuizzGame");
    }

    @FXML
    private void goToWordHunt(MouseEvent event) {
        goToGame("WordHunt");
    }

    @FXML
    private void goToHangman(MouseEvent event) {
        goToGame("Hangman");
    }

    private void goToGame(String game) {
        Parent page;
        ResourceBundle bundle = App.getBundle();
        try {
            page = FXMLLoader.load(Objects.requireNonNull(Games.class.getResource("/controller/" + game + ".fxml")), bundle);
            functionPane.getChildren().clear();
            functionPane.getChildren().add(page);
        }
        catch (IOException e) {
            throw new RuntimeException();
        }
    }
}
