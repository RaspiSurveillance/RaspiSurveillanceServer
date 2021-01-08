package de.calltopower.raspisurveillance.impl.dto;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import de.calltopower.raspisurveillance.api.dto.RSDto;
import de.calltopower.raspisurveillance.impl.model.RSLanguagesModel;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

/**
 * DTO for languages
 */
@Data
@Builder
@JsonInclude(Include.NON_EMPTY)
public class RSLanguagesDto implements RSDto<RSLanguagesModel> {

    @SuppressWarnings("javadoc")
    @JsonProperty
    public Map<String, String> languages;

}
