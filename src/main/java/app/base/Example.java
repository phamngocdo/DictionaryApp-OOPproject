package app.base;

public class Example extends Base {
    private int explainId;
    private String example;
    private String translate;

    public Example(int exampleId, int explainId, String example, String translate) {
        super(exampleId);
        this.explainId = explainId;
        this.example = example;
        this.translate = translate;
    }

    public int getExplainId() {
        return explainId;
    }

    public void setExplainId(int explainId) {
        this.explainId = explainId;
    }

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }

    public String getTranslate() {
        return translate;
    }

    public void setTranslate(String translate) {
        this.translate = translate;
    }
}