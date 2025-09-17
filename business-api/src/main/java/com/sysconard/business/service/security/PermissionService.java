package com.sysconard.business.service.security;

import com.sysconard.business.dto.security.PermissionResponse;
import com.sysconard.business.entity.security.Permission;
import com.sysconard.business.repository.security.PermissionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Serviço para gerenciamento de permissões do sistema.
 * Implementa lógica de negócio para consulta de permissões seguindo princípios de Clean Code.
 * Utiliza injeção de dependência via construtor e logging estruturado.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionService {
    
    private final PermissionRepository permissionRepository;
    
    /**
     * Busca todas as permissões cadastradas no sistema.
     * Converte entidades para DTOs de resposta.
     * 
     * @return Lista de todas as permissões
     */
    public List<PermissionResponse> findAllPermissions() {
        log.debug("Buscando todas as permissões cadastradas");
        
        try {
            List<Permission> permissions = permissionRepository.findAll();
            
            List<PermissionResponse> response = permissions.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
            
            log.info("Encontradas {} permissões no sistema", response.size());
            return response;
            
        } catch (Exception e) {
            log.error("Erro ao buscar permissões: {}", e.getMessage(), e);
            throw new RuntimeException("Erro interno ao buscar permissões", e);
        }
    }
    
    /**
     * Converte entidade Permission para DTO de resposta.
     * Método privado para encapsular lógica de conversão.
     * 
     * @param permission Entidade Permission
     * @return DTO de resposta
     */
    private PermissionResponse convertToResponse(Permission permission) {
        return new PermissionResponse(
            permission.getId(),
            permission.getName(),
            permission.getResource(),
            permission.getAction(),
            permission.getDescription(),
            permission.getCreatedAt()
        );
    }
}
