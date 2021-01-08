package de.calltopower.raspisurveillance.impl.model;

import java.io.Serializable;
import java.util.Map;

import de.calltopower.raspisurveillance.api.model.RSModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Model implementation for languages
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class RSLanguagesModel implements Serializable, RSModel {

    private static final long serialVersionUID = 939526081797608869L;

    public Map<String, String> languages;

    @Override
    public String toString() {
        // @formatter:off
        return String.format(
                "RSLanguagesModel["
                    + "size='%d'"
                + "]",
                languages.size()
               );
        // @formatter:on
    }

}
