package kms.onlinezoostore.exceptions;

public class EntityCannotBeDeleted extends RuntimeException {
    public EntityCannotBeDeleted(String message) {
        super(message);
    }
}
