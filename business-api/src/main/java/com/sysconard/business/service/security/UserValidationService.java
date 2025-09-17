package com.sysconard.business.service.security;

import com.sysconard.business.dto.security.ChangePasswordRequest;
import com.sysconard.business.dto.security.CreateUserRequest;
import com.sysconard.business.dto.security.UpdateUserRequest;
import com.sysconard.business.entity.security.Role;
import com.sysconard.business.entity.security.User;
import com.sysconard.business.exception.security.InvalidPasswordException;
import com.sysconard.business.exception.security.RoleNotFoundException;
import com.sysconard.business.exception.security.UserAlreadyExistsException;
import com.sysconard.business.exception.security.UserNotFoundException;
import com.sysconard.business.repository.security.RoleRepository;
import com.sysconard.business.repository.security.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Serviço responsável pela validação de dados de usuário.
 * Segue o princípio de responsabilidade única (SRP).
 */
@Service
public class UserValidationService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserValidationService.class);
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    
    public UserValidationService(UserRepository userRepository, 
                                RoleRepository roleRepository,
                                PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    /**
     * Valida os dados para criação de um novo usuário
     * 
     * @param request Dados do usuário a ser validado
     * @throws UserAlreadyExistsException se username ou email já existirem
     */
    public void validateUserCreation(CreateUserRequest request) {
        validateUsernameUniqueness(request.getUsername());
        validateEmailUniqueness(request.getEmail());
    }
    
    /**
     * Valida os dados para atualização de um usuário
     * 
     * @param userId ID do usuário a ser atualizado
     * @param request Dados do usuário a ser validado
     * @throws UserNotFoundException se o usuário não existir
     * @throws UserAlreadyExistsException se email já existir em outro usuário
     */
    public void validateUserUpdate(UUID userId, UpdateUserRequest request) {
        // Verificar se o usuário existe
        userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
        
        // Validar se o email não está sendo usado por outro usuário
        userRepository.findByEmail(request.getEmail())
            .ifPresent(user -> {
                if (!user.getId().equals(userId)) {
                    logger.warn("Tentativa de atualizar usuário com email já existente: {} (usuário: {})", 
                               request.getEmail(), userId);
                    throw new UserAlreadyExistsException("email", request.getEmail());
                }
            });
    }
    
    /**
     * Valida a alteração de senha
     * 
     * @param userId ID do usuário
     * @param request Dados da alteração de senha
     * @throws UserNotFoundException se o usuário não existir
     * @throws InvalidPasswordException se a senha atual estiver incorreta ou as senhas não coincidirem
     */
    public void validatePasswordChange(UUID userId, ChangePasswordRequest request) {
        // Verificar se o usuário existe
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
        
        // Verificar se a senha atual está correta
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            logger.warn("Tentativa de alterar senha com senha atual incorreta para usuário: {}", userId);
            throw new InvalidPasswordException("currentPassword", "Senha atual incorreta");
        }
        
        // Verificar se as novas senhas coincidem
        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            logger.warn("Tentativa de alterar senha com confirmação incorreta para usuário: {}", userId);
            throw new InvalidPasswordException("confirmNewPassword", "Confirmação da nova senha não coincide");
        }
        
        // Verificar se a nova senha é diferente da atual
        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            logger.warn("Tentativa de alterar senha para a mesma senha para usuário: {}", userId);
            throw new InvalidPasswordException("newPassword", "Nova senha deve ser diferente da senha atual");
        }
    }
    
    /**
     * Valida e busca as roles especificadas
     * 
     * @param roleNames Nomes das roles a serem validadas
     * @return Set de roles encontradas
     * @throws RoleNotFoundException se alguma role não existir
     */
    public Set<Role> validateAndGetRoles(Set<String> roleNames) {
        Set<Role> roles = new HashSet<>();
        
        for (String roleName : roleNames) {
            Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RoleNotFoundException(roleName));
            roles.add(role);
            logger.debug("Role encontrada: {}", roleName);
        }
        
        return roles;
    }
    
    /**
     * Valida se o username é único no sistema
     * 
     * @param username Username a ser validado
     * @throws UserAlreadyExistsException se o username já existir
     */
    private void validateUsernameUniqueness(String username) {
        if (userRepository.findByUsername(username).isPresent()) {
            logger.warn("Tentativa de criar usuário com username já existente: {}", username);
            throw new UserAlreadyExistsException("username", username);
        }
    }
    
    /**
     * Valida se o email é único no sistema
     * 
     * @param email Email a ser validado
     * @throws UserAlreadyExistsException se o email já existir
     */
    private void validateEmailUniqueness(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            logger.warn("Tentativa de criar usuário com email já existente: {}", email);
            throw new UserAlreadyExistsException("email", email);
        }
    }
}
