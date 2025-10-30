package com.sysconard.business.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.sysconard.business.client.LegacyApiClient;
import com.sysconard.business.dto.PurchaseAnalysisPageResponseDTO;
import com.sysconard.business.dto.PurchaseAnalysisSearchRequest;

import jakarta.validation.Valid;

/**
 * Service para análise de compras.
 * Responsável por consumir a Legacy API e aplicar validações.
 * 
 * @author Sysconard Business API
 * @version 1.0
 */
@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class PurchaseAnalysisService {
    
    private final LegacyApiClient legacyApiClient;
    
    /**
     * Busca análise de compras com filtros, paginação e ordenação.
     * 
     * @param request Requisição de busca validada
     * @return Resposta paginada com itens de análise de compras
     * @throws RuntimeException Se houver erro na comunicação com Legacy API
     */
    public PurchaseAnalysisPageResponseDTO getPurchaseAnalysis(@Valid PurchaseAnalysisSearchRequest request) {
        log.info("Buscando análise de compras: refplu={}, hideNoSales={}, page={}, size={}, sortBy={}, sortDir={}",
                request.refplu(), request.hideNoSales(), request.page(), request.size(), request.sortBy(), request.sortDir());
        
        try {
            // Chamar Legacy API
            PurchaseAnalysisPageResponseDTO response = legacyApiClient.getPurchaseAnalysis(
                request.refplu(),
                request.hideNoSales(),
                request.page(),
                request.size(),
                request.sortBy(),
                request.sortDir()
            );
            
            log.info("Análise de compras obtida com sucesso: {} itens de {} total",
                    response.getContent().size(), 
                    response.getPagination().getTotalElements());
            
            return response;
            
        } catch (Exception e) {
            log.error("Erro ao buscar análise de compras", e);
            throw new RuntimeException("Erro ao buscar análise de compras: " + e.getMessage(), e);
        }
    }
}

