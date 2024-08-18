package app.main;

import app.database.DictionaryDatabase;

public class RunApp {
    public static void main(String[] args) {
        App.launch(App.class,args);
        DictionaryDatabase.close();
    }
}