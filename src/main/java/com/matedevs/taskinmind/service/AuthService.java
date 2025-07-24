package com.matedevs.taskinmind.service;

import com.matedevs.taskinmind.model.ERole;
import com.matedevs.taskinmind.model.Role;
import com.matedevs.taskinmind.model.User; // Fontos, hogy ez a TE entitásod legyen
import com.matedevs.taskinmind.repository.RoleRepository;
import com.matedevs.taskinmind.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails; // <-- Ezt importáld!
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RoleRepository roleRepository;

    @Autowired
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager, JwtService jwtService, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.roleRepository = roleRepository;
    }

    // Felhasználó regisztrációja - jelszó enkódolással
    public User register(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("A felhasználónév már foglalt!");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        Set<Role> roles = new HashSet<>();
        // Keresd meg a "ROLE_USER" szerepkört az adatbázisban
        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        roles.add(userRole);
        user.setRoles(roles);

        return userRepository.save(user);
    }

    // Felhasználó hitelesítése (bejelentkezés)
    public String authenticateUser(String username, String rawPassword) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, rawPassword)
            );
            // Ha idáig eljutottunk, a hitelesítés sikeres
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Szerezd meg a UserDetails objektumot
            UserDetails userDetails = (UserDetails) authentication.getPrincipal(); // <-- Kasztold UserDetails-re!

            // Generáljuk a JWT tokent a UserDetails objektumból
            // Ehhez a JwtService.generateToken() metódusának UserDetails-t kell fogadnia
            String jwt = jwtService.generateToken(userDetails);
            logger.info("Felhasználó sikeresen bejelentkezett: {}", username);
            return jwt;
        } catch (AuthenticationException e) {
            logger.warn("Bejelentkezés sikertelen felhasználó: {}. Hiba: {}", username, e.getMessage());
            throw new RuntimeException("Hibás felhasználónév vagy jelszó.");
        }
    }
}