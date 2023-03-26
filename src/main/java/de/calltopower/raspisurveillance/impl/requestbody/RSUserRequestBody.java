package de.calltopower.raspisurveillance.impl.requestbody;

import java.util.Set;

import de.calltopower.raspisurveillance.api.requestbody.RSRequestBody;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Request body for a user
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class RSUserRequestBody implements RSRequestBody {

    @NotBlank
    @Size(min = 3, max = 100)
    private String username;

    @NotBlank
    @Size(max = 100)
    @Email
    private String email;

    @NotBlank
    @Size(min = 6, max = 100)
    private String password;

    private Set<String> roles;

    private String jsonData;

    @Override
    public String toString() {
        return String.format("STDUserRequestBody[username=%s, email=%s, roles=%s, jsonData=%s]", username, email, roles,
                jsonData);
    }

}
