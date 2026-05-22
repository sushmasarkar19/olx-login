package com.sushma.olxlogin.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sushma.olxlogin.utility.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtTokenUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    // ── Public paths – JWT filter is skipped entirely for these ───────────
    //
    // BUG FIX (two mistakes in the original):
    //
    //  1. Paths must start with '/'
    //     getServletPath() always returns a path with a leading slash.
    //     "olx/user/authenticate" (no slash) never matches "/user/authenticate".
    //
    //  2. Paths must NOT include the /olx context-path prefix.
    //     getServletPath() returns the path AFTER the context-path is stripped.
    //     With server.servlet.context-path=/olx:
    //       Full URL:              /olx/user/authenticate
    //       getServletPath():      /user/authenticate     ← match against this
    //
    // Rule: PUBLIC_PATHS here must mirror the permitAll() rules in SecurityConfig exactly.
    private static final List<String> PUBLIC_PATHS = List.of(
        "/user/authenticate", "/user",
        "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs",
        "/v3/api-docs/**", "/swagger-resources", "/swagger-resources/**",
        "/webjars/**", "/actuator/**"
    );

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // getServletPath() returns path WITHOUT context-path and WITH leading slash
        // e.g. for request to /olx/user/authenticate → returns /user/authenticate
        String path = request.getServletPath();
        return PUBLIC_PATHS.stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

    // ── Main filter – only reached for protected routes ───────────────────

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        // Case 1: No Authorization header
        if (!StringUtils.hasText(authHeader)) {
            sendUnauthorized(response,
                "Missing Authorization header",
                "Add header: Authorization: Bearer <your-token>");
            return;
        }

        // Case 2: Header present but wrong format
        if (!authHeader.startsWith("Bearer ")) {
            sendUnauthorized(response,
                "Invalid Authorization format",
                "Header must start with 'Bearer '. Example: Authorization: Bearer eyJhb...");
            return;
        }

        String token = authHeader.substring(7);

        // Case 3: Validate the token
        try {
            String username = jwtTokenUtil.getUsernameFromToken(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (!jwtTokenUtil.validateToken(token, userDetails.getUsername())) {
                sendUnauthorized(response,
                    "Token validation failed",
                    "Token may be expired (1 hour TTL) or tampered. Re-login to get a fresh token.");
                return;
            }

            // Valid – set authentication in SecurityContext
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            sendUnauthorized(response,
                "Token has expired",
                "Re-login via POST /olx/user/authenticate to get a new token.");
            return;

        } catch (io.jsonwebtoken.JwtException e) {
            sendUnauthorized(response,
                "Invalid token: " + e.getMessage(),
                "Ensure you are copying the full token from the login response.");
            return;

        } catch (Exception e) {
            sendUnauthorized(response,
                "Authentication error: " + e.getMessage(),
                "Re-login and try again.");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void sendUnauthorized(HttpServletResponse response,
                                  String error,
                                  String hint) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Map<String, String> body = new LinkedHashMap<>();
        body.put("status", "401 Unauthorized");
        body.put("error", error);
        body.put("hint",  hint);

        objectMapper.writeValue(response.getWriter(), body);
        response.flushBuffer();
    }
}