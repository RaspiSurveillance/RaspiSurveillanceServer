package de.calltopower.raspisurveillance.impl.exception;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import de.calltopower.raspisurveillance.api.exception.RSEntryPoint;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * "Auth Entry Point" exception
 * 
 * Triggered any time an unauthenticated user requests a secured HTTP resource
 */
@Component
public class RSAuthEntryPointJwt implements AuthenticationEntryPoint, RSEntryPoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(RSAuthEntryPointJwt.class);

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {
        LOGGER.error("Unauthorized error: {}", authException.getMessage());
        throw new RSNotAuthorizedException("Not authorized");
    }

}
