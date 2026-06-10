package ru.job4j.shortcut.exception;

public class PersonNotFoundException extends RuntimeException {
    public PersonNotFoundException(int id) {
        super("Site with id=%d not found".formatted(id));
    }
}
