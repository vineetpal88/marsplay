package com.vineet.marsplay.event;

public class RefershEvent {
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    private String imageUrl;
    private String status;

    public RefershEvent(String status, String imageUrl) {
        this.status = status;
        this.imageUrl = imageUrl;
    }
}
