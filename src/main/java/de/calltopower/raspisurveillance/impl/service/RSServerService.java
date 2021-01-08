package de.calltopower.raspisurveillance.impl.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.calltopower.raspisurveillance.api.service.RSService;
import de.calltopower.raspisurveillance.impl.db.repository.RSServerRepository;
import de.calltopower.raspisurveillance.impl.enums.RSServerStatus;
import de.calltopower.raspisurveillance.impl.exception.RSFunctionalException;
import de.calltopower.raspisurveillance.impl.exception.RSNotFoundException;
import de.calltopower.raspisurveillance.impl.model.RSServerModel;
import de.calltopower.raspisurveillance.impl.model.RSServerModel.RSServerModelBuilder;
import de.calltopower.raspisurveillance.impl.requestbody.RSServerRequestBody;
import de.calltopower.raspisurveillance.impl.utils.RSJsonUtils;

/**
 * Service for server results
 */
@Service
public class RSServerService implements RSService {

	private static final Logger LOGGER = LoggerFactory.getLogger(RSServerService.class);

	private RSServerRepository serverRepository;
	private RSJsonUtils jsonUtils;

	/**
	 * Initializes the service
	 * 
	 * @param serverRepository The server DB repository
	 * @param jsonUtils        The Json utilities
	 */
	@Autowired
	public RSServerService(RSServerRepository serverRepository, RSJsonUtils jsonUtils) {
		this.serverRepository = serverRepository;
		this.jsonUtils = jsonUtils;
	}

