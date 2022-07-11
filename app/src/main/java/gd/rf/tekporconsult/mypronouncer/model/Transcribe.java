package gd.rf.tekporconsult.mypronouncer.model;

public class Transcribe {
    String fromLang;
    String fromKey;
    int id;

    public String getFromLang() {
        return fromLang;
    }

    public void setFromLang(String fromLang) {
        this.fromLang = fromLang;
    }

    public String getFromKey() {
        return fromKey;
    }

    public void setFromKey(String fromKey) {
        this.fromKey = fromKey;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToLang() {
        return toLang;
    }

    public void setToLang(String toLang) {
        this.toLang = toLang;
    }

    public String getToKey() {
        return toKey;
    }

    public void setToKey(String toKey) {
        this.toKey = toKey;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Transcribe(int id, String fromLang, String fromKey, String message, String toLang, String toKey) {
        this.fromLang = fromLang;
        this.fromKey = fromKey;
        this.id = id;
        this.message = message;
        this.toLang = toLang;
        this.toKey = toKey;
    }

    String message;
    String toLang;
    String toKey;
}
