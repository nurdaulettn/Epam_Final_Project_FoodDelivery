package kz.nurdaulet.exception;

public class DeletingActiveFoodException extends RuntimeException {
    public DeletingActiveFoodException(String message) {
        super(message);
    }
}
