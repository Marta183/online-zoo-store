package kms.onlinezoostore.exceptions.files;

public class InvalidFileException extends SpringBootFileUploadException {
    public InvalidFileException(String message) {
        super(message);
    }
}
