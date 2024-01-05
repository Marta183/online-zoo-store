package kms.onlinezoostore.exceptions.authentication;

import io.jsonwebtoken.JwtException;
import kms.onlinezoostore.exceptions.ErrorMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@RestControllerAdvice(annotations = RestController.class)
public class AuthenticationExceptionHandler {

    @ExceptionHandler(AccountAlreadyVerifiedException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    protected ErrorMessage handleAccountAlreadyVerifiedException(AccountAlreadyVerifiedException ex) {
        log.warn(ex.getMessage());
        return new ErrorMessage(ex.getMessage());
    }

    @ExceptionHandler(InvalidVerificationLink.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    protected ErrorMessage handleInvalidVerificationLink(InvalidVerificationLink ex) {
        log.warn(ex.getMessage());
        return new ErrorMessage(ex.getMessage());
    }

    @ExceptionHandler(VerificationLimitException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    protected ErrorMessage handleVerificationLimitException(VerificationLimitException ex) {
        log.warn(ex.getMessage());
        return new ErrorMessage(ex.getMessage());
    }


    @ExceptionHandler(JwtException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    protected ErrorMessage handleJwtException(JwtException ex) {
        log.warn(ex.getMessage());
        return new ErrorMessage("Invalid web token: " + ex.getMessage());
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    protected ErrorMessage handleBadCredentialsException(BadCredentialsException ex) {
        log.warn(ex.getMessage());
        return new ErrorMessage("Invalid credentials: " + ex.getMessage());
    }
}
