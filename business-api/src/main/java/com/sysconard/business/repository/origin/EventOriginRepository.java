package com.sysconard.business.repository.origin;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sysconard.business.entity.origin.EventOrigin;
import com.sysconard.business.enums.EventSource;

/**
 * Repository para operações de persistência de EventOrigin.
 * Segue padrões de Clean Code com métodos bem definidos e consultas otimizadas.
 */
@Repository
public interface EventOriginRepository extends JpaRepository<EventOrigin, UUID> {
    
    /**
     * Verifica se existe um EventOrigin com o sourceCode especificado.
     * 
     * @param sourceCode Código da fonte a ser verificado
     * @return true se existe, false caso contrário
     */
    boolean existsBySourceCode(String sourceCode);
    
    /**
     * Busca EventOrigin por sourceCode.
     * 
     * @param sourceCode Código da fonte
     * @return Optional contendo o EventOrigin se encontrado
     */
    Optional<EventOrigin> findBySourceCode(String sourceCode);
    
    /**
     * Busca EventOrigins por EventSource com paginação.
     * 
     * @param eventSource Fonte do evento para filtro
     * @param pageable Configuração de paginação
     * @return Página de EventOrigins
     */
    Page<EventOrigin> findByEventSource(EventSource eventSource, Pageable pageable);
    
    /**
     * Busca todos os EventOrigins por EventSource.
     * 
     * @param eventSource Fonte do evento para filtro
     * @return Lista de EventOrigins
     */
    // criar metodo para buscar todos os EventOrigins do tipo danfe
    List<EventOrigin> findByEventSource(EventSource eventSource);

    /**
     * Conta total de EventOrigins por EventSource.
     * 
     * @param eventSource Fonte do evento
     * @return Número total de EventOrigins
     */
    long countByEventSource(EventSource eventSource);
    
    /**
     * Conta total de EventOrigins do tipo PDV.
     * 
     * @return Número total de EventOrigins PDV
     */
    @Query("SELECT COUNT(e) FROM EventOrigin e WHERE e.eventSource = 'PDV'")
    long countPdvEventOrigins();
    
    /**
     * Conta total de EventOrigins do tipo EXCHANGE.
     * 
     * @return Número total de EventOrigins EXCHANGE
     */
    @Query("SELECT COUNT(e) FROM EventOrigin e WHERE e.eventSource = 'EXCHANGE'")
    long countExchangeEventOrigins();
    
    /**
     * Conta total de EventOrigins do tipo DANFE.
     * 
     * @return Número total de EventOrigins DANFE
     */
    @Query("SELECT COUNT(e) FROM EventOrigin e WHERE e.eventSource = 'DANFE'")
    long countDanfeEventOrigins();
    
    /**
     * Conta total geral de EventOrigins.
     * 
     * @return Número total de EventOrigins
     */
    @Query("SELECT COUNT(e) FROM EventOrigin e")
    long countTotalEventOrigins();
    
    /**
     * Busca EventOrigins com filtros dinâmicos e paginação.
     * 
     * @param eventSource Fonte do evento (opcional)
     * @param pageable Configuração de paginação
     * @return Página de EventOrigins
     */
    @Query("SELECT e FROM EventOrigin e WHERE " +
           "(:eventSource IS NULL OR e.eventSource = :eventSource)")
    Page<EventOrigin> findByFilters(
        @Param("eventSource") EventSource eventSource,
        Pageable pageable
    );
}
