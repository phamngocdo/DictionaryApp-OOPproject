package app.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import app.base.Example;
import app.base.Explain;
import app.base.Word;
import app.database.DictionaryDatabase;
import app.main.App;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class EditWord {

    @FXML
    private TextField wordField;

    @FXML
    private TextField pronounceField;

    @FXML
    private Button reset;

    @FXML
    private Button save;

    @FXML
    private Button addTypeOfExplain;

    @FXML
    private VBox editVbox;

    private static Word word = null;

    private static ArrayList<String> infoList;

    private ImageView addImage;

    private ImageView minusImage;

    public static void setWordToEdit(Word w) {
        word = w;
        infoList = new ArrayList<>();
    }

    @FXML
    private void initialize() {
        loadWord();
    }

    @FXML
    private void saveEdition(ActionEvent event) {
        String wordText = wordField.getText().trim();
        String pronounceText = pronounceField.getText().trim();

        if (wordText.isEmpty() || pronounceText.isEmpty()) {
            showNullTextFieldAlert();
            return;
        }

        if (word != null) {
            DictionaryDatabase.removeWord(word);
        }
        word = new Word(0, wordText, pronounceText);
        DictionaryDatabase.addWord(word);

        String currentType = "";
        int currentExplainId = -1;

        for (int i = 0; i < editVbox.getChildren().size(); i++) {
            HBox node = (HBox) editVbox.getChildren().get(i);
            if (infoList.get(i).equals("type")) {
                currentType = ((TextField) node.getChildren().get(2)).getText().trim();
                if (currentType.isEmpty()) {
                    showNullTextFieldAlert();
                    return;
                }
            }
            else if (infoList.get(i).equals("explain")) {
                String meaningText = ((TextField) node.getChildren().get(2)).getText().trim();
                if (meaningText.isEmpty()) {
                    showNullTextFieldAlert();
                    return;
                }
                Explain explain = new Explain(0, word.getId(), currentType, meaningText);
                DictionaryDatabase.addExplain(explain);
                currentExplainId = explain.getId();
            }
            else if (infoList.get(i).equals("example")){
                String exampleText = ((TextField) node.getChildren().get(1)).getText().trim();
                String translateText = ((TextField) node.getChildren().get(2)).getText().trim();
                if (exampleText.isEmpty() || translateText.isEmpty()) {
                    showNullTextFieldAlert();
                    return;
                }
                Example example = new Example(0, currentExplainId, exampleText, translateText);
                DictionaryDatabase.addExample(example);
            }
        }
    } 

    private void showNullTextFieldAlert() {
        boolean isEnglish = App.getLanguage().equals("english");
        String title = isEnglish ? "Incomplete Information" : "Thông tin chưa đầy đủ";
        String message = isEnglish ? "Please fill in all required fields." : "Vui lòng điền đầy đủ các ô cần thiết.";
        AlertScreen.showAlert(AlertType.WARNING, title, message);
    }

    @FXML
    private void resetEdition(ActionEvent event) {
        loadWord();
    }

    @FXML
    private void onClickAddType(ActionEvent event) {
        addType(null, null);
    }

    private void loadWord() {
        editVbox.getChildren().clear();
        if (word != null) {
            wordField.setText(word.getWord());
            pronounceField.setText(word.getPronounce());
            ArrayList<Explain> explains = DictionaryDatabase.getAllExplainsFromWord(word.getId());
            HashMap<String, ArrayList<Explain>> map = new HashMap<>();

            for (Explain explain : explains) {
                String type = explain.getType();
                if (!map.containsKey(type)) {
                    map.put(type, new ArrayList<>());
                }
                map.get(type).add(explain);
            }

            for (Map.Entry<String, ArrayList<Explain>> entry : map.entrySet()) {
                addType(entry.getKey(), explains);
            }
        }  
    }

    private void addType(String type, ArrayList<Explain> explains) {
        HBox parent = new HBox();
        parent.getStyleClass().add("type-hbox");

        Button add = new Button();
        addImage = new ImageView();
        addImage.getStyleClass().add("add-icon");
        add.setGraphic(addImage);
        add.getStyleClass().add("general-button");
        
        Label label = new Label(App.getBundle().getString("label.type"));
        label.getStyleClass().add("general-text");
        label.getStyleClass().add("general-text");
        label.setStyle("-fx-font-weight:bold;" + "-fx-font-size:15;");

        TextField typeField = new TextField();
        typeField.setPromptText(App.getBundle().getString("prompttext.type"));;
        typeField.getStyleClass().add("type-field");

        Button delete = new Button();
        delete.setOnAction(event -> deleteHbox(parent));
        minusImage = new ImageView();
        minusImage.getStyleClass().add("minus-icon");
        delete.setGraphic(minusImage);
        delete.getStyleClass().add("general-button");

        parent.getChildren().addAll(add, label, typeField, delete);
        editVbox.getChildren().add(parent);
        infoList.add("type");

        add.setOnAction(event -> {
            int index = editVbox.getChildren().indexOf(parent);
            addExlain(null, index + 1);
        });

        if (explains != null) {
            for (Explain explain : explains) {
                addExlain(explain, -1);
            }
        }
        else {
            addExlain(null, -1);
        }
    }

    private void addExlain(Explain explain, int i) {
        HBox parent = new HBox();
        parent.getStyleClass().add("explain-hbox");
    
        Button add = new Button();
        addImage = new ImageView();
        addImage.getStyleClass().add("add-icon");
        add.setGraphic(addImage);
        add.getStyleClass().add("general-button");

        Label label = new Label(App.getBundle().getString("label.explain"));
        label.getStyleClass().add("general-text");
        label.getStyleClass().add("general-text");
        label.setStyle("-fx-font-weight:bold;" + "-fx-font-size:15;");
    
        TextField meaningField = new TextField();
        meaningField.getStyleClass().add("explain-field");

        meaningField.setPromptText(App.getBundle().getString("prompttext.explain"));
        if (explain != null) {
            meaningField.setText(explain.getMeaning());
        } 
        else {
            meaningField.setText("");
        }
    
        Button delete = new Button();
        delete.setOnAction(event -> deleteHbox(parent));
        minusImage = new ImageView();
        minusImage.getStyleClass().add("minus-icon");
        delete.setGraphic(minusImage);
        delete.getStyleClass().add("general-button");
    
        parent.getChildren().addAll(add, label, meaningField, delete);
        if (i == -1) {
            editVbox.getChildren().add(parent);
            infoList.add("explain");
        }
        else {
            editVbox.getChildren().add(i, parent);
            infoList.add(i,"explain");
        }

        add.setOnAction(event -> {
            int index = editVbox.getChildren().indexOf(parent);
            addExample(null, index + 1);
        });
    
        if (explain != null) {
            for (Example example : DictionaryDatabase.getAllExamplesFromExplain(explain.getId())) {
                    addExample(example, -1);
            }
        }
    }
    
    private void addExample(Example example, int i) {
        HBox parent = new HBox();
        parent.getStyleClass().add("example-hbox");

        Label label = new Label(App.getBundle().getString("label.example"));
        label.getStyleClass().add("general-text");
        label.setStyle("-fx-font-weight:bold;" + "-fx-font-size:15;");

        TextField exampleField = new TextField();
        exampleField.setPromptText(App.getBundle().getString("prompttext.example"));
        exampleField.getStyleClass().add("example-field");

        TextField translateField = new TextField();
        translateField.setPromptText(App.getBundle().getString("prompttext.example"));
        translateField.getStyleClass().add("example-field");

        if (example != null) {
            exampleField.setText(example.getExample());
            translateField.setText(example.getTranslate());
        }
        else {
            exampleField.setText("");
            translateField.setText("");
        }
        
        Button delete = new Button();
        delete.setOnAction(event -> deleteHbox(parent));
        minusImage = new ImageView();
        minusImage.getStyleClass().add("minus-icon");
        delete.setGraphic(minusImage);
        delete.getStyleClass().add("general-button");

        parent.getChildren().addAll(label, exampleField, translateField, delete);
        if (i == -1) {
            editVbox.getChildren().add(parent);
            infoList.add("example");
        }
        else {
            editVbox.getChildren().add(i, parent);
            infoList.add(i,"example");
        }
    }

    private void deleteHbox(HBox hbox) {
        int index = editVbox.getChildren().indexOf(hbox);
        if (infoList.get(index).equals("type")) {
            editVbox.getChildren().remove(index);
            infoList.remove(index);
            while (index < infoList.size() && !infoList.get(index).equals("type")) {
                if (infoList.get(index).equals("explain")) {
                    editVbox.getChildren().remove(index);
                    infoList.remove(index);
                    while (index < infoList.size() && infoList.get(index).equals("example")) {
                        editVbox.getChildren().remove(index);
                        infoList.remove(index);
                    }
                } 
                else {
                    editVbox.getChildren().remove(index);
                    infoList.remove(index);
                }
            }
        }
        else if (infoList.get(index).equals("explain")) {
            editVbox.getChildren().remove(index);
            infoList.remove(index);
            while (index < infoList.size() && infoList.get(index).equals("example")) {
                editVbox.getChildren().remove(index);
                infoList.remove(index);
            }
        }
    }
}