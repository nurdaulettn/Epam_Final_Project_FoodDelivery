package kz.nurdaulet.exception;

public class UserCreatingException extends RuntimeException {
    public UserCreatingException(String message) {
        super(message);
    }
}
