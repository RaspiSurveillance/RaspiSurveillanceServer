package de.calltopower.raspisurveillance.impl.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import de.calltopower.raspisurveillance.api.config.RSConfig;

/**
 * Scheduling configuration
 */
@Configuration
@EnableScheduling
public class RSSchedulingConfig implements RSConfig {
    // Nothing to see here...
}
