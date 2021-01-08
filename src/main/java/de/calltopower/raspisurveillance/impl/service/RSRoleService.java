package de.calltopower.raspisurveillance.impl.service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.calltopower.raspisurveillance.api.service.RSService;
import de.calltopower.raspisurveillance.impl.db.repository.RSUserRoleRepository;
import de.calltopower.raspisurveillance.impl.enums.RSUserRole;
import de.calltopower.raspisurveillance.impl.exception.RSNotFoundException;
import de.calltopower.raspisurveillance.impl.model.RSRoleModel;

/**
 * Service for role results
 */
@Service
public class RSRoleService implements RSService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RSRoleService.class);

    private RSUserRoleRepository roleRepository;

    /**
     * Initializes the service
     * 
     * @param roleRepository The role repository
     */
    @Autowired
    public RSRoleService(RSUserRoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    /**
     * Converts given roles as string to STDRoleModel
     * 
     * @param roles The roles as string
     * @return converted roles
     */
    public Set<RSRoleModel> convertRoles(Set<String> roles) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(String.format("Converting roles \"%s\"", roles));
        }
        if (roles == null) {
            return new HashSet<>();
        }

        Set<RSRoleModel> allRoles = roleRepository.findAll().stream().collect(Collectors.toSet());

        // @formatter:off
        return roles.stream()
                .map(String::toUpperCase)
                .map(role -> {
                    Optional<RSUserRole> userRoleOpt = RSUserRole.get(role);
                    if (!userRoleOpt.isPresent()) {
                        throw new RSNotFoundException(String.format("Role \"%s\" not found.", role));
                    }
                    
                    return userRoleOpt;
                })
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(role -> {
                    Optional<RSRoleModel> mappedRole = allRoles.stream()
                    .filter(r -> r.getName().getInternalName().equalsIgnoreCase(role.getInternalName()))
                    .findFirst();
                    if (!mappedRole.isPresent()) {
                        throw new RSNotFoundException(String.format("Role \"%s\" not found in database.", role));
                    }
                    
                    return mappedRole;
                })
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
        // @formatter:on
    }

    /**
     * Returns the standard user role
     * 
     * @return the standard user role
     */
    public RSRoleModel getStandardUserRole() {
        return getRole(RSUserRole.ROLE_USER);
    }

    /**
     * Returns the admin user role
     * 
     * @return the admin user role
     */
    public RSRoleModel getAdminUserRole() {
        return getRole(RSUserRole.ROLE_ADMIN);
    }

    private RSRoleModel getRole(RSUserRole role) {
        return roleRepository.findByName(role)
                .orElseThrow(() -> new RSNotFoundException(String.format("Role \"%s\" not found.", role)));
    }

}
