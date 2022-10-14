package ru.practicum.shareit.enums;

public enum State {
    PAST("PAST"),
    REJECTED("REJECTED"),
    WAITING("WAITING"),
    CURRENT("CURRENT"),
    FUTURE("FUTURE"),
    ALL("ALL");

    private final String state;

    State(String state) {
        this.state = state;
    }

    public String getSTATE() {
        return state;
    }
}
