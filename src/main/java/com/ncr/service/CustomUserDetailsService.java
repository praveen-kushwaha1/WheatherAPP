
        package com.ncr.service;

import com.ncr.repo.UserDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Custom implementation of Spring Security's UserDetailsService.
 *
 * REVISION NOTES:
 *
 * 1. UserDetailsService
 *    - Spring Security interface used to load user information.
 *    - It is mainly used during authentication.
 *
 * 2. loadUserByUsername()
 *    - Spring Security automatically calls this method when it needs
 *      to find a user during username/password authentication.
 *
 * 3. UserDetails
 *    - Represents the authenticated user's information.
 *    - It contains details such as:
 *        - username
 *        - password
 *        - authorities/roles
 *        - account status
 *
 * 4. UserDetailsRepository
 *    - Used to fetch the user from the database.
 *
 * 5. Optional.orElseThrow()
 *    - If the username exists, the UserDetails object is returned.
 *    - If the username does not exist, UsernameNotFoundException is thrown.
 *
 * 6. @Service
 *    - Registers this class as a Spring Bean.
 *    - Spring Security can then use this service for authentication.
 *
 * AUTHENTICATION FLOW:
 *
 * Client
 *   ↓
 * Login Request
 *   ↓
 * Spring Security
 *   ↓
 * CustomUserDetailsService
 *   ↓
 * UserDetailsRepository
 *   ↓
 * Database
 *   ↓
 * UserDetails
 *   ↓
 * Password verification
 *   ↓
 * Authentication success/failure
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    // Repository used to find the user from the database.
    @Autowired
    private UserDetailsRepository userDetailsRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userDetailsRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found"));
    }
}

