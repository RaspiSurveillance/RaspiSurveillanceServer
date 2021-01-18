package de.calltopower.raspisurveillance.impl.task;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import de.calltopower.raspisurveillance.api.task.RSSchedulingTask;
import de.calltopower.raspisurveillance.impl.db.repository.RSServerRepository;
import de.calltopower.raspisurveillance.impl.enums.RSServerStatus;
import de.calltopower.raspisurveillance.impl.model.RSServerModel;
import de.calltopower.raspisurveillance.impl.service.RSServerService;

/**
 * A server refresh task
 */
@Configuration
public class RSServerRefreshTask implements RSSchedulingTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(RSServerRefreshTask.class);

    private final long UPDATE_SERVER_STATUS_INITIAL_DELAY_UPDATE_MILLIS = 10 * 1000;
    private final long UPDATE_SERVER_STATUS_FIXED_RATE_UPDATE_MILLIS = 60 * 60 * 1000;
    private final long UPDATE_SERVER_STATUS_STARTSTOP_INITIAL_DELAY_UPDATE_MILLIS = 30 * 1000;
    private final long UPDATE_SERVER_STATUS_STARTSTOP_FIXED_RATE_UPDATE_MILLIS = 30 * 1000;

    private RSServerRepository serverRepository;
    private RSServerService serverService;

    /**
     * Initializes the component
     * 
     * @param serverRepository The server repository
     * @param serverService    The server service
     */
    @Autowired
    public RSServerRefreshTask(RSServerRepository serverRepository, RSServerService serverService) {
        this.serverRepository = serverRepository;
        this.serverService = serverService;
    }

    @Scheduled(initialDelay = UPDATE_SERVER_STATUS_INITIAL_DELAY_UPDATE_MILLIS, fixedRate = UPDATE_SERVER_STATUS_FIXED_RATE_UPDATE_MILLIS)
    public void updateServerStatus() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Updating servers");
        }

        List<RSServerModel> servers = serverRepository.findAll();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(String.format("Found %d server(s)", servers.size()));
        }
        // @formatter:off
        servers.stream()
            .forEach(s -> {
                serverService.refreshServerStatus(s, false);
            });
        // @formatter:on

        serverRepository.flush();
    }

    @Scheduled(initialDelay = UPDATE_SERVER_STATUS_STARTSTOP_INITIAL_DELAY_UPDATE_MILLIS, fixedRate = UPDATE_SERVER_STATUS_STARTSTOP_FIXED_RATE_UPDATE_MILLIS)
    public void updateServerStatusStartingStopping() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Updating servers (start/stop)");
        }

        List<RSServerModel> servers = serverRepository.findAll();
        // @formatter:off
        servers.stream()
            .filter(s -> s.getStatus() == RSServerStatus.STARTING || s.getStatus() == RSServerStatus.INITIALIZING)
            .forEach(s -> {
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info(String.format("Checking starting/initializing server with id \"%s\"", s.getId()));
                }
                serverService.refreshServerStatus(s, false);
            });
        // @formatter:on

        // @formatter:off
        servers.stream()
            .filter(s -> s.getStatus() == RSServerStatus.STOPPING)
            .forEach(s -> {
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info(String.format("Checking stopping server with id \"%s\"", s.getId()));
                }
                serverService.masterShutdownServer(s, false);
            });
        // @formatter:on

        serverRepository.flush();
    }

}
