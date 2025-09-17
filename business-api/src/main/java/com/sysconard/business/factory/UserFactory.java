package com.sysconard.business.factory;

import com.sysconard.business.dto.security.CreateUserRequest;
import com.sysconard.business.entity.security.Role;
import com.sysconard.business.entity.security.User;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Factory responsável pela criação de entidades User.
 * Segue o padrão Factory Method e princípios de Clean Code.
 */
@Component
public class UserFactory {
    
    private final PasswordEncoder passwordEncoder;
    
    public UserFactory(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
    
    /**
     * Cria uma nova instância de User a partir dos dados da requisição
     * 
     * @param request Dados da requisição
     * @param roles Roles do usuário
     * @return Nova instância de User configurada
     */
    public User createUser(CreateUserRequest request, Set<Role> roles) {
        return User.builder()
            .name(request.getName())
            .username(request.getUsername())
            .email(request.getEmail())
            .password(encodePassword(request.getPassword()))
            .profileImageUrl(request.getProfileImageUrl())
            .joinDate(getCurrentTimestamp())
            .isActive(true)
            .isNotLocked(true)
            .roles(roles)
            .build();
    }
    
    /**
     * Cria uma nova instância de User com configurações customizadas
     * 
     * @param request Dados da requisição
     * @param roles Roles do usuário
     * @param isActive Status de ativação do usuário
     * @param isNotLocked Status de bloqueio do usuário
     * @return Nova instância de User configurada
     */
    public User createUser(CreateUserRequest request, Set<Role> roles, 
                          boolean isActive, boolean isNotLocked) {
        return User.builder()
            .name(request.getName())
            .username(request.getUsername())
            .email(request.getEmail())
            .password(encodePassword(request.getPassword()))
            .profileImageUrl(request.getProfileImageUrl())
            .joinDate(getCurrentTimestamp())
            .isActive(isActive)
            .isNotLocked(isNotLocked)
            .roles(roles)
            .build();
    }
    
    /**
     * Codifica a senha usando o encoder configurado
     * 
     * @param rawPassword Senha em texto plano
     * @return Senha codificada
     */
    private String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }
    
    /**
     * Obtém o timestamp atual para o joinDate
     * 
     * @return Timestamp atual
     */
    private LocalDateTime getCurrentTimestamp() {
        return LocalDateTime.now();
    }
}
