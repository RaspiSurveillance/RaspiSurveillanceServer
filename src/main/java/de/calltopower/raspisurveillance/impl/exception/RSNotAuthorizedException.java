package de.calltopower.raspisurveillance.impl.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import de.calltopower.raspisurveillance.api.exception.RSException;

/**
 * "Not authorized" exception
 */
@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class RSNotAuthorizedException extends RuntimeException implements RSException {

    private static final long serialVersionUID = -5490671963188802180L;

    @SuppressWarnings("javadoc")
    public RSNotAuthorizedException() {
        super();
    }

    @SuppressWarnings("javadoc")
    public RSNotAuthorizedException(String msg) {
        super(msg);
    }

    @SuppressWarnings("javadoc")
    public RSNotAuthorizedException(Exception e) {
        super(e);
    }

}
