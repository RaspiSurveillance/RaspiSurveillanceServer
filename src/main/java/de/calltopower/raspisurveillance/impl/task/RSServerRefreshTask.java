package de.calltopower.raspisurveillance.impl.task;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import de.calltopower.raspisurveillance.api.task.RSSchedulingTask;
import de.calltopower.raspisurveillance.impl.db.repository.RSServerRepository;
import de.calltopower.raspisurveillance.impl.model.RSServerModel;
import de.calltopower.raspisurveillance.impl.service.RSServerService;

/**
 * A server refresh task
 */
@Configuration
public class RSServerRefreshTask implements RSSchedulingTask {

	private static final Logger LOGGER = LoggerFactory.getLogger(RSServerRefreshTask.class);

	private final long INITIAL_DELAY_UPDATE_MILLIS = 10000;
	private final long FIXED_RATE_UPDATE_MILLIS = 60 * 60 * 1000;

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

	@Scheduled(initialDelay = INITIAL_DELAY_UPDATE_MILLIS, fixedRate = FIXED_RATE_UPDATE_MILLIS)
	public void updateCurrentTodos() {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("Updating servers");
		}

		List<RSServerModel> servers = serverRepository.findAll();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format("Found %d server(s)", servers.size()));
		}
		servers.stream().forEach(s -> {
			serverService.refreshServerStatus(s, false);
		});

		serverRepository.flush();
	}

}
