package cz.rohlik.gsz.exception;

public class OrderCancelException extends RuntimeException {
    public OrderCancelException(String message) {
        super(message);
    }
}
