package de.calltopower.raspisurveillance.impl.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import de.calltopower.raspisurveillance.api.config.RSConfig;
import de.calltopower.raspisurveillance.impl.controller.RSConnectionController;
import de.calltopower.raspisurveillance.impl.controller.RSI18nController;
import de.calltopower.raspisurveillance.impl.controller.RSUserController;
import de.calltopower.raspisurveillance.impl.exception.RSAuthEntryPointJwt;
import de.calltopower.raspisurveillance.impl.filter.RSAuthTokenFilter;
import de.calltopower.raspisurveillance.impl.service.RSUserDetailsService;

/**
 * Web security configuration
 */
@Configuration
@EnableWebSecurity
// @formatter:off
@EnableGlobalMethodSecurity(
    // securedEnabled = true,
    // jsr250Enabled = true,
    prePostEnabled = true
)
// @formatter:on
public class RSWebSecurityConfig extends WebSecurityConfigurerAdapter implements RSConfig {

    @Autowired
    private RSUserDetailsService userDetailsService;

    @Autowired
    private RSAuthEntryPointJwt unauthorizedHandler;

    @Bean
    public RSAuthTokenFilter authenticationJwtTokenFilter() {
        return new RSAuthTokenFilter();
    }

    @Override
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        // @formatter:off
        httpSecurity
            .cors().and().csrf().disable()
            .exceptionHandling().authenticationEntryPoint(unauthorizedHandler)
            .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and().authorizeRequests()
                .antMatchers("/api/auth/**").permitAll()
                .antMatchers(String.format("%s/password/forgot", RSUserController.PATH)).permitAll()
                .antMatchers(String.format("%s/password/reset/**", RSUserController.PATH)).permitAll()
                .antMatchers(String.format("%s/activate/**", RSConnectionController.PATH)).permitAll()
                .antMatchers(String.format("%s/available", RSConnectionController.PATH)).permitAll()
                .antMatchers(String.format("%s/**", RSI18nController.PATH)).permitAll()
                .anyRequest().authenticated();
        // @formatter:on

        httpSecurity.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

}
