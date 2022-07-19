package gd.rf.tekporconsult.mypronouncer.model;

public class Example {
    String word;

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }

    String example;

    public Example(String word, String example) {
        this.word = word;
        this.example = example;
    }
}
