package ru.practicum.shareit.exceptions;

public class AlreadyExistsEmailException extends RuntimeException {

    public AlreadyExistsEmailException(String message) {
        super(message);
    }
}
