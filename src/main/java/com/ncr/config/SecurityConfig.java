package com.ncr.config;

import com.ncr.dto.Permissions;
import com.ncr.filters.JwtAuthFilter;
import com.ncr.service.CustomUserDetailsService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;
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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


/*
 * @Configuration
 * → Marks this class as a Spring configuration class.
 *
 * @EnableWebSecurity
 * → Enables Spring Security's web security support.
 *
 * Main responsibility:
 * → Configure authentication
 * → Configure authorization
 * → Configure password encoding
 * → Configure JWT filter
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final CustomUserDetailsService customUserDetailsService;

    /*
     * Constructor Injection:
     *
     * Spring automatically provides:
     *
     * JwtAuthFilter
     * CustomUserDetailsService
     *
     * Constructor injection is preferred over field injection
     * because dependencies can be final and clearly defined.
     */
    public SecurityConfig(JwtAuthFilter jwtAuthFilter, CustomUserDetailsService customUserDetailsService) {

        this.jwtAuthFilter = jwtAuthFilter;
        this.customUserDetailsService = customUserDetailsService;
    }


    /*
     * ============================================================
     * SECURITY FILTER CHAIN
     * ============================================================
     *
     * Defines security rules for incoming HTTP requests.
     *
     * Request
     *    ↓
     * Spring Security Filter Chain
     *    ↓
     * JwtAuthFilter
     *    ↓
     * Authentication
     *    ↓
     * Authorization
     *    ↓
     * Controller
     *
     * /authenticate/** → Public
     *                    Used to login and generate JWT.
     *
     * /h2-console/**   → Public
     *                    Useful only for local H2 testing.
     *
     * anyRequest()     → Requires authentication.
     *
     * csrf().disable()
     * → Common for stateless REST APIs using JWT in the
     *   Authorization header.
     *
     * IMPORTANT:
     * JWT authentication is handled by JwtAuthFilter.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests(auth -> auth

                        // Public endpoint for JWT login
                        .requestMatchers("/authenticate/**").permitAll()

                        // Public H2 console for local development
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers(HttpMethod.GET,"/weather/**").hasAuthority(Permissions.WEATHER_READ.name())
                        .requestMatchers(HttpMethod.POST,"/weather/**").hasAuthority(Permissions.WEATHER_WRITE.name())
                        .requestMatchers(HttpMethod.DELETE,"/weather/**").hasAuthority(Permissions.WEATHER_DELETE.name())

                        // Every other endpoint requires authentication
                        .anyRequest().authenticated())

                /*
                 * Add JWT filter before Spring Security's
                 * UsernamePasswordAuthenticationFilter.
                 *
                 * Why before?
                 *
                 * JWT must be processed before the request
                 * reaches protected controller endpoints.
                 */.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    /*
     * ============================================================
     * USER DETAILS SERVICE
     * ============================================================
     *
     * UserDetailsService is responsible for loading user
     * information during authentication.
     *
     * Flow:
     *
     * username
     *    ↓
     * CustomUserDetailsService
     *    ↓
     * Repository
     *    ↓
     * Database
     *    ↓
     * UserDetails
     *
     * CustomUserDetailsService should normally be annotated
     * with @Service.
     */
    @Bean
    public UserDetailsService userDetailsService() {

        return customUserDetailsService;
    }


    /*
     * ============================================================
     * PASSWORD ENCODER
     * ============================================================
     *
     * BCrypt is used to securely hash passwords.
     *
     * IMPORTANT:
     *
     * Plain password:
     *     password
     *
     * Database:
     *     $2a$10$........
     *
     * BCrypt is one-way.
     * The password is NOT decrypted.
     *
     * During login:
     *
     * Raw Password
     *      +
     * Stored BCrypt Password
     *      ↓
     * PasswordEncoder
     *      ↓
     * Match / No Match
     */
    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }


    /*
     * ============================================================
     * AUTHENTICATION MANAGER
     * ============================================================
     *
     * AuthenticationManager is the main entry point for
     * username/password authentication.
     *
     * Flow:
     *
     * AuthenticationManager
     *          ↓
     * ProviderManager
     *          ↓
     * DaoAuthenticationProvider
     *          ↓
     * UserDetailsService
     *          ↓
     * Database
     *          +
     * PasswordEncoder
     *          ↓
     * Authentication Success / Failure
     *
     * ProviderManager:
     * → Implementation of AuthenticationManager.
     *
     * DaoAuthenticationProvider:
     * → Loads user using UserDetailsService.
     * → Verifies password using PasswordEncoder.
     */
    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {

        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();

        authenticationProvider.setUserDetailsService(userDetailsService);

        authenticationProvider.setPasswordEncoder(passwordEncoder);

        return new ProviderManager(authenticationProvider);
    }
}

