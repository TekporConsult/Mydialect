package gd.rf.tekporconsult.mypronouncer.model;

public class Summary {
    long totalProgress;
    long dictionaryProgress;
    long phonicsProgress;

    public Summary(long totalProgress, long dictionaryProgress, long phonicsProgress) {
        this.totalProgress = totalProgress;
        this.dictionaryProgress = dictionaryProgress;
        this.phonicsProgress = phonicsProgress;
    }

    public long getTotalProgress() {
        return totalProgress;
    }

    public void setTotalProgress(long totalProgress) {
        this.totalProgress = totalProgress;
    }

    public long getDictionaryProgress() {
        return dictionaryProgress;
    }

    public void setDictionaryProgress(long dictionaryProgress) {
        this.dictionaryProgress = dictionaryProgress;
    }

    public long getPhonicsProgress() {
        return phonicsProgress;
    }

    public void setPhonicsProgress(long phonicsProgress) {
        this.phonicsProgress = phonicsProgress;
    }
}
