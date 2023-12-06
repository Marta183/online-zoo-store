package kms.onlinezoostore.exceptions.authentication;

public class VerificationLimitException extends AccountAlreadyVerifiedException {
    public VerificationLimitException(String msg) {
        super(msg);
    }
}
