package app.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import app.base.Example;
import app.base.Explain;
import app.base.Word;
import app.trie.Trie;
import javafx.util.Pair;


public class DictionaryDatabase {
    private static DataBaseProcess data;

    public static void loadData() {
        data = new DataBaseProcess(DictionaryDatabase.class.getResource("/database/dictionary.db").getPath());
        getAllWords().forEach(word -> Trie.insertWord(word.getKey(), word.getValue()));
    }

    public static void close() {
        data.close();
    }

    public static ArrayList<Pair<Integer, String>> getAllWords() {
        ArrayList<Pair<Integer, String>> list = new ArrayList<>();
        ResultSet resultSet = data.getResultSet("words", null);
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

    public static Word getWord(int wordId) {
        ResultSet resultSet = data.getResultSet("words", "word_id = " + wordId);
        Word word = null;
        try {
            if (resultSet.next()) {
                word = new Word(wordId, resultSet.getString("word"), resultSet.getString("pronounce")); 
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return word;
    }

    public static void addWord(Word word) {
        word.setId(data.getNextId("words", "word_id"));
        String value = "(" + word.getId() + ", '" + word.getWord() + "', '" + word.getPronounce() + "')";
        data.addTuple("words", "word_id, word, pronounce", value);
        Trie.insertWord(word.getId(), word.getWord());
    }

    public static void removeWord(Word word) {
        ArrayList<Explain> explains = getAllExplainsFromWord(word.getId());
        for (Explain explain : explains) {
            removeExplain(explain);
        }
        data.removeTuple("words", "word_id = " + word.getId());
        Trie.removeVocabulary(word.getWord());
    }

    public static ArrayList<Explain> getAllExplainsFromWord(int wordId) {
        ArrayList<Explain> list = new ArrayList<>();
        ResultSet resultSet = data.getResultSet("explains", "word_id = " + wordId);
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

    public static Explain getExplain(int explainId) {
        ResultSet resultSet = data.getResultSet("explains", "explain_id = " + explainId);
        Explain explain = null;
        try {
            if (resultSet.next()) {
                explain = new Explain(explainId, resultSet.getInt("explain_id"), resultSet.getString("type"), resultSet.getString("meaning")); 
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return explain;
    }

    public static void addExplain(Explain explain) {
        explain.setId(data.getNextId("explains", "explain_id"));
        String value = "(" + explain.getId() + ", " + explain.getWordId() + ", '" + explain.getType() + "', '" + explain.getMeaning() + "')";
        data.addTuple("explains", "explain_id, word_id, type, meaning", value);
    }
    
    public static void removeExplain(Explain explain) {
        ArrayList<Example> examples = getAllExamplesFromExplain(explain.getId());
        for (Example example : examples) {
            removeExample(example);
        }
        data.removeTuple("explains", "explain_id = " + explain.getId());
    }

    public static ArrayList<Example> getAllExamplesFromExplain(int explainId) {
        ArrayList<Example> list = new ArrayList<>();
        ResultSet resultSet = data.getResultSet("examples", "explain_id = " + explainId);
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

    public static Example getExample(int exampleId) {
        ResultSet resultSet = data.getResultSet("examples", "example_id = " + exampleId);
        Example example = null;
        try {
            if (resultSet.next()) {
                example = new Example(exampleId, resultSet.getInt("explain_id"), resultSet.getString("example"), resultSet.getString("translate")); 
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return example;
    }

    public static void addExample(Example example) {
        example.setId(data.getNextId("examples", "example_id"));
        String value = "(" + example.getId() + ", " + example.getExplainId() + ", '" + example.getExample() + "', '" + example.getTranslate() + "')";
        data.addTuple("examples", "example_id, explain_id, example, translate", value);
    }

    public static void removeExample(Example example) {
        data.removeTuple("examples", "example_id = " + example.getId());
    }

    public static void main(String[] args) {
        loadData();
        //Test
        System.out.println();
        close();
    }
}