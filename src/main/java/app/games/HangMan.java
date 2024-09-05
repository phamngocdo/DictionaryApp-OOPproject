package app.games;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import app.controller.AlertScreen;
import app.main.App;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.util.Duration;

public class Hangman {
    @FXML
    private Pane topicPane, gamePane;

    @FXML
    private Pane animal, weather, music, humanBody, technology, foodAndDrink, nature, transportation;

    @FXML
    private HBox hbox1, hbox2, hbox3;

    @FXML
    private Label timeLabel, scoreLabel1, scoreLabel2, wordLabel;

    @FXML
    private Button playAgainButton;

    @FXML
    private Circle head;

    @FXML
    private Line body, leftArm, rightArm, leftLeg, rightLeg;

    private String topic, nextBodyPart, currentWord;

    private int score, currentWordIndex;

    private ArrayList<String> wordList = new ArrayList<>();

    private final int TIME_SECONDS = 300;

    private final String TOPIC_PATH = "src/main/resources/games/hangman_words.txt";


    @FXML
    private void initialize() {
        loadTopicChoice();
    }

    @FXML
    private void chooseAnimal(MouseEvent event) {
        topic = "Animal";
        loadGame();
    }

    @FXML
    private void chooseWeather(MouseEvent event) {
        topic = "Weather";
        loadGame();
    }

    @FXML
    private void chooseNature(MouseEvent event) {
        topic = "Nature";
        loadGame();
    }

    @FXML
    private void chooseHumanBody(MouseEvent event) {
        topic = "Human Body";
        loadGame();
    }

    @FXML
    private void chooseTechnology(MouseEvent event) {
        topic = "Technology";
        loadGame();
    }

    @FXML
    private void chooseTransportation(MouseEvent event) {
        topic = "Transportation";
        loadGame();
    }

    @FXML
    private void chooseFoodAndDrink(MouseEvent event) {
        topic = "Food and drink";
        loadGame();
    }

    @FXML
    private void chooseMusic(MouseEvent event) {
        topic = "Music";
        loadGame();
    }

    @FXML
    private void chooseSport(MouseEvent event) {
        topic = "Sport";
        loadGame();
    }

    @FXML
    private void playAgain(ActionEvent event) {
        loadTopicChoice();
    }

    private void loadTopicChoice() {
        topicPane.setVisible(true);
        gamePane.setVisible(false);
        scoreLabel2.setVisible(false);
        playAgainButton.setVisible(false);
    }

    private void loadGame() {
        topicPane.setVisible(false);
        gamePane.setVisible(true);

        TimeCountDown.initialize(TIME_SECONDS, 
            () -> {
                timeLabel.setText(TimeCountDown.getFormattedTime());
            }, 
            () -> {
                finishGame();
                Platform.runLater(() -> {
                    boolean isEnglish = App.getLanguage() == "english";
                    String title = isEnglish ? "Time out" : "Hết thời gian";
                    String message = isEnglish ?  "Time's up! The game has ended." : "Đã hết thời gian";
                    AlertScreen.showAlert(AlertType.INFORMATION, title, message);
                });
            });
        timeLabel.setText(TimeCountDown.getFormattedTime());
        TimeCountDown.start();

        try (BufferedReader reader = new BufferedReader(new FileReader(TOPIC_PATH))) {
            String line;
            boolean isTopicFound = false;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("#")) {
                    if (line.substring(1).trim().equalsIgnoreCase(topic)) {
                        isTopicFound = true;
                    } 
                    else if (isTopicFound) {
                        break;
                    }
                } 
                else if (isTopicFound && !line.isEmpty()) {
                    line.toUpperCase();
                    wordList.add(line);
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        Collections.shuffle(wordList);

        score = 0;
        currentWordIndex = 0;
        playWord();
    }

    private void loadKeyboard() {
        hbox1.getChildren().clear();
        hbox2.getChildren().clear();
        hbox3.getChildren().clear();
        String allCharacter = "qwertyuiopasdfghjklzxcvbnm";
        for (int i = 0; i < allCharacter.length(); i++) {
            Button character = new Button();
            character.setText(allCharacter.charAt(i) + "");
            character.getStyleClass().add("keycap");
            character.setOnAction(event -> chooseCharacter(character));
            
            if (hbox1.getChildren().size() < 10) {
                hbox1.getChildren().add(character);
            }
            else if (hbox2.getChildren().size() < 9) {
                hbox2.getChildren().add(character);
            }
            else {
                hbox3.getChildren().add(character);
            }
        }
    }

    private void playWord() {
        head.setVisible(false);
        body.setVisible(false);
        leftArm.setVisible(false);
        rightArm.setVisible(false);
        leftLeg.setVisible(false);
        rightLeg.setVisible(false);
        scoreLabel1.setText(String.valueOf(score));
        loadKeyboard();
        if (currentWordIndex < wordList.size()) {

            currentWord = wordList.get(currentWordIndex);
            wordLabel.setText(("_  ".repeat(currentWord.length())).trim()); 
            nextBodyPart = "head"; 
        } 
        else {
            finishGame();
        }
    }

    private void chooseCharacter(Button character) {
        character.setDisable(true);
        String guessedChar = character.getText();
        boolean correctGuess = false;
    
        if (currentWord.contains(guessedChar)) {
            StringBuilder updatedWord = new StringBuilder(wordLabel.getText());
            for (int i = 0; i < currentWord.length(); i++) {
                if (currentWord.charAt(i) == guessedChar.charAt(0)) {
                    updatedWord.setCharAt(i * 3, guessedChar.charAt(0));
                    correctGuess = true;
                }
            }
            wordLabel.setText(updatedWord.toString());
        } else {
            drawHangman();
            score -= 2;
            scoreLabel1.setText(String.valueOf(score));
            correctGuess = false;
        }
        if (correctGuess) {
            score += 2;
            scoreLabel1.setText(String.valueOf(score));
            if (!wordLabel.getText().contains("_")) {
                PauseTransition pause = new PauseTransition(Duration.seconds(1));
                pause.setOnFinished(event -> {
                    currentWordIndex++;
                    if (currentWordIndex < wordList.size()) {
                        playWord();
                    } else {
                        finishGame();
                    }
                });
                pause.play();
            }
        } else {
            if (nextBodyPart.equals("done")) {
                PauseTransition pause = new PauseTransition(Duration.seconds(1));
                pause.setOnFinished(event -> {
                    currentWordIndex++;
                    if (currentWordIndex < wordList.size()) {
                        playWord();
                    } else {
                        finishGame();
                    }
                });
                pause.play();
            }
        }
    }

    private void drawHangman() {
        switch (nextBodyPart) {
            case "head":
                head.setVisible(true);
                nextBodyPart = "body";
                break;
        
            case "body":
                body.setVisible(true);
                nextBodyPart = "leftarm";
                break;

            case "leftarm":
                leftArm.setVisible(true);
                nextBodyPart = "rightarm";
                break;
            
            case "rightarm":
                rightArm.setVisible(true);
                nextBodyPart = "leftleg";
                break;
                
            case "leftleg":
                leftLeg.setVisible(true);
                nextBodyPart = "rightleg";
                break;
                
            case "rightleg":
                rightLeg.setVisible(true);
                nextBodyPart = "done";
                break;
        }
    }

    private void finishGame() {
        gamePane.setVisible(false);
        scoreLabel2.setVisible(true);
        scoreLabel2.setText(String.valueOf(score));
        playAgainButton.setVisible(true);
    }
}