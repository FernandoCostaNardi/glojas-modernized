package com.sysconard.legacy.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.sysconard.legacy.dto.PurchaseAnalysisItemDTO;
import com.sysconard.legacy.dto.PurchaseAnalysisPageResponse;

import java.util.List;

/**
 * Controller Service para análise de compras.
 * Orquestra chamadas e monta respostas paginadas.
 * 
 * @author Sysconard Legacy API
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PurchaseAnalysisControllerService {
    
    private final PurchaseAnalysisService purchaseAnalysisService;
    
    /**
     * Busca análise de compras com filtros, paginação e ordenação.
     * 
     * @param refplu Filtro opcional por REFPLU
     * @param descricao Filtro opcional por descrição (busca também em grupo e marca)
     * @param grupo Filtro opcional por grupo
     * @param marca Filtro opcional por marca
     * @param hideNoSales Ocultar produtos sem vendas nos últimos 90 dias
     * @param page Número da página (base 0)
     * @param size Tamanho da página
     * @param sortBy Campo para ordenação
     * @param sortDir Direção da ordenação
     * @return Resposta paginada com itens de análise de compras
     */
    public PurchaseAnalysisPageResponse getPurchaseAnalysis(
            String refplu, String descricao, String grupo, String marca, Boolean hideNoSales, int page, int size, String sortBy, String sortDir) {
        
        log.info("Processando requisição de análise de compras: refplu={}, descricao={}, grupo={}, marca={}, hideNoSales={}, page={}, size={}, sortBy={}, sortDir={}",
                refplu, descricao, grupo, marca, hideNoSales, page, size, sortBy, sortDir);
        
        try {
            // Buscar dados
            List<PurchaseAnalysisItemDTO> content = purchaseAnalysisService.findPurchaseAnalysisWithFilters(
                refplu, descricao, grupo, marca, hideNoSales, page, size, sortBy, sortDir
            );
            
            // Contar total
            long totalElements = purchaseAnalysisService.countWithFilters(refplu, descricao, grupo, marca, hideNoSales);
            
            // Calcular total de páginas
            int totalPages = (int) Math.ceil((double) totalElements / size);
            
            // Montar informações de paginação
            PurchaseAnalysisPageResponse.PaginationInfo pagination = PurchaseAnalysisPageResponse.PaginationInfo.builder()
                    .currentPage(page)
                    .pageSize(size)
                    .totalElements(totalElements)
                    .totalPages(totalPages)
                    .first(page == 0)
                    .last(page >= totalPages - 1)
                    .build();
            
            // Montar resposta
            PurchaseAnalysisPageResponse response = PurchaseAnalysisPageResponse.builder()
                    .content(content)
                    .pagination(pagination)
                    .build();
            
            log.info("Análise de compras processada com sucesso: {} itens de {} total", 
                    content.size(), totalElements);
            
            return response;
            
        } catch (IllegalArgumentException e) {
            log.warn("Erro de validação na análise de compras: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Erro ao processar análise de compras", e);
            throw new RuntimeException("Erro ao processar análise de compras: " + e.getMessage(), e);
        }
    }
}

