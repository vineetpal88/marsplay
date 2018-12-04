package com.vineet.marsplay.glide;

public interface ProgressListener {
    void update(long bytesRead, long contentLength, boolean done);
}
