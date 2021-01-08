package de.calltopower.raspisurveillance.impl.requestbody;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import de.calltopower.raspisurveillance.api.requestbody.RSRequestBody;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Request body for a signin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class RSSigninRequestBody implements RSRequestBody {

    @NotBlank
    @Size(min = 3, max = 100)
    private String username;

    @NotBlank
    @Size(min = 6, max = 100)
    private String password;

    @Override
    public String toString() {
        return String.format("STDSigninRequestBody[username=%s]", username);
    }

}
