package de.calltopower.raspisurveillance.impl.service;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.calltopower.raspisurveillance.api.service.RSService;
import de.calltopower.raspisurveillance.impl.db.repository.RSUserRepository;
import de.calltopower.raspisurveillance.impl.db.repository.RSUserVerificationTokensRepository;
import de.calltopower.raspisurveillance.impl.enums.RSUserRole;
import de.calltopower.raspisurveillance.impl.exception.RSFunctionalException;
import de.calltopower.raspisurveillance.impl.exception.RSNotFoundException;
import de.calltopower.raspisurveillance.impl.exception.RSUserException;
import de.calltopower.raspisurveillance.impl.model.RSRoleModel;
import de.calltopower.raspisurveillance.impl.model.RSTokenModel;
import de.calltopower.raspisurveillance.impl.model.RSUserDetailsImpl;
import de.calltopower.raspisurveillance.impl.model.RSUserModel;
import de.calltopower.raspisurveillance.impl.model.RSUserVerificationTokenModel;
import de.calltopower.raspisurveillance.impl.properties.RSSettingsProperties;
import de.calltopower.raspisurveillance.impl.requestbody.RSSigninRequestBody;
import de.calltopower.raspisurveillance.impl.requestbody.RSSignupRequestBody;
import de.calltopower.raspisurveillance.impl.utils.RSJsonUtils;
import de.calltopower.raspisurveillance.impl.utils.RSTokenUtils;

/**
 * Service for user results
 */
@Service
public class RSAuthService implements RSService {

	private static final Logger LOGGER = LoggerFactory.getLogger(RSAuthService.class);

	private AuthenticationManager authenticationManager;
	private PasswordEncoder encoder;
	private RSTokenUtils jwtUtils;
	private RSJsonUtils jsonUtils;
	private RSUserRepository userRepository;
	private RSRoleService roleService;
	private RSSettingsProperties functionalProperties;
	private RSUserVerificationTokensRepository userActivationTokensRepository;
	private RSUserActionService userActionService;

	/**
	 * Initializes the service
	 * 
	 * @param authenticationManager          The authentication manager
	 * @param encoder                        The encoder
	 * @param jwtUtils                       The JWT utilities
	 * @param jsonUtils                      The Json utilities
	 * @param userRepository                 The user repository
	 * @param roleService                    The role service
	 * @param functionalProperties           Functional properties
	 * @param userActivationTokensRepository user activation tokens repository
	 * @param userActionService              The user action service
	 */
	@Autowired
	public RSAuthService(AuthenticationManager authenticationManager, PasswordEncoder encoder, RSTokenUtils jwtUtils,
			RSJsonUtils jsonUtils, RSUserRepository userRepository, RSRoleService roleService,
			RSSettingsProperties functionalProperties,
			RSUserVerificationTokensRepository userActivationTokensRepository, RSUserActionService userActionService) {
		this.authenticationManager = authenticationManager;
		this.encoder = encoder;
		this.jwtUtils = jwtUtils;
		this.jsonUtils = jsonUtils;
		this.userRepository = userRepository;
		this.roleService = roleService;
		this.functionalProperties = functionalProperties;
		this.userActivationTokensRepository = userActivationTokensRepository;
		this.userActionService = userActionService;
	}

