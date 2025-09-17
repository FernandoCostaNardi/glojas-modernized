package com.sysconard.business.repository.security;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sysconard.business.entity.security.Role;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {
    
    Optional<Role> findByName(String name);
    
    boolean existsByName(String name);
}
