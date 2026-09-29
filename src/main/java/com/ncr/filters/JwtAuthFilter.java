
        package com.ncr.filters;

import com.ncr.service.CustomUserDetailsService;
import com.ncr.util.JWTUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;

    // Constructor Injection:
    // Spring injects the required dependencies through the constructor.
    public JwtAuthFilter(JWTUtil jwtUtil,
                         CustomUserDetailsService customUserDetailsService) {
        this.jwtUtil = jwtUtil;
        this.customUserDetailsService = customUserDetailsService;
    }

    /*
     * JWT FILTER FLOW:
     *
     * 1. Client sends JWT in the Authorization header.
     *
     *    Authorization: Bearer <JWT>
     *
     * 2. Extract the Authorization header.
     *
     * 3. Check whether the header starts with "Bearer ".
     *
     * 4. Extract the JWT by removing "Bearer ".
     *
     * 5. Extract the username from the JWT.
     *
     * 6. Check SecurityContext:
     *    If authentication already exists, don't authenticate again.
     *
     * 7. Load the user from the database using username.
     *
     * 8. Validate the JWT.
     *
     * 9. If valid, create UsernamePasswordAuthenticationToken.
     *
     * 10. Store authentication in SecurityContext.
     *
     * 11. Continue the request using filterChain.doFilter().
     *
     * FLOW:
     *
     * Client
     *   |
     *   | Authorization: Bearer JWT
     *   ↓
     * JwtAuthFilter
     *   |
     *   ↓
     * Extract JWT
     *   |
     *   ↓
     * Extract Username
     *   |
     *   ↓
     * Load UserDetails
     *   |
     *   ↓
     * Validate JWT
     *   |
     *   ↓
     * Create Authentication
     *   |
     *   ↓
     * SecurityContextHolder
     *   |
     *   ↓
     * Controller
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        // Get JWT from Authorization header
        String authHeader = request.getHeader("Authorization");

        String token = null;
        String username = null;

        // Check whether the request contains a Bearer token
        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            // Remove "Bearer " and keep only the JWT
            token = authHeader.substring(7);

            // Extract username from JWT
            username = jwtUtil.extractUsername(token);
        }

        /*
         * Authenticate only when:
         *
         * 1. Username was successfully extracted.
         * 2. SecurityContext does not already contain authentication.
         */
        if (username != null
                && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Load user information from database
            UserDetails userDetails =
                    customUserDetailsService.loadUserByUsername(username);

            // Validate JWT against the user details
            if (jwtUtil.validateToken(username, userDetails, token)) {

                /*
                 * Create authenticated SecurityContext object.
                 *
                 * Principal     → userDetails
                 * Credentials   → null because JWT is already validated
                 * Authorities   → USER/ADMIN roles
                 */
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                // Attach request-specific details
                authentication.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request)
                );

                // Mark the request as authenticated
                SecurityContextHolder
                        .getContext()
                        .setAuthentication(authentication);
            }
        }

        // Continue the request to the next filter/controller
        filterChain.doFilter(request, response);
    }
}

