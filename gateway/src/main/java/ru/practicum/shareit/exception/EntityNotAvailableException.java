package ru.practicum.shareit.exception;

public class EntityNotAvailableException extends RuntimeException {
    public EntityNotAvailableException(String message) {
        super(message);
    }
}