	/**
	 * Retrieves all servers from DB
	 * 
	 * @param userDetails The user authentication
	 * @return a list of servers (empty if none found)
	 */
	@Transactional(readOnly = true)
	public Set<RSServerModel> getAllServers(UserDetails userDetails) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Searching for all servers");
		}

		return serverRepository.findAll().stream().collect(Collectors.toSet());
	}

	/**
	 * Returns a server
	 * 
	 * @param userDetails The user authentication
	 * @param strId       The server ID
	 * @return a server
	 */
	@Transactional(readOnly = true)
	public RSServerModel getServer(UserDetails userDetails, String strId) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format("Getting server with ID \"%s\"", strId));
		}

		return getServer(strId);
	}

	/**
	 * Creates and persists a new server
	 * 
	 * @param userDetails The user authentication
	 * @param requestBody The request body
	 * @return The newly persisted server
	 */
	@Transactional(readOnly = false)
	public RSServerModel createServer(UserDetails userDetails, RSServerRequestBody requestBody) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format("Creating server from request body \"%s\"", requestBody));
		}

		// @formatter:off
        String jsonData = jsonUtils.getNonEmptyJson(requestBody.getJsonData());
        RSServerModelBuilder serverBuilder = RSServerModel.builder()
                                            .name(requestBody.getName())
                                            .url(requestBody.getUrl())
                                            .username(requestBody.getUsername())
                                            .password(requestBody.getPassword())
                                            .urlCamerastream(requestBody.getUrlCamerastream())
                                            .usernameCamerastream(requestBody.getUsernameCamerastream())
                                            .passwordCamerastream(requestBody.getPasswordCamerastream())
                                            .status(RSServerStatus.OFFLINE)
                                            .jsonData(jsonData);
        // @formatter:on
		if (requestBody.getAttributesCamerastream() != null) {
			serverBuilder.attributesCamerastream(requestBody.getAttributesCamerastream());
		}
		if (requestBody.getAttributesSurveillance() != null) {
			serverBuilder.attributesSurveillance(requestBody.getAttributesSurveillance());
		}

		RSServerModel model = serverBuilder.build();

		return refreshServerStatus(model, true);
	}

	/**
	 * Updates and persists a server
	 * 
	 * @param userDetails The user authentication
	 * @param strId       the ID
	 * @param requestBody The request body
	 * @return The persisted server
	 */
	@Transactional(readOnly = false)
	public RSServerModel updateServer(UserDetails userDetails, String strId, RSServerRequestBody requestBody) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format("Updating server with ID \"%s\" from request body \"%s\"", strId, requestBody));
		}

		RSServerModel server = getServer(userDetails, strId);

		stopAll(server);
		server.setStatus(RSServerStatus.OFFLINE);

		if (StringUtils.isNotBlank(requestBody.getName())) {
			server.setName(requestBody.getName());
		}
		if (StringUtils.isNotBlank(requestBody.getUrl())) {
			server.setUrl(requestBody.getUrl());
		}
		if (requestBody.getUsername() != null) {
			server.setUsername(requestBody.getUsername());
		}
		if (requestBody.getPassword() != null) {
			server.setPassword(requestBody.getPassword());
		}
		if (requestBody.getUrlCamerastream() != null) {
			server.setUrlCamerastream(requestBody.getUrlCamerastream());
		}
		if (requestBody.getUsernameCamerastream() != null) {
			server.setUsernameCamerastream(requestBody.getUsernameCamerastream());
		}
		if (requestBody.getPasswordCamerastream() != null) {
			server.setPasswordCamerastream(requestBody.getPasswordCamerastream());
		}
		if (requestBody.getAttributesCamerastream() != null) {
			server.setAttributesCamerastream(requestBody.getAttributesCamerastream());
		}
		if (requestBody.getAttributesSurveillance() != null) {
			server.setAttributesSurveillance(requestBody.getAttributesSurveillance());
		}
		if (requestBody.getJsonData() != null) {
			server.setJsonData(jsonUtils.getNonEmptyJson(requestBody.getJsonData()));
		}

		return refreshServerStatus(server, true);
	}

	/**
	 * Deletes a server from DB
	 * 
	 * @param userDetails The user authentication
	 * @param strId       the ID
	 */
	@Transactional(readOnly = false)
	public void deleteServer(UserDetails userDetails, String strId) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format("Deleting server with ID \"%s\"", strId));
		}

		// TODO: Update status

		try {
			serverRepository.deleteById(UUID.fromString(strId));
		} catch (Exception ex) {
			String errMsg = String.format("Could not delete server with ID \"%s\"", strId);
			LOGGER.error(errMsg);
			throw new RSNotFoundException(errMsg);
		}
	}

	/**
	 * Starts the camera stream on the given server
	 * 
	 * @param userDetails The user authentication
	 * @param strId       the ID
	 */
	@Transactional(readOnly = false)
	public RSServerModel startCameraStream(UserDetails userDetails, String strId) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format("Starting camera stream on server with ID \"%s\"", strId));
		}

		RSServerModel server = getServer(userDetails, strId);

		if (!startCamerastream(server)) {
			throw new RSFunctionalException("Could not start camerastream");
		}

		return serverRepository.saveAndFlush(server);
	}

	/**
	 * Starts the surveillance on the given server
	 * 
	 * @param userDetails The user authentication
	 * @param strId       the ID
	 */
	@Transactional(readOnly = false)
	public RSServerModel startSurveillance(UserDetails userDetails, String strId) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format("Starting surveillance on server with ID \"%s\"", strId));
		}

		RSServerModel server = getServer(userDetails, strId);

		if (!startSurveillance(server)) {
			throw new RSFunctionalException("Could not start surveillance");
		}

		return serverRepository.saveAndFlush(server);
	}

	/**
	 * Stops any active service on the given server
	 * 
	 * @param userDetails The user authentication
	 * @param strId       the ID
	 */
	@Transactional(readOnly = false)
	public RSServerModel stopServices(UserDetails userDetails, String strId) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format("Stopping any given service on server with ID \"%s\"", strId));
		}

		RSServerModel server = getServer(userDetails, strId);

		stopAll(server);

		return serverRepository.saveAndFlush(server);
	}

	/**
	 * Refreshes the server status
	 * 
	 * @param userDetails The user authentication
	 * @param strId       the ID
	 */
	@Transactional(readOnly = false)
	public RSServerModel refreshServer(UserDetails userDetails, String strId) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format("Refreshing server with ID \"%s\"", strId));
		}

		RSServerModel server = getServer(userDetails, strId);

		refreshServerStatus(server, false);

		return serverRepository.saveAndFlush(server);
	}

	/**
	 * Refreshes the status of a given server
	 * 
	 * @param server The server to be refreshed
	 * @param flush  Whether to flush the repository
	 */
	@Transactional(readOnly = false)
	public RSServerModel refreshServerStatus(RSServerModel server, boolean flush) {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info(String.format("Refreshing local server \"%s\"", server.getName()));
		}
		try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
			String url = String.format("%s/start/camerastream", server.getUrl());
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(String.format("URL: %s", url));
			}
			HttpGet httpGet = new HttpGet(url);

			UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(server.getUsername(),
					server.getPassword());
			httpGet.addHeader(new BasicScheme().authenticate(credentials, httpGet, null));

			ResponseHandler<Boolean> responseHandler = response -> {
				int status = response.getStatusLine().getStatusCode();
				return status >= 200 && status < 300;
			};

			Boolean responseBody = httpclient.execute(httpGet, responseHandler);
			if (responseBody.booleanValue()) {
				if (server.getStatus() == RSServerStatus.OFFLINE) {
					server.setStatus(RSServerStatus.ONLINE);
				} else {
					server.setStatus(RSServerStatus.OFFLINE);
				}
			}
		} catch (Exception e) {
			LOGGER.error(String.format("Could not refresh server status of server \"%s\" (\"%s\")", server.getName(),
					server.getUrl()));
			server.setStatus(RSServerStatus.OFFLINE);
		}

		if (flush) {
			return serverRepository.saveAndFlush(server);
		} else {
			return serverRepository.save(server);
		}
	}

	private void stopAll(RSServerModel server) {
		stop(server);
	}

	@SuppressWarnings("unchecked")
	private String mapToJson(Map<String, Object> attrs) {
		StringBuffer sb = new StringBuffer();
		sb.append("{");
		int i = 0;
		for (String key : attrs.keySet()) {
			if (i > 0) {
				sb.append(",");
			}
			++i;
			boolean isMap = attrs.get(key) instanceof Map;
			// @formatter:off
			sb.append("\"").append(key).append("\"");
			sb.append(isMap ? ": " : ": \"");
			sb.append(isMap ? mapToJson((Map<String, Object>) attrs.get(key)) : attrs.get(key));
			sb.append(isMap ? "" : "\"");
			// @formatter:on
		}
		sb.append("}");

		return sb.toString();
	}

	private boolean startCamerastream(RSServerModel server) {
		if (server.getStatus() == RSServerStatus.OFFLINE) {
			throw new RSFunctionalException("Server is offline");
		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Creating HTTP client");
		}
		try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
			String url = String.format("%s/start/camerastream", server.getUrl());
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(String.format("URL: %s", url));
			}
			HttpPut httpPut = new HttpPut(url);

			Map<String, Object> options = new HashMap<>();
			options.put("name", server.getUsernameCamerastream());
			options.put("password", server.getPasswordCamerastream());
			options.put("options", server.getAttributesCamerastream());
			httpPut.setEntity(new StringEntity(mapToJson(options)));

			UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(server.getUsername(),
					server.getPassword());
			httpPut.addHeader(new BasicScheme().authenticate(credentials, httpPut, null));
			httpPut.setHeader("Content-type", "application/json");

			ResponseHandler<Boolean> responseHandler = response -> {
				int status = response.getStatusLine().getStatusCode();
				return status >= 200 && status < 300;
			};

			Boolean responseBody = httpclient.execute(httpPut, responseHandler);
			if (responseBody.booleanValue()) {
				server.setStatus(RSServerStatus.CAMERA_STREAM);
			}

			return server.getStatus() == RSServerStatus.CAMERA_STREAM;
		} catch (Exception e) {
			throw new RSFunctionalException("Could not start camerastream");
		}
	}

	private boolean startSurveillance(RSServerModel server) {
		if (server.getStatus() == RSServerStatus.OFFLINE) {
			throw new RSFunctionalException("Server is offline");
		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Creating HTTP client");
		}
		try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
			String url = String.format("%s/start/surveillance", server.getUrl());
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(String.format("URL: %s", url));
			}
			HttpPut httpPut = new HttpPut(url);

			UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(server.getUsername(),
					server.getPassword());
			httpPut.addHeader(new BasicScheme().authenticate(credentials, httpPut, null));
			httpPut.setHeader("Content-type", "application/json");

			ResponseHandler<Boolean> responseHandler = response -> {
				int status = response.getStatusLine().getStatusCode();
				return status >= 200 && status < 300;
			};

			Boolean responseBody = httpclient.execute(httpPut, responseHandler);
			if (responseBody.booleanValue()) {
				server.setStatus(RSServerStatus.SURVEILLANCE);
			}

			return server.getStatus() == RSServerStatus.SURVEILLANCE;
		} catch (Exception e) {
			throw new RSFunctionalException("Could not start surveillance");
		}
	}

	private boolean stop(RSServerModel server) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Creating HTTP client");
		}
		try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
			String url = String.format("%s/stop", server.getUrl());
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(String.format("URL: %s", url));
			}
			HttpPut httpPut = new HttpPut(url);

			UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(server.getUsername(),
					server.getPassword());
			httpPut.addHeader(new BasicScheme().authenticate(credentials, httpPut, null));
			httpPut.setHeader("Content-type", "application/json");

			ResponseHandler<Boolean> responseHandler = response -> {
				int status = response.getStatusLine().getStatusCode();
				return status >= 200 && status < 300;
			};

			Boolean responseBody = httpclient.execute(httpPut, responseHandler);
			if (responseBody.booleanValue()) {
				server.setStatus(RSServerStatus.ONLINE);
			}

			return server.getStatus() == RSServerStatus.ONLINE;
		} catch (Exception e) {
			LOGGER.debug("Could not stop services");
			return false;
		}
	}

	private RSServerModel getServer(String strId) {
		Optional<RSServerModel> serverOptional = serverRepository.findById(UUID.fromString(strId));
		if (!serverOptional.isPresent()) {
			String errorMsg = String.format("Server with ID \"%s\" not found", strId);
			LOGGER.error(errorMsg);
			throw new RSNotFoundException(errorMsg);
		}

		return serverOptional.get();
	}

}
