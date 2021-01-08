package de.calltopower.raspisurveillance.impl.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import de.calltopower.raspisurveillance.api.exception.RSException;

/**
 * "General" exception
 */
@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
public class RSGeneralException extends RuntimeException implements RSException {

    private static final long serialVersionUID = 7422448735206699827L;

    @SuppressWarnings("javadoc")
    public RSGeneralException() {
        super();
    }

    @SuppressWarnings("javadoc")
    public RSGeneralException(String msg) {
        super(msg);
    }

    @SuppressWarnings("javadoc")
    public RSGeneralException(Exception e) {
        super(e);
    }

}
