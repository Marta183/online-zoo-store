package kms.onlinezoostore.exceptions.files;

public class FileUploadException extends SpringBootFileUploadException {
    public FileUploadException(String message) {
        super(message);
    }
}
