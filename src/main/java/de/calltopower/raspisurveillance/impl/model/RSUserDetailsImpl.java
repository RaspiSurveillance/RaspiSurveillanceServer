package de.calltopower.raspisurveillance.impl.model;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.calltopower.raspisurveillance.api.model.RSModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * UserDetails implementation
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class RSUserDetailsImpl implements UserDetails, RSModel {

    private static final long serialVersionUID = 4861963699296040781L;

    private UUID id;
    private String username;
    private String email;

    @JsonIgnore
    private String password;

    private Collection<? extends GrantedAuthority> authorities;

    /**
     * Constructor
     * 
     * @param id          The ID
     * @param username    The username
     * @param email       The email
     * @param password    The password
     * @param authorities The authorities
     */
    public RSUserDetailsImpl(UUID id, String username, String email, String password,
            Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
    }

    /**
     * Builder
     * 
     * @param user The user model
     * @return STDUserDetailsImpl
     */
    public static RSUserDetailsImpl build(RSUserModel user) {
        // @formatter:off
        List<GrantedAuthority> authorities = user.getRoles().stream()
                                                               .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                                                               .collect(Collectors.toList());
        // @formatter:on

        // @formatter:off
        return new RSUserDetailsImpl(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                authorities
            );
        // @formatter:on
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
