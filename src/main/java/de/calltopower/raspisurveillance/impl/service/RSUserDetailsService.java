package de.calltopower.raspisurveillance.impl.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.calltopower.raspisurveillance.api.service.RSService;
import de.calltopower.raspisurveillance.impl.db.repository.RSUserRepository;
import de.calltopower.raspisurveillance.impl.exception.RSUserException;
import de.calltopower.raspisurveillance.impl.model.RSUserDetailsImpl;
import de.calltopower.raspisurveillance.impl.model.RSUserModel;

/**
 * UserDetailsService implementation
 */
@Service
public class RSUserDetailsService implements UserDetailsService, RSService {

    @Autowired
    RSUserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws RSUserException {
        // @formatter:off
        RSUserModel user = userRepository.findByUsername(username)
                                          .orElseThrow(() -> new RSUserException(String.format("User with username \"%s\" not found", username)));
        // @formatter:on

        return RSUserDetailsImpl.build(user);
    }

}
