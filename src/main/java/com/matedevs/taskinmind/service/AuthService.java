package com.matedevs.taskinmind.service;

import com.matedevs.taskinmind.model.User;
import com.matedevs.taskinmind.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService; // Injektáljuk a JwtService-t!

    @Autowired
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService; // Inicializáljuk!
    }

    // Felhasználó regisztrációja - jelszó enkódolással
    public User register(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("A felhasználónév már foglalt!");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    // Felhasználó hitelesítése (bejelentkezés)
    public String authenticateUser(String username, String rawPassword) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, rawPassword)
            );
            // Ha idáig eljutottunk, a hitelesítés sikeres
            // Frissítsd a SecurityContext-et (bár stateless API-nál nem annyira releváns)
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Generáljuk a JWT tokent
            String jwt = jwtService.generateToken((User) authentication.getPrincipal()); // UserDetails-t vár
            logger.info("Felhasználó sikeresen bejelentkezett: {}", username);
            return jwt; // Visszaadjuk a tokent
        } catch (AuthenticationException e) {
            logger.warn("Bejelentkezés sikertelen felhasználó: {}. Hiba: {}", username, e.getMessage());
            throw new RuntimeException("Hibás felhasználónév vagy jelszó.");
        }
    }

}
