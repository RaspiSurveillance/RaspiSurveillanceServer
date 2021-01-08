package de.calltopower.raspisurveillance.impl.dtoservice;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import de.calltopower.raspisurveillance.api.dtoservice.RSDtoService;
import de.calltopower.raspisurveillance.impl.dto.RSTranslationsDto;
import de.calltopower.raspisurveillance.impl.model.RSTranslationsModel;
import lombok.NoArgsConstructor;

/**
 * DTO service implementation for the translations DTO
 */
@NoArgsConstructor
@Service
public class RSTranslationsDtoService implements RSDtoService<RSTranslationsDto, RSTranslationsModel> {

    @Override
    public RSTranslationsDto convert(RSTranslationsModel model) {
        if (model == null) {
            return null;
        }

        // @formatter:off
        return RSTranslationsDto.builder()
                .translations(model.getTranslations())
                .build();
        // @formatter:on
    }

    @Override
    public RSTranslationsDto convertAbridged(RSTranslationsModel model) {
        return convert(model);
    }

    @Override
    public Set<RSTranslationsDto> convert(Set<RSTranslationsModel> models) {
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
    public Set<RSTranslationsDto> convertAbridged(Set<RSTranslationsModel> models) {
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
