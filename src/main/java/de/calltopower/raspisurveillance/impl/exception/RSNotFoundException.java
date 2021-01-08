package de.calltopower.raspisurveillance.impl.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import de.calltopower.raspisurveillance.api.exception.RSException;

/**
 * "Not Found" exception
 */
@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class RSNotFoundException extends RuntimeException implements RSException {

    private static final long serialVersionUID = -1932271206882271621L;

    @SuppressWarnings("javadoc")
    public RSNotFoundException() {
        super();
    }

    @SuppressWarnings("javadoc")
    public RSNotFoundException(String msg) {
        super(msg);
    }

    @SuppressWarnings("javadoc")
    public RSNotFoundException(Exception e) {
        super(e);
    }

}
