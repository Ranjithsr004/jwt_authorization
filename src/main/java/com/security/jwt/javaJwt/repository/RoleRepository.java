package com.security.jwt.javaJwt.repository;

import com.security.jwt.javaJwt.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName (String name);
}
