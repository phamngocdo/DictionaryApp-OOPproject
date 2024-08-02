package app.base;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Word extends Base {
    private String word;
    private String pronounce;

    public Word(int wordId, String word, String pronounce) {
        super(wordId);
        this.word = word;
        this.pronounce = pronounce;
    }

    @SuppressWarnings("exports")
    public Word(ResultSet resultSet) throws SQLException {
        this(resultSet.getInt("word_id"), 
            resultSet.getString("word"), 
            resultSet.getString("pronounce"));
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getPronounce() {
        return pronounce;
    }

    public void setPronounce(String pronounce) {
        this.pronounce = pronounce;
    }
}
