package com.sysconard.business.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.sysconard.business.client.LegacyApiClient;
import com.sysconard.business.dto.CriticalStockPageResponseDTO;
import com.sysconard.business.dto.CriticalStockSearchRequest;

import jakarta.validation.Valid;

/**
 * Service para estoque crítico.
 * Responsável por consumir a Legacy API e aplicar validações.
 * 
 * @author Sysconard Business API
 * @version 1.0
 */
@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class CriticalStockService {
    
    private final LegacyApiClient legacyApiClient;
    
    /**
     * Busca produtos com estoque crítico.
     * 
     * @param request Requisição de busca validada
     * @return Resposta paginada com itens de estoque crítico
     * @throws RuntimeException Se houver erro na comunicação com Legacy API
     */
    public CriticalStockPageResponseDTO getCriticalStock(@Valid CriticalStockSearchRequest request) {
        log.info("Buscando estoque crítico: refplu={}, descricao={}, grupo={}, marca={}, page={}, size={}, sortBy={}, sortDir={}",
                request.refplu(), request.descricao(), request.grupo(), request.marca(), request.page(), request.size(), request.sortBy(), request.sortDir());
        
        try {
            // Chamar Legacy API
            CriticalStockPageResponseDTO response = legacyApiClient.getCriticalStock(
                request.refplu(),
                request.descricao(),
                request.grupo(),
                request.marca(),
                request.page(),
                request.size(),
                request.sortBy(),
                request.sortDir()
            );
            
            log.info("Estoque crítico obtido com sucesso: {} itens de {} total",
                    response.getContent().size(), 
                    response.getPagination().getTotalElements());
            
            return response;
            
        } catch (Exception e) {
            log.error("Erro ao buscar estoque crítico", e);
            throw new RuntimeException("Erro ao buscar estoque crítico: " + e.getMessage(), e);
        }
    }
}

