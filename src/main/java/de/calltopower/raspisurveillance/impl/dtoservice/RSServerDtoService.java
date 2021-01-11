package de.calltopower.raspisurveillance.impl.dtoservice;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import de.calltopower.raspisurveillance.api.dtoservice.RSDtoService;
import de.calltopower.raspisurveillance.impl.dto.RSServerDto;
import de.calltopower.raspisurveillance.impl.model.RSServerModel;
import lombok.NoArgsConstructor;

/**
 * DTO service implementation for the server DTO
 */
@NoArgsConstructor
@Service
public class RSServerDtoService implements RSDtoService<RSServerDto, RSServerModel> {

    @Override
    public RSServerDto convert(RSServerModel model) {
        if (model == null) {
            return null;
        }

        // @formatter:off
        return RSServerDto.builder()
                .id(model.getId())
                //.createdDate(model.getCreatedDate())
                .name(model.getName())
                .url(model.getUrl())
                .username(model.getUsername())
                .password(model.getPassword())
                .isMaster(model.isMaster())
                .hasServiceCamerastream(model.isHasServiceCamerastream())
                .hasServiceSurveillance(model.isHasServiceSurveillance())
                .urlMaster(model.getUrlMaster())
                .idMaster(model.getIdMaster())
                .usernameMaster(model.getUsernameMaster())
                .passwordMaster(model.getPasswordMaster())
                .urlCamerastream(model.getUrlCamerastream())
                .usernameCamerastream(model.getUsernameCamerastream())
                .passwordCamerastream(model.getPasswordCamerastream())
                .attributesCamerastream(model.getAttributesCamerastream())
                .attributesSurveillance(model.getAttributesSurveillance())
                .status(model.getStatus())
                .jsonData(model.getJsonData())
                .build();
        // @formatter:on
    }

    @Override
    public RSServerDto convertAbridged(RSServerModel model) {
        if (model == null) {
            return null;
        }

        // @formatter:off
        return RSServerDto.builder()
                .id(model.getId())
                //.createdDate(model.getCreatedDate())
                .name(model.getName())
                .isMaster(model.isMaster())
                .hasServiceCamerastream(model.isHasServiceCamerastream())
                .hasServiceSurveillance(model.isHasServiceSurveillance())
                .urlCamerastream(model.getUrlCamerastream())
                .usernameCamerastream(model.getUsernameCamerastream())
                .passwordCamerastream(model.getPasswordCamerastream())
                .status(model.getStatus())
                .jsonData(model.getJsonData())
                .build();
        // @formatter:on
    }

    @Override
    public Set<RSServerDto> convert(Set<RSServerModel> models) {
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
    public Set<RSServerDto> convertAbridged(Set<RSServerModel> models) {
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
