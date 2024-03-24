package com.example.dreamteam.simulation;

public class AttentionTrack {
    Long time;
    int attendedItemId;

    public AttentionTrack(Long time, int attendedItemId) {
        this.time = time;
        this.attendedItemId = attendedItemId;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public int getattendedItemId() {
        return attendedItemId;
    }

    public void setattendedItemId(int attendedItemId) {
        this.attendedItemId = attendedItemId;
    }
}
