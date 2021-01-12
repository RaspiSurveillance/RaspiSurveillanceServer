package de.calltopower.raspisurveillance.impl.controller;

import java.util.Set;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.calltopower.raspisurveillance.api.controller.RSController;
import de.calltopower.raspisurveillance.impl.dto.RSServerDto;
import de.calltopower.raspisurveillance.impl.dtoservice.RSServerDtoService;
import de.calltopower.raspisurveillance.impl.requestbody.RSServerRequestBody;
import de.calltopower.raspisurveillance.impl.service.RSAuthService;
import de.calltopower.raspisurveillance.impl.service.RSServerService;

/**
 * Server controller
 */
@RestController
@RequestMapping(path = RSServerController.PATH)
public class RSServerController implements RSController {

	/**
	 * The controller path
	 */
	public static final String PATH = APIPATH + "/servers";

	private static final Logger LOGGER = LoggerFactory.getLogger(RSServerController.class);

	private RSServerDtoService serverDtoService;
	private RSServerService serverService;
	private RSAuthService authService;

	/**
	 * Initializes the controller
	 * 
	 * @param serverDtoService Injected DTO service
	 * @param serverService    Injected server service
	 * @param authService      Injected authentication service
	 */
	@Autowired
	public RSServerController(RSServerDtoService serverDtoService, RSServerService serverService,
			RSAuthService authService) {
		this.serverDtoService = serverDtoService;
		this.serverService = serverService;
		this.authService = authService;
	}

	@SuppressWarnings("javadoc")
	@GetMapping(path = "", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
	public Set<RSServerDto> getAll(@AuthenticationPrincipal UserDetails userDetails) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Requested all servers");
		}

		if (authService.isAdmin(authService.authenticate(userDetails))) {
			return serverDtoService.convert(serverService.getAllServers(userDetails));
		} else {
			return serverDtoService.convertAbridged(serverService.getAllServers(userDetails));
		}
	}

	@SuppressWarnings("javadoc")
	@GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
	public RSServerDto getServer(@NotNull @PathVariable(name = "id") String id,
			@AuthenticationPrincipal UserDetails userDetails) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Requested specific server");
		}

		if (authService.isAdmin(authService.authenticate(userDetails))) {
			return serverDtoService.convert(serverService.getServer(userDetails, id));
		} else {
			return serverDtoService.convertAbridged(serverService.getServer(userDetails, id));
		}
	}

	@SuppressWarnings("javadoc")
	@PostMapping(path = "", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@PreAuthorize("hasRole('ADMIN')")
	public RSServerDto createServer(@NotNull @RequestBody RSServerRequestBody requestBody,
			@AuthenticationPrincipal UserDetails userDetails) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Requested creating workspace");
		}

		return serverDtoService.convert(serverService.createServer(userDetails, requestBody));
	}

	@SuppressWarnings("javadoc")
	@PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@PreAuthorize("hasRole('ADMIN')")
	public RSServerDto updateServer(@NotNull @PathVariable(name = "id") String id,
			@NotNull @RequestBody RSServerRequestBody requestBody, @AuthenticationPrincipal UserDetails userDetails) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Requested updating server");
		}

		return serverDtoService.convert(serverService.updateServer(userDetails, id, requestBody));
	}

	@SuppressWarnings("javadoc")
	@DeleteMapping(path = "/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public void deleteServer(@NotNull @PathVariable(name = "id") String id,
			@AuthenticationPrincipal UserDetails userDetails) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Requested deleting server");
		}

		serverService.deleteServer(userDetails, id);
	}

	@SuppressWarnings("javadoc")
	@PutMapping(path = "/{id}/start/camerastream", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
	public RSServerDto startCameraStream(@NotNull @PathVariable(name = "id") String id,
			@AuthenticationPrincipal UserDetails userDetails) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Requested starting camera stream");
		}

		if (authService.isAdmin(authService.authenticate(userDetails))) {
			return serverDtoService.convert(serverService.startCameraStream(userDetails, id));
		} else {
			return serverDtoService.convertAbridged(serverService.startCameraStream(userDetails, id));
		}
	}

	@SuppressWarnings("javadoc")
	@PutMapping(path = "/{id}/start/surveillance", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
	public RSServerDto startSurveillance(@NotNull @PathVariable(name = "id") String id,
			@AuthenticationPrincipal UserDetails userDetails) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Requested starting surveillance");
		}

		if (authService.isAdmin(authService.authenticate(userDetails))) {
			return serverDtoService.convert(serverService.startSurveillance(userDetails, id));
		} else {
			return serverDtoService.convertAbridged(serverService.startSurveillance(userDetails, id));
		}
	}

	@SuppressWarnings("javadoc")
	@PutMapping(path = "/{id}/stop", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
	public RSServerDto stopServicesOnServer(@NotNull @PathVariable(name = "id") String id,
			@AuthenticationPrincipal UserDetails userDetails) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Requested stopping services");
		}

		if (authService.isAdmin(authService.authenticate(userDetails))) {
			return serverDtoService.convert(serverService.stopServices(userDetails, id));
		} else {
			return serverDtoService.convertAbridged(serverService.stopServices(userDetails, id));
		}
	}

	@SuppressWarnings("javadoc")
	@PutMapping(path = "/{id}/refresh", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
	public RSServerDto refreshServer(@NotNull @PathVariable(name = "id") String id,
			@AuthenticationPrincipal UserDetails userDetails) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Requested refreshing server");
		}

		if (authService.isAdmin(authService.authenticate(userDetails))) {
			return serverDtoService.convert(serverService.refreshServer(userDetails, id));
		} else {
			return serverDtoService.convertAbridged(serverService.refreshServer(userDetails, id));
		}
	}

	@SuppressWarnings("javadoc")
	@PutMapping(path = "/{id}/startup", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
	public RSServerDto startupServer(@NotNull @PathVariable(name = "id") String id,
			@AuthenticationPrincipal UserDetails userDetails) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Requested starting up server");
		}

		if (authService.isAdmin(authService.authenticate(userDetails))) {
			return serverDtoService.convert(serverService.masterStartupServer(userDetails, id));
		} else {
			return serverDtoService.convertAbridged(serverService.masterStartupServer(userDetails, id));
		}
	}

	@SuppressWarnings("javadoc")
	@PutMapping(path = "/{id}/shutdown", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
	public RSServerDto shutdownServer(@NotNull @PathVariable(name = "id") String id,
			@AuthenticationPrincipal UserDetails userDetails) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Requested shutting down server");
		}

		if (authService.isAdmin(authService.authenticate(userDetails))) {
			return serverDtoService.convert(serverService.shutdownServer(userDetails, id));
		} else {
			return serverDtoService.convertAbridged(serverService.shutdownServer(userDetails, id));
		}
	}

	@Override
	public String getPath() {
		return PATH;
	}

}
