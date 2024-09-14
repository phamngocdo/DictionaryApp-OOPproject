package app.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import app.database.DictionaryDatabase;
import app.main.App;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.util.Pair;
import app.api.TextToSpeech;
import app.base.*;

public class WordExplain {

    private static Pair<Integer, String> item;

    @FXML
    private Label wordLabel, pronounceLabel;

    @FXML
    private TextArea explainArea;

    @FXML
    private Button usSound, ukSound, saveButton, savedButton, editButton, deleteButton;

    private final String BOOKMARK_PATH = "src/main/resources/bookmark/bookmark.txt";

    @FXML
    private void initialize() {
        load();
    }

    @FXML
    private void playUSPronounceSound(ActionEvent event) {
        TextToSpeech.stopSpeaking();
        try {
            TextToSpeech.speakText(wordLabel.getText(), "en-us");
        } catch (IOException e) {
            String title = App.getBundle().getString("title.warning");
            String message = App.getBundle().getString("message.nointernet");
            AlertScreen.showAlert(Alert.AlertType.WARNING,title,message);
        }
    }

    @FXML
    private void playUKPronounceSound(ActionEvent event) {
        TextToSpeech.stopSpeaking();
        try {
            TextToSpeech.speakText(wordLabel.getText(), "en-gb");
        } catch (IOException e) {
            String title = App.getBundle().getString("title.warning");
            String message = App.getBundle().getString("message.nointernet");
            AlertScreen.showAlert(Alert.AlertType.WARNING,title,message);
        }
    }

    @FXML
    private void goToEdit(ActionEvent event) {
        EditWord.setWordToEdit(DictionaryDatabase.getWord(item.getKey()));
        App.getMainScreen().goToEditFunction();
    }

    @FXML
    private void deleteWord(ActionEvent event) {
        String title = App.getBundle().getString("title.confirmation");
        String message = App.getBundle().getString("message.deleteword");
        String confirmText = App.getBundle().getString("button.delete");
        boolean confirm = AlertScreen.showConfirmationAlert(title, message,confirmText);
        if (confirm) {
            DictionaryDatabase.removeWord(DictionaryDatabase.getWord(item.getKey()));
        }
    }

    @FXML
    private void saveToBookmark(ActionEvent event) {
        ArrayList<String> words = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(BOOKMARK_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                words.add(line.trim());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(BOOKMARK_PATH, true))) {
            if (words.add(wordLabel.getText().trim())) {
                writer.write(wordLabel.getText());
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        handleSaveButton(wordLabel.getText());
    }

    @FXML
    private void unsaveFromBookmark(ActionEvent event) {
        ArrayList<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(BOOKMARK_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().equals(wordLabel.getText().trim())) {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(BOOKMARK_PATH))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        handleSaveButton(wordLabel.getText());
    }

    private void load() {
        Word word = DictionaryDatabase.getWord(item.getKey());
        wordLabel.setText(word.getWord());
        pronounceLabel.setText("/" + word.getPronounce() + "/");
        StringBuilder explainText = new StringBuilder();
        ArrayList<Explain> explains = DictionaryDatabase.getAllExplainsFromWord(item.getKey());
        ArrayList<String> processedTypes = new ArrayList<>();
        for (Explain explain : explains) {
            String type = explain.getType();
            if (!processedTypes.contains(type)) {
                explainText.append(type).append("\n");
                processedTypes.add(type);
            }
            explainText.append("\t■ ").append(explain.getMeaning()).append("\n");
            for (Example example : DictionaryDatabase.getAllExamplesFromExplain(explain.getId())) {
                explainText.append("\t\t- ").append(example.getExample()).append("\n\t\t  ").append(example.getTranslate()).append("\n");
            }
        }
        explainArea.setText(explainText.toString());
        handleSaveButton(wordLabel.getText());
    }
    

    private void handleSaveButton(String word) {
        boolean isSaved = false;
        try (BufferedReader reader = new BufferedReader(new FileReader(BOOKMARK_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().equals(word.trim())) {
                    isSaved = true;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (isSaved) {
            savedButton.setVisible(true);
            saveButton.setVisible(false);
        } else {
            savedButton.setVisible(false);
            saveButton.setVisible(true);
        }
    }

    public static void setItem(Pair<Integer, String> itemPair) {
        item = itemPair;
    }
}
