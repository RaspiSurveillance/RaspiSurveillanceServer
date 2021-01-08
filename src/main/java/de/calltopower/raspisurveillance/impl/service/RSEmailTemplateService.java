package de.calltopower.raspisurveillance.impl.service;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import de.calltopower.raspisurveillance.api.service.RSService;
import de.calltopower.raspisurveillance.impl.properties.RSSettingsProperties;

/**
 * Service for email templates
 */
@Service
public class RSEmailTemplateService implements RSService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RSEmailTemplateService.class);

    private TemplateEngine templateEngine;
    private RSSettingsProperties settingsProperties;

    /**
     * Initializes the service
     * 
     * @param templateEngine     The template engine
     * @param settingsProperties The settings properties
     */
    @Autowired
    public RSEmailTemplateService(TemplateEngine templateEngine, RSSettingsProperties settingsProperties) {
        this.templateEngine = templateEngine;
        this.settingsProperties = settingsProperties;
    }

    /**
     * Builds the account created message
     * 
     * @param username The username
     * @return account created message
     */
    public String buildAccountCreated(String username) {
        LOGGER.debug("Building account created HTML email");

        Map<String, String> templateVariables = new HashMap<>();
        templateVariables.put("url", settingsProperties.getUrl());
        templateVariables.put("url_text", settingsProperties.getUrl());
        templateVariables.put("username", username);

        return build("account_created", templateVariables);
    }

    /**
     * Builds the forgot password message
     * 
     * @param token Token
     * @return forgot password message
     */
    public String buildForgotPassword(String token) {
        LOGGER.debug("Building forgot password HTML email");

        Map<String, String> templateVariables = new HashMap<>();
        templateVariables.put("url", settingsProperties.getUrlPasswordReset());
        templateVariables.put("url_text", settingsProperties.getUrlPasswordReset());
        templateVariables.put("token", token);

        return build("forgot_password", templateVariables);
    }

    /**
     * Builds the new password generated message
     * 
     * @param newPassword The new password
     * @return new password generated message
     */
    public String buildNewPasswordGenerated(String newPassword) {
        LOGGER.debug("Building new password generated HTML email");

        Map<String, String> templateVariables = new HashMap<>();
        templateVariables.put("url", settingsProperties.getUrlPasswordResetSuccess());
        templateVariables.put("url_text", settingsProperties.getUrlPasswordResetSuccess());
        templateVariables.put("password", newPassword);

        return build("new_password_generated", templateVariables);
    }

    /**
     * Builds the verify email address message
     * 
     * @param userId The user ID
     * @param token  The token
     * @return verify email address message
     */
    public String buildVerifyEmailAddress(String userId, String token) {
        LOGGER.debug("Building verify email address HTML email");

        Map<String, String> templateVariables = new HashMap<>();
        String url = String.format(settingsProperties.getUrlUserVerification(), userId);
        templateVariables.put("url", url);
        templateVariables.put("url_text", url);
        templateVariables.put("token", token);

        return build("verify_email_address", templateVariables);
    }

    /**
     * Builds the email address verified message
     * 
     * @return email address verified message
     */
    public String buildEmailAddressVerified() {
        LOGGER.debug("Building email address verified HTML email");

        Map<String, String> templateVariables = new HashMap<>();

        return build("email_address_verified", templateVariables);
    }

    private String build(String template, Map<String, String> templateVariables) {
        Context context = new Context();
        for (Map.Entry<String, String> entry : templateVariables.entrySet()) {
            context.setVariable(entry.getKey(), entry.getValue());
        }

        return templateEngine.process(template, context);
    }

}
