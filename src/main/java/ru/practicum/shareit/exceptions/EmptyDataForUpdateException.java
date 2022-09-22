package ru.practicum.shareit.exceptions;

public class EmptyDataForUpdateException extends RuntimeException {

    public EmptyDataForUpdateException(String message) {
        super(message);
    }
}
