package app.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Locale;
import java.util.ResourceBundle;

import app.api.TextToSpeech;
import app.controller.MainScreen;
import app.database.DictionaryDatabase;

public class App extends Application {

    private static Scene scene;
    private static ResourceBundle bundle;
    private static MainScreen mainScreen;

    private static final int WIDTH_SCENE = 1024;
    private static final int HEIGHT_SCENE = 633;
    private static final String APP_NAME = "Dolingo";
    private static final String ICON_PATH = "/graphic/logo.png";
    private static final String CSS_PATH = "/graphic/dark_style/dark_style.css";
    private static final String LANGUAGE_FILE = "src/main/resources/bundle/language_choosed.txt";
    private static String language = "english";

    @SuppressWarnings("exports")
    @Override
    public void start(Stage stage) throws IOException {
        DictionaryDatabase.loadData();
        TextToSpeech.addVoice();

        try (FileInputStream fis = new FileInputStream(LANGUAGE_FILE);
            ObjectInputStream ois = new ObjectInputStream(fis)) {
            language = ois.readBoolean() ? "english" : "vietnamese"; 
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
        bundle = ResourceBundle.getBundle("bundle." + language, Locale.getDefault());
        loadMainScreen();
        scene.getStylesheets().add(App.class.getResource(CSS_PATH).toExternalForm());
        stage.setTitle(APP_NAME);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.getIcons().add(new Image(App.class.getResourceAsStream(ICON_PATH)));
        stage.show();
    }

    private void loadMainScreen() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/controller/MainScreen.fxml"), bundle);
        Parent root = fxmlLoader.load();
        mainScreen = fxmlLoader.getController();
        scene = new Scene(root, WIDTH_SCENE, HEIGHT_SCENE);
    }

    public static MainScreen getMainScreen() {
        return mainScreen;
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml, bundle));
    }

    private static Parent loadFXML(String fxml, ResourceBundle bundle) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/controller/" + fxml + ".fxml"), bundle);
        return fxmlLoader.load();
    }

    public static void setSceneStyle(String cssPath) {
        scene.getStylesheets().clear();
        scene.getStylesheets().add(cssPath);
    }

    public static String getSceneStyle() {
        return String.join("", scene.getStylesheets());
    }

    public static void setBundle(String language) {
        bundle = ResourceBundle.getBundle("bundle." + language, Locale.getDefault());
        try {
            Parent newRoot = loadFXML("MainScreen", bundle);
            scene.setRoot(newRoot);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ResourceBundle getBundle() {
        return bundle;
    }

    public static String getLanguage() {
        return language;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
