package ru.practicum.shareit.exceptions;

public class NonExistedUserIdException extends RuntimeException {

    public NonExistedUserIdException(String message) {
        super(message);
    }
}
