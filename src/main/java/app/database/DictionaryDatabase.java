package app.database;

import app.base.Example;
import app.base.Explain;
import app.trie.Trie;
import app.base.Word;

import java.sql.*;
import java.util.ArrayList;
import javafx.util.Pair;

public class DictionaryDatabase {
    private static Connection connection;
    private static Statement statement;

    private static final String DB_PATH = "src/main/resources/database/dictionary.db";

    public static  void loadData() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH);
            statement = connection.createStatement();
            getAllWords().forEach(word -> Trie.insertWord(word.getKey(), word.getValue()));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static ArrayList<Pair<Integer, String>> getAllWords() {
        ArrayList<Pair<Integer, String>> list = new ArrayList<>();
        try {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM words");
            while (resultSet.next()) {
                String word = resultSet.getString("word");
                int wordId = resultSet.getInt("word_id");
                list.add(new Pair<>(wordId, word));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public static Word getWord(int wordId) {
        Word word;
        StringBuilder query = new StringBuilder();
        query.append("SELECT * ");
        query.append("FROM words ");
        query.append("WHERE word_id = ?");
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query.toString());
            preparedStatement.setString(1, String.valueOf(wordId));
            ResultSet resultSet = preparedStatement.executeQuery();
            word = new Word(wordId, resultSet.getString("word"), resultSet.getString("pronounce"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return word;
    }

    public static void addWord(Word word) {
        StringBuilder query = new StringBuilder();
        query.append("INSERT INTO words (word_id, word, pronounce) ");
        query.append("VALUES (?, ?, ?)");
        word.setId(getNextId("words", "word_id"));
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query.toString());
            preparedStatement.setString(1, String.valueOf(word.getId()));
            preparedStatement.setString(2, word.getWord());
            preparedStatement.setString(3, word.getPronounce());
            preparedStatement.executeUpdate();
            Trie.insertWord(word.getId(), word.getWord());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void removeWord(Word word) {
        StringBuilder query = new StringBuilder();
        query.append("DELETE FROM words ");
        query.append("WHERE word_id = ? ");
        try {
            ArrayList<Explain> explains = getAllExplainsFromWord(word.getId());
            for (Explain explain : explains) {
                removeExplain(explain);
            }
            PreparedStatement preparedStatement = connection.prepareStatement(query.toString());
            preparedStatement.setString(1, String.valueOf(word.getId()));
            preparedStatement.executeUpdate();
            Trie.removeWord(word.getWord());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static ArrayList<Explain> getAllExplainsFromWord(int wordId) {
        ArrayList<Explain> list = new ArrayList<>();
        StringBuilder query = new StringBuilder();
        query.append("SELECT * ");
        query.append("FROM explains ");
        query.append("WHERE word_id = ? ");
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query.toString());
            preparedStatement.setString(1, String.valueOf(wordId));
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int explainId = resultSet.getInt("explain_id");
                String type = resultSet.getString("type");
                String meaning = resultSet.getString("meaning");
                list.add(new Explain(explainId, wordId, type, meaning));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public static void addExplain(Explain explain) {
        StringBuilder query = new StringBuilder();
        query.append("INSERT INTO explains (explain_id, word_id, type, meaning) ");
        query.append("VALUES (?, ?, ?, ?)");
        explain.setId(getNextId("explains", "explain_id"));
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query.toString());
            preparedStatement.setString(1, String.valueOf(explain.getId()));
            preparedStatement.setString(2, String.valueOf(explain.getWordId()));
            preparedStatement.setString(3, explain.getType());
            preparedStatement.setString(4, explain.getMeaning());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void removeExplain(Explain explain) {
        StringBuilder query = new StringBuilder();
        query.append("DELETE FROM explains ");
        query.append("WHERE explain_id = ? ");
        try {
            ArrayList<Example> examples = getAllExamplesFromExplain(explain.getId());
            for (Example example : examples) {
                removeExample(example);
            }
            PreparedStatement preparedStatement = connection.prepareStatement(query.toString());
            preparedStatement.setString(1, String.valueOf(explain.getId()));
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static ArrayList<Example> getAllExamplesFromExplain(int explainId) {
        ArrayList<Example> list = new ArrayList<>();
        StringBuilder query = new StringBuilder();
        query.append("SELECT * ");
        query.append("FROM examples ");
        query.append("WHERE explain_id = ?");
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query.toString());
            preparedStatement.setString(1, String.valueOf(explainId));
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int exampleId = resultSet.getInt("example_id");
                String example = resultSet.getString("example");
                String translate = resultSet.getString("translate");
                list.add(new Example(exampleId, explainId, example, translate));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public static void addExample(Example example) {
        StringBuilder query = new StringBuilder();
        query.append("INSERT TO examples (example_id, explain_id, example, translate");
        query.append("VALUES (?, ?, ?, ?");
        example.setId(getNextId("examples", "example_id"));
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query.toString());
            preparedStatement.setString(1, String.valueOf(example.getId()));
            preparedStatement.setString(2, String.valueOf(example.getExplainId()));
            preparedStatement.setString(3, example.getExample());
            preparedStatement.setString(4, example.getTranslate());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void removeExample(Example example) {
        StringBuilder query = new StringBuilder();
        query.append("DELETE FROM examples ");
        query.append("WHERE example_id = ? ");
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query.toString());
            preparedStatement.setString(1, String.valueOf(example.getId()));
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static int getNextId(String tableName, String idColumn) {
        int maxId = 0;
        String query = "SELECT MAX(" + idColumn + ") AS max_id FROM " + tableName;

        try (ResultSet resultSet = statement.executeQuery(query)) {
            if (resultSet.next()) {
                maxId = resultSet.getInt("max_id");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return maxId + 1;
    }

    public static void main(String[] args) {
        loadData();
        //Test
        System.out.println();
    }
}