package ru.practicum.shareit.exception;

public class EmailUniqueException extends RuntimeException {
    public EmailUniqueException(String message) {
        super(message);
    }
}
