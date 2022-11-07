package ru.practicum.shareit.exceptions;

public class AlreadyExistsStatusException extends RuntimeException {

    public AlreadyExistsStatusException(String message) {
        super(message);
    }
}
