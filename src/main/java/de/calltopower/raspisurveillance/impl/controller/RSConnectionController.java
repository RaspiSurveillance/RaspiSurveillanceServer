package de.calltopower.raspisurveillance.impl.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.calltopower.raspisurveillance.api.controller.RSController;

/**
 * Connection controller
 */
@RestController
@RequestMapping(path = RSConnectionController.PATH)
public class RSConnectionController implements RSController {

    /**
     * The controller path
     */
    public static final String PATH = APIPATH + "/connection";

    private static final Logger LOGGER = LoggerFactory.getLogger(RSConnectionController.class);

    /**
     * Initializes the controller
     */
    @Autowired
    public RSConnectionController() {
        // Nothing to see here...
    }

    @SuppressWarnings("javadoc")
    @GetMapping(path = "/available", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public boolean checkAvailability(@AuthenticationPrincipal UserDetails userDetails) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Requested check availability");
        }

        return true;
    }

    @SuppressWarnings("javadoc")
    @GetMapping(path = "/authorized", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public boolean checkAuthorized(@AuthenticationPrincipal UserDetails userDetails) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Requested check authorized");
        }

        return true;
    }

    @Override
    public String getPath() {
        return PATH;
    }

}
