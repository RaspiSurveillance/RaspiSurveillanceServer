package de.calltopower.raspisurveillance.impl.enums;

import java.util.Arrays;
import java.util.Optional;

import de.calltopower.raspisurveillance.api.enums.RSEnum;
import lombok.Getter;

/**
 * Enum for the language
 */
public enum RSServerStatus implements RSEnum {
    // @formatter:off
    OFFLINE("offline"),
    STARTING("starting"),
    STOPPING("stopping"),
    ONLINE("online"),
    CAMERA_STREAM("camerastream"),
    SURVEILLANCE("surveillance");
    // @formatter:on

    @Getter
    private String name;

    private RSServerStatus(String name) {
        this.name = name;
    }

    /**
     * Returns the enum server status
     * 
     * @param name The name as string
     * @return the language
     */
    public static Optional<RSServerStatus> get(String name) {
        return Arrays.stream(RSServerStatus.values()).filter(r -> r.getName().equalsIgnoreCase(name)).findFirst();
    }

}
