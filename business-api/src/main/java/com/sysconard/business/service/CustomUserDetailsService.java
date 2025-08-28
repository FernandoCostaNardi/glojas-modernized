package com.sysconard.business.service;

import com.sysconard.business.entity.User;
import com.sysconard.business.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("=== LOADING USER BY USERNAME ===");
        logger.info("Username solicitado: {}", username);
        
        try {
            User user = userRepository.findByUsernameWithRolesAndPermissions(username)
                .orElse(null);
            
            if (user == null) {
                logger.error("Usuário não encontrado: {}", username);
                throw new UsernameNotFoundException("Usuário não encontrado: " + username);
            }
            
            logger.info("Usuário encontrado: {} (ID: {})", user.getUsername(), user.getId());
            logger.info("Usuário ativo: {}", user.isEnabled());
            logger.info("Usuário não bloqueado: {}", user.isAccountNonLocked());
            logger.info("Número de roles: {}", user.getRoles().size());
            logger.info("Roles: {}", user.getRoles().stream().map(role -> role.getName()).toList());
            
            // Log das permissions
            user.getRoles().forEach(role -> {
                logger.info("Role '{}' tem {} permissions", role.getName(), role.getPermissions().size());
                role.getPermissions().forEach(permission -> {
                    logger.info("  - Permission: {}", permission.getName());
                });
            });
            
            logger.info("=== USER LOADED SUCCESSFULLY ===");
            return user;
            
        } catch (Exception e) {
            logger.error("=== ERRO AO CARREGAR USUÁRIO ===");
            logger.error("Exceção: {}", e.getClass().getSimpleName());
            logger.error("Mensagem: {}", e.getMessage());
            logger.error("Stack trace:", e);
            throw e;
        }
    }
}
