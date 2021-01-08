package de.calltopower.raspisurveillance.impl.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import de.calltopower.raspisurveillance.api.dto.RSDto;
import de.calltopower.raspisurveillance.impl.model.RSTranslationsModel;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

/**
 * DTO for translations
 */
@Data
@Builder
@JsonInclude(Include.NON_EMPTY)
public class RSTranslationsDto implements RSDto<RSTranslationsModel> {

    @SuppressWarnings("javadoc")
    @JsonProperty
    public String translations;

}
