package com.example.joseph.picsound.Utils;

public class AudioInformation {
    private int resourceId;
    private int streamId;
    private float volume;
    private long delay;
    private int loopNumber;

    public AudioInformation(int resourceId, int streamId, float volume, long delay, int loopNumber) {
        this.resourceId = resourceId;
        this.streamId = streamId;
        this.volume = volume;
        this.delay = delay;
        this.loopNumber = loopNumber;
    }

    public int getLoopNumber() {
        return loopNumber;
    }

    public void setLoopNumber(int loopNumber) {
        this.loopNumber = loopNumber;
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public float getVolume() {
        return volume;
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

    public int getStreamId() {
        return streamId;
    }

    public void setStreamId(int streamId) {
        this.streamId = streamId;
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }
}
