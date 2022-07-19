package gd.rf.tekporconsult.mypronouncer.model;

public class Notification {
    Integer data;

    public Integer getData() {
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

    public Notification(Integer data, Integer rememberMe) {
        this.data = data;
        this.rememberMe = rememberMe;
    }

    Integer rememberMe;
}
