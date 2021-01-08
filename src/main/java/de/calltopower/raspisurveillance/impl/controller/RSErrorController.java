package de.calltopower.raspisurveillance.impl.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.calltopower.raspisurveillance.api.controller.RSController;
import de.calltopower.raspisurveillance.impl.exception.RSNotFoundException;

/**
 * Error controller
 */
@RestController
@RequestMapping(path = RSErrorController.PATH)
public class RSErrorController implements RSController, ErrorController {

    /**
     * The controller path
     */
    public static final String PATH = "";

    /**
     * The controller error path
     */
    public static final String PATH_ERROR = "/error";

    private static final Logger LOGGER = LoggerFactory.getLogger(RSErrorController.class);

    @SuppressWarnings("javadoc")
    @RequestMapping(value = PATH_ERROR)
    public void error() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Requested error");
        }

        throw new RSNotFoundException("Error");
    }

    @Override
    public String getErrorPath() {
        return PATH_ERROR;
    }

    @Override
    public String getPath() {
        return PATH;
    }

}
