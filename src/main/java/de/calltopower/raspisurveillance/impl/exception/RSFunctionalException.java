package de.calltopower.raspisurveillance.impl.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import de.calltopower.raspisurveillance.api.exception.RSException;

/**
 * "Functional" exception
 */
@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
public class RSFunctionalException extends RuntimeException implements RSException {

    private static final long serialVersionUID = 5950759560263566315L;

    @SuppressWarnings("javadoc")
    public RSFunctionalException() {
        super();
    }

    @SuppressWarnings("javadoc")
    public RSFunctionalException(String msg) {
        super(msg);
    }

    @SuppressWarnings("javadoc")
    public RSFunctionalException(Exception e) {
        super(e);
    }

}
