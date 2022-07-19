package gd.rf.tekporconsult.mypronouncer.model;

public class Definition {
    String word;


    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public Definition( String word, String definition) {
        this.word = word;
        this.definition = definition;
    }

    String definition;
}
