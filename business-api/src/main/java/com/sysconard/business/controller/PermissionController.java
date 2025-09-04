package com.sysconard.business.controller;

import com.sysconard.business.dto.PermissionResponse;
import com.sysconard.business.service.PermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller para gerenciamento de permissões do sistema.
 * Implementa endpoints REST para consulta de permissões seguindo princípios de Clean Code.
 * Utiliza injeção de dependência via construtor e logging estruturado.
 */
@Slf4j
@RestController
@RequestMapping("/permissions")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class PermissionController {
    
    private final PermissionService permissionService;
    
    /**
     * Endpoint para buscar todas as permissões cadastradas no sistema.
     * Acesso permitido para usuários autenticados com role USER ou superior.
     * 
     * @return Lista de todas as permissões
     */
    @GetMapping
    @PreAuthorize("hasAuthority('permission:read')")
    public ResponseEntity<List<PermissionResponse>> getAllPermissions() {
        log.info("Recebida requisição para buscar todas as permissões");
        
        try {
            List<PermissionResponse> permissions = permissionService.findAllPermissions();
            
            log.info("Retornando {} permissões encontradas", permissions.size());
            return ResponseEntity.ok(permissions);
            
        } catch (Exception e) {
            log.error("Erro ao buscar permissões: {}", e.getMessage(), e);
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
        log.debug("Health check do PermissionController");
        return ResponseEntity.ok("PermissionController está funcionando");
    }
}
