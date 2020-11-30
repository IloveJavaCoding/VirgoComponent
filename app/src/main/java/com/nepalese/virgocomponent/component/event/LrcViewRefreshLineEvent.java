package com.nepalese.virgocomponent.component.event;

public class LrcViewRefreshLineEvent {
    private long time;

    public LrcViewRefreshLineEvent(long time) {
        this.time = time;
    }

    public long getTime() {
        return time;
    }
}
