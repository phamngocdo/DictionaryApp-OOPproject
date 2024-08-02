package app.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import app.api.TextToSpeech;
import app.api.WordRelations;
import app.base.Example;
import app.base.Explain;
import app.base.Word;
import app.database.DictionaryDatabase;
import app.trie.Trie;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.util.Pair;

public class Dictionary {
    private static Trie trie = new Trie();
    private static DictionaryDatabase data;

    @FXML
    private Pane explainPane;

    @FXML
    private Pane synonymsPane;

    @FXML
    private Pane antonymsPane;

    @FXML
    private TextField search;

    @FXML
    private Button searchButton;

    @FXML
    private Button remove;

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

    @FXML
    private ListView<Pair<Integer, String>> wordListView;

    @FXML
    private ListView<Pair<Integer, String>> synonyms;

    @FXML
    private ListView<Pair<Integer, String>> antonyms;

    @FXML
    private ProgressIndicator synProgress;

    @FXML
    private ProgressIndicator antProgress;

    private String currentWord;

    private final String BOOKMARK_PATH = "src/main/resources/bookmark/bookmark.txt";

    @FXML
    private void initialize() {
        currentWord = null;
        setVisibility(false, false, false, false, false);
        search.getStyleClass().add("search-bar");
        setupEventHandlers();
    }

    @FXML
    private void searchWord(ActionEvent event) {
        String word = search.getText();
        if (!word.isEmpty()) {
            currentWord = word;
            handleWordSearch(word);
        }
    }

    @FXML
    private void removeAllCharacter(ActionEvent event) {
        search.clear();
        wordListView.setVisible(false);
    }

    @FXML
    private void playUSPronounceSound(ActionEvent event) {
        TextToSpeech.speakText(currentWord, "en-us");
    }

    @FXML
    private void playUKPronounceSound(ActionEvent event) {
        TextToSpeech.speakText(currentWord, "en-gb");
    }

