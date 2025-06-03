package cz.rohlik.gsz.exception;

public class ProductCannotBeDeleted extends RuntimeException {
    public ProductCannotBeDeleted(String message) {
        super(message);
    }
}
