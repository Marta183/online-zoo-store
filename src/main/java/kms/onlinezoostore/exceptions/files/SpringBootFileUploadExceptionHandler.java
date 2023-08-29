package kms.onlinezoostore.exceptions.files;

import com.amazonaws.AmazonServiceException;
import kms.onlinezoostore.exceptions.ErrorMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestControllerAdvice(annotations = RestController.class)
public class SpringBootFileUploadExceptionHandler {

    @ExceptionHandler(FileEmptyException.class)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    protected ErrorMessage handleFileEmptyException(FileEmptyException ex) {
        log.warn(ex.getMessage());
        return new ErrorMessage(ex.getMessage());
    }

    @ExceptionHandler(FileNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    protected ErrorMessage handleFileNotFoundException(FileNotFoundException ex) {
        log.warn(ex.getMessage());
        return new ErrorMessage(ex.getMessage());
    }

    @ExceptionHandler(value = {InvalidFileException.class, FileUploadException.class, SpringBootFileUploadException.class, })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ErrorMessage handleFileUploadException(SpringBootFileUploadException ex) {
        log.warn(ex.getMessage());
        return new ErrorMessage(ex.getMessage());
    }

    // Handle exceptions that occur when the call was transmitted successfully, but Amazon S3 couldn't process
    // it, so it returned an error response.
    @ExceptionHandler(AmazonServiceException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    protected ErrorMessage handleAmazonServiceException(AmazonServiceException ex) {
        log.warn(ex.getMessage());
        return new ErrorMessage(ex.getMessage());
    }

//    // Handle exceptions that occur when Amazon S3 couldn't be contacted for a response, or the client
//    // couldn't parse the response from Amazon S3.
//    @ExceptionHandler(SdkClientException.class)
//    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
//    protected ErrorMessage handleSdkClientException(RuntimeException ex) {
//        log.warn(ex.getMessage());
//        return new ErrorMessage(ex.getMessage());
//    }

}