    @FXML
    private void saveWord(ActionEvent event) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(BOOKMARK_PATH))){
            writer.write(currentWord);
            writer.newLine();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        handleSaveButton(true);
        search.requestFocus();
    }

    @FXML
    private void unsaveWord(ActionEvent event) {
        ArrayList<String> lines = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(BOOKMARK_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.equals(currentWord)) {
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
        handleSaveButton(false);
    }

    public static void loadTrie() {
        data = new DictionaryDatabase(Dictionary.class.getResource("/database/dictionary.db").getPath());
        data.getAllWords().forEach(word -> trie.insertWord(word.getKey(), word.getValue()));
    }

    @SuppressWarnings("exports")
    public static Trie getTrie() {
        return trie;
    }

    private void setupEventHandlers() {
        Platform.runLater(() -> {
            search.getScene().addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                if (!search.getBoundsInParent().contains(event.getSceneX(), event.getSceneY()) &&
                    !wordListView.getBoundsInParent().contains(event.getSceneX(), event.getSceneY())) {
                    wordListView.setVisible(false);
                    notFound.setVisible(false);
                }
            });
        });
        search.textProperty().addListener((observable, oldValue, newValue) -> {
            boolean isEmpty = newValue == null || newValue.trim().isEmpty();
            remove.setVisible(!isEmpty);
            notFound.setVisible(false);
            if (!isEmpty) {
                wordListView.setVisible(true);
                handleWordListView(newValue.toLowerCase());
            } 
            else {
                wordListView.setVisible(false);
            }
        });
    }
    private void handleWordSearch(String word) {
        Pair<Integer, String> result = trie.getWord(word);
        if (result == null) {
            AlertScreen.showAlert(
                AlertType.WARNING,
                "Not Found",
                "Your word is not in dictionary\nTừ của bạn không có trong từ điển");
            setVisibility(false, false, false, true, false);
        } else {
            handleExplainPane(result);
            handleSynAnt(word);
        }
    }

    private void handleWordListView(String prefix) {
        ObservableList<Pair<Integer, String>> items = FXCollections.observableArrayList(trie.getAllVocabStartWith(prefix));
        wordListView.setItems(items);
        notFound.setVisible(items.isEmpty());
        setupListViewCellFactory(wordListView, this::handleListItem);
        adjustListViewHeight(wordListView, 514, 30);
    }

    private void handleListItem(ListCell<Pair<Integer, String>> cell, Pair<Integer, String> item) {
        cell.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1 && !cell.isEmpty()) {
                currentWord = item.getValue();
                handleExplainPane(item);
                handleSynAnt(currentWord);
            }
        });
        cell.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            if (event.isPrimaryButtonDown()) {
                wordListView.getSelectionModel().clearSelection();
                event.consume();
            }
        });
    }

    private void setupListViewCellFactory(ListView<Pair<Integer, String>> listView, ListCellHandler handler) {
        listView.setCellFactory(view -> new ListCell<>() {
            @Override
            protected void updateItem(Pair<Integer, String> item, boolean empty) {
                super.updateItem(item, empty);
                setText(item == null ? null : item.getValue());
                if (item != null) 
                {
                    handler.handle(this, item);
                }
            }
        });
    }

    private void handleExplainPane(Pair<Integer, String> item) {
        wordListView.setVisible(false);
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
        explainPane.setVisible(true);
        handleSaveButton(isSaved(item.getValue()));
    }

    private void handleSynAnt(String word) {
        setVisibility(true, true, true, false, false);
        synonyms.setVisible(false);
        antonyms.setVisible(false);
        synProgress.setVisible(true);
        antProgress.setVisible(true);
        new Thread(() -> {
            try {
                ArrayList<Pair<Integer, String>> syns = WordRelations.getSynonyms(word);
                ArrayList<Pair<Integer, String>> ants = WordRelations.getAntonyms(word);
                Platform.runLater(() -> {
                    updateListView(synonyms, syns);
                    synProgress.setVisible(false);
                    synonyms.setVisible(true);
                    updateListView(antonyms, ants);
                    antProgress.setVisible(false);
                    antonyms.setVisible(true);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void updateListView(ListView<Pair<Integer, String>> listView, ArrayList<Pair<Integer, String>> items) {
        ObservableList<Pair<Integer, String>> observableItems = FXCollections.observableArrayList(items);
        listView.setItems(observableItems);
        setupListViewCellFactory(listView, this::handleListItem);
        adjustListViewHeight(listView, 200, 0);
    }

    private void adjustListViewHeight(ListView<?> listView, double maxHeight, double emptyHeight) {
        Platform.runLater(() -> {
            int itemCount = listView.getItems().size();
            if (itemCount > 0) {
                double itemHeight = 30;
                double totalHeight = itemCount * itemHeight;
                listView.setPrefHeight(Math.min(totalHeight, maxHeight));
            } 
            else {
                listView.setPrefHeight(emptyHeight);
            }
        });
    }
    
    
    private void setVisibility(boolean explain, boolean synonyms, boolean antonyms, boolean wordList, boolean notFound) {
        explainPane.setVisible(explain);
        this.synonymsPane.setVisible(synonyms);
        this.antonymsPane.setVisible(antonyms);
        wordListView.setVisible(wordList);
        this.notFound.setVisible(notFound);
    }

    private boolean isSaved(String word){
       try (BufferedReader reader = new BufferedReader(new FileReader(BOOKMARK_PATH))){
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.trim().equals(word)) { 
                return true;
            }
        }
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
        
        return false;
    }

    private void handleSaveButton(boolean isSaved){
        if (isSaved) {
            saved.setVisible(true);
            save.setVisible(false);
        }
        else {
            saved.setVisible(false);
            save.setVisible(true);
        }
    }

    @FunctionalInterface
    private interface ListCellHandler {
        void handle(ListCell<Pair<Integer, String>> cell, Pair<Integer, String> item);
    }
}
