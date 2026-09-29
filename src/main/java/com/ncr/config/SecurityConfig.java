package com.ncr.config;

import com.ncr.service.CustomUserDetailsService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

import org.springframework.security.core.userdetails.UserDetailsService;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;


/*
 * @Configuration
 * Marks this class as a Spring configuration class.
 *
 * @EnableWebSecurity
 * Enables Spring Security's web security support.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {


    /*
     * ============================================================
     * SecurityFilterChain
     * ============================================================
     *
     * Defines the security rules for incoming HTTP requests.
     *
     * Flow:
     *
     * Request
     *    ↓
     * Spring Security Filters
     *    ↓
     * Authentication
     *    ↓
     * Authorization
     *    ↓
     * Controller
     *
     * csrf().disable()
     * → Disables CSRF protection for this REST API example.
     *
     * requestMatchers().permitAll()
     * → Allows the specified endpoint without authentication.
     *
     * anyRequest().authenticated()
     * → All other endpoints require authentication.
     *
     * httpBasic()
     * → Enables HTTP Basic Authentication.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/h2-console/**").permitAll()
                        .anyRequest().authenticated())
                .httpBasic(withDefaults());

        return http.build();
    }


    /*
     * ============================================================
     * UserDetailsService
     * ============================================================
     *
     * Responsible for loading user information.
     *
     * Spring Security asks:
     *
     * "Give me the details of this username."
     *
     * CustomUserDetailsService can load the user from a database.
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailsService();
    }


    /*
     * ============================================================
     * PasswordEncoder
     * ============================================================
     *
     * Used to securely hash and verify passwords.
     *
     * BCrypt is a one-way password hashing algorithm.
     *
     * Database should store:
     *
     * BCrypt password
     *
     * NOT:
     *
     * Plain-text password
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    /*
     * ============================================================
     * AuthenticationManager
     * ============================================================
     *
     * Main component responsible for authentication.
     *
     * AuthenticationManager
     *        ↓
     * DaoAuthenticationProvider
     *        ↓
     * UserDetailsService
     *        +
     * PasswordEncoder
     *
     * DaoAuthenticationProvider:
     * → Loads user using UserDetailsService.
     * → Verifies password using PasswordEncoder.
     *
     * ProviderManager:
     * → Implementation of AuthenticationManager.
     * → Delegates authentication to AuthenticationProvider.
     */
    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {

        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();

        daoAuthenticationProvider.setUserDetailsService(userDetailsService);

        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);

        return new ProviderManager(daoAuthenticationProvider);
    }
}

/*
        HTTP Request
     ↓
        SecurityFilterChain
     ↓
        AuthenticationManager
     ↓
        DaoAuthenticationProvider
     ↓
        UserDetailsService → Find User
     ↓
        PasswordEncoder → Verify Password
     ↓
        Authentication Success
     ↓
        Controller

        */
