package de.calltopower.raspisurveillance.impl.utils;

import org.springframework.stereotype.Component;

import de.calltopower.raspisurveillance.api.utils.RSUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Server API endpoints
 */
@Component
@NoArgsConstructor
@Getter
public class RSServerAPIEndpoints implements RSUtils {

    private final String startup = "startup";
    private final String shutdown = "shutdown";
    private final String shutdownMaster = "shutdown/master";
    private final String status = "status";
    private final String startCamerastream = "start/camerastream";
    private final String startSurveillance = "start/surveillance";
    private final String stop = "stop";

}
