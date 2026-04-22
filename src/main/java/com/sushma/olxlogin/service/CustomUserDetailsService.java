package com.sushma.olxlogin.service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.sushma.olxlogin.Repository.OlxLoginRepository;
import com.sushma.olxlogin.entity.UserEntity;

/**
 * Loads a User from the olx-users database and wraps it in a Spring Security
 * UserDetails object.  Roles stored as comma-separated strings
 * (e.g. "ROLE_USER,ROLE_ADMIN") are split and converted to GrantedAuthority.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private OlxLoginRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        UserEntity user = userRepository.findByUserName(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found: " + username));

        // Only allow active users to authenticate
        if (!"true".equalsIgnoreCase(user.getActive())) {
            throw new UsernameNotFoundException("User is inactive: " + username);
        }

        List<SimpleGrantedAuthority> authorities = Arrays
                .stream(user.getRoles().split(","))
                .map(String::trim)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(
                user.getUserName(),
                user.getPassword(),
                authorities
        );
    }
}