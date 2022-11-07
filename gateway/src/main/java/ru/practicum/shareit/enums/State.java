package ru.practicum.shareit.enums;

import java.util.Optional;

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

    public static Optional<State> from(String stringState) {
        for (State state : values()) {
            if (state.name().equalsIgnoreCase(stringState)) {
                return Optional.of(state);
            }
        }
        return Optional.empty();
    }
}
