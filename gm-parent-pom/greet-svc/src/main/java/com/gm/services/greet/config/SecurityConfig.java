package com.gm.services.greet.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import com.gm.spring.security.oauth2.server.resource.config.OAuth2ResourceServiceConfig;

@EnableWebSecurity
@Import(value = { OAuth2ResourceServiceConfig.class })
public class SecurityConfig {
	@Autowired
	@Qualifier("authorityPrefixJwtConverter")
	JwtAuthenticationConverter authorityPrefixJwtConverter;

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		// @formatter:off
		http.authorizeHttpRequests(authorize -> authorize.antMatchers(HttpMethod.GET, "/test/**").permitAll().anyRequest().authenticated())
					.oauth2ResourceServer(oauth2 ->
						oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(authorityPrefixJwtConverter)));
		// @formatter:on
		return http.build();
	}
}
