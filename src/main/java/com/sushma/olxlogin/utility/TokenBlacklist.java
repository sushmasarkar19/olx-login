package com.sushma.olxlogin.utility;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Simple in-memory blacklist for invalidated tokens.
 *
 * When a user calls DELETE /user/logout, the bearer token is added here.
 * JwtAuthenticationFilter (or the validate endpoint) checks this set
 * before trusting the token.
 *
 * NOTE: This is an in-process store.  In a production clustered deployment
 * replace with a Redis-backed solution so all nodes share the blacklist.
 */
@Component
public class TokenBlacklist {

    private final Set<String> invalidatedTokens =
            Collections.synchronizedSet(new HashSet<>());

    public void add(String token) {
        invalidatedTokens.add(token);
    }

    public boolean contains(String token) {
        return invalidatedTokens.contains(token);
    }
}
