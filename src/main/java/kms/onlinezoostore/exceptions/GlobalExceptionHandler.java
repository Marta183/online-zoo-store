package kms.onlinezoostore.exceptions;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

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

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    protected ErrorMessage handleEntityNotFoundException(EntityNotFoundException ex) {
        log.warn(ex.getMessage());
        return new ErrorMessage("Not exists in DB: " + ex.getMessage());
    }

    @ExceptionHandler(PriceConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    protected ErrorMessage handlePriceConflictException(PriceConflictException ex) {
        log.warn(ex.getMessage());
        return new ErrorMessage(ex.getMessage());
    }

    @ExceptionHandler(HierarchyException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    protected ErrorMessage handleHierarchyException(HierarchyException ex) {
        log.warn(ex.getMessage());
        return new ErrorMessage(ex.getMessage());
    }

    @ExceptionHandler(UnavailableProductException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    protected ErrorMessage handleUnavailableProductException(UnavailableProductException ex) {
        log.warn(ex.getMessage());
        return new ErrorMessage(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorMessage handleConstraintViolationException(ConstraintViolationException ex) {
        log.warn(ex.getMessage());
        return new ErrorMessage(ex.getMessage());
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
}
