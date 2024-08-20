package app.main;

import app.api.TextToSpeech;
import app.database.DictionaryDatabase;

public class RunApp {
    public static void main(String[] args) {
        DictionaryDatabase.loadData();
        TextToSpeech.addVoice();

        App.launch(App.class,args);
        
        DictionaryDatabase.close();
    }
}