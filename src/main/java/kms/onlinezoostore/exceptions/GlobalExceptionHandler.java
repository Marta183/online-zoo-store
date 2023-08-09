package kms.onlinezoostore.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestControllerAdvice(annotations = RestController.class)
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityDuplicateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public ErrorMessage handleEntityDuplicateException(EntityDuplicateException ex) {
        log.warn(ex.getMessage());
        return new ErrorMessage("Duplicate value! " + ex.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ResponseBody
    public ErrorMessage handleEntityNotFoundException(EntityNotFoundException ex) {
        log.warn(ex.getMessage());
        return new ErrorMessage("Not exists in DB: " + ex.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorMessage DataIntegrityViolationException(DataIntegrityViolationException ex) {
        log.error(ex.getMessage());
        return new ErrorMessage("Data integrity violation: " + ex.getMessage());
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorMessage handleUnhandledException(Throwable ex) {
        log.error(ex.getMessage());
        return new ErrorMessage(ex.getMessage());
    }
}
