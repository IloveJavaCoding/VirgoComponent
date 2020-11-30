package com.nepalese.virgocomponent.component.event;

public class BookSentTotalLineEvent {
    private int totalLines;

    public BookSentTotalLineEvent(int totalLines) {
        this.totalLines = totalLines;
    }

    public int getTotalLines() {
        return totalLines;
    }
}
