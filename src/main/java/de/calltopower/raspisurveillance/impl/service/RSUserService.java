package de.calltopower.raspisurveillance.impl.service;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.calltopower.raspisurveillance.api.service.RSService;
import de.calltopower.raspisurveillance.impl.db.repository.RSUserForgotPasswordTokensRepository;
import de.calltopower.raspisurveillance.impl.db.repository.RSUserRepository;
import de.calltopower.raspisurveillance.impl.db.repository.RSUserVerificationTokensRepository;
import de.calltopower.raspisurveillance.impl.exception.RSFunctionalException;
import de.calltopower.raspisurveillance.impl.exception.RSNotAuthorizedException;
import de.calltopower.raspisurveillance.impl.exception.RSNotFoundException;
import de.calltopower.raspisurveillance.impl.exception.RSUserException;
import de.calltopower.raspisurveillance.impl.model.RSRoleModel;
import de.calltopower.raspisurveillance.impl.model.RSUserForgotPasswordTokenModel;
import de.calltopower.raspisurveillance.impl.model.RSUserModel;
import de.calltopower.raspisurveillance.impl.model.RSUserVerificationTokenModel;
import de.calltopower.raspisurveillance.impl.requestbody.RSForgotPasswordRequestBody;
import de.calltopower.raspisurveillance.impl.requestbody.RSUserRequestBody;
import de.calltopower.raspisurveillance.impl.utils.RSJsonUtils;

/**
 * Service for user results
 */
@Service
public class RSUserService implements RSService {

	private static final Logger LOGGER = LoggerFactory.getLogger(RSUserService.class);

	private RSUserRepository userRepository;
	private RSUserForgotPasswordTokensRepository userForgotPasswordTokensRepository;
	private RSUserVerificationTokensRepository userActivationTokensRepository;
	private RSRoleService roleService;
	private RSAuthService authService;
	private PasswordEncoder encoder;
	private RSJsonUtils jsonUtils;
	private RSUserActionService userActionService;

	/**
	 * Initializes the service
	 * 
	 */
	/**
	 * Initializes the service
	 * 
	 * @param userRepository                     The user DB repository
	 * @param userForgotPasswordTokensRepository The user forgot password tokens
	 *                                           repository
	 * @param userActivationTokensRepository     The user activation tokens
	 *                                           repository
	 * @param roleService                        The role service
	 * @param authService                        The authentication service
	 * @param encoder                            The encoder
	 * @param jsonUtils                          The Json utils
	 * @param userActionService                  The user action service
	 */
	@Autowired
	public RSUserService(RSUserRepository userRepository,
			RSUserForgotPasswordTokensRepository userForgotPasswordTokensRepository,
			RSUserVerificationTokensRepository userActivationTokensRepository, RSRoleService roleService,
			RSAuthService authService, PasswordEncoder encoder, RSJsonUtils jsonUtils,
			RSUserActionService userActionService) {
		this.userRepository = userRepository;
		this.userForgotPasswordTokensRepository = userForgotPasswordTokensRepository;
		this.userActivationTokensRepository = userActivationTokensRepository;
		this.roleService = roleService;
		this.authService = authService;
		this.encoder = encoder;
		this.jsonUtils = jsonUtils;
		this.userActionService = userActionService;
	}

