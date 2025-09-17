package com.sysconard.business.repository.security;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sysconard.business.entity.security.User;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    @Query("SELECT u FROM User u JOIN FETCH u.roles r JOIN FETCH r.permissions WHERE u.username = :username")
    Optional<User> findByUsernameWithRolesAndPermissions(@Param("username") String username);
    
    @Query("SELECT u FROM User u JOIN FETCH u.roles r JOIN FETCH r.permissions WHERE u.email = :email")
    Optional<User> findByEmailWithRolesAndPermissions(@Param("email") String email);
    
    @Query("SELECT u FROM User u JOIN FETCH u.roles WHERE u.username = :username")
    Optional<User> findByUsernameWithRoles(@Param("username") String username);
    
    /**
     * Busca usuários com filtros e paginação.
     * Permite filtrar por nome, roles, status ativo e bloqueado.
     * 
     * @param name Filtro por nome (busca parcial)
     * @param roles Filtro por roles específicas
     * @param isActive Filtro por status ativo
     * @param isNotLocked Filtro por status não bloqueado
     * @param pageable Configuração de paginação e ordenação
     * @return Página de usuários com filtros aplicados
     */
        @Query(value = "SELECT DISTINCT u.* FROM users u " +
                   "LEFT JOIN user_roles ur ON u.id = ur.user_id " +
                   "LEFT JOIN roles r ON r.id = ur.role_id " +
                   "WHERE (:name IS NULL OR LOWER(u.name) LIKE LOWER(CONCAT('%', CAST(:name AS TEXT), '%'))) " +
                   "AND (:isActive IS NULL OR u.is_active = :isActive) " +
                   "AND (:isNotLocked IS NULL OR u.is_not_locked = :isNotLocked) " +
                   "ORDER BY u.name",
           countQuery = "SELECT COUNT(DISTINCT u.id) FROM users u " +
                       "LEFT JOIN user_roles ur ON u.id = ur.user_id " +
                       "LEFT JOIN roles r ON r.id = ur.role_id " +
                       "WHERE (:name IS NULL OR LOWER(u.name) LIKE LOWER(CONCAT('%', CAST(:name AS TEXT), '%'))) " +
                       "AND (:isActive IS NULL OR u.is_active = :isActive) " +
                       "AND (:isNotLocked IS NULL OR u.is_not_locked = :isNotLocked)",
           nativeQuery = true)
    Page<User> findUsersWithFilters(
        @Param("name") String name,
        @Param("isActive") Boolean isActive,
        @Param("isNotLocked") Boolean isNotLocked,
        Pageable pageable
    );

    @Query(value = "SELECT COUNT(*) FROM users WHERE is_active = true", nativeQuery = true)
    long countActiveUsers();
    
    @Query(value = "SELECT COUNT(*) FROM users WHERE is_active = false", nativeQuery = true)
    long countInactiveUsers();
    
    @Query(value = "SELECT COUNT(*) FROM users WHERE is_not_locked = false", nativeQuery = true)
    long countBlockedUsers();
    
    @Query(value = "SELECT COUNT(*) FROM users", nativeQuery = true)
    long countTotalUsers();
}
