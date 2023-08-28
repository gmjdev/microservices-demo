package com.gm.services.configuration.security.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Bean
    UserDetailsService userDetailsService(@Value("${spring.security.user.name}") String configUser,
                                          @Value("${spring.security.user.password}") String configPhrase,
                                          @Value("${spring.security.actuator-user.name}") String actuatorUser,
                                          @Value("${spring.security.actuator-user.password}") String actuatorUserPhrase)
			throws Exception {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(createUser(configUser, configPhrase, "CONFIG_READ"));
        manager.createUser(createUser(actuatorUser, actuatorUserPhrase, "ACTUATOR_READ"));
        return manager;
    }

    private UserDetails createUser(String user, String phrase, String role) {
        return User.withUsername(user).password(encoder().encode(phrase)).authorities(role).build();
    }

    @Bean
    PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
      // @formatter:off
      httpSecurity.authorizeHttpRequests(authorizer ->
              authorizer.requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/error")).permitAll()
                  .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.GET,"/actuator/health/liveness")).permitAll()
                  .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.GET,"/actuator/health/readiness")).permitAll()
                  .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.GET,"/actuator/info")).permitAll()
                  .requestMatchers(new AntPathRequestMatcher("/actuator/**")).hasAuthority("ACTUATOR_READ")
                  .anyRequest().hasAuthority("CONFIG_READ")
          )
          .httpBasic(Customizer.withDefaults())
          .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
          .csrf(customizer -> customizer.disable());
      return httpSecurity.build();
      // @formatter:on
    }
}
