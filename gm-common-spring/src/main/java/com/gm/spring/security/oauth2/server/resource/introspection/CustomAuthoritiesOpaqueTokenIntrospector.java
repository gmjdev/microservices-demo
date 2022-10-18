package com.gm.spring.security.oauth2.server.resource.introspection;


import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2TokenIntrospectionClaimNames;
import org.springframework.security.oauth2.server.resource.introspection.NimbusReactiveOpaqueTokenIntrospector;
import org.springframework.security.oauth2.server.resource.introspection.ReactiveOpaqueTokenIntrospector;

import reactor.core.publisher.Mono;

public class CustomAuthoritiesOpaqueTokenIntrospector implements ReactiveOpaqueTokenIntrospector {
	private final ReactiveOpaqueTokenIntrospector delegate;
	@Value("${app.security.token.authority-prefix:}")
	private String authorityPrefix;

	public CustomAuthoritiesOpaqueTokenIntrospector(OAuth2ResourceServerProperties properties) {
		delegate = new NimbusReactiveOpaqueTokenIntrospector(properties.getOpaquetoken().getIntrospectionUri(),
				properties.getOpaquetoken().getClientId(), properties.getOpaquetoken().getClientSecret());
	}

	@Override
	public Mono<OAuth2AuthenticatedPrincipal> introspect(String token) {
		return delegate.introspect(token).map(principal -> new DefaultOAuth2AuthenticatedPrincipal(principal.getName(),
				principal.getAttributes(), extractAuthorities(principal)));
	}

	private Collection<GrantedAuthority> extractAuthorities(OAuth2AuthenticatedPrincipal principal) {
		List<String> scopes = principal.getAttribute(OAuth2TokenIntrospectionClaimNames.SCOPE);
		return scopes.stream().parallel().map(authorityPrefix::concat).map(SimpleGrantedAuthority::new)
				.collect(Collectors.toList());
	}
}
