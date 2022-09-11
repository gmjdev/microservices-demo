package com.gm.services.gateway.config;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity.OAuth2ResourceServerSpec;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.ReactiveOAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.server.resource.introspection.ReactiveOpaqueTokenIntrospector;
import org.springframework.security.web.server.SecurityWebFilterChain;

import com.gm.spring.security.oauth2.client.userinfo.CustomReactiveOAuth2UserService;
import com.gm.spring.security.oauth2.server.resource.introspection.CustomAuthoritiesOpaqueTokenIntrospector;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
	@Value("${app.security.endpoints.allowed}")
	String[] allowedEndpoints = { "/actuator/health", "/actuator/**" };

	@Bean
	SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, OAuth2ResourceServerProperties properties) {
		// @formatter:off
		http.csrf().disable()
			.authorizeExchange()
				.pathMatchers(HttpMethod.GET, allowedEndpoints).permitAll()
				.anyExchange()
					.authenticated()
				.and()
				.oauth2Login(withDefaults())
				.oauth2ResourceServer(OAuth2ResourceServerSpec::opaqueToken);
		// @formatter:on
		return http.build();
	}

	@Bean
	ReactiveOAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService() {
		return new CustomReactiveOAuth2UserService();
	}

	@Bean
	ReactiveOpaqueTokenIntrospector introspector(OAuth2ResourceServerProperties properties) {
		return new CustomAuthoritiesOpaqueTokenIntrospector(properties);
	}
}