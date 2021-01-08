package de.calltopower.raspisurveillance.impl.enums;

import java.util.Arrays;
import java.util.Optional;

import de.calltopower.raspisurveillance.api.enums.RSEnum;
import lombok.Getter;

/**
 * Enum for the language
 */
public enum RSLanguage implements RSEnum {
    // @formatter:off
    GERMAN("de-DE", "Deutsch"),
    ENGLISH("en-US", "English");
    // @formatter:on

    @Getter
    private String id;

    @Getter
    private String name;

    private RSLanguage(String id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Returns the enum language
     * 
     * @param id The id as string
     * @return the language
     */
    public static Optional<RSLanguage> get(String id) {
        return Arrays.stream(RSLanguage.values()).filter(r -> r.getId().equalsIgnoreCase(id)).findFirst();
    }

}
