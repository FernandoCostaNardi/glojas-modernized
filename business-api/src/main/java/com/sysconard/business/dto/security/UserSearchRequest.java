package com.sysconard.business.dto.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * DTO para requisição de busca de usuários com filtros e paginação.
 * Utiliza Lombok para reduzir boilerplate e melhorar manutenibilidade.
 * Permite filtrar usuários por nome, roles, status ativo e bloqueado.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSearchRequest {
    
    /**
     * Filtro por nome (busca parcial - contém)
     */
    private String name;
    
    /**
     * Filtro por roles específicas
     */
    private Set<String> roles;
    
    /**
     * Filtro por status ativo
     */
    private Boolean isActive;
    
    /**
     * Filtro por status não bloqueado
     */
    private Boolean isNotLocked;
    
    /**
     * Número da página (baseado em 0)
     */
    @Builder.Default
    private Integer page = 0;
    
    /**
     * Tamanho da página
     */
    private Integer size;
    
    /**
     * Campo para ordenação
     */
    @Builder.Default
    private String sortBy = "name";
    
    /**
     * Direção da ordenação (asc ou desc)
     */
    @Builder.Default
    private String sortDir = "asc";
}
