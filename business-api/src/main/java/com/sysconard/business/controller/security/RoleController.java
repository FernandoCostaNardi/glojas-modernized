package com.sysconard.business.controller.security;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sysconard.business.dto.security.CreateRoleRequest;
import com.sysconard.business.dto.security.RoleResponse;
import com.sysconard.business.dto.security.UpdateRoleRequest;
import com.sysconard.business.dto.security.UpdateRoleStatusRequest;
import com.sysconard.business.service.security.RoleService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller para gerenciamento de roles do sistema.
 * Implementa endpoints REST para consulta, criação e atualização de roles seguindo princípios de Clean Code.
 * Utiliza injeção de dependência via construtor e logging estruturado.
 */
@Slf4j
@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
@Validated
@CrossOrigin(origins = "http://localhost:3000")
public class RoleController {
    
    private final RoleService roleService;
    
    /**
     * Endpoint para criar uma nova role no sistema.
     * Acesso permitido apenas para usuários com permissão role:create.
     * 
     * @param request Dados da role a ser criada
     * @return Resposta com os dados da role criada
     */
    @PostMapping("/create")
    @PreAuthorize("hasAuthority('role:create')")
    public ResponseEntity<RoleResponse> createRole(@Valid @RequestBody CreateRoleRequest request) {
        log.info("Recebida requisição para criar role: {}", request.getName());
        
        try {
            RoleResponse response = roleService.createRole(request);
            
            log.info("Role criada com sucesso: {} (ID: {})", response.getName(), response.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            log.error("Erro ao criar role: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Endpoint para atualizar uma role existente no sistema.
     * Acesso permitido apenas para usuários com permissão role:update.
     * 
     * @param roleId ID da role a ser atualizada
     * @param request Dados da role a ser atualizada
     * @return Resposta com os dados da role atualizada
     */
    @PutMapping("/{roleId}")
    @PreAuthorize("hasAuthority('role:update')")
    public ResponseEntity<RoleResponse> updateRole(
            @PathVariable UUID roleId,
            @Valid @RequestBody UpdateRoleRequest request) {
        
        log.info("Recebida requisição para atualizar role: {} (ID: {})", request.getName(), roleId);
        
        try {
            RoleResponse response = roleService.updateRole(roleId, request);
            
            log.info("Role atualizada com sucesso: {} (ID: {})", response.getName(), response.getId());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Erro ao atualizar role {}: {}", roleId, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Endpoint para alterar o status (ativo/inativo) de uma role existente.
     * Acesso permitido apenas para usuários com permissão role:update.
     * 
     * @param roleId ID da role a ter o status alterado
     * @param request Dados do novo status da role
     * @return Resposta com os dados da role atualizada
     */
    @PatchMapping("/{roleId}/status")
    @PreAuthorize("hasAuthority('role:update')")
    public ResponseEntity<RoleResponse> updateRoleStatus(
            @PathVariable UUID roleId,
            @Valid @RequestBody UpdateRoleStatusRequest request) {
        
        log.info("Recebida requisição para alterar status da role: {} (ID: {})", 
                request.active() ? "ATIVAR" : "DESATIVAR", roleId);
        
        try {
            RoleResponse response = roleService.updateRoleStatus(roleId, request);
            
            log.info("Status da role alterado com sucesso: {} (ID: {}) - Status: {}", 
                    response.getName(), response.getId(), response.isActive() ? "ATIVO" : "INATIVO");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Erro ao alterar status da role {}: {}", roleId, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Endpoint para buscar todas as roles cadastradas no sistema.
     * Acesso permitido para usuários com permissão role:read.
     * 
     * @return Lista de todas as roles com suas permissões
     */
    @GetMapping
    @PreAuthorize("hasAuthority('role:read')")
    public ResponseEntity<List<RoleResponse>> getAllRoles() {
        log.info("Recebida requisição para buscar todas as roles");
        
        try {
            List<RoleResponse> roles = roleService.findAllRoles();
            
            log.info("Retornando {} roles encontradas", roles.size());
            return ResponseEntity.ok(roles);
            
        } catch (Exception e) {
            log.error("Erro ao buscar roles: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Endpoint para buscar apenas as roles ativas no sistema.
     * Acesso permitido para usuários com permissão role:read.
     * 
     * @return Lista de roles ativas com suas permissões
     */
    @GetMapping("/active")
    @PreAuthorize("hasAuthority('role:read')")
    public ResponseEntity<List<RoleResponse>> getActiveRoles() {
        log.info("Recebida requisição para buscar roles ativas");
        
        try {
            List<RoleResponse> activeRoles = roleService.findAllActiveRoles();
            
            log.info("Retornando {} roles ativas encontradas", activeRoles.size());
            return ResponseEntity.ok(activeRoles);
            
        } catch (Exception e) {
            log.error("Erro ao buscar roles ativas: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Endpoint de health check para verificar se o controller está funcionando.
     * Acesso público para monitoramento.
     * 
     * @return Mensagem de status
     */
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        log.debug("Health check do RoleController");
        return ResponseEntity.ok("RoleController está funcionando");
    }
    
    /**
     * Endpoint de teste para verificar se o roteamento está funcionando.
     * Acesso público para debug.
     * 
     * @return Informações de debug do controller
     */
    @GetMapping("/debug")
    public ResponseEntity<String> debugInfo() {
        log.info("Endpoint de debug acessado");
        return ResponseEntity.ok("RoleController - Debug: Roteamento funcionando corretamente");
    }
}
