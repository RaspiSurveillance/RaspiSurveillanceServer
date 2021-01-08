package de.calltopower.raspisurveillance.impl.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import de.calltopower.raspisurveillance.api.exception.RSException;

/**
 * "User" exception
 */
@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class RSUserException extends RuntimeException implements RSException {

    private static final long serialVersionUID = -3633741342985442301L;

    @SuppressWarnings("javadoc")
    public RSUserException() {
        super();
    }

    @SuppressWarnings("javadoc")
    public RSUserException(String msg) {
        super(msg);
    }

    @SuppressWarnings("javadoc")
    public RSUserException(Exception e) {
        super(e);
    }

}
