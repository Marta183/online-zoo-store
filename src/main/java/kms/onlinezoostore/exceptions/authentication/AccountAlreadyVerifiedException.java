package kms.onlinezoostore.exceptions.authentication;

import org.springframework.security.core.AuthenticationException;

public class AccountAlreadyVerifiedException extends AuthenticationException {

    public AccountAlreadyVerifiedException(String msg) {
        super(msg);
    }
}
