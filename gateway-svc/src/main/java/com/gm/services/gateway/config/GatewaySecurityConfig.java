package com.gm.services.gateway.config;

import static org.springframework.security.config.Customizer.withDefaults;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity.OAuth2ResourceServerSpec;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.util.ObjectUtils;

@EnableWebFluxSecurity
@Configuration
public class GatewaySecurityConfig {
	@Value("${app.security.endpoints.allowed:#{null}}")
	List<String> allowedEndpoints;

	@Bean
	SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, OAuth2ResourceServerProperties properties) {
		// @formatter:off
		http.csrf().disable()
			.oauth2Login(withDefaults())
			.oauth2ResourceServer(OAuth2ResourceServerSpec::opaqueToken);

		if(!ObjectUtils.isEmpty(allowedEndpoints)) {
			http.authorizeExchange()
				.pathMatchers(HttpMethod.GET, allowedEndpoints.toArray(new String[allowedEndpoints.size()])).permitAll();
		}

		//protect all APIs
		http.authorizeExchange().anyExchange().authenticated();

		// @formatter:on
		return http.build();
	}
}
