package com.matedevs.taskinmind.repository;

import com.matedevs.taskinmind.model.ERole;
import com.matedevs.taskinmind.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role,Long> {
    Optional<Role> findByName(ERole name);
}
