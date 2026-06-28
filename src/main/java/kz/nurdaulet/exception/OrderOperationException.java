package kz.nurdaulet.exception;

public class OrderOperationException extends RuntimeException {
    public OrderOperationException(String message) {
        super(message);
    }
}
