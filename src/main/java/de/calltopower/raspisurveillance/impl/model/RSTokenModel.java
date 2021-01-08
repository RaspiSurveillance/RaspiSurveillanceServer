package de.calltopower.raspisurveillance.impl.model;

import java.io.Serializable;

import javax.persistence.Embeddable;

import de.calltopower.raspisurveillance.api.model.RSModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Model implementation for tokens
 */
@Embeddable
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RSTokenModel implements Serializable, RSModel {

    private static final long serialVersionUID = 3734859122797868303L;

    private String token;
    private RSUserModel user;

    @Override
    public String toString() {
        // @formatter:off
        return String.format(
                "RSTokenModel["
                    + "jwt='%s',"
                    + "user='%s'"
                + "]",
                token,
                user
               );
        // @formatter:on
    }

}
