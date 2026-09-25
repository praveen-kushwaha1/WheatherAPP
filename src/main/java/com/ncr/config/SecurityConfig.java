package com.ncr.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
            // REST APIs normally don't use browser session-based CSRF protection
            .csrf(csrf -> csrf.disable())

            // Every API requires authentication
            .authorizeHttpRequests(auth -> auth
                .anyRequest().authenticated()
            )

            // Enable Basic Authentication
            .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}