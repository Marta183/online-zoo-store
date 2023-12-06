package kms.onlinezoostore.exceptions.authentication;

import org.springframework.security.core.AuthenticationException;

public class InvalidVerificationLink extends AuthenticationException {

    public InvalidVerificationLink(String msg) {
        super(msg);
    }

    public InvalidVerificationLink() {
        super("Verification link has expired. Try to resend verification link");
    }
}
