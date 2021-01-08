package de.calltopower.raspisurveillance.api.controller;

/**
 * A REST controller
 */
public interface RSController {

    /**
     * Use this API path for the controller paths
     */
    public static String APIPATH = "/api";

    /**
     * Returns the "global" path of the controller
     * 
     * @return The "global" path of the controller
     */
    String getPath();

}
