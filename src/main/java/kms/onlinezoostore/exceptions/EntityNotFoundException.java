package kms.onlinezoostore.exceptions;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String entityName, Long id) {
        super(entityName + " not found with id=" + id);
    }

    public EntityNotFoundException(String entityName, String key) {
        super(entityName + " not found with key=" + key);
    }

    public EntityNotFoundException(String message) {
        super(message);
    }
}
