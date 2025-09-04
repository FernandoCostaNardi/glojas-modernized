package com.sysconard.business.mapper;

import com.sysconard.business.dto.CreateUserResponse;
import com.sysconard.business.dto.UpdateUserResponse;
import com.sysconard.business.dto.ChangePasswordResponse;
import com.sysconard.business.entity.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.stream.Collectors;
import java.util.UUID;

/**
 * Componente responsável pelo mapeamento entre entidades User e DTOs.
 * Segue o princípio de responsabilidade única (SRP).
 */
@Component
public class UserMapper {
    
    /**
     * Converte uma entidade User para CreateUserResponse
     * 
     * @param user Entidade User
     * @return CreateUserResponse com os dados do usuário
     */
    public CreateUserResponse toCreateUserResponse(User user) {
        CreateUserResponse response = new CreateUserResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setProfileImageUrl(user.getProfileImageUrl());
        response.setActive(user.isActive());
        response.setNotLocked(user.isNotLocked());
        response.setRoles(user.getRoles().stream()
            .map(role -> role.getName())
            .collect(Collectors.toSet()));
        response.setMessage("Usuário criado com sucesso");
        
        return response;
    }
    
    /**
     * Converte uma entidade User para UpdateUserResponse
     * 
     * @param user Entidade User
     * @return UpdateUserResponse com os dados do usuário atualizado
     */
    public UpdateUserResponse toUpdateUserResponse(User user) {
        UpdateUserResponse response = new UpdateUserResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setProfileImageUrl(user.getProfileImageUrl());
        response.setJoinDate(user.getJoinDate());
        response.setLastLoginDate(user.getLastLoginDate());
        response.setActive(user.isActive());
        response.setNotLocked(user.isNotLocked());
        response.setRoles(user.getRoles().stream()
            .map(role -> role.getName())
            .collect(Collectors.toSet()));
        response.setMessage("Usuário atualizado com sucesso");
        response.setUpdatedAt(LocalDateTime.now());
        
        return response;
    }
    
    /**
     * Converte dados para ChangePasswordResponse
     * 
     * @param userId ID do usuário
     * @param username Username do usuário
     * @return ChangePasswordResponse com os dados da alteração
     */
    public ChangePasswordResponse toChangePasswordResponse(UUID userId, String username) {
        return ChangePasswordResponse.builder()
            .userId(userId)
            .username(username)
            .message("Senha alterada com sucesso")
            .changedAt(LocalDateTime.now())
            .success(true)
            .build();
    }
}
