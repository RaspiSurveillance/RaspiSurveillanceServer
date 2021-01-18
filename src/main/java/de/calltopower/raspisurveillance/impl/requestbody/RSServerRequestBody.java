package de.calltopower.raspisurveillance.impl.requestbody;

import java.util.Map;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import de.calltopower.raspisurveillance.api.requestbody.RSRequestBody;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Request body for a server
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class RSServerRequestBody implements RSRequestBody {

    @NotBlank
    @Size(min = 1, max = 100)
    private String name;

    @NotBlank
    @Size(min = 2, max = 100)
    private String url;

    @Size(min = 2, max = 100)
    private String username;

    @Size(min = 2, max = 100)
    private String password;

    @NotBlank
    private Boolean isMaster;

    @NotBlank
    private Boolean hasServiceCamerastream;

    @NotBlank
    private Boolean hasServiceSurveillance;

    @Size(min = 2, max = 100)
    private String urlMaster;

    @Size(min = 2, max = 100)
    private String idMaster;

    @Size(min = 2, max = 100)
    private String usernameMaster;

    @Size(min = 2, max = 100)
    private String passwordMaster;

    @Size(min = 2, max = 100)
    private String urlCamerastream;

    @Size(min = 2, max = 100)
    private String usernameCamerastream;

    @Size(min = 2, max = 100)
    private String passwordCamerastream;

    private Map<String, String> attributesCamerastream;

    private Map<String, String> attributesSurveillance;

    private String jsonData;

    @Override
    public String toString() {
        return String.format(
                "RSServerRequestBody[name=%s, url=%s, username=%s, isMaster=%b, hasServiceCamerastream=%b, hasServiceSurveillance=%b, urlMaster=%s, usernameMaster=%s, urlCamerastream=%s, usernameCamerastream=%s, jsonData=%s]",
                name, url, username, isMaster, hasServiceCamerastream, hasServiceSurveillance, urlMaster,
                usernameMaster, urlCamerastream, usernameCamerastream, jsonData);
    }

}
