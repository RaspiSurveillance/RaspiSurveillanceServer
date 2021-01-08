package de.calltopower.raspisurveillance.impl.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

import de.calltopower.raspisurveillance.api.config.RSConfig;

/**
 * Cache configuration
 */
@Configuration
@EnableCaching
public class RSCacheConfig implements RSConfig {
    // Nothing to see here...
}
