package gd.rf.tekporconsult.mypronouncer.model;

public class Migration {
    String name;
    long date;

    public Migration(String name, long date) {
        this.name = name;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
