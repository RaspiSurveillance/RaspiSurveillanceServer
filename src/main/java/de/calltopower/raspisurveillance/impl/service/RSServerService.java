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
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
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
import de.calltopower.raspisurveillance.impl.properties.RSSettingsProperties;
import de.calltopower.raspisurveillance.impl.requestbody.RSServerRequestBody;
import de.calltopower.raspisurveillance.impl.utils.RSJsonUtils;
import de.calltopower.raspisurveillance.impl.utils.RSServerAPIEndpoints;

/**
 * Service for server results
 */
@Service
public class RSServerService implements RSService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RSServerService.class);

    private RSServerRepository serverRepository;
    private RSJsonUtils jsonUtils;
    private RSServerAPIEndpoints serverApiEndpoints;
    private RSSettingsProperties settingsProperties;

    /**
     * Initializes the service
     * 
     * @param serverRepository   The server DB repository
     * @param jsonUtils          The Json utilities
     * @param serverApiEndpoints The Server API endpoints
     * @param settingsProperties The settings properties
     */
    @Autowired
    public RSServerService(RSServerRepository serverRepository, RSJsonUtils jsonUtils,
            RSServerAPIEndpoints serverApiEndpoints, RSSettingsProperties settingsProperties) {
        this.serverRepository = serverRepository;
        this.jsonUtils = jsonUtils;
        this.serverApiEndpoints = serverApiEndpoints;
        this.settingsProperties = settingsProperties;
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
			.isMaster(requestBody.getIsMaster())
			.hasServiceCamerastream(requestBody.getHasServiceCamerastream())
			.hasServiceSurveillance(requestBody.getHasServiceSurveillance())
            .urlMaster(requestBody.getUrlMaster())
            .idMaster(requestBody.getIdMaster())
			.usernameMaster(requestBody.getUsernameMaster())
			.passwordMaster(requestBody.getPasswordMaster())
			.urlCamerastream(requestBody.getUrlCamerastream())
			.usernameCamerastream(requestBody.getUsernameCamerastream())
			.passwordCamerastream(requestBody.getPasswordCamerastream())
			.status(RSServerStatus.INITIALIZING)
			.jsonData(jsonData);
		// @formatter:on
        if (requestBody.getAttributesCamerastream() != null) {
            serverBuilder.attributesCamerastream(requestBody.getAttributesCamerastream());
        }
        if (requestBody.getAttributesSurveillance() != null) {
            serverBuilder.attributesSurveillance(requestBody.getAttributesSurveillance());
        }

        RSServerModel model = serverBuilder.build();

        return serverRepository.saveAndFlush(model);
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

        stopServices(server);
        server.setStatus(RSServerStatus.INITIALIZING);

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
        if (requestBody.getIsMaster() != null) {
            server.setMaster(requestBody.getIsMaster());
        }
        if (requestBody.getHasServiceCamerastream() != null) {
            server.setHasServiceCamerastream(requestBody.getHasServiceCamerastream());
        }
        if (requestBody.getHasServiceSurveillance() != null) {
            server.setHasServiceSurveillance(requestBody.getHasServiceSurveillance());
        }
        if (requestBody.getUrlMaster() != null) {
            server.setUrlMaster(requestBody.getUrlMaster());
        }
        if (requestBody.getIdMaster() != null) {
            server.setIdMaster(requestBody.getIdMaster());
        }
        if (requestBody.getUsernameMaster() != null) {
            server.setUsernameMaster(requestBody.getUsernameMaster());
        }
        if (requestBody.getPasswordMaster() != null) {
            server.setPasswordMaster(requestBody.getPasswordMaster());
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

        return serverRepository.saveAndFlush(server);
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

        RSServerModel server = getServer(userDetails, strId);

        stopServices(server);

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

        stopServices(server);

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
     * Starts up the server
     * 
     * @param userDetails The user authentication
     * @param strId       the ID
     */
    @Transactional(readOnly = false)
    public RSServerModel masterStartupServer(UserDetails userDetails, String strId) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(String.format("Start up server with ID \"%s\"", strId));
        }

        RSServerModel server = getServer(userDetails, strId);

        // Check status
        if (server.getStatus() != RSServerStatus.OFFLINE) {
            throw new RSFunctionalException("Server is not offline");
        } else if (server.getStatus() == RSServerStatus.INITIALIZING) {
            throw new RSFunctionalException("Server is still initializing");
        }

        // Check master server
        if (server.isMaster()) {
            throw new RSFunctionalException("Master servers cannot be started");
        }

        // Check mandatory (for this call) attributes
        if (StringUtils.isBlank(server.getUrlMaster())) {
            throw new RSFunctionalException("Master URL is blank");
        } else if (StringUtils.isBlank(server.getIdMaster())) {
            throw new RSFunctionalException("Server master ID is blank");
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Creating HTTP client");
        }
        boolean error = false;
        // @formatter:off
        RequestConfig config = RequestConfig.custom()
                                    .setConnectTimeout(settingsProperties.getRequestsTimeout())
                                    .setConnectionRequestTimeout(settingsProperties.getRequestsTimeout())
                                    .setSocketTimeout(settingsProperties.getRequestsTimeout())
                                .build();
        // @formatter:on
        try (CloseableHttpClient httpclient = HttpClientBuilder.create().setDefaultRequestConfig(config).build()) {
            String url = String.format("%s/%s", server.getUrlMaster(), serverApiEndpoints.getStartup());
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(String.format("URL: %s", url));
            }
            HttpPut httpPut = new HttpPut(url);

            Map<String, Object> options = new HashMap<>();
            options.put("id", server.getIdMaster());
            httpPut.setEntity(new StringEntity(jsonUtils.mapToJson(options)));

            UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(server.getUsernameMaster(),
                    server.getPasswordMaster());
            httpPut.addHeader(new BasicScheme().authenticate(credentials, httpPut, null));
            httpPut.setHeader("Content-type", "application/json");

            ResponseHandler<Boolean> responseHandler = response -> {
                int status = response.getStatusLine().getStatusCode();
                return status >= 200 && status < 300;
            };

            Boolean responseBody = httpclient.execute(httpPut, responseHandler);
            if (responseBody.booleanValue()) {
                server.setStatus(RSServerStatus.STARTING);
            } else {
                error = true;
            }
        } catch (Exception e) {
            error = true;
        }

        if (error) {
            throw new RSFunctionalException("Could not start up server");
        }

        return serverRepository.saveAndFlush(server);
    }

    /**
     * Shuts down the server
     * 
     * @param userDetails The user authentication
     * @param strId       the ID
     */
    @Transactional(readOnly = false)
    public RSServerModel shutdownServer(UserDetails userDetails, String strId) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(String.format("Shutting down server with ID \"%s\"", strId));
        }

        RSServerModel server = getServer(userDetails, strId);

        // Check status
        if (server.getStatus() == RSServerStatus.OFFLINE) {
            throw new RSFunctionalException("Server is offline");
        } else if (server.getStatus() == RSServerStatus.STOPPING) {
            throw new RSFunctionalException("Server is already stopping");
        } else if (server.getStatus() == RSServerStatus.INITIALIZING) {
            throw new RSFunctionalException("Server is still initializing");
        }

        // Check master server
        if (server.isMaster()) {
            throw new RSFunctionalException("Master servers cannot be shut down");
        }

        // Check mandatory (for this call) attributes
        if (StringUtils.isBlank(server.getUrl())) {
            throw new RSFunctionalException("URL is blank");
        } else if (StringUtils.isBlank(server.getIdMaster())) {
            throw new RSFunctionalException("Server master ID is blank");
        }

        stopServices(server);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Creating HTTP client");
        }
        boolean error = false;
        // @formatter:off
        RequestConfig config = RequestConfig.custom()
                                    .setConnectTimeout(settingsProperties.getRequestsTimeout())
                                    .setConnectionRequestTimeout(settingsProperties.getRequestsTimeout())
                                    .setSocketTimeout(settingsProperties.getRequestsTimeout())
                                .build();
        // @formatter:on
        try (CloseableHttpClient httpclient = HttpClientBuilder.create().setDefaultRequestConfig(config).build()) {
            String url = String.format("%s/%s", server.getUrl(), serverApiEndpoints.getShutdown());
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
                server.setStatus(RSServerStatus.STOPPING);
            } else {
                error = true;
            }
        } catch (Exception e) {
            error = true;
        }

        if (error) {
            serverRepository.saveAndFlush(server);
            throw new RSFunctionalException("Could not shut down server");
        }

        return serverRepository.saveAndFlush(server);
    }

    /**
     * Shuts down the server
     * 
     * @param server The server to be refreshed
     * @param flush  Whether to flush the repository
     */
    @Transactional(readOnly = false)
    public RSServerModel masterShutdownServer(RSServerModel server, boolean flush) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(String.format("Master shutting down server with ID \"%s\"", server.getId()));
        }

        // Check status
        if (server.getStatus() != RSServerStatus.STOPPING) {
            if (LOGGER.isWarnEnabled()) {
                LOGGER.warn(String.format("Server \"%s\" is not in mode STOPPING, stopping anyways", server.getName()));
            }
            // return server;
        }

        // Check master server
        if (server.isMaster()) {
            LOGGER.warn("Master servers cannot be shut down");
            return server;
        }

        // Check mandatory (for this call) attributes
        if (StringUtils.isBlank(server.getUrl())) {
            LOGGER.warn("URL is blank");
            return server;
        } else if (StringUtils.isBlank(server.getUrlMaster())) {
            LOGGER.warn("Master URL is blank");
            return server;
        } else if (StringUtils.isBlank(server.getIdMaster())) {
            LOGGER.warn("Server master ID is blank");
            return server;
        }

        // 1. Check whether server is still stopping
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Creating HTTP client");
        }
        boolean stillStopping = false;
        // @formatter:off
        RequestConfig config = RequestConfig.custom()
                                    .setConnectTimeout(settingsProperties.getRequestsTimeout())
                                    .setConnectionRequestTimeout(settingsProperties.getRequestsTimeout())
                                    .setSocketTimeout(settingsProperties.getRequestsTimeout())
                                .build();
        // @formatter:on
        try (CloseableHttpClient httpclient = HttpClientBuilder.create().setDefaultRequestConfig(config).build()) {
            String url = String.format("%s/%s", server.getUrl(), serverApiEndpoints.getStatus());
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
            stillStopping = responseBody.booleanValue();
        } catch (Exception e) {
            LOGGER.error(String.format("Could not check server status of server \"%s\" (\"%s\")", server.getName(),
                    server.getUrl()));
        }

        if (stillStopping) {
            if (LOGGER.isWarnEnabled()) {
                LOGGER.warn(String.format("Server \"%s\" is still stopping, not shutting down", server.getName()));
            }
            return server;
        }

        // 2. Stop server master server if server is stopped
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Creating HTTP client");
        }
        boolean error = false;
        try (CloseableHttpClient httpclient = HttpClientBuilder.create().setDefaultRequestConfig(config).build()) {
            String url = String.format("%s/%s", server.getUrlMaster(), serverApiEndpoints.getShutdownMaster());
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(String.format("URL: %s", url));
            }
            HttpPut httpPut = new HttpPut(url);

            Map<String, Object> options = new HashMap<>();
            options.put("id", server.getIdMaster());
            httpPut.setEntity(new StringEntity(jsonUtils.mapToJson(options)));

            UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(server.getUsernameMaster(),
                    server.getPasswordMaster());
            httpPut.addHeader(new BasicScheme().authenticate(credentials, httpPut, null));
            httpPut.setHeader("Content-type", "application/json");

            ResponseHandler<Boolean> responseHandler = response -> {
                int status = response.getStatusLine().getStatusCode();
                return status >= 200 && status < 300;
            };

            Boolean responseBody = httpclient.execute(httpPut, responseHandler);
            if (responseBody.booleanValue()) {
                server.setStatus(RSServerStatus.OFFLINE);
            } else {
                error = true;
            }
        } catch (Exception e) {
            error = true;
        }

        if (error) {
            if (LOGGER.isWarnEnabled()) {
                LOGGER.warn(String.format("Could not shut down server \"%s\"", server.getName()));
            }
            return server;
        }

        if (flush) {
            return serverRepository.saveAndFlush(server);
        } else {
            return serverRepository.save(server);
        }
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

        // Check mandatory (for this call) attributes
        if (StringUtils.isBlank(server.getUrl())) {
            LOGGER.warn("URL is blank");
            return server;
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Creating HTTP client");
        }
        // @formatter:off
        RequestConfig config = RequestConfig.custom()
                                    .setConnectTimeout(settingsProperties.getRequestsTimeout())
                                    .setConnectionRequestTimeout(settingsProperties.getRequestsTimeout())
                                    .setSocketTimeout(settingsProperties.getRequestsTimeout())
                                .build();
        // @formatter:on
        try (CloseableHttpClient httpclient = HttpClientBuilder.create().setDefaultRequestConfig(config).build()) {
            String url = String.format("%s/%s", server.getUrl(), serverApiEndpoints.getStatus());
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
                if (server.getStatus() == RSServerStatus.OFFLINE || server.getStatus() == RSServerStatus.STARTING
                        || server.getStatus() == RSServerStatus.INITIALIZING) {
                    server.setStatus(RSServerStatus.ONLINE);
                }
            } else if (server.getStatus() != RSServerStatus.STARTING && server.getStatus() != RSServerStatus.STOPPING) {
                server.setStatus(RSServerStatus.OFFLINE);
            }
        } catch (Exception e) {
            LOGGER.error(String.format("Could not refresh server status of server \"%s\" (\"%s\")", server.getName(),
                    server.getUrl()));
            if (server.getStatus() != RSServerStatus.STARTING && server.getStatus() != RSServerStatus.STOPPING) {
                server.setStatus(RSServerStatus.OFFLINE);
            }
        }

        if (flush) {
            return serverRepository.saveAndFlush(server);
        } else {
            return serverRepository.save(server);
        }
    }

    private boolean startCamerastream(RSServerModel server) {
        // Check status
        switch (server.getStatus()) {
        case OFFLINE:
            throw new RSFunctionalException("Server is offline");
        case INITIALIZING:
            throw new RSFunctionalException("Server is currently initializing");
        case STARTING:
            throw new RSFunctionalException("Server is currently starting");
        case STOPPING:
            throw new RSFunctionalException("Server is currently stopping");
        case CAMERA_STREAM:
            throw new RSFunctionalException("Server is already in mode camerastream");
        case SURVEILLANCE:
            throw new RSFunctionalException("Server is currently in mode surveillance");
        case ONLINE:
        default:
            break;
        }

        // Check services
        if (!server.isHasServiceCamerastream()) {
            throw new RSFunctionalException("Server does not have service camerastream");
        }

        // Check mandatory (for this call) attributes
        if (StringUtils.isBlank(server.getUrl())) {
            throw new RSFunctionalException("URL is blank");
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Creating HTTP client");
        }
        // @formatter:off
        RequestConfig config = RequestConfig.custom()
                                    .setConnectTimeout(settingsProperties.getRequestsTimeout())
                                    .setConnectionRequestTimeout(settingsProperties.getRequestsTimeout())
                                    .setSocketTimeout(settingsProperties.getRequestsTimeout())
                                .build();
        // @formatter:on
        try (CloseableHttpClient httpclient = HttpClientBuilder.create().setDefaultRequestConfig(config).build()) {
            String url = String.format("%s/%s", server.getUrl(), serverApiEndpoints.getStartCamerastream());
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(String.format("URL: %s", url));
            }
            HttpPut httpPut = new HttpPut(url);

            Map<String, Object> options = new HashMap<>();
            options.put("name", server.getUsernameCamerastream());
            options.put("password", server.getPasswordCamerastream());
            options.put("options", server.getAttributesCamerastream());
            httpPut.setEntity(new StringEntity(jsonUtils.mapToJson(options)));

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
                return true;
            }

            return false;
        } catch (Exception e) {
            throw new RSFunctionalException("Could not start camerastream");
        }
    }

    private boolean startSurveillance(RSServerModel server) {
        // Check status
        switch (server.getStatus()) {
        case OFFLINE:
            throw new RSFunctionalException("Server is offline");
        case INITIALIZING:
            throw new RSFunctionalException("Server is currently initializing");
        case STARTING:
            throw new RSFunctionalException("Server is currently starting");
        case STOPPING:
            throw new RSFunctionalException("Server is currently stopping");
        case CAMERA_STREAM:
            throw new RSFunctionalException("Server is currently in mode camerastream");
        case SURVEILLANCE:
            throw new RSFunctionalException("Server is already in mode surveillance");
        case ONLINE:
        default:
            break;
        }

        // Check services
        if (!server.isHasServiceSurveillance()) {
            throw new RSFunctionalException("Server does not have service surveillance");
        }

        // Check mandatory (for this call) attributes
        if (StringUtils.isBlank(server.getUrl())) {
            throw new RSFunctionalException("URL is blank");
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Creating HTTP client");
        }
        // @formatter:off
        RequestConfig config = RequestConfig.custom()
                                    .setConnectTimeout(settingsProperties.getRequestsTimeout())
                                    .setConnectionRequestTimeout(settingsProperties.getRequestsTimeout())
                                    .setSocketTimeout(settingsProperties.getRequestsTimeout())
                                .build();
        // @formatter:on
        try (CloseableHttpClient httpclient = HttpClientBuilder.create().setDefaultRequestConfig(config).build()) {
            String url = String.format("%s/%s", server.getUrl(), serverApiEndpoints.getStartSurveillance());
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(String.format("URL: %s", url));
            }
            HttpPut httpPut = new HttpPut(url);

            Map<String, Object> options = new HashMap<>();
            options.put("options", server.getAttributesSurveillance());
            httpPut.setEntity(new StringEntity(jsonUtils.mapToJson(options)));

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
                return true;
            }

            return false;
        } catch (Exception e) {
            throw new RSFunctionalException("Could not start surveillance");
        }
    }

    private boolean stopServices(RSServerModel server) {
        // Check status
        switch (server.getStatus()) {
        case OFFLINE:
            if (LOGGER.isWarnEnabled()) {
                LOGGER.warn(String.format("Server \"%s\" is offline", server.getName()));
            }
            return false;
        case INITIALIZING:
            if (LOGGER.isWarnEnabled()) {
                LOGGER.warn(String.format("Server \"%s\" is still initializing", server.getName()));
            }
            return false;
        case STARTING:
            if (LOGGER.isWarnEnabled()) {
                LOGGER.warn(String.format("Server \"%s\" is currently starting", server.getName()));
            }
            return false;
        case STOPPING:
            if (LOGGER.isWarnEnabled()) {
                LOGGER.warn(String.format("Server \"%s\" is currently stopping", server.getName()));
            }
            return false;
        case CAMERA_STREAM:
        case SURVEILLANCE:
        case ONLINE:
        default:
            break;
        }

        // Check services
        if (!server.isHasServiceCamerastream() && !server.isHasServiceSurveillance()) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info(String.format("Server \"%s\" does not have any activated services", server.getName()));
            }

            return true;
        }

        // Check mandatory (for this call) attributes
        if (StringUtils.isBlank(server.getUrl())) {
            throw new RSFunctionalException("URL is blank");
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Creating HTTP client");
        }
        // @formatter:off
        RequestConfig config = RequestConfig.custom()
                                    .setConnectTimeout(settingsProperties.getRequestsTimeout())
                                    .setConnectionRequestTimeout(settingsProperties.getRequestsTimeout())
                                    .setSocketTimeout(settingsProperties.getRequestsTimeout())
                                .build();
        // @formatter:on
        try (CloseableHttpClient httpclient = HttpClientBuilder.create().setDefaultRequestConfig(config).build()) {
            String url = String.format("%s/%s", server.getUrl(), serverApiEndpoints.getStop());
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
                return true;
            }

            return false;
        } catch (Exception e) {
            LOGGER.error(
                    String.format("Could not stop services for server \"%s\": %s", server.getName(), e.getMessage()));
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
