package app.controller;
import app.api.TextToSpeech;
import app.database.DictionaryDatabase;
import app.main.App;
import app.trie.Trie;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.util.Pair;

public class Bookmark {

    @FXML
    private HBox columnName;

    @FXML
    private Pane explain;

    @FXML
    private ListView<Pair<Integer, String>> wordList;

    private ArrayList<Pair<Integer, String>> list = new ArrayList<>();

    @FXML
    private Button backButton;

    private static final String BOOKMARK_PATH = "src/main/resources/bookmark/bookmark.txt";

    @FXML
    private void initialize() {
        backButton.setVisible(false);
        explain.setVisible(false);
        columnName.setVisible(true);
        loadList();
        wordList.setVisible(true);
    }

    @FXML
    private void goBack(ActionEvent event) {
        backButton.setVisible(false);
        explain.setVisible(false);
        columnName.setVisible(true);
        wordList.setVisible(true);
        loadList();
    }

    private void loadList() {
        list.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(BOOKMARK_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Pair<Integer, String> word = Trie.getWord(line);
                if (word != null) {
                    list.add(word);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        ObservableList<Pair<Integer, String>> items = FXCollections.observableArrayList(list);
        wordList.setCellFactory(listview -> new BookmarkItem());
        wordList.setItems(items);
   
    }

    private class BookmarkItem extends ListCell<Pair<Integer, String>> {

        private HBox hbox;
        private Label word, pronouce;
        private ImageView soundImage, infoImage;
        private Button ukSound, usSound, fullExplainView;
    
        public BookmarkItem() {
            hbox = new HBox();
            hbox.getStyleClass().add("cell-hbox");

            word = new Label();
            word.getStyleClass().add("word");

            pronouce = new Label();
            pronouce.getStyleClass().add("pronounce");

            usSound = new Button("US");
            usSound.getStyleClass().add("general-button");
            soundImage = new ImageView();
            soundImage.getStyleClass().add("speaker-icon");
            usSound.setGraphic(soundImage);

            ukSound = new Button("UK");
            ukSound.getStyleClass().add("general-button");
            soundImage = new ImageView();
            soundImage.getStyleClass().add("speaker-icon");
            ukSound.setGraphic(soundImage);

            fullExplainView = new Button();
            fullExplainView.getStyleClass().add("general-button");
            infoImage = new ImageView();
            infoImage.getStyleClass().add("info-icon");
            fullExplainView.setGraphic(infoImage);

            hbox.getChildren().addAll(word, pronouce, usSound, ukSound, fullExplainView);
        }
    
        @Override
        protected void updateItem(Pair<Integer, String> item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setGraphic(null);
            } 
            else {
                word.setText(item.getValue());
                pronouce.setText("/" + DictionaryDatabase.getWord(item.getKey()).getPronounce() + "/");
                ukSound.setOnAction(event -> playUKSound(item.getValue()));
                usSound.setOnAction(event -> playUSSound(item.getValue()));
                fullExplainView.setOnAction(event -> seeFullExplain(item));
                setGraphic(hbox);
            }
        }
    
        private void playUKSound(String word) {
            TextToSpeech.speakText(word, "en-gb");
        }
    
        private void playUSSound(String word) {
            TextToSpeech.speakText(word, "en-us");
        }
    
        private void seeFullExplain(Pair<Integer, String> item) {
            try {
                WordExplain.setItem(item);
                Parent page;
                page = FXMLLoader.load(Bookmark.class.getResource("/app/controller/WordExplain.fxml"), App.getBundle());
                explain.getChildren().clear();
                explain.getChildren().add(page);
                explain.setVisible(true);
                backButton.setVisible(true);
                columnName.setVisible(false);
                wordList.setVisible(false);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
