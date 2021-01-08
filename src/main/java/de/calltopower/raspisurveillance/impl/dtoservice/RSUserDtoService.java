package de.calltopower.raspisurveillance.impl.dtoservice;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import de.calltopower.raspisurveillance.api.dtoservice.RSDtoService;
import de.calltopower.raspisurveillance.impl.dto.RSUserDto;
import de.calltopower.raspisurveillance.impl.model.RSRoleModel;
import de.calltopower.raspisurveillance.impl.model.RSUserModel;
import lombok.NoArgsConstructor;

/**
 * DTO service implementation for the user DTO
 */
@NoArgsConstructor
@Service
public class RSUserDtoService implements RSDtoService<RSUserDto, RSUserModel> {

    @Override
    public RSUserDto convert(RSUserModel model) {
        if (model == null) {
            return null;
        }

        // @formatter:off
        return RSUserDto.builder()
                .id(model.getId())
                .username(model.getUsername())
                .email(model.getEmail())
                .statusVerified(model.isStatusVerified())
                .roles(getRoleNames(model.getRoles()))
                .jsonData(model.getJsonData())
                .build();
        // @formatter:on
    }

    @Override
    public RSUserDto convertAbridged(RSUserModel model) {
        if (model == null) {
            return null;
        }

        // @formatter:off
        return RSUserDto.builder()
                .id(model.getId())
                .username(model.getUsername())
                .build();
        // @formatter:on
    }

    @Override
    public Set<RSUserDto> convert(Set<RSUserModel> models) {
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
    public Set<RSUserDto> convertAbridged(Set<RSUserModel> models) {
        if (models == null) {
            return new HashSet<>();
        }

        // @formatter:off
        return models.stream()
                        .map(m -> convertAbridged(m))
                        .collect(Collectors.toSet());
        // @formatter:on
    }

    private Set<String> getRoleNames(Set<RSRoleModel> roles) {
        return roles.stream().map(role -> role.getName().getInternalName()).collect(Collectors.toSet());
    }

}
