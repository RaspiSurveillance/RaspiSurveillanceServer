package de.calltopower.raspisurveillance.impl.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.crypto.password.PasswordEncoder;

import de.calltopower.raspisurveillance.api.config.RSConfig;
import de.calltopower.raspisurveillance.impl.service.RSUserDetailsService;

public class RSGlobalConfig implements RSConfig {

	private PasswordEncoder passwordEncoder;
	private RSUserDetailsService userDetailsService;

	@Autowired
	public RSGlobalConfig(PasswordEncoder passwordEncoder, RSUserDetailsService userDetailsService) {
		this.passwordEncoder = passwordEncoder;
		this.userDetailsService = userDetailsService;
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
	}

}
