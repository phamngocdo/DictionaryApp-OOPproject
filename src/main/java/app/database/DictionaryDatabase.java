package app.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import app.base.Example;
import app.base.Explain;
import app.base.Word;
import javafx.util.Pair;


public class DictionaryDatabase {
    private DataBaseProcess database;

    public DictionaryDatabase(String dbPath) {
        database = new DataBaseProcess(dbPath);
    }

    public ArrayList<Pair<Integer, String>> getAllWords() {
        ArrayList<Pair<Integer, String>> list = new ArrayList<>();
        ResultSet resultSet = database.getResultSet("words", null);
        try {
            while (resultSet.next()) {
                String word = resultSet.getString("word");
                int wordId = resultSet.getInt("word_id");
                list.add(new Pair<Integer,String>(wordId, word));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Word getWord(int wordId) {
        ResultSet resultSet = database.getResultSet("words", "word_id = " + wordId);
        Word word = null;
        try {
            if (resultSet.next()) {
                word = new Word(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return word;
    }

    public void addWord(Word word) {
        word.setId(database.getNextId("words", "word_id", null));
        String value = "(" + word.getId() + ", '" + word.getWord() + "', '" + word.getPronounce() + "')";
        database.addTuple("words", "word_id, word, pronounce", value);
    }

    public void updateWord(Word word) {
        StringBuilder value = new StringBuilder();
        value.append("word_id = " + word.getId() + ", ");
        value.append("word = '" + word.getWord() + "', ");
        value.append("pronounce = '" + word.getPronounce() + "'");
        database.updateTuple("words", value.toString(), "word_id = " + word.getId());
    }

    public void removeWord(Word word) {
        ArrayList<Explain> explains = getAllExplainsFromWord(word.getId());
        for (Explain explain : explains) {
            removeExplain(explain);
        }
        database.removeTuple("words", "word_id = " + word.getId());
    }

    public ArrayList<Explain> getAllExplainsFromWord(int wordId) {
        ArrayList<Explain> list = new ArrayList<>();
        ResultSet resultSet = database.getResultSet("explains", "word_id = " + wordId);
        try {
            while (resultSet.next()) {
                int explainId = resultSet.getInt("explain_id");
                String type = resultSet.getString("type");
                String meaning = resultSet.getString("meaning");
                list.add(new Explain(explainId, wordId, type, meaning));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void addExplain(Explain explain) {
        explain.setId(database.getNextId("explains", "explain_id", "word_id = " + explain.getWordId()));
        String value = "(" + explain.getId() + ", " + explain.getWordId() + ", '" + explain.getType() + "', '" + explain.getMeaning() + "')";
        database.addTuple("explains", "explain_id, word_id, type, meaning", value);
    }

    public void updateExplain(Explain explain) {
        StringBuilder value = new StringBuilder();
        value.append("explain_id = " + explain.getId() + ", ");
        value.append("word_id = " + explain.getWordId() + ", ");
        value.append("type = '" + explain.getType() + "', ");
        value.append("meaning = '" + explain.getMeaning() + "'");
        database.updateTuple("explains", value.toString(), "explain_id = " + explain.getId());
    }

    public void removeExplain(Explain explain) {
        ArrayList<Example> examples = getAllExamplesFromExplain(explain.getId());
        for (Example example : examples) {
            database.removeTuple("examples", "example_id = " + example.getId());
        }
        database.removeTuple("explains", "explain_id = " + explain.getId());
    }

    public ArrayList<Example> getAllExamplesFromExplain(int explainId) {
        ArrayList<Example> list = new ArrayList<>();
        ResultSet resultSet = database.getResultSet("examples", "explain_id = " + explainId);
        try {
            while (resultSet.next()) {
                int exampleId = resultSet.getInt("example_id");
                String example = resultSet.getString("example");
                String translate = resultSet.getString("translate");
                list.add(new Example(exampleId, explainId, example, translate));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void addExample(Example example) {
        example.setId(database.getNextId("examples", "example_id", "explain_id = " + example.getExplainId()));
        String value = "(" + example.getId() + ", " + example.getExplainId() + ", '" + example.getExample() + "', '" + example.getTranslate() + "')";
        database.addTuple("examples", "example_id, explain_id, example, translate", value);
    }

    public void updateExample(Example example) {
        StringBuilder value = new StringBuilder();
        value.append("example_id = " + example.getId() + ", ");
        value.append("explain_id = " + example.getExplainId() + ", ");
        value.append("example = '" + example.getExample() + "', ");
        value.append("translate = '" + example.getTranslate() + "'");
        database.updateTuple("examples", value.toString(), "example_id = " + example.getId());
    }

    public void removeExample(Example example) {
        database.removeTuple("examples", "example_id = " + example.getId());
    }
}