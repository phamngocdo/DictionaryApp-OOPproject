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
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import app.api.TextToSpeech;
import app.controller.Dictionary;

public class App extends Application {

    private static Scene scene;

    private final int WIDTH_SCENE = 907;
    private final int HEIGHT_SCENE = 605;
    private final String APP_NAME = "Dolingo";
    private final Image APP_ICON = new Image(App.class.getResourceAsStream("/graphic/logo.png"));

    private static ResourceBundle bundle;

    @SuppressWarnings("exports")
    @Override
    public void start(Stage stage) throws IOException {
        Dictionary.loadTrie();
        TextToSpeech.addVoice();
        
        boolean chooseEng = true;
        String language;
        try (FileInputStream fis = new FileInputStream("src/main/resources/app/bundle/language_choosed.txt");
            ObjectInputStream ois = new ObjectInputStream(fis)) {
            chooseEng = ois.readBoolean();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(chooseEng){
            language = "english";
        }
        else{
            language = "vietnamese";
        }

        bundle = ResourceBundle.getBundle("app.bundle." + language, Locale.getDefault());
        scene = new Scene(loadFXML("MainScreen", bundle), WIDTH_SCENE, HEIGHT_SCENE);

        String cssPath = App.class.getResource("/graphic/dark_style/dark_style.css").toExternalForm();
        scene.getStylesheets().add(cssPath);

        stage.setTitle(APP_NAME);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.getIcons().add(APP_ICON);
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml, bundle));
    }

    private static Parent loadFXML(String fxml, ResourceBundle bundle) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/app/controller/" + fxml + ".fxml"), bundle);
        return fxmlLoader.load();
    }

    public static void setSceneStyle(String cssPath) {
        scene.getStylesheets().clear();
        scene.getStylesheets().add(cssPath);
    }

    public static String getSceneStyle(){
        String result = "";
        List<String> stylesheets = scene.getStylesheets();
        for (String stylesheet : stylesheets) {
            result += stylesheet;
        }
        return result;
    }

    public static void setBundle(String language){
        bundle = ResourceBundle.getBundle("app.bundle." + language, Locale.getDefault());
        try {
            Parent newRoot = loadFXML("MainScreen", bundle);
            scene.setRoot(newRoot);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ResourceBundle getBundle(){
        return bundle;
    }

}
