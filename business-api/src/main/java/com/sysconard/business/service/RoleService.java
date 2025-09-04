package com.sysconard.business.service;

import com.sysconard.business.dto.RoleResponse;
import com.sysconard.business.dto.CreateRoleRequest;
import com.sysconard.business.dto.UpdateRoleRequest;
import com.sysconard.business.dto.UpdateRoleStatusRequest;
import com.sysconard.business.entity.Role;
import com.sysconard.business.entity.Permission;
import com.sysconard.business.repository.RoleRepository;
import com.sysconard.business.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Serviço para gerenciamento de roles do sistema.
 * Implementa a lógica de negócio relacionada às roles seguindo princípios de Clean Code.
 * Utiliza injeção de dependência via construtor e logging estruturado.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoleService {
    
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    
    /**
     * Cria uma nova role no sistema.
     * Valida os dados de entrada e associa as permissões especificadas.
     * 
     * @param request Dados da role a ser criada
     * @return RoleResponse com dados da role criada
     */
    @Transactional
    public RoleResponse createRole(CreateRoleRequest request) {
        log.info("Criando nova role: {}", request.getName());
        
        try {
            // Verificar se já existe uma role com o mesmo nome
            if (roleRepository.existsByName(request.getName())) {
                throw new IllegalArgumentException("Já existe uma role com o nome: " + request.getName());
            }
            
            // Buscar as permissões especificadas
            Set<Permission> permissions = permissionRepository.findAllById(request.getPermissionIds())
                    .stream()
                    .collect(Collectors.toSet());
            
            if (permissions.size() != request.getPermissionIds().size()) {
                throw new IllegalArgumentException("Algumas permissões especificadas não foram encontradas");
            }
            
            // Criar a nova role
            Role newRole = Role.builder()
                    .name(request.getName())
                    .description(request.getDescription())
                    .active(true)
                    .permissions(permissions)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            
            Role savedRole = roleRepository.save(newRole);
            
            log.info("Role criada com sucesso: {} (ID: {})", savedRole.getName(), savedRole.getId());
            
            return mapToRoleResponse(savedRole);
            
        } catch (Exception e) {
            log.error("Erro ao criar role {}: {}", request.getName(), e.getMessage(), e);
            throw new RuntimeException("Erro interno ao criar role", e);
        }
    }
    
    /**
     * Atualiza uma role existente no sistema.
     * Valida os dados de entrada e atualiza as permissões associadas.
     * 
     * @param roleId ID da role a ser atualizada
     * @param request Dados da role a ser atualizada
     * @return RoleResponse com dados da role atualizada
     */
    @Transactional
    public RoleResponse updateRole(UUID roleId, UpdateRoleRequest request) {
        log.info("Atualizando role: {} (ID: {})", request.getName(), roleId);
        
        try {
            // Buscar a role existente
            Role existingRole = roleRepository.findById(roleId)
                    .orElseThrow(() -> new IllegalArgumentException("Role não encontrada com ID: " + roleId));
            
            // Verificar se já existe outra role com o mesmo nome (excluindo a atual)
            if (!existingRole.getName().equals(request.getName()) && 
                roleRepository.existsByName(request.getName())) {
                throw new IllegalArgumentException("Já existe uma role com o nome: " + request.getName());
            }
            
            // Buscar as permissões especificadas
            Set<Permission> permissions = permissionRepository.findAllById(request.getPermissionIds())
                    .stream()
                    .collect(Collectors.toSet());
            
            if (permissions.size() != request.getPermissionIds().size()) {
                throw new IllegalArgumentException("Algumas permissões especificadas não foram encontradas");
            }
            
            // Atualizar os dados da role
            existingRole.setName(request.getName());
            existingRole.setDescription(request.getDescription());
            existingRole.setActive(request.isActive());
            existingRole.setPermissions(permissions);
            existingRole.setUpdatedAt(LocalDateTime.now());
            
            Role updatedRole = roleRepository.save(existingRole);
            
            log.info("Role atualizada com sucesso: {} (ID: {})", updatedRole.getName(), updatedRole.getId());
            
            return mapToRoleResponse(updatedRole);
            
        } catch (Exception e) {
            log.error("Erro ao atualizar role {}: {}", roleId, e.getMessage(), e);
            throw new RuntimeException("Erro interno ao atualizar role", e);
        }
    }
    
    /**
     * Altera o status (ativo/inativo) de uma role existente no sistema.
     * Operação específica para ativação/desativação sem alterar outros campos.
     * 
     * @param roleId ID da role a ter o status alterado
     * @param request Dados do novo status da role
     * @return RoleResponse com dados da role atualizada
     */
    @Transactional
    public RoleResponse updateRoleStatus(UUID roleId, UpdateRoleStatusRequest request) {
        log.info("Alterando status da role: {} (ID: {})", request.active() ? "ATIVAR" : "DESATIVAR", roleId);
        
        try {
            // Buscar a role existente
            Role existingRole = roleRepository.findById(roleId)
                    .orElseThrow(() -> new IllegalArgumentException("Role não encontrada com ID: " + roleId));
            
            // Verificar se o status já é o desejado
            if (existingRole.isActive() == request.active()) {
                log.warn("Role {} já possui o status desejado: {}", existingRole.getName(), request.active());
                return mapToRoleResponse(existingRole);
            }
            
            // Atualizar apenas o status e timestamp
            existingRole.setActive(request.active());
            existingRole.setUpdatedAt(LocalDateTime.now());
            
            Role updatedRole = roleRepository.save(existingRole);
            
            log.info("Status da role alterado com sucesso: {} (ID: {}) - Status: {}", 
                    updatedRole.getName(), updatedRole.getId(), updatedRole.isActive() ? "ATIVO" : "INATIVO");
            
            return mapToRoleResponse(updatedRole);
            
        } catch (Exception e) {
            log.error("Erro ao alterar status da role {}: {}", roleId, e.getMessage(), e);
            throw new RuntimeException("Erro interno ao alterar status da role", e);
        }
    }
    
    /**
     * Busca todas as roles cadastradas no sistema.
     * Retorna apenas roles ativas com suas permissões associadas.
     * 
     * @return Lista de roles com informações básicas e permissões
     */
    public List<RoleResponse> findAllRoles() {
        log.info("Buscando todas as roles cadastradas no sistema");
        
        try {
            List<Role> roles = roleRepository.findAll();
            
            List<RoleResponse> roleResponses = roles.stream()
                    .map(this::mapToRoleResponse)
                    .collect(Collectors.toList());
            
            log.info("Encontradas {} roles no sistema", roleResponses.size());
            
            return roleResponses;
            
        } catch (Exception e) {
            log.error("Erro ao buscar roles: {}", e.getMessage(), e);
            throw new RuntimeException("Erro interno ao buscar roles", e);
        }
    }
    
    /**
     * Busca todas as roles ativas no sistema.
     * Retorna apenas roles com status ativo.
     * 
     * @return Lista de roles ativas
     */
    public List<RoleResponse> findAllActiveRoles() {
        log.info("Buscando todas as roles ativas no sistema");
        
        try {
            List<Role> activeRoles = roleRepository.findAll().stream()
                    .filter(Role::isActive)
                    .collect(Collectors.toList());
            
            List<RoleResponse> roleResponses = activeRoles.stream()
                    .map(this::mapToRoleResponse)
                    .collect(Collectors.toList());
            
            log.info("Encontradas {} roles ativas no sistema", roleResponses.size());
            
            return roleResponses;
            
        } catch (Exception e) {
            log.error("Erro ao buscar roles ativas: {}", e.getMessage(), e);
            throw new RuntimeException("Erro interno ao buscar roles ativas", e);
        }
    }
    
    /**
     * Mapeia uma entidade Role para RoleResponse.
     * Extrai os nomes das permissões para facilitar o consumo da API.
     * 
     * @param role Entidade Role a ser mapeada
     * @return RoleResponse com dados da role
     */
    private RoleResponse mapToRoleResponse(Role role) {
        Set<String> permissionNames = role.getPermissions().stream()
                .map(permission -> permission.getName())
                .collect(Collectors.toSet());
        
        return RoleResponse.builder()
                .id(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .active(role.isActive())
                .createdAt(role.getCreatedAt())
                .updatedAt(role.getUpdatedAt())
                .permissionNames(permissionNames)
                .build();
    }
}
