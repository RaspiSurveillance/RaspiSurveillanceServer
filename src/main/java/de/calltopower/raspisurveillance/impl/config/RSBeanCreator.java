package de.calltopower.raspisurveillance.impl.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import de.calltopower.raspisurveillance.api.config.RSConfig;
import de.calltopower.raspisurveillance.impl.filter.RSAuthTokenFilter;

@Configuration
public class RSBeanCreator implements RSConfig {

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public RSAuthTokenFilter authenticationJwtTokenFilter() {
		return new RSAuthTokenFilter();
	}

}
