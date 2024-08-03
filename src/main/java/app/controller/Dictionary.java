package app.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.ResourceBundle;

import app.api.TextToSpeech;
import app.api.WordRelations;
import app.database.DictionaryDatabase;
import app.main.App;
import app.trie.Trie;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.Parent;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.util.Pair;

public class Dictionary {
    private static Trie trie = new Trie();

    private static DictionaryDatabase data;

    @FXML
    private Pane explain;

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
    private Label notFound;

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

    @FXML
    private void initialize() {
        currentWord = null;
        setVisibility(false, false, false, false, false);
        search.getStyleClass().add("search-bar");
        search.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case ENTER:
                    searchWord(new ActionEvent());
                    break;
                default:
                    break;
            }
        });
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
                wordListView.setVisible(false);
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
        Parent page;
        ResourceBundle bundle = App.getBundle();
        try {
            WordExplain.item = item;
            page = FXMLLoader.load(Dictionary.class.getResource("/app/controller/WordExplain.fxml"), bundle);
            explain.getChildren().clear();
            explain.getChildren().add(page);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        this.explain.setVisible(explain);
        this.synonymsPane.setVisible(synonyms);
        this.antonymsPane.setVisible(antonyms);
        wordListView.setVisible(wordList);
        this.notFound.setVisible(notFound);
    }

    @FunctionalInterface
    private interface ListCellHandler {
        void handle(ListCell<Pair<Integer, String>> cell, Pair<Integer, String> item);
    }
}
