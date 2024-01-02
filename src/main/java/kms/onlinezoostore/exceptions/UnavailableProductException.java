package kms.onlinezoostore.exceptions;

public class UnavailableProductException extends RuntimeException {
    public UnavailableProductException(String message) {
        super(message);
    }
}
