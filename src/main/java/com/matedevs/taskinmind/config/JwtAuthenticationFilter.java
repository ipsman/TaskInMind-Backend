package com.matedevs.taskinmind.config;

import com.matedevs.taskinmind.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Service
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        final String authorizationHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        // --- CORRECTED LOGIC START ---
        // Check if the header is missing OR does not start with "Bearer "
        // In these cases, we simply pass the request to the next filter in the chain
        // without attempting JWT validation. This is for public endpoints or
        // requests without a token (like the login request).
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return; // IMPORTANT: Exit the filter method immediately
        }

        // If execution reaches here, it means authorizationHeader is NOT null
        // and it DOES start with "Bearer ". So, it's safe to extract the JWT.
        jwt = authorizationHeader.substring(7); // This is now safe

        username = jwtService.extractUsername(jwt);

        // Check if a username was extracted and if the user is not already authenticated
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // Validate the token against the user details
            if (jwtService.isTokenValid(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null, // credentials are null as the token is the proof
                        userDetails.getAuthorities()
                );
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                // Set the user as authenticated in the Spring Security context
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        // --- CORRECTED LOGIC END ---

        // ALWAYS pass the request to the next filter in the chain at the very end
        // (unless you returned earlier due to missing/invalid header)
        filterChain.doFilter(request, response);
    }
}