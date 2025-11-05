package com.yusufelkaan.jwt_auth.auth.config;

import java.io.IOException;

import com.yusufelkaan.jwt_auth.auth.dtos.Token;
import com.yusufelkaan.jwt_auth.auth.utils.JwtUtils;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;

    public JwtFilter(JwtUtils jwtUtils, UserDetailsService userDetails) {
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetails;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Skip Auth paths immediately
        // Note: The path check is slightly too permissive (path.startsWith vs equals/matcher)
        // but is kept here based on your previous config.
        String path = request.getServletPath();
        if (path.startsWith("/api/v1/auth")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");
        String token = null;

        // 2. Extract Token
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        } else {
            // Fallback to Cookie check ONLY if header is absent/malformed
            token = jwtUtils.getTokenFromCookie(request, Token.ACCESS);
        }

        // 3. Validation and Context Population
        // Only proceed if a token was found AND the security context is currently empty.
        if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            String userEmail = null;

            try {
                // Try to extract the username (will throw exception if token is invalid/expired)
                userEmail = jwtUtils.extractUsername(token);
            } catch (Exception e) {
                // CRITICAL FIX: If token is invalid (expired/malformed),
                // log the error and stop the filter from processing this token.
                // DO NOT THROW an exception here unless you use an AuthenticationEntryPoint/AccessDeniedHandler.
                // By doing nothing, we let the request continue without authentication,
                // and the final security rule will reject it with a 401.
                log.warn("Invalid or expired JWT token: {}", e.getMessage());
                userEmail = null; // Ensure userEmail is nullified after exception
            }

            if (userEmail != null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

                if (jwtUtils.validateToken(token, userDetails)) { // Validate against user details

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // CRITICAL: Set authentication
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        }

        // 4. Final Pass: This is the only place filterChain.doFilter() should be called.
        // If authentication failed (token was null or invalid), the context is empty,
        // and the request will be blocked by 'anyRequest().authenticated()' with a 401.
        filterChain.doFilter(request, response);
    }
}
