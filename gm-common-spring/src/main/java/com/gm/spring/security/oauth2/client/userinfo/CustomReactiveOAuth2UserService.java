package com.gm.spring.security.oauth2.client.userinfo;

import java.text.MessageFormat;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultReactiveOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.AuthenticationMethod;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.UnsupportedMediaTypeException;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import com.nimbusds.oauth2.sdk.ErrorObject;
import com.nimbusds.openid.connect.sdk.UserInfoErrorResponse;

import net.minidev.json.JSONObject;
import reactor.core.publisher.Mono;

public class CustomReactiveOAuth2UserService extends DefaultReactiveOAuth2UserService {
	private static final String INVALID_USER_INFO_RESPONSE_ERROR_CODE = "invalid_user_info_response";
	private static final String MISSING_USER_INFO_URI_ERROR_CODE = "missing_user_info_uri";
	private static final String MISSING_USER_NAME_ATTRIBUTE_ERROR_CODE = "missing_user_name_attribute";
	private static final ParameterizedTypeReference<Map<String, Object>> STRING_OBJECT_MAP = new ParameterizedTypeReference<>() {
	};
	private static final ParameterizedTypeReference<Map<String, String>> STRING_STRING_MAP = new ParameterizedTypeReference<>() {
	};

	private static final String UNSUPPORTED_MEDIA_ERR = """
			An error occurred while attempting to retrieve the UserInfo Resource from {0}'
			 ': response contains invalid content type '{1}'.
			 The UserInfo Response should return a JSON object (content type 'application/json')
			 that contains a collection of name and value pairs of the claims about the authenticated End-User.
			 Please ensure the UserInfo Uri in UserInfoEndpoint for Client Registration '{2}' conforms to the UserInfo Endpoint,
			 as defined in OpenID Connect 1.0: 'https://openid.net/specs/openid-connect-core-1_0.html#UserInfo';
			""";

	private WebClient webClient = WebClient.create();
	@Value("${app.security.token.authority-prefix:}")
	private String authorityPrefix;

	@Override
	public Mono<OAuth2User> loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		return Mono.defer(() -> {
			var userInfoUri = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUri();
			if (!StringUtils.hasText(userInfoUri)) {
				var oauth2Error = new OAuth2Error(MISSING_USER_INFO_URI_ERROR_CODE,
						"Missing required UserInfo Uri in UserInfoEndpoint for Client Registration: "
								+ userRequest.getClientRegistration().getRegistrationId(),
						null);
				throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
			}
			var userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint()
					.getUserNameAttributeName();
			if (!StringUtils.hasText(userNameAttributeName)) {
				var oauth2Error = new OAuth2Error(MISSING_USER_NAME_ATTRIBUTE_ERROR_CODE,
						"Missing required \"user name\" attribute name in UserInfoEndpoint for Client Registration: "
								+ userRequest.getClientRegistration().getRegistrationId(),
						null);
				throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
			}
			var authenticationMethod = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint()
					.getAuthenticationMethod();
			var requestHeadersSpec = getRequestHeaderSpec(userRequest, userInfoUri, authenticationMethod);
			// @formatter:off
			Mono<Map<String, Object>> userAttributes = requestHeadersSpec.retrieve()
					.onStatus(HttpStatus::isError, response -> parse(response)
						.map(userInfoErrorResponse -> {
							var description = userInfoErrorResponse.getErrorObject().getDescription();
							var oauth2Error = new OAuth2Error(INVALID_USER_INFO_RESPONSE_ERROR_CODE, description,
								null);
							throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
						})
					)
					.bodyToMono(STRING_OBJECT_MAP);
			return userAttributes.map(attrs -> {
				var authorities = userRequest.getAccessToken().getScopes()
						.parallelStream().map(authorityPrefix::concat).map(SimpleGrantedAuthority::new)
						.collect(Collectors.toSet());
				return new DefaultOAuth2User(authorities, attrs, userNameAttributeName);
			})
			.onErrorMap(ex -> (ex instanceof UnsupportedMediaTypeException ||
					ex.getCause() instanceof UnsupportedMediaTypeException), ex -> {
				var contentType = ex instanceof UnsupportedMediaTypeException ?
						((UnsupportedMediaTypeException) ex).getContentType().toString() :
						((UnsupportedMediaTypeException) ex.getCause()).getContentType().toString();
				var errorMessage = MessageFormat.format(UNSUPPORTED_MEDIA_ERR,
						userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint(),contentType,
						userRequest.getClientRegistration().getRegistrationId());
				var oauth2Error = new OAuth2Error(INVALID_USER_INFO_RESPONSE_ERROR_CODE, errorMessage,
						null);
				throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString(), ex);
			})
			.onErrorMap(ex -> {
				var oauth2Error = new OAuth2Error(INVALID_USER_INFO_RESPONSE_ERROR_CODE,
						"An error occurred reading the UserInfo response: " + ex.getMessage(), null);
				return new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString(), ex);
			});
		});
	}

	private WebClient.RequestHeadersSpec<?> getRequestHeaderSpec(OAuth2UserRequest userRequest, String userInfoUri,
			AuthenticationMethod authenticationMethod) {
		if (AuthenticationMethod.FORM.equals(authenticationMethod)) {
			// @formatter:off
			return webClient.post()
					.uri(userInfoUri)
					.header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
					.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
					.bodyValue("access_token=" + userRequest.getAccessToken().getTokenValue());
			// @formatter:on
		}
		// @formatter:off
		return webClient.get()
				.uri(userInfoUri)
				.header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
				.headers(headers -> headers
						.setBearerAuth(userRequest.getAccessToken().getTokenValue())
				);
		// @formatter:on
	}

	private static Mono<UserInfoErrorResponse> parse(ClientResponse httpResponse) {
		var wwwAuth = httpResponse.headers().asHttpHeaders().getFirst(HttpHeaders.WWW_AUTHENTICATE);
		if (!ObjectUtils.isEmpty(wwwAuth)) {
			// Bearer token error?
			return Mono.fromCallable(() -> UserInfoErrorResponse.parse(wwwAuth));
		}
		// Other error?
		return httpResponse.bodyToMono(STRING_STRING_MAP)
				.map(body -> new UserInfoErrorResponse(ErrorObject.parse(new JSONObject(body))));
	}
}