	/**
	 * Signs up a user
	 * 
	 * @param requestBody The signup request body
	 * @param userDetails User details. My be empty if registering
	 * @return A user model
	 */
	@Transactional(readOnly = false)
	public RSUserModel signup(RSSignupRequestBody requestBody, UserDetails userDetails) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format("Requesting signup new user: \"%s\"", requestBody));
		}

		if (userDetails == null && !functionalProperties.signupAllowed()) {
			LOGGER.error("New users are not allowed to register themselves");
			throw new RSFunctionalException("New users are not allowed to register themselves.");
		}

		Boolean existsByUsername = userRepository.existsByUsername(requestBody.getUsername());
		if (existsByUsername != null && existsByUsername.booleanValue()) {
			LOGGER.error("Username is already taken");
			throw new RSUserException("Username is already taken");
		}

		Boolean existsByEmail = userRepository.existsByEmail(requestBody.getEmail());
		if (existsByEmail != null && existsByEmail.booleanValue()) {
			LOGGER.error("Email is already in use");
			throw new RSUserException("Email is already in use");
		}

		String jsonData = jsonUtils.getNonEmptyJson(requestBody.getJsonData());
		// @formatter:off
        RSUserModel user = RSUserModel.builder()
                                            .username(requestBody.getUsername())
                                            .email(requestBody.getEmail())
                                            .password(encoder.encode(requestBody.getPassword()))
                                            .jsonData(jsonData)
                                        .build();
        // @formatter:on

		Set<RSRoleModel> roles = new HashSet<>();
		roles.add(roleService.getStandardUserRole());
		user.setRoles(roles);

		user = userRepository.saveAndFlush(user);

		userActionService.sendEmailAccountCreated(user);
		processUserActivation(user, user.getEmail());

		return user;
	}

	/**
	 * Signs up a user
	 * 
	 * @param requestBody The signin request body
	 * @return A token model
	 */
	@Transactional(readOnly = false)
	public RSTokenModel signin(RSSigninRequestBody requestBody) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format("Requesting signing in user: \"%s\"", requestBody));
		}

		Authentication authentication;
		try {
			authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(requestBody.getUsername(), requestBody.getPassword()));
		} catch (Exception ex) {
			LOGGER.error("Could not authenticate");
			throw new RSUserException("Could not authenticate");
		}

		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);

		RSUserDetailsImpl userDetails = (RSUserDetailsImpl) authentication.getPrincipal();

		// @formatter:off
        return RSTokenModel.builder()
                .token(jwt)
                .user(authenticate((userDetails)))
               .build();
        // @formatter:off
    }

    /**
     * Authenticates a user
     * 
     * @param userDetails The user details
     * @return The authenticated user
     */
    @Transactional(readOnly = true)
    public RSUserModel authenticate(UserDetails userDetails) {
        return userRepository.findByUsername(userDetails.getUsername()).orElseThrow(() -> new RSNotFoundException(
                String.format("User with username \"%s\" not found", userDetails.getUsername())));
    }

    /**
     * Returns whether the given user is an admin user
     * 
     * @param user The user to check
     * @return True if the given user is an admin user, false else
     */
    @Transactional(readOnly = true)
    public boolean isAdmin(RSUserModel user) {
        // @formatter:off
        return user.getRoles().stream()
                                .map(r -> r.getName())
                                .anyMatch(r -> r.equals(RSUserRole.ROLE_ADMIN));
        // @formatter:on
	}

	/**
	 * Checks whether the userDetails user is the requested authenticated user with
	 * ID strId
	 * 
	 * @param userDetails The user details
	 * @param strId       The requested ID
	 * @return true if the userDetails user is the requested authenticated user with
	 *         ID strId, false else
	 */
	@Transactional(readOnly = true)
	public boolean isAdminOrRequestedUser(RSUserModel user, String strId) {
		return user != null && (isAdmin(user) || user.getId().equals(UUID.fromString(strId)));
	}

	/**
	 * Checks whether the userDetails user is the requested authenticated user with
	 * ID strId
	 * 
	 * @param userDetails The user details
	 * @param strId       The requested ID
	 * @return true if the userDetails user is the requested authenticated user with
	 *         ID strId, false else
	 */
	@Transactional(readOnly = true)
	public boolean isAdminOrRequestedUser(UserDetails userDetails, String strId) {
		return isAdminOrRequestedUser(authenticate(userDetails), strId);
	}

	protected void processUserActivation(RSUserModel user, String newEmail) {
		LOGGER.debug(String.format("Deleting all old user activation tokens for user with ID \"%s\"", user.getId()));
		userActionService.deleteAllUserActivationTokensForUserId(user.getId());

		RSUserVerificationTokenModel model = RSUserVerificationTokenModel.builder().userId(user.getId()).build();
		model = userActivationTokensRepository.saveAndFlush(model);

		LOGGER.debug(String.format("Saved user activation token with id \"%s\"", model.getId()));

		userActionService.sendEmailVerifyEmailAddress(user, newEmail, model);
	}

}
