package app.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import app.api.TextToSpeech;
import app.base.Example;
import app.base.Explain;
import app.base.Word;
import app.database.DictionaryDatabase;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.util.Pair;

public class WordExplain {

    public static Pair<Integer, String> item;

    private static DictionaryDatabase data = new DictionaryDatabase(Dictionary.class.getResource("/database/dictionary.db").getPath());;

    @FXML
    private Label wordLabel;

    @FXML
    private Label pronounceLabel;

    @FXML
    private Label notFound;

    @FXML
    private TextArea explainArea;

    @FXML
    private Button usSound;

    @FXML
    private Button ukSound;

    @FXML
    private Button save;

    @FXML
    private Button saved;

    private final String BOOKMARK_PATH = "src/main/resources/bookmark/bookmark.txt";

    @FXML
    private void initialize() {
        Word word = data.getWord(item.getKey());
        wordLabel.setText(word.getWord());
        pronounceLabel.setText("/" + word.getPronounce() + "/");
        StringBuilder explainText = new StringBuilder();
        for (Explain explain : data.getAllExplainsFromWord(item.getKey())) {
            explainText.append(explain.getType()).append("\n\t■ ").append(explain.getMeaning()).append("\n");
            for (Example example : data.getAllExamplesFromExplain(explain.getId())) {
                explainText.append("\t\t- ").append(example.getExample()).append("\n\t\t  ").append(example.getTranslate()).append("\n");
            }
        }
        explainArea.setText(explainText.toString());
        handleSaveButton(item.getValue());
    }

    @FXML
    private void playUSPronounceSound(ActionEvent event) {
        TextToSpeech.speakText(wordLabel.getText(), "en-us");
    }

    @FXML
    private void playUKPronounceSound(ActionEvent event) {
        TextToSpeech.speakText(wordLabel.getText(), "en-gb");
    }

    @FXML
    private void saveWord(ActionEvent event) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(BOOKMARK_PATH))){
            writer.write(wordLabel.getText());
            writer.newLine();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        handleSaveButton(wordLabel.getText());
    }

    @FXML
    private void unsaveWord(ActionEvent event) {
        ArrayList<String> lines = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(BOOKMARK_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.equals(wordLabel.getText())) {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(BOOKMARK_PATH))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        handleSaveButton(wordLabel.getText());
    }
 
    private void handleSaveButton(String word){
        boolean isSav = false;
        try (BufferedReader reader = new BufferedReader(new FileReader(BOOKMARK_PATH))){
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().equals(word)) { 
                    isSav = true;
                }
            }
            } 
            catch (IOException e) {
                e.printStackTrace();
            }
        if (isSav) {
            saved.setVisible(true);
            save.setVisible(false);
        }
        else {
            saved.setVisible(false);
            save.setVisible(true);
        }
     }
}
