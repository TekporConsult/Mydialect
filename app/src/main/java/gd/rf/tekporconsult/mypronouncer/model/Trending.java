package gd.rf.tekporconsult.mypronouncer.model;

public class Trending {
    String definition, word;

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public Trending(String definition, String word) {
        this.definition = definition;
        this.word = word;
    }
}
