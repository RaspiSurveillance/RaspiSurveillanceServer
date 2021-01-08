package de.calltopower.raspisurveillance.impl.error;

import java.util.Date;

import de.calltopower.raspisurveillance.api.error.RSError;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * An error details object that is sent as an answer to erroneous requests
 */
@Getter
@AllArgsConstructor
public class RSErrorDetails implements RSError {

    private Date timestamp;
    private String message;
    private String details;

}