	/**
	 * Retrieves all users from DB
	 * 
	 * @param userDetails The user authentication
	 * @return a list of users (empty if none found)
	 */
	@Transactional(readOnly = true)
	public Set<RSUserModel> getAllUsers(UserDetails userDetails) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format("Searching for all users"));
		}

		@SuppressWarnings("unused")
		RSUserModel user = authService.authenticate(userDetails);

		return userRepository.findAll().stream().collect(Collectors.toSet());
	}

	/**
	 * Returns a user
	 * 
	 * @param userDetails The user authentication
	 * @param strId       The user ID
	 * @return a user
	 */
	@Transactional(readOnly = true)
	public RSUserModel getUser(UserDetails userDetails, String strId) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format("Getting user with ID \"%s\"", strId));
		}

		@SuppressWarnings("unused")
		RSUserModel user = authService.authenticate(userDetails);

		return getUser(strId);
	}

	/**
	 * Updates a user
	 * 
	 * @param userDetails The user authentication
	 * @param strId       The user ID
	 * @param requestBody The request Body
	 * @return the updated user
	 */
	@Transactional(readOnly = false)
	public RSUserModel updateUser(UserDetails userDetails, String strId, RSUserRequestBody requestBody) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format("Updating user with ID \"%s\"", strId));
		}

		RSUserModel authenticatedUser = authService.authenticate(userDetails);

		RSUserModel user = getUser(strId);

		if (authService.isAdminOrRequestedUser(authenticatedUser, strId)) {
			if (StringUtils.isNotBlank(requestBody.getUsername())) {
				user.setUsername(requestBody.getUsername());
			}
			if (StringUtils.isNotBlank(requestBody.getEmail())) {
				if (!user.getEmail().equals(requestBody.getEmail())) {
					user.setStatusVerified(false);
					authService.processUserActivation(user, requestBody.getEmail());
				}
				user.setEmail(requestBody.getEmail());
			}
			if (StringUtils.isNotBlank(requestBody.getPassword())) {
				user.setPassword(encoder.encode(requestBody.getPassword()));
			}
			user.setJsonData(jsonUtils.getNonEmptyJson(requestBody.getJsonData()));
			if ((requestBody.getRoles() != null) && authService.isAdmin(authenticatedUser)) {
				Set<RSRoleModel> roles = roleService.convertRoles(requestBody.getRoles());
				RSRoleModel standardUserRole = roleService.getStandardUserRole();
				if (!roles.contains(standardUserRole)) {
					roles.add(standardUserRole);
				}
				// Make sure one admin exists at any time
				if (!atLeastOneAdminExists(user)) {
					roles.add(roleService.getAdminUserRole());
				}
				user.setRoles(roles);
			}
		} else {
			throw new RSNotAuthorizedException("You are not allowed to update this user.");
		}

		return userRepository.saveAndFlush(user);
	}

	/**
	 * Deletes a user from DB
	 * 
	 * @param userDetails The user authentication
	 * @param strId       the ID
	 */
	@Transactional(readOnly = false)
	public void deleteUser(UserDetails userDetails, String strId) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format("Deleting user with ID \"%s\"", strId));
		}

		RSUserModel authenticatedUser = authService.authenticate(userDetails);

		if (authService.isAdminOrRequestedUser(authenticatedUser, strId)) {
			try {
				RSUserModel user = getUser(strId);
				// Make sure one admin exists at any time
				if (!atLeastOneAdminExists(user)) {
					throw new RSFunctionalException("There has to be at least one admin.");
				}
				userRepository.deleteById(UUID.fromString(strId));
				userActionService.deleteAllUserForgotPasswordTokensForUserId(user.getId());
				userActionService.deleteAllUserActivationTokensForUserId(user.getId());
			} catch (Exception ex) {
				String errMsg = String.format("Could not delete user with ID \"%s\"", strId);
				LOGGER.error(errMsg);
				throw new RSNotFoundException(errMsg);
			}
		} else {
			String errMsg = String.format("User with ID \"%s\" is not allowed to delete user with ID \"%s\"",
					authenticatedUser.getId(), strId);
			LOGGER.error(errMsg);
			throw new RSNotAuthorizedException(errMsg);
		}
	}

	@Transactional(readOnly = false)
	public void forgotPassword(RSForgotPasswordRequestBody requestBody) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format("Processing forgot password for \"%s\"", requestBody.toString()));
		}

		if (requestBody.getUsername() == null || requestBody.getEmail() == null) {
			String errMsg = "Username and email must be provided";
			LOGGER.error(errMsg);
			throw new RSUserException(errMsg);
		}

		Optional<RSUserModel> userOptional = userRepository.findByUsername(requestBody.getUsername());
		if (!userOptional.isPresent()) {
			String errMsg = String.format("User with username \"%s\" not found", requestBody.getUsername());
			LOGGER.error(errMsg);
			throw new RSNotFoundException(errMsg);
		}

		RSUserModel foundUser = userOptional.get();
		if (!foundUser.getEmail().equalsIgnoreCase(requestBody.getEmail())) {
			String errMsg = String.format("User with username \"%s\" not associated with email \"%s\"",
					requestBody.getUsername(), requestBody.getEmail());
			LOGGER.error(errMsg);
			throw new RSUserException(errMsg);
		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format("Deleting all old forgot password tokens for user with ID \"%s\"",
					foundUser.getId()));
		}
		userActionService.deleteAllUserForgotPasswordTokensForUserId(foundUser.getId());

		RSUserForgotPasswordTokenModel model = RSUserForgotPasswordTokenModel.builder().userId(foundUser.getId())
				.build();
		model = userForgotPasswordTokensRepository.saveAndFlush(model);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format("Saved forgot password token with id \"%s\"", model.getId()));
		}

		userActionService.sendEmailForgotPassword(foundUser, model);
	}

	@Transactional(readOnly = false)
	public void resetPassword(String strId) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format("Processing reset password for \"%s\"", strId));
		}

		RSUserForgotPasswordTokenModel token = getForgotPasswordToken(strId);

		RSUserModel user = getUser(token.getUserId());
		String newPassword = String.format("std_pw_%s", UUID.randomUUID().toString());
		user.setPassword(encoder.encode(newPassword));

		// JSON validation workaround
		user.setJsonData(jsonUtils.getNonEmptyJson(user.getJsonData()));

		userRepository.saveAndFlush(user);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					String.format("Deleting all old forgot password tokens for user with ID \"%s\"", user.getId()));
		}
		userActionService.deleteAllUserForgotPasswordTokensForUserId(user.getId());

		userActionService.sendEmailNewPasswordGenerated(user, newPassword);
	}

	/**
	 * Resends the activation
	 * 
	 * @param userDetails The user authentication
	 */
	@Transactional(readOnly = false)
	public void resendVerification(UserDetails userDetails) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Resending verification");
		}

		RSUserModel user = authService.authenticate(userDetails);

		authService.processUserActivation(user, user.getEmail());
	}

	@Transactional(readOnly = false)
	public void verify(String strId) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format("Processing verification for \"%s\"", strId));
		}

		RSUserVerificationTokenModel token = getUserActivationToken(strId);

		RSUserModel user = getUser(token.getUserId());
		user.setStatusVerified(true);

		// JSON validation workaround
		user.setJsonData(jsonUtils.getNonEmptyJson(user.getJsonData()));

		userRepository.saveAndFlush(user);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					String.format("Deleting all old user activation tokens for user with ID \"%s\"", user.getId()));
		}
		userActionService.deleteAllUserActivationTokensForUserId(user.getId());

		userActionService.sendEmailEmailAddressVerified(user);
	}

	private RSUserModel getUser(String strId) {
		return getUser(UUID.fromString(strId));
	}

	private RSUserModel getUser(UUID strId) {
		Optional<RSUserModel> userOptional = userRepository.findById(strId);
		if (!userOptional.isPresent()) {
			String errMsg = String.format("Could not find user with ID \"%s\"", strId);
			LOGGER.error(errMsg);
			throw new RSNotFoundException(errMsg);
		}

		return userOptional.get();
	}

	private RSUserForgotPasswordTokenModel getForgotPasswordToken(String strId) {
		UUID uuid;
		try {
			uuid = UUID.fromString(strId);
		} catch (Exception ex) {
			String errMsg = String.format("Could not process ID \"%s\"", strId);
			LOGGER.error(errMsg);
			throw new RSNotFoundException(errMsg);
		}

		Optional<RSUserForgotPasswordTokenModel> tokenOptional = userForgotPasswordTokensRepository.findById(uuid);
		if (!tokenOptional.isPresent()) {
			String errMsg = String.format("Could not find forgot password token with ID \"%s\"", strId);
			LOGGER.error(errMsg);
			throw new RSNotFoundException(errMsg);
		}

		return tokenOptional.get();
	}

	private RSUserVerificationTokenModel getUserActivationToken(String strId) {
		UUID uuid;
		try {
			uuid = UUID.fromString(strId);
		} catch (Exception ex) {
			String errMsg = String.format("Could not process ID \"%s\"", strId);
			LOGGER.error(errMsg);
			throw new RSNotFoundException(errMsg);
		}

		Optional<RSUserVerificationTokenModel> tokenOptional = userActivationTokensRepository.findById(uuid);
		if (!tokenOptional.isPresent()) {
			String errMsg = String.format("Could not find user activation token with ID \"%s\"", strId);
			LOGGER.error(errMsg);
			throw new RSNotFoundException(errMsg);
		}

		return tokenOptional.get();
	}

	private boolean atLeastOneAdminExists(RSUserModel excludeUserInSearch) {
		// @formatter:off
        long nrOfAdmins = userRepository.findAll().stream()
                                                    .filter(u -> !u.getId().equals(excludeUserInSearch.getId()))
                                                    .filter(u -> u.getRoles().contains(roleService.getAdminUserRole()))
                                                    .count();
        // @formatter:on
		return nrOfAdmins > 0;
	}

}
