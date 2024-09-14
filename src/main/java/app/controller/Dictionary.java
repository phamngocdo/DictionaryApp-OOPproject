package app.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;

import app.api.WordRelations;
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
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.util.Pair;

public class Dictionary {

    @FXML
    private Pane explainPane, synonymsPane, antonymsPane;

    @FXML
    private TextField search;

    @FXML
    private Button searchButton, removeButton;

    @FXML
    private Label notFound;

    @FXML
    private ListView<Pair<Integer, String>> wordListView, synonyms, antonyms;

    @FXML
    private ImageView noInternetImage1, noInternetImage2;

    @FXML
    private ProgressIndicator synProgress, antProgress;

    private String currentWord;

    @FXML
    private void initialize() {
        currentWord = null;
        explainPane.setVisible(false);
        synonymsPane.setVisible(false);
        antonymsPane.setVisible(false);
        wordListView.setVisible(false);
        notFound.setVisible(false);
        search.getStyleClass().add("search-bar");
        search.setOnKeyPressed(event -> {
            if (Objects.requireNonNull(event.getCode()) == KeyCode.ENTER) {
                searchWord(new ActionEvent());
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

    private void setupEventHandlers() {
        Platform.runLater(() -> search.getScene().addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            if (!search.getBoundsInParent().contains(event.getSceneX(), event.getSceneY()) &&
                !wordListView.getBoundsInParent().contains(event.getSceneX(), event.getSceneY()) &&
                !isChildOf(wordListView, (Node) event.getTarget())) {
                wordListView.setVisible(false);
                notFound.setVisible(false);
            }
        }));
        search.textProperty().addListener((observable, oldValue, newValue) -> {
            boolean isEmpty = newValue == null || newValue.trim().isEmpty();
            removeButton.setVisible(!isEmpty);
            notFound.setVisible(false);
            wordListView.setVisible(!isEmpty);
            if (!isEmpty) {
                handleWordListView(newValue.toLowerCase());
            }
        });
    }

    private boolean isChildOf(Node parent, Node child) {
        while (child != null) {
            if (child.equals(parent)) {
                return true;
            }
            child = child.getParent();
        }
        return false;
    }

    private void handleWordSearch(String word) {
        Pair<Integer, String> result = Trie.getWord(word);
        if (result == null) {
            String title = App.getBundle().getString("title.warning");
            String message = App.getBundle().getString("message.notfoundword");
            AlertScreen.showAlert(AlertType.WARNING,title,message);
            explainPane.setVisible(false);
            synonymsPane.setVisible(false);
            antonymsPane.setVisible(false);
            wordListView.setVisible(false);
            notFound.setVisible(false);
        } 
        else {
            handleExplainPane(result);
            handleSynAnt(word);
        }
    }

    private void handleWordListView(String prefix) {
        ObservableList<Pair<Integer, String>> items = FXCollections.observableArrayList(Trie.getAllWordStartWith(prefix));
        wordListView.setItems(items);
        notFound.setVisible(items.isEmpty());
        setupListViewCellFactory(wordListView);
        adjustListViewHeight(wordListView, 33, 532, 36);
    }

    private void setupListViewCellFactory(ListView<Pair<Integer, String>> listView) {
        listView.setCellFactory(view -> new ListCell<>() {
            {
                setOnMouseClicked(event -> {
                    if (event.getClickCount() == 1 && !isEmpty()) {
                        Pair<Integer, String> item = getItem();
                        currentWord = item.getValue();
                        wordListView.setVisible(false);
                        handleExplainPane(item);
                        handleSynAnt(currentWord);
                    }
                });
                addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                    if (event.isPrimaryButtonDown()) {
                        wordListView.getSelectionModel().clearSelection();
                        event.consume();
                    }
                });
            }
            @Override
            protected void updateItem(Pair<Integer, String> item, boolean empty) {
                super.updateItem(item, empty);
                setText(item == null ? null : item.getValue());
            }
        });
    }

    private void handleExplainPane(Pair<Integer, String> item) {
        Parent page;
        ResourceBundle bundle = App.getBundle();
        try {
            WordExplain.setItem(item);
            page = FXMLLoader.load(Objects.requireNonNull(Dictionary.class.getResource("/controller/WordExplain.fxml")), bundle);
            explainPane.getChildren().clear();
            explainPane.getChildren().add(page);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleSynAnt(String word) {
        explainPane.setVisible(true);
        synonymsPane.setVisible(true);
        antonymsPane.setVisible(true);
        wordListView.setVisible(false);
        notFound.setVisible(false);

        synonyms.setVisible(false);
        antonyms.setVisible(false);

        synProgress.setVisible(true);
        antProgress.setVisible(true);
        noInternetImage1.setVisible(false);
        noInternetImage2.setVisible(false);
        new Thread(() -> {
            try {
                ArrayList<Pair<Integer, String>> syns = WordRelations.getSynonyms(word);
                ArrayList<Pair<Integer, String>> ants = WordRelations.getAntonyms(word);
                Platform.runLater(() -> {
                    updateListView(synonyms, syns);
                    synonyms.setVisible(true);
                    updateListView(antonyms, ants);
                    antProgress.setVisible(false);
                });
            } catch (Exception e) {
                noInternetImage1.setVisible(true);
                noInternetImage2.setVisible(true);
            } finally {
                synProgress.setVisible(false);
                antProgress.setVisible(false);
            }
        }).start();
    }

    private void updateListView(ListView<Pair<Integer, String>> listView, ArrayList<Pair<Integer, String>> items) {
        ObservableList<Pair<Integer, String>> observableItems = FXCollections.observableArrayList(items);
        listView.setItems(observableItems);
        setupListViewCellFactory(listView);
        adjustListViewHeight(listView, 26, 226, 0);
    }

    private void adjustListViewHeight(ListView<?> listView, double itemHeight, double maxHeight, double emptyHeight) {
        Platform.runLater(() -> {
            int itemCount = listView.getItems().size();
            if (itemCount > 0) {
                double totalHeight = itemCount * itemHeight;
                listView.setPrefHeight(Math.min(totalHeight, maxHeight));
            } 
            else {
                listView.setPrefHeight(emptyHeight);
            }
        });
    }
}
