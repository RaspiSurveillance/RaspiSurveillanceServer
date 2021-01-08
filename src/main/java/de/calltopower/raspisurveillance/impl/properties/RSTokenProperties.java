package de.calltopower.raspisurveillance.impl.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import de.calltopower.raspisurveillance.api.properties.RSProperties;
import lombok.Getter;

/**
 * Property mapping for prefix "security.token"
 */
@Getter
@Configuration
@ConfigurationProperties(prefix = RSTokenProperties.PREFIX)
public class RSTokenProperties implements RSProperties {

    /**
     * prefix in the application.properties
     */
    public static final String PREFIX = "security.token";

    @Value("${" + PREFIX + ".secret}")
    private String secret;

    @Value("${" + PREFIX + ".expirationMs}")
    private int expirationMs;

    @Override
    public String getPrefix() {
        return PREFIX;
    }

}
