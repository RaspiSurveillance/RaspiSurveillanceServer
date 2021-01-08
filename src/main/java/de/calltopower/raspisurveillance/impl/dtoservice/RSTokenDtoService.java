package de.calltopower.raspisurveillance.impl.dtoservice;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.calltopower.raspisurveillance.api.dtoservice.RSDtoService;
import de.calltopower.raspisurveillance.impl.dto.RSTokenDto;
import de.calltopower.raspisurveillance.impl.dto.RSUserDto;
import de.calltopower.raspisurveillance.impl.model.RSTokenModel;
import lombok.NoArgsConstructor;

/**
 * DTO service implementation for the token DTO
 */
@NoArgsConstructor
@Service
public class RSTokenDtoService implements RSDtoService<RSTokenDto, RSTokenModel> {

    private RSUserDtoService userDtoService;

    @Autowired
    public RSTokenDtoService(RSUserDtoService userDtoService) {
        this.userDtoService = userDtoService;
    }

    @Override
    public RSTokenDto convert(RSTokenModel model) {
        if (model == null) {
            return null;
        }

        // @formatter:off
        RSUserDto user = userDtoService.convert(model.getUser());
        return RSTokenDto.builder()
                .token(model.getToken())
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .statusVerified(user.getStatusVerified())
                .roles(user.getRoles())
                .build();
        // @formatter:on
    }

    @Override
    public RSTokenDto convertAbridged(RSTokenModel model) {
        return convert(model);
    }

    @Override
    public Set<RSTokenDto> convert(Set<RSTokenModel> models) {
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
    public Set<RSTokenDto> convertAbridged(Set<RSTokenModel> models) {
        return convert(models);
    }

}
