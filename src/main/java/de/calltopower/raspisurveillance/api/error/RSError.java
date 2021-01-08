package de.calltopower.raspisurveillance.api.error;

import java.util.Date;

/**
 * An error
 */
public interface RSError {

    /**
     * Returns the timestamp of the error
     * 
     * @return The timestamp
     */
    default Date getTimestamp() {
        return new Date();
    }

    /**
     * Returns the message of the error
     * 
     * @return The message
     */
    String getMessage();

    /**
     * Returns the details of the error
     * 
     * @return The details
     */
    String getDetails();

}
