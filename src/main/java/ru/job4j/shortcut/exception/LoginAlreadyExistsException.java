package ru.job4j.shortcut.exception;

public class LoginAlreadyExistsException extends RuntimeException {
    public LoginAlreadyExistsException(String login) {
        super(new StringBuilder()
                .append("Login already exists: ")
                .append(login)
                .toString());
    }
}
