package de.calltopower.raspisurveillance.impl.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

import de.calltopower.raspisurveillance.api.service.RSService;
import de.calltopower.raspisurveillance.impl.model.RSUserForgotPasswordTokenModel;
import de.calltopower.raspisurveillance.impl.model.RSUserModel;
import de.calltopower.raspisurveillance.impl.model.RSUserVerificationTokenModel;
import de.calltopower.raspisurveillance.impl.properties.RSSettingsProperties;

@Service
public class RSEmailService implements RSService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RSEmailService.class);

    private RSSettingsProperties settingsProperties;
    private JavaMailSender javaMailSender;
    private RSEmailTemplateService emailTemplateService;

    /**
     * Initializes the service
     * 
     * @param settingsProperties   Settings properties
     * @param javaMailSender       Java Mail Sender
     * @param emailTemplateService Email template service
     */
    @Autowired
    public RSEmailService(RSSettingsProperties settingsProperties, JavaMailSender javaMailSender,
            RSEmailTemplateService emailTemplateService) {
        this.settingsProperties = settingsProperties;
        this.javaMailSender = javaMailSender;
        this.emailTemplateService = emailTemplateService;
    }

    /**
     * Sends an account created email
     * 
     * @param toEmail The email address to send the email to
     * @param user    The user
     */
    public void sendAccountCreatedEmail(String toEmail, RSUserModel user) {
        LOGGER.debug("Sending account created email");

        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setFrom(settingsProperties.getMailFrom());
            messageHelper.setTo(toEmail);
            messageHelper.setSubject("RaspiSurveillance: Account erstellt / Account created");
            String content = emailTemplateService.buildAccountCreated(user.getUsername());
            messageHelper.setText(content, true);
        };

        javaMailSender.send(messagePreparator);
    }

    /**
     * Sends a forgot password email
     * 
     * @param toEmail The email address to send the email to
     * @param token   The forgot password token
     */
    public void sendPasswordForgotEmail(String toEmail, RSUserForgotPasswordTokenModel model) {
        LOGGER.debug("Sending forgot password email");

        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setFrom(settingsProperties.getMailFrom());
            messageHelper.setTo(toEmail);
            messageHelper
                    .setSubject("RaspiSurveillance: Passwort-Zurücksetzen-Token generiert / Password Reset Token generated");
            String content = emailTemplateService.buildForgotPassword(model.getId().toString());
            messageHelper.setText(content, true);
        };

        javaMailSender.send(messagePreparator);
    }

    /**
     * Sends a new password generated email
     * 
     * @param toEmail     The email address to send the email to
     * @param newPassword The new password
     */
    public void sendNewPasswordGeneratedEmail(String toEmail, String newPassword) {
        LOGGER.debug("Sending new password generated email");

        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setFrom(settingsProperties.getMailFrom());
            messageHelper.setTo(toEmail);
            messageHelper.setSubject("RaspiSurveillance: Neues Passwort generiert / New Password generated");
            String content = emailTemplateService.buildNewPasswordGenerated(newPassword);
            messageHelper.setText(content, true);
        };

        javaMailSender.send(messagePreparator);
    }

    /**
     * Sends an email address verification email
     * 
     * @param toEmail The email address to send the email to
     * @param user    The user
     * @param model   The verify email address token
     */
    public void sendVerifyEmailAddressEmail(String toEmail, RSUserModel user, RSUserVerificationTokenModel model) {
        LOGGER.debug("Sending verify email address email");

        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setFrom(settingsProperties.getMailFrom());
            messageHelper.setTo(toEmail);
            messageHelper.setSubject("RaspiSurveillance: Verifiziere deine E-Mail-Adresse / Verify your email address");
            String content = emailTemplateService.buildVerifyEmailAddress(user.getId().toString(),
                    model.getId().toString());
            messageHelper.setText(content, true);
        };

        javaMailSender.send(messagePreparator);
    }

    /**
     * Sends an email address verified email
     * 
     * @param toEmail The email address to send the email to
     */
    public void sendEmailAddressverifiedEmail(String toEmail) {
        LOGGER.debug("Sending email address verified email");

        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setFrom(settingsProperties.getMailFrom());
            messageHelper.setTo(toEmail);
            messageHelper.setSubject("RaspiSurveillance: E-Mail-Adresse verifiziert / Email address verified");
            String content = emailTemplateService.buildEmailAddressVerified();
            messageHelper.setText(content, true);
        };

        javaMailSender.send(messagePreparator);
    }

    /**
     * Sends a plain text email
     * 
     * @param toEmail The email address to send the email to
     * @param subject The subject
     * @param message The message
     */
    public void sendEmail(String toEmail, String subject, String message) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(settingsProperties.getMailFrom());
        mailMessage.setTo(toEmail);
        mailMessage.setSubject(String.format("RaspiSurveillance: %s", subject));
        mailMessage.setText(message);

        javaMailSender.send(mailMessage);
    }

}
