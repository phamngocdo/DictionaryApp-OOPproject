package app.base;

public class Explain extends Base {
    private int wordId;
    private String type;
    private String meaning;

    public Explain(int explainId, int wordId, String type, String meaning) {
        super(explainId);
        this.wordId = wordId;
        this.type = type;
        this.meaning = meaning;
    }

    public int getWordId() {
        return wordId;
    }

    public void setWordId(int wordId) {
        this.wordId = wordId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }
}