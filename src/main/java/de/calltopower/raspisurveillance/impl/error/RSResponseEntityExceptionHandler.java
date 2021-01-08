package de.calltopower.raspisurveillance.impl.error;

import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import de.calltopower.raspisurveillance.api.error.RSExceptionHandler;
import de.calltopower.raspisurveillance.impl.exception.RSFunctionalException;
import de.calltopower.raspisurveillance.impl.exception.RSGeneralException;
import de.calltopower.raspisurveillance.impl.exception.RSNotAuthorizedException;
import de.calltopower.raspisurveillance.impl.exception.RSNotFoundException;
import de.calltopower.raspisurveillance.impl.exception.RSUserException;

/**
 * An exception handler for all of the occurring exceptions
 */
@ControllerAdvice
@RestController
public class RSResponseEntityExceptionHandler extends ResponseEntityExceptionHandler implements RSExceptionHandler {

    @SuppressWarnings({ "static-method", "javadoc" })
    @ExceptionHandler(RSNotFoundException.class)
    public final ResponseEntity<RSErrorDetails> handleNotFoundException(RSNotFoundException ex, WebRequest request) {
        RSErrorDetails errorDetails = new RSErrorDetails(new Date(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @SuppressWarnings({ "static-method", "javadoc" })
    @ExceptionHandler(RSUserException.class)
    public final ResponseEntity<RSErrorDetails> handleNotFoundException(RSUserException ex, WebRequest request) {
        RSErrorDetails errorDetails = new RSErrorDetails(new Date(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @SuppressWarnings({ "static-method", "javadoc" })
    @ExceptionHandler(RSFunctionalException.class)
    public final ResponseEntity<RSErrorDetails> handleNotFoundException(RSFunctionalException ex,
            WebRequest request) {
        RSErrorDetails errorDetails = new RSErrorDetails(new Date(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @SuppressWarnings({ "static-method", "javadoc" })
    @ExceptionHandler(RSNotAuthorizedException.class)
    public final ResponseEntity<RSErrorDetails> handleNotFoundException(RSNotAuthorizedException ex,
            WebRequest request) {
        RSErrorDetails errorDetails = new RSErrorDetails(new Date(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.UNAUTHORIZED);
    }

    @SuppressWarnings({ "static-method", "javadoc" })
    @ExceptionHandler(RSGeneralException.class)
    public final ResponseEntity<RSErrorDetails> handleZPAResultException(RSGeneralException ex, WebRequest request) {
        RSErrorDetails errorDetails = new RSErrorDetails(new Date(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @SuppressWarnings({ "static-method", "javadoc" })
    @ExceptionHandler(Exception.class)
    public final ResponseEntity<RSErrorDetails> handleAllExceptions(Exception ex, WebRequest request) {
        RSErrorDetails errorDetails = new RSErrorDetails(new Date(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
