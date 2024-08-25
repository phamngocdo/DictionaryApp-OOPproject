package app.games;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import app.controller.AlertScreen;
import app.main.App;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

public class QuizzGame {

    @FXML
    private Label timeLabel, scoreLabel;

    @FXML
    private Pane playPane;

    @FXML
    private Pane questionPane, answerAPane, answerBPane, answerCPane, answerDPane;

    @FXML
    private Label questionLabel, answerA, answerB, answerC, answerD;

    @FXML
    private HBox questionHBox1, questionHBox2;

    @FXML
    private Button submitButton;

    @FXML
    private Button playAgainButton;

    private int currentQuestion;

    private ArrayList<Question> allQuestions = new ArrayList<>();

    private ArrayList<Question> randQuestions = new ArrayList<>();

    private ArrayList<Character> userAnswer = new ArrayList<>();

    private final int TIME_SECONDS = 300;

    private final int QUESTIONS_TOTAL = 20;

    private final String QUESTIONS_PATH = "src/main/resources/games/quizz_game.txt";

    @FXML
    private void initialize() {
        scoreLabel.setVisible(false);
        playAgainButton.setVisible(false);
        playPane.setVisible(true);
        loadAllQuestions();
        loadGame();
    }

    @FXML
    private void playAgain(ActionEvent event) {
        loadGame();
    }

    @FXML
    private void submitGame(ActionEvent event) {
        boolean isEnglish = App.getLanguage() == "english";
        String title = isEnglish ? "Submit confirm" : "Xác nhận nộp bài";
        String message = isEnglish ?  "Are you to submit?" : "Bạn có chắc muốn nộp bài không?";
        String confirmText = isEnglish ? "Submit" : "Xác nhận";
        boolean confirm = AlertScreen.showConfirmationAlert(title, message,confirmText);
        if (confirm) {
            finishGame();
        }
    }

    @FXML
    private void chooseAnswerA(MouseEvent event) {
        userAnswer.set(currentQuestion, 'A');
        answerAPane.getStyleClass().clear();
        answerAPane.getStyleClass().add("choice-pane-choosed");
        answerBPane.getStyleClass().clear();
        answerBPane.getStyleClass().add("choice-pane");
        answerCPane.getStyleClass().clear();
        answerCPane.getStyleClass().add("choice-pane");
        answerDPane.getStyleClass().clear();
        answerDPane.getStyleClass().add("choice-pane");
    }

    @FXML
    private void chooseAnswerB() {
        userAnswer.set(currentQuestion, 'B');
        answerAPane.getStyleClass().clear();
        answerAPane.getStyleClass().add("choice-pane");
        answerBPane.getStyleClass().clear();
        answerBPane.getStyleClass().add("choice-pane-choosed");
        answerCPane.getStyleClass().clear();
        answerCPane.getStyleClass().add("choice-pane");
        answerDPane.getStyleClass().clear();
        answerDPane.getStyleClass().add("choice-pane");
    }

    @FXML
    private void chooseAnswerC() {
        userAnswer.set(currentQuestion, 'C');
        answerAPane.getStyleClass().clear();
        answerAPane.getStyleClass().add("choice-pane");
        answerBPane.getStyleClass().clear();
        answerBPane.getStyleClass().add("choice-pane");
        answerCPane.getStyleClass().clear();
        answerCPane.getStyleClass().add("choice-pane-choosed");
        answerDPane.getStyleClass().clear();
        answerDPane.getStyleClass().add("choice-pane");
    }

    @FXML
    private void chooseAnswerD() {
        userAnswer.set(currentQuestion, 'D');
        answerAPane.getStyleClass().clear();
        answerAPane.getStyleClass().add("choice-pane");
        answerBPane.getStyleClass().clear();
        answerBPane.getStyleClass().add("choice-pane");
        answerCPane.getStyleClass().clear();
        answerCPane.getStyleClass().add("choice-pane");
        answerDPane.getStyleClass().clear();
        answerDPane.getStyleClass().add("choice-pane-choosed");
    }

    private void loadGame() {
        submitButton.setVisible(true);
        playAgainButton.setVisible(false);
        answerA.setDisable(false);
        answerB.setDisable(false);
        answerC.setDisable(false);
        answerD.setDisable(false);
        TimeCountDown.initialize(TIME_SECONDS, 
            () -> {
                timeLabel.setText(TimeCountDown.getFormattedTime());
            }, 
            () -> {
                finishGame();
                Platform.runLater(() -> {
                    boolean isEnglish = App.getLanguage() == "english";
                    String title = isEnglish ? "Time out" : "Hết thời gian";
                    String message = isEnglish ?  "Time's up! The quizz has ended." : "Đã hết thời gian làm bài quizz";
                    AlertScreen.showAlert(AlertType.INFORMATION, title, message);
                });
            });
        timeLabel.setText(TimeCountDown.getFormattedTime());
        TimeCountDown.start();

        loadRandomQuestion(QUESTIONS_TOTAL);
        currentQuestion = 0;
        
        userAnswer.clear();
        for (int i = 0; i < QUESTIONS_TOTAL; i++) {
            userAnswer.add(null);
        }

        questionHBox1.getChildren().clear();
        questionHBox2.getChildren().clear();
        for (int i = 0; i < randQuestions.size(); i++) {
            Button button = new Button();
            button.setText(String.valueOf(i + 1));
            button.setOnAction(event -> chooseQuestionFromHBox(button));
            button.getStyleClass().add("question-button");
            if (i < QUESTIONS_TOTAL / 2) {
                questionHBox1.getChildren().add(button);
            }
            else {
                questionHBox2.getChildren().add(button);
            }
        }

        getCurrentQuestionButton().getStyleClass().clear();
        getCurrentQuestionButton().getStyleClass().add("question-button-choosed");
        displayQuestion();
    }

