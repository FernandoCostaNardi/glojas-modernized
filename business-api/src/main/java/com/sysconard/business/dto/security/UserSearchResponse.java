package com.sysconard.business.dto.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Resposta da busca de usuários com totalizadores
 * Inclui a página de usuários e estatísticas de contagem
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSearchResponse {
    
    /**
     * Lista paginada de usuários
     */
    private List<UpdateUserResponse> users;
    
    /**
     * Informações de paginação
     */
    private PaginationInfo pagination;
    
    /**
     * Totalizadores de usuários
     */
    private UserCounts counts;
    
    /**
     * Informações de paginação
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaginationInfo {
        private int currentPage;
        private int totalPages;
        private long totalElements;
        private int pageSize;
        private boolean hasNext;
        private boolean hasPrevious;
    }
    
    /**
     * Totalizadores de usuários por status
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserCounts {
        /**
         * Total de usuários ativos
         */
        private long totalActive;
        
        /**
         * Total de usuários inativos
         */
        private long totalInactive;
        
        /**
         * Total de usuários bloqueados
         */
        private long totalBlocked;
        
        /**
         * Total geral de usuários
         */
        private long totalUsers;
    }
    
    /**
     * Cria uma resposta a partir de uma página de usuários e contadores
     */
    public static UserSearchResponse fromPage(Page<UpdateUserResponse> usersPage, 
                                           long totalActive, long totalInactive, long totalBlocked) {
        
        PaginationInfo paginationInfo = PaginationInfo.builder()
                .currentPage(usersPage.getNumber())
                .totalPages(usersPage.getTotalPages())
                .totalElements(usersPage.getTotalElements())
                .pageSize(usersPage.getSize())
                .hasNext(usersPage.hasNext())
                .hasPrevious(usersPage.hasPrevious())
                .build();
        
        UserCounts userCounts = UserCounts.builder()
                .totalActive(totalActive)
                .totalInactive(totalInactive)
                .totalBlocked(totalBlocked)
                .totalUsers(totalActive + totalInactive + totalBlocked)
                .build();
        
        return UserSearchResponse.builder()
                .users(usersPage.getContent())
                .pagination(paginationInfo)
                .counts(userCounts)
                .build();
    }
}
