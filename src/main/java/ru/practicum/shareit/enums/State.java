package ru.practicum.shareit.enums;

public enum State {
    PAST("PAST"),
    REJECTED("REJECTED"),
    WAITING("WAITING"),
    CURRENT("CURRENT"),
    FUTURE("FUTURE"),
    ALL("ALL");

    private final String STATE;

    State(String STATE) {
        this.STATE = STATE;
    }

    public String getSTATE() {
        return STATE;
    }
}
