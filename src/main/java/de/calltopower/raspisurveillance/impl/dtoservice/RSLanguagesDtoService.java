package de.calltopower.raspisurveillance.impl.dtoservice;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import de.calltopower.raspisurveillance.api.dtoservice.RSDtoService;
import de.calltopower.raspisurveillance.impl.dto.RSLanguagesDto;
import de.calltopower.raspisurveillance.impl.model.RSLanguagesModel;
import lombok.NoArgsConstructor;

/**
 * DTO service implementation for the languages DTO
 */
@NoArgsConstructor
@Service
public class RSLanguagesDtoService implements RSDtoService<RSLanguagesDto, RSLanguagesModel> {

    @Override
    public RSLanguagesDto convert(RSLanguagesModel model) {
        if (model == null) {
            return null;
        }

        // @formatter:off
        return RSLanguagesDto.builder()
                .languages(model.getLanguages())
                .build();
        // @formatter:on
    }

    @Override
    public RSLanguagesDto convertAbridged(RSLanguagesModel model) {
        return convert(model);
    }

    @Override
    public Set<RSLanguagesDto> convert(Set<RSLanguagesModel> models) {
        if (models == null) {
            return new HashSet<>();
        }

        // @formatter:off
        return models.stream()
                        .map(m -> convert(m))
                        .collect(Collectors.toSet());
        // @formatter:on
    }

    @Override
    public Set<RSLanguagesDto> convertAbridged(Set<RSLanguagesModel> models) {
        if (models == null) {
            return new HashSet<>();
        }

        // @formatter:off
        return models.stream()
                        .map(m -> convertAbridged(m))
                        .collect(Collectors.toSet());
        // @formatter:on
    }

}
