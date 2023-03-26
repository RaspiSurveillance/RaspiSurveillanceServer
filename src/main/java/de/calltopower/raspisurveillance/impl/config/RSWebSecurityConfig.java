package de.calltopower.raspisurveillance.impl.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import de.calltopower.raspisurveillance.api.config.RSConfig;
import de.calltopower.raspisurveillance.impl.controller.RSConnectionController;
import de.calltopower.raspisurveillance.impl.controller.RSI18nController;
import de.calltopower.raspisurveillance.impl.controller.RSUserController;
import de.calltopower.raspisurveillance.impl.exception.RSAuthEntryPointJwt;
import de.calltopower.raspisurveillance.impl.filter.RSAuthTokenFilter;

/**
 * Web security configuration
 */
@Configuration
@EnableWebSecurity
// @formatter:off
@EnableMethodSecurity(
        // securedEnabled = true,
        // jsr250Enabled = true,
        prePostEnabled = true
    )
// @formatter:on
public class RSWebSecurityConfig implements RSConfig {

    // @formatter:off
    private static final AntPathRequestMatcher[] AUTH_WHITELIST = {
        new AntPathRequestMatcher("/api/auth/**"),
        new AntPathRequestMatcher(String.format("%s/password/forgot", RSUserController.PATH)),
        new AntPathRequestMatcher(String.format("%s/password/reset/**", RSUserController.PATH)),
        new AntPathRequestMatcher(String.format("%s/activate/**", RSConnectionController.PATH)),
        new AntPathRequestMatcher(String.format("%s/available", RSConnectionController.PATH)),
        new AntPathRequestMatcher(String.format("%s/**", RSI18nController.PATH))
    };
    // @formatter:on

    private RSAuthTokenFilter authTokenFilter;
    private RSAuthEntryPointJwt unauthorizedHandler;

    @Autowired
    public RSWebSecurityConfig(RSAuthTokenFilter authTokenFilter, RSAuthEntryPointJwt unauthorizedHandler) {
        this.authTokenFilter = authTokenFilter;
        this.unauthorizedHandler = unauthorizedHandler;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        // @formatter:off
        httpSecurity
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(requests -> requests
                .requestMatchers(AUTH_WHITELIST).permitAll()
                .anyRequest().authenticated()
            )
            .exceptionHandling(handler -> handler.authenticationEntryPoint(unauthorizedHandler))
            .addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class)
            .httpBasic(Customizer.withDefaults());
        // @formatter:on

        return httpSecurity.build();
    }

}
