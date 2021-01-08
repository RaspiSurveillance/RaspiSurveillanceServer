package de.calltopower.raspisurveillance.impl.utils;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import de.calltopower.raspisurveillance.api.utils.RSUtils;
import de.calltopower.raspisurveillance.impl.model.RSUserDetailsImpl;
import de.calltopower.raspisurveillance.impl.properties.RSTokenProperties;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

/**
 * Token utilities
 */
@Component
public class RSTokenUtils implements RSUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(RSTokenUtils.class);

    private RSTokenProperties tokenProperties;

    /**
     * Constructor
     * 
     * @param appProperties The application properties
     */
    @Autowired
    RSTokenUtils(RSTokenProperties appProperties) {
        this.tokenProperties = appProperties;
    }

    /**
     * Generates a new JWT
     * 
     * @param authentication The authentication
     * @return A JWT
     */
    public String generateJwtToken(Authentication authentication) {

        RSUserDetailsImpl userPrincipal = (RSUserDetailsImpl) authentication.getPrincipal();

        // @formatter:off
        return Jwts.builder()
                .setSubject((userPrincipal.getUsername()))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + tokenProperties.getExpirationMs()))
                .signWith(SignatureAlgorithm.HS512, tokenProperties.getSecret()).compact();
        // @formatter:on
    }

    /**
     * Returns the username from a JWT
     * 
     * @param token The JWT
     * @return the username
     */
    public String getUserNameFromJwtToken(String token) {
        // @formatter:off
        return Jwts.parser()
                    .setSigningKey(tokenProperties.getSecret())
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        // @formatter:on
    }

    /**
     * Validates a JWT
     * 
     * @param authToken The authentication token
     * @return True if successfully validated, false else
     */
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(tokenProperties.getSecret()).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            LOGGER.error("Invalid token signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            LOGGER.error("Invalid token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            LOGGER.error("Token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            LOGGER.error("Token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            LOGGER.error("Token claims string is empty: {}", e.getMessage());
        }

        return false;
    }

}
