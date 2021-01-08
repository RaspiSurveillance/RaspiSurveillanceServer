package de.calltopower.raspisurveillance.impl.dto;

import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import de.calltopower.raspisurveillance.api.dto.RSDto;
import de.calltopower.raspisurveillance.impl.model.RSUserModel;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

/**
 * DTO for a user model
 */
@Data
@Builder
@JsonInclude(Include.NON_EMPTY)
public class RSUserDto implements RSDto<RSUserModel> {

    @SuppressWarnings("javadoc")
    @JsonProperty
    public UUID id;

    @SuppressWarnings("javadoc")
    @JsonProperty
    public String username;

    @SuppressWarnings("javadoc")
    @JsonProperty
    public String email;

    @SuppressWarnings("javadoc")
    @JsonProperty
    public Boolean statusVerified;

    @SuppressWarnings("javadoc")
    @JsonProperty
    public Set<String> roles;

    @SuppressWarnings("javadoc")
    @JsonProperty
    private String jsonData;

}
