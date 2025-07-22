package com.matedevs.taskinmind.config;

import com.matedevs.taskinmind.model.ERole;
import com.matedevs.taskinmind.model.Role;
import com.matedevs.taskinmind.repository.RoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(DataLoader.class); // Hozzáadva

    private final RoleRepository roleRepository;

    public DataLoader(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info("DataLoader fut. Szerepkörök ellenőrzése és inicializálása..."); // Hozzáadva

        try {
            if (roleRepository.findByName(ERole.ROLE_USER).isEmpty()) {
                Role userRole = new Role(null, ERole.ROLE_USER); // Lombok @AllArgsConstructor használata
                roleRepository.save(userRole);
                logger.info("Szerepkör ROLE_USER létrehozva és mentve."); // Hozzáadva
            } else {
                logger.info("Szerepkör ROLE_USER már létezik."); // Hozzáadva
            }

            if (roleRepository.findByName(ERole.ROLE_ADMIN).isEmpty()) {
                Role adminRole = new Role(null, ERole.ROLE_ADMIN); // Lombok @AllArgsConstructor használata
                roleRepository.save(adminRole);
                logger.info("Szerepkör ROLE_ADMIN létrehozva és mentve."); // Hozzáadva
            } else {
                logger.info("Szerepkör ROLE_ADMIN már létezik."); // Hozzáadva
            }
        } catch (Exception e) {
            logger.error("Hiba történt a szerepkörök inicializálása során: " + e.getMessage(), e); // Hozzáadva
        }

        logger.info("DataLoader futása befejeződött."); // Hozzáadva
    }
}
