package ru.yandex.practicum.filmorate.exception;

public class InvalidDataException extends Exception {
    public InvalidDataException(final String message) {
        super(message);
    }
}