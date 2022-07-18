package gd.rf.tekporconsult.mypronouncer.model;

public class Definition {
    String category;
    String word;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

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

    public Definition( String word,String category, String definition) {
        this.category = category;
        this.word = word;
        this.definition = definition;
    }

    String definition;
}