    private void chooseQuestionFromHBox(Button button) {
        getCurrentQuestionButton().getStyleClass().clear();
        getCurrentQuestionButton().getStyleClass().add("question-button");
        currentQuestion = Integer.parseInt(button.getText()) - 1;
        getCurrentQuestionButton().getStyleClass().clear();
        getCurrentQuestionButton().getStyleClass().add("question-button-choosed");
        displayQuestion();
    }

    private Button getCurrentQuestionButton() {
        if (currentQuestion < 10) {
            return (Button) questionHBox1.getChildren().get(currentQuestion);
        }
        return (Button) questionHBox2.getChildren().get(currentQuestion - 10);
    }

    private void displayQuestion() {
        Question question = randQuestions.get(currentQuestion);
        questionLabel.setText(currentQuestion + 1 + question.getQuestion());
        answerA.setText(question.getChoices().get(0));
        answerAPane.getStyleClass().clear();
        answerAPane.getStyleClass().add("choice-pane");
        answerB.setText(question.getChoices().get(1));
        answerBPane.getStyleClass().clear();
        answerBPane.getStyleClass().add("choice-pane");
        answerC.setText(question.getChoices().get(2));
        answerCPane.getStyleClass().clear();
        answerCPane.getStyleClass().add("choice-pane");
        answerD.setText(question.getChoices().get(3));
        answerDPane.getStyleClass().clear();
        answerDPane.getStyleClass().add("choice-pane");

        if (userAnswer.get(currentQuestion) != null) {
            if (userAnswer.get(currentQuestion) == 'A') {
                answerAPane.getStyleClass().clear();
                answerAPane.getStyleClass().add("choice-pane-choosed");
            }
            else if (userAnswer.get(currentQuestion) == 'B') {
                answerBPane.getStyleClass().clear();
                answerBPane.getStyleClass().add("choice-pane-choosed");
            }
            else if (userAnswer.get(currentQuestion) == 'C') {
                answerCPane.getStyleClass().clear();
                answerCPane.getStyleClass().add("choice-pane-choosed");
            }
            else if (userAnswer.get(currentQuestion) == 'D') {
                answerDPane.getStyleClass().clear();
                answerDPane.getStyleClass().add("choice-pane-choosed");
            }
        }
    }
    
    private void loadRandomQuestion(int numberOfQuestions) {
        randQuestions.clear();
        Set<Integer> randomNumbers = new HashSet<>();
        Random random = new Random();
        while (randomNumbers.size() < numberOfQuestions) {
            int number = random.nextInt(allQuestions.size()); 
            randomNumbers.add(number); 
        }
        for (int rand : randomNumbers) {
            randQuestions.add(allQuestions.get(rand));
        }
    }

    private void loadAllQuestions() {
        try (BufferedReader reader = new BufferedReader(new FileReader(QUESTIONS_PATH))) {
            String line;
            String question = "";
            ArrayList<String> choices = new ArrayList<>();
            char correctAnswer = ' ';
            boolean isReadingQuestion = true;

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Answer")) {
                    correctAnswer = line.charAt(8);
                    allQuestions.add(new Question(question, choices, correctAnswer));
                    question = "";
                    choices = new ArrayList<>();
                    isReadingQuestion = true;
                } 
                else if (line.startsWith("A") || line.startsWith("B") || line.startsWith("C") || line.startsWith("D")) {
                    choices.add(line);
                    isReadingQuestion = false;
                } 
                else if (isReadingQuestion) {
                    question = line.replaceAll("\\d+", "").trim();
                    isReadingQuestion = false;
                }
            }
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void finishGame() {
        TimeCountDown.stop();
        scoreLabel.setVisible(true);
        playAgainButton.setVisible(true);
        scoreLabel.setText(String.valueOf(checkScore()) + " / " + String.valueOf(randQuestions.size()));
        playPane.setVisible(false);
    }

    private int checkScore() {
        int score = 0;
        for (int i = 0; i < randQuestions.size(); i++) {
            if (userAnswer.get(i) != null && userAnswer.get(i) == randQuestions.get(i).getCorrectAnswer()) {
                score ++;
            }
        }
        return score;
    }

    private class Question {
        private String question;
        private ArrayList<String> choices;
        private char correctAnswer;

        public Question(String question, ArrayList<String> choices, char correctAnswer) {
            this.question = question;
            this.choices = choices;
            this.correctAnswer = correctAnswer;
        }

        public String getQuestion() {
            return question;
        }

        public ArrayList<String> getChoices() {
            return choices;
        }

        public char getCorrectAnswer() {
            return correctAnswer;
        }
    }
}