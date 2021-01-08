package de.calltopower.raspisurveillance.impl.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import de.calltopower.raspisurveillance.api.utils.RSUtils;
import lombok.NoArgsConstructor;

/**
 * Json utilities
 */
@Component
@NoArgsConstructor
public class RSJsonUtils implements RSUtils {

    public static final String JSON_STR_DEFAULT_EMPTY = "{}";

    /**
     * Returns a valid empty Json string if given string is empty, returns the given
     * string else
     * 
     * @param str The string
     * @return valid empty Json string if given string is empty, the given string
     *         else
     */
    public String getNonEmptyJson(String str) {
        return StringUtils.isNotBlank(str) ? str : JSON_STR_DEFAULT_EMPTY;
    }

}
