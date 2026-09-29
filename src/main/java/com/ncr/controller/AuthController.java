
package com.ncr.controller;

import com.ncr.entity.AuthRequest;
import com.ncr.util.JWTUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;

    // Constructor Injection:
    // Dependencies are provided by Spring through the constructor.
    public AuthController(AuthenticationManager authenticationManager,
                          JWTUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    /*
     * JWT LOGIN FLOW:
     *
     * 1. Client sends username + password in AuthRequest.
     * 2. UsernamePasswordAuthenticationToken holds these credentials.
     * 3. AuthenticationManager validates the credentials.
     * 4. If authentication succeeds, generate a JWT token.
     * 5. Return the JWT token to the client.
     *
     * AuthenticationManager
     *        ↓
     * Username + Password validation
     *        ↓
     * Authentication successful
     *        ↓
     * JWTUtil
     *        ↓
     * JWT Token
     */
    @PostMapping("/authenticate")
    public String generateToken(@RequestBody AuthRequest authRequest) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequest.getUsername(),
                        authRequest.getPassword()
                )
        );

        return jwtUtil.generateToken(authRequest.getUsername());
    }
}

