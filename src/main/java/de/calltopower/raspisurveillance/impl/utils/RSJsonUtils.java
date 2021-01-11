package de.calltopower.raspisurveillance.impl.utils;

import java.util.Map;
import java.util.Map.Entry;

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

    @SuppressWarnings("unchecked")
    public String mapToJson(Map<String, Object> attrs) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        int i = 0;
        for (Entry<String, Object> entry : attrs.entrySet()) {
            if (i > 0) {
                sb.append(",");
            }
            ++i;
            boolean isMap = entry.getValue() instanceof Map;
            // @formatter:off
            sb.append("\"").append(entry.getKey()).append("\"");
            sb.append(isMap ? ": " : ": \"");
            sb.append(isMap ? mapToJson((Map<String, Object>) entry.getValue()) : entry.getValue());
            sb.append(isMap ? "" : "\"");
            // @formatter:on
        }
        sb.append("}");

        return sb.toString();
    }

}
