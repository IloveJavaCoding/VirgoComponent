package com.nepalese.virgocomponent.component.event;

public class BookViewRefreshTagEvent {
    private int tag;

    public BookViewRefreshTagEvent(int tag) {
        this.tag = tag;
    }

    public int getTag() {
        return tag;
    }
}
