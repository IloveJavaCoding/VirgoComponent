package com.nepalese.virgocomponent.component.event;

public class BookViewClickEvent {
    private boolean isShow;

    public BookViewClickEvent(boolean isShow) {
        this.isShow = isShow;
    }

    public boolean isShow() {
        return isShow;
    }
}
