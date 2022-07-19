package gd.rf.tekporconsult.mypronouncer.model;

public class MigrationHistory{
    String url;
    Integer at;
    Integer to;
    Long date;

    @Override
    public String toString() {
        return "MigrationHistory{" +
                "url='" + url + '\'' +
                ", at=" + at +
                ", to=" + to +
                ", date=" + date +
                ", type='" + type + '\'' +
                '}';
    }

    String type;

    public MigrationHistory(String url, Integer at, Integer to, Long date, String type) {
        this.url = url;
        this.at = at;
        this.to = to;
        this.date = date;
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getAt() {
        return at;
    }

    public void setAt(Integer at) {
        this.at = at;
    }

    public Integer getTo() {
        return to;
    }

    public void setTo(Integer to) {
        this.to = to;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
