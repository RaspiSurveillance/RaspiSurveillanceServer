package de.calltopower.raspisurveillance.impl.dto;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.calltopower.raspisurveillance.api.dto.RSDto;
import de.calltopower.raspisurveillance.impl.enums.RSServerStatus;
import de.calltopower.raspisurveillance.impl.model.RSServerModel;
import lombok.Builder;
import lombok.Data;

/**
 * DTO for a server model
 */
@Data
@Builder
@JsonInclude(Include.NON_EMPTY)
public class RSServerDto implements RSDto<RSServerModel> {

    @SuppressWarnings("javadoc")
    @JsonProperty
    public UUID id;

    @SuppressWarnings("javadoc")
    @JsonProperty
    public Date createdDate;

    @SuppressWarnings("javadoc")
    @JsonProperty
    public String name;

    @SuppressWarnings("javadoc")
    @JsonProperty
    public String url;

    @SuppressWarnings("javadoc")
    @JsonProperty
    public String username;

    @SuppressWarnings("javadoc")
    @JsonProperty
    public String password;

    @SuppressWarnings("javadoc")
    @JsonProperty
    private boolean isMaster;

    @SuppressWarnings("javadoc")
    @JsonProperty
    private boolean hasServiceCamerastream;

    @SuppressWarnings("javadoc")
    @JsonProperty
    private boolean hasServiceSurveillance;

    @SuppressWarnings("javadoc")
    @JsonProperty
    public String urlMaster;

    @SuppressWarnings("javadoc")
    @JsonProperty
    public String idMaster;

    @SuppressWarnings("javadoc")
    @JsonProperty
    public String usernameMaster;

    @SuppressWarnings("javadoc")
    @JsonProperty
    public String passwordMaster;

    @SuppressWarnings("javadoc")
    @JsonProperty
    private String urlCamerastream;

    @SuppressWarnings("javadoc")
    @JsonProperty
    private String usernameCamerastream;

    @SuppressWarnings("javadoc")
    @JsonProperty
    private String passwordCamerastream;

    @SuppressWarnings("javadoc")
    @JsonProperty
    private Map<String, String> attributesCamerastream;

    @SuppressWarnings("javadoc")
    @JsonProperty
    private Map<String, String> attributesSurveillance;

    @SuppressWarnings("javadoc")
    @JsonProperty
    public RSServerStatus status;

    @SuppressWarnings("javadoc")
    @JsonProperty
    private String jsonData;

}
