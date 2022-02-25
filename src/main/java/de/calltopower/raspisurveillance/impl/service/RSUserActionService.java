package de.calltopower.raspisurveillance.impl.service;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.calltopower.raspisurveillance.impl.db.repository.RSUserForgotPasswordTokensRepository;
import de.calltopower.raspisurveillance.impl.db.repository.RSUserVerificationTokensRepository;
import de.calltopower.raspisurveillance.impl.exception.RSGeneralException;
import de.calltopower.raspisurveillance.impl.model.RSUserForgotPasswordTokenModel;
import de.calltopower.raspisurveillance.impl.model.RSUserModel;
import de.calltopower.raspisurveillance.impl.model.RSUserVerificationTokenModel;

/**
 * Service for user actions
 */
@Service
public class RSUserActionService {

	private static final Logger LOGGER = LoggerFactory.getLogger(RSUserActionService.class);

	private RSEmailService emailService;
	private RSUserVerificationTokensRepository userActivationTokensRepository;
	private RSUserForgotPasswordTokensRepository userForgotPasswordTokensRepository;

	/**
	 * Initializes the service
	 * 
	 * @param emailService                       Email service
	 * @param userActivationTokensRepository     user activation tokens repository
	 * @param userForgotPasswordTokensRepository The user forgot password tokens
	 *                                           repository
	 */
	@Autowired
	public RSUserActionService(RSEmailService emailService,
			RSUserVerificationTokensRepository userActivationTokensRepository,
			RSUserForgotPasswordTokensRepository userForgotPasswordTokensRepository) {
		this.emailService = emailService;
		this.userActivationTokensRepository = userActivationTokensRepository;
		this.userForgotPasswordTokensRepository = userForgotPasswordTokensRepository;
	}

	protected void sendEmailAccountCreated(RSUserModel user) {
		LOGGER.debug("Sending account created email");
		try {
			emailService.sendAccountCreatedEmail(user.getEmail(), user);
		} catch (Exception ex) {
			LOGGER.error("Something went wrong with the email service: ", ex);
		}
	}

	protected void sendEmailVerifyEmailAddress(RSUserModel user, String newEmail, RSUserVerificationTokenModel model) {
		LOGGER.debug("Sending verify email address email");
		try {
			emailService.sendVerifyEmailAddressEmail(newEmail, user, model);
		} catch (Exception ex) {
			LOGGER.error("Something went wrong with the email service: ", ex);
		}
	}

	protected void sendEmailForgotPassword(RSUserModel user, RSUserForgotPasswordTokenModel model) {
		LOGGER.debug("Sending forgot password email");
		try {
			emailService.sendPasswordForgotEmail(user.getEmail(), model);
		} catch (Exception ex) {
			LOGGER.error("Something went wrong with the email service: ", ex);
		}
	}

	protected void sendEmailNewPasswordGenerated(RSUserModel user, String newPassword) {
		LOGGER.debug("Sending new password generated email");
		try {
			emailService.sendNewPasswordGeneratedEmail(user.getEmail(), newPassword);
		} catch (Exception ex) {
			LOGGER.error("Something went wrong with the email service: ", ex);
		}
	}

	protected void sendEmailEmailAddressVerified(RSUserModel user) {
		LOGGER.debug("Sending email address verified email");
		try {
			emailService.sendEmailAddressverifiedEmail(user.getEmail());
		} catch (Exception ex) {
			LOGGER.error("Something went wrong with the email service: ", ex);
		}
	}

	protected void deleteAllUserActivationTokensForUserId(UUID userId) {
		try {
			for (RSUserVerificationTokenModel token : userActivationTokensRepository.findAllByUserId(userId)) {
				userActivationTokensRepository.deleteById(token.getId());
			}
		} catch (Exception ex) {
			String errMsg = String.format(
					"Something went wrong deleting all user activation tokens for user with username \"%s\"", userId);
			LOGGER.error(errMsg);
			throw new RSGeneralException(errMsg);
		}
	}

	protected void deleteAllUserForgotPasswordTokensForUserId(UUID userId) {
		try {
			for (RSUserForgotPasswordTokenModel token : userForgotPasswordTokensRepository.findAllByUserId(userId)) {
				userForgotPasswordTokensRepository.deleteById(token.getId());
			}
		} catch (Exception ex) {
			String errMsg = String.format(
					"Something went wrong deleting all forgot password tokens for user with username \"%s\"", userId);
			LOGGER.error(errMsg);
			throw new RSGeneralException(errMsg);
		}
	}

}
