package de.calltopower.raspisurveillance.impl.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.calltopower.raspisurveillance.api.controller.RSController;
import de.calltopower.raspisurveillance.impl.dto.RSTokenDto;
import de.calltopower.raspisurveillance.impl.dto.RSUserDto;
import de.calltopower.raspisurveillance.impl.dtoservice.RSTokenDtoService;
import de.calltopower.raspisurveillance.impl.dtoservice.RSUserDtoService;
import de.calltopower.raspisurveillance.impl.requestbody.RSSigninRequestBody;
import de.calltopower.raspisurveillance.impl.requestbody.RSSignupRequestBody;
import de.calltopower.raspisurveillance.impl.service.RSAuthService;
import jakarta.validation.Valid;

/**
 * Authentication controller
 */
@RestController
@RequestMapping(path = RSAuthController.PATH)
public class RSAuthController implements RSController {

    /**
     * The controller path
     */
    public static final String PATH = APIPATH + "/auth";

    private static final Logger LOGGER = LoggerFactory.getLogger(RSAuthController.class);

    private RSAuthService authService;
    private RSUserDtoService userDtoService;
    private RSTokenDtoService tokenDtoService;

    /**
     * Initializes the controller
     * 
     * @param userService     Injected user service
     * @param userDtoService  Injected user DTO service
     * @param tokenDtoService Injected token DTO service
     */
    @Autowired
    public RSAuthController(RSAuthService userService, RSUserDtoService userDtoService,
            RSTokenDtoService tokenDtoService) {
        this.authService = userService;
        this.userDtoService = userDtoService;
        this.tokenDtoService = tokenDtoService;
    }

    @SuppressWarnings("javadoc")
    @PostMapping(path = "/signup", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public RSUserDto registerUser(@Valid @RequestBody RSSignupRequestBody requestBody,
            @AuthenticationPrincipal UserDetails userDetails) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Requested signup new user");
        }

        return userDtoService.convert(authService.signup(requestBody, userDetails));
    }

    @SuppressWarnings("javadoc")
    @PostMapping(path = "/signin", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public RSTokenDto authenticateUser(@Valid @RequestBody RSSigninRequestBody requestBody) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Requested signing in user");
        }

        return tokenDtoService.convert(authService.signin(requestBody));
    }

    @Override
    public String getPath() {
        return PATH;
    }

}
