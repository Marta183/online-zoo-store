package kms.onlinezoostore.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@RestControllerAdvice(annotations = RestController.class)
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityDuplicateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    protected ErrorMessage handleEntityDuplicateException(EntityDuplicateException ex) {
        log.warn(ex.getMessage());
        return new ErrorMessage("Duplicate value! " + ex.getMessage());
    }

    @ExceptionHandler(EntityCannotBeDeleted.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    protected ErrorMessage handleEntityCannotBeDeleted(EntityCannotBeDeleted ex) {
        log.warn(ex.getMessage());
        return new ErrorMessage(ex.getMessage());
    }

    @ExceptionHandler(EntityCannotBeUpdated.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    protected ErrorMessage handleEntityCannotBeUpdated(EntityCannotBeUpdated ex) {
        log.warn(ex.getMessage());
        return new ErrorMessage(ex.getMessage());
    }

    @ExceptionHandler(PriceConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    protected ErrorMessage handlePriceConflictException(PriceConflictException ex) {
        log.warn(ex.getMessage());
        return new ErrorMessage(ex.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ResponseBody
    protected ErrorMessage handleEntityNotFoundException(EntityNotFoundException ex) {
        log.warn(ex.getMessage());
        return new ErrorMessage("Not exists in DB: " + ex.getMessage());
    }

    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    protected ErrorMessage handleBindException(BindException ex) {
        log.error(ex.getMessage());
        String errorMessage = "Validation error occurred: ";
        for (FieldError fieldError : ex.getFieldErrors()) {
            errorMessage += fieldError.getDefaultMessage() + "; ";
        }
        if (errorMessage.endsWith("; ")) {
            errorMessage = errorMessage.substring(0, errorMessage.length()-2);
        }
        return new ErrorMessage(errorMessage);
    }

//    @ExceptionHandler(Throwable.class)
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    @ResponseBody
//    protected ErrorMessage handleUnhandledException(Throwable ex) {
//        log.info("Fatal exception ===> {}", ex);
//        log.error(ex.getMessage());
//        return new ErrorMessage("We apologize. Something is not right");
//    }
}
