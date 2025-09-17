package com.sysconard.business.repository.security;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sysconard.business.entity.security.Permission;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, UUID> {
    
    Optional<Permission> findByName(String name);
    
    boolean existsByName(String name);
}
