package app.games;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import app.controller.AlertScreen;
import app.main.App;
import app.trie.Trie;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class WordsHunt {

    @FXML
    private Pane gamePane;

    @FXML
    private GridPane gridPane;

    @FXML
    private Button rotateButton, playAgainButton;

    @FXML
    private VBox wordList;

    @FXML
    private Label wordLabel, timeLabel, scoreLabel1, scoreLabel2;

    private final int MATRIX_SIZE = 10;

    private final int TIME_SECONDS = 300;

    private char[][] randomCharMatrix = new char[MATRIX_SIZE][MATRIX_SIZE];

    private SimpleStringProperty currentWord = new SimpleStringProperty();

    private boolean isDragging = false;

    private int score;

    private Set<String> wordSet = new HashSet<>();

    private List<StackPane> draggedPanes = new ArrayList<>();

    @FXML
    private void initialize() {
        loadGame();
    }

    @FXML
    private void rotateMatrix() {
        randomMatrix();
    }

    @FXML
    private void playAgain() {
        loadGame();
    }

    private void loadGame() {
        gamePane.setVisible(true);
        scoreLabel2.setVisible(false);
        playAgainButton.setVisible(false);

        TimeCountDown.initialize(TIME_SECONDS, 
            () -> timeLabel.setText(TimeCountDown.getFormattedTime()), 
            () -> {
                finishGame();
                Platform.runLater(() -> {
                    String title = "";
                    String message = App.getBundle().getString("message.endgame");
                    AlertScreen.showAlert(AlertType.INFORMATION, title, message);
                });
            });
        timeLabel.setText(TimeCountDown.getFormattedTime());
        TimeCountDown.start();

        wordLabel.textProperty().bind(currentWord);
        loadGridPane();
        gridPane.setOnMousePressed(this::handleMousePressed);
        gridPane.setOnMouseDragged(this::handleMouseDragged);
        gridPane.setOnMouseReleased(this::handleMouseReleased);

        wordSet.clear();
        wordList.getChildren().clear();
    }

    private void loadGridPane() {
        for (int i = 0; i < MATRIX_SIZE; i++) {
            for (int j = 0; j < MATRIX_SIZE; j++) {
                StackPane pane = new StackPane();
                pane.getStyleClass().add("choice-pane");

                Label label = new Label();
                label.getStyleClass().add("matrix-character");

                pane.getChildren().add(label);
                gridPane.add(pane, j, i);
            }
        }
        randomMatrix();
    }

    private void handleMousePressed(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            Node node = getNodeByMousePosition(event.getX(), event.getY());
            if (node != null) {
                isDragging = true;
                currentWord.set("");
                draggedPanes.clear();  
                processNodeSelection(node);
            }
        }
    }

    private void handleMouseDragged(MouseEvent event) {
        if (isDragging) {
            Node node = getNodeByMousePosition(event.getX(), event.getY());
            if (node != null) {
                processNodeSelection(node);
            }
        }
    }

    private void handleMouseReleased(MouseEvent event) {
        isDragging = false;
        for (Node node : gridPane.getChildren()) {
            if (node instanceof StackPane) {
                node.getStyleClass().clear();
                node.getStyleClass().add("choice-pane");
            }
        }
        checkCorrectWord();
        currentWord.set("");
    }

    private Node getNodeByMousePosition(double x, double y) {
        for (Node node : gridPane.getChildren()) {
            if (node.getBoundsInParent().contains(x, y)) {
                return node;
            }
        }
        return null;
    }

    private void processNodeSelection(Node node) {
        StackPane pane = (StackPane) node;
        if (!draggedPanes.contains(pane)) {
            pane.getStyleClass().clear();
            pane.getStyleClass().add("choice-pane-choosed");
            Label label = (Label) pane.getChildren().get(0);
            addChar(label.getText().charAt(0));
            draggedPanes.add(pane);  
        } 
        else if (draggedPanes.indexOf(pane) != draggedPanes.size() - 1) {
            int removeFromIndex = draggedPanes.indexOf(pane) + 1;
            removeCharsFromIndex(removeFromIndex);
        }
    }

    private void addChar(char ch) {
        if (currentWord.get() != null) {
            String prev = currentWord.get();
            currentWord.set(prev + " " + ch);
        } else {
            currentWord.set(ch + "");
        }
    }

    private void removeCharsFromIndex(int index) {
        for (int i = index; i < draggedPanes.size(); i++) {
            StackPane pane = draggedPanes.get(i);
            pane.getStyleClass().clear();
            pane.getStyleClass().add("choice-pane");
        }
        String newWord = currentWord.get().substring(0, index * 2);
        currentWord.set(newWord.trim());  
        draggedPanes.subList(index, draggedPanes.size()).clear();
    }
    
    private void randomMatrix() {
        Random random = new Random();
        for (int i = 0; i < MATRIX_SIZE; i++) {
            for (int j = 0; j < MATRIX_SIZE; j++) {
                randomCharMatrix[i][j] = (char) (random.nextInt(26) + 'A');
            }
        }
        for (Node node : gridPane.getChildren()) {
            if (node instanceof StackPane) {
                int row = GridPane.getRowIndex(node);
                int col = GridPane.getColumnIndex(node);
                Label label = (Label) ((StackPane) node).getChildren().get(0);
                label.setText(randomCharMatrix[row][col] + "");
            }
        }
    }

    private void checkCorrectWord() {
        String word = currentWord.get().replaceAll(" ", "");
        word = word.toLowerCase();
        if (!wordSet.contains(word) && !word.isEmpty()) {
            if (word.length() > 1 && Trie.getWord(word) != null) {
                score += 10;
                wordSet.add(word);
                Label label = new Label(currentWord.get());
                label.getStyleClass().add("general-text");
                label.setStyle("-fx-font-weight:bold;" + "-fx-font-size:17;");
                wordList.getChildren().add(label);
            } 
            else {
                score -= 5;
            }
        }
        scoreLabel1.setText(String.valueOf(score));
    }

    private void finishGame() {
        gamePane.setVisible(false);
        scoreLabel2.setVisible(true);
        playAgainButton.setVisible(true);
    }
}
