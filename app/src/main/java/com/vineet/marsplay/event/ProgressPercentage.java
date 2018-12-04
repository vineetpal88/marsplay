package com.vineet.marsplay.event;

public class ProgressPercentage {

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }

    private int progress;
    private boolean isShow;

    public ProgressPercentage(boolean isShow, int progress) {
        this.isShow = isShow;
        this.progress = progress;
    }
}
