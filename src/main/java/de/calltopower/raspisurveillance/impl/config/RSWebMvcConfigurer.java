package de.calltopower.raspisurveillance.impl.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import de.calltopower.raspisurveillance.api.config.RSConfig;

/**
 * Web MVC configuration - only allow CORS-requests in dev mode
 */
@Configuration
@EnableWebMvc
@Profile("dev")
public class RSWebMvcConfigurer implements WebMvcConfigurer, RSConfig {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**").allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS");
    }

}
