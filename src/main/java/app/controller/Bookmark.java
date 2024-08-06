package app.controller;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ResourceBundle;

import app.main.App;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.util.Pair;

public class Bookmark {

    @FXML
    private Pane explain;

    @FXML
    private ListView<Pair<Integer, String>> wordList;

    private ArrayList<Pair<Integer, String>> list = new ArrayList<>();

    private static final String BOOKMARK_PATH = "src/main/resources/bookmark/bookmark.txt";

    @FXML
    private void initialize() {
        explain.setVisible(false);
        loadList();
        handleListView();
    }

    private void loadList() {
        list.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(BOOKMARK_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Pair<Integer, String> word = Dictionary.getTrie().getWord(line);
                if (word != null) {
                    list.add(word);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        ObservableList<Pair<Integer, String>> items = FXCollections.observableArrayList(list);
        wordList.setItems(items);
        adjustListViewHeight(wordList, 26, 484, 0);
    }

    private void handleListView() {
        wordList.setCellFactory(view -> new ListCell<>() {
            {
                setOnMouseClicked(event -> {
                    if (event.getClickCount() == 1 && !isEmpty()) {
                        Pair<Integer, String> item = getItem();
                        ResourceBundle bundle = App.getBundle();
                        try {
                            WordExplain.setItem(item);
                            Parent page = FXMLLoader.load(Dictionary.class.getResource("/app/controller/WordExplain.fxml"), bundle);
                            explain.getChildren().setAll(page);
                            explain.setVisible(true);
                            loadList();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

                addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                    if (event.isPrimaryButtonDown()) {
                        wordList.getSelectionModel().clearSelection();
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

    private void adjustListViewHeight(ListView<?> listView, double itemHeight, double maxHeight, double emptyHeight) {
        Platform.runLater(() -> {
            double totalHeight = Math.min(listView.getItems().size() * itemHeight, maxHeight);
            listView.setPrefHeight(totalHeight > 0 ? totalHeight : emptyHeight);
        });
    }
}
