package gd.rf.tekporconsult.mypronouncer.model;

public class Notification {
    long data;

    public long getData() {
        return data;
    }

    public void setData(Integer data) {
        this.data = data;
    }

    public Integer getRememberMe() {
        return rememberMe;
    }

    public void setRememberMe(Integer rememberMe) {
        this.rememberMe = rememberMe;
    }

    public Notification(long data, Integer rememberMe) {
        this.data = data;
        this.rememberMe = rememberMe;
    }

    Integer rememberMe;
}
