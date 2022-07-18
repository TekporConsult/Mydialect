package gd.rf.tekporconsult.mypronouncer.model;
public class Category {
    String word;

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Category(String word, String category) {
        this.word = word;
        this.category = category;
    }

    String category;
}
