package kms.onlinezoostore.exceptions;

public class EntityDuplicateException extends RuntimeException  {
    public EntityDuplicateException(String className, String fieldName, String fieldValue) {
        super(className + " " + fieldName + " already exists: " + fieldValue);
    }

    public EntityDuplicateException(String fieldName, String fieldValue) {
        super(fieldName + " already exists: " + fieldValue);
    }

    public EntityDuplicateException(String message) {
        super(message);
    }
}
