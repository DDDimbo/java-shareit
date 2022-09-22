package ru.practicum.shareit.exceptions;

public class EmptyRequestParameterException extends RuntimeException {

    public EmptyRequestParameterException(String message) {
        super(message);
    }
}
