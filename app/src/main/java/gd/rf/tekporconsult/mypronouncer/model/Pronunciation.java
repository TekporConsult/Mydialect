package gd.rf.tekporconsult.mypronouncer.model;

public class Pronunciation {
    String word,phonics;

    public String getWord() {
        return word;
    }

    public Pronunciation(String word, String phonics) {
        this.word = word;
        this.phonics = phonics;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getPhonics() {
        return phonics;
    }

    public void setPhonics(String phonics) {
        this.phonics = phonics;
    }
}
