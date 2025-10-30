package com.sysconard.legacy.service;

import com.sysconard.legacy.dto.StockItemDTO;
import com.sysconard.legacy.dto.StockPageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Serviço de controle para operações de estoque
 * Orquestra chamadas entre StockService e StoreService
 * 
 * Segue os princípios de Clean Code:
 * - Responsabilidade única: orquestração de serviços
 * - Injeção de dependência via construtor
 * - Delegação de responsabilidades
 * - Tratamento de exceções
 * - Logging estruturado
 * 
 * @author Sysconard Legacy API
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StockControllerService {

    private final StockService stockService;

    /**
     * Busca estoque com filtros, paginação e ordenação
     * 
     * @param refplu Filtro por refplu (opcional)
     * @param marca Filtro por marca (opcional)
     * @param descricao Filtro por descrição (opcional)
     * @param hasStock Filtrar apenas produtos com estoque total > 0 (padrão: true)
     * @param page Número da página (0-based, padrão: 0)
     * @param size Tamanho da página (padrão: 15)
     * @param sortBy Campo para ordenação (padrão: refplu)
     * @param sortDir Direção da ordenação (asc/desc, padrão: asc)
     * @return Resposta paginada com itens de estoque
     */
    public StockPageResponse getStocks(
            String refplu, String marca, String descricao, Boolean hasStock,
            int page, int size, String sortBy, String sortDir) {

        log.info("Processando requisição de estoque - página: {}, tamanho: {}, filtros: refplu={}, marca={}, descricao={}, hasStock={}",
                page, size, refplu, marca, descricao, hasStock);

        try {
            // Buscar dados de estoque
            Page<StockItemDTO> stockPage = stockService.findStocksWithFilters(
                    refplu, marca, descricao, hasStock, page, size, sortBy, sortDir);

            // Construir resposta
            StockPageResponse response = StockPageResponse.builder()
                    .content(stockPage.getContent())
                    .totalElements(stockPage.getTotalElements())
                    .totalPages(stockPage.getTotalPages())
                    .currentPage(stockPage.getNumber())
                    .pageSize(stockPage.getSize())
                    .hasNext(stockPage.hasNext())
                    .hasPrevious(stockPage.hasPrevious())
                    .first(stockPage.isFirst())
                    .last(stockPage.isLast())
                    .numberOfElements(stockPage.getNumberOfElements())
                    .build();

            log.info("Resposta de estoque construída - total: {}, página atual: {}, elementos: {}",
                    response.getTotalElements(), response.getCurrentPage(), response.getNumberOfElements());

            return response;

        } catch (Exception e) {
            log.error("Erro ao processar requisição de estoque", e);
            
            // Retornar resposta vazia em caso de erro
            return StockPageResponse.builder()
                    .content(java.util.Collections.emptyList())
                    .totalElements(0L)
                    .totalPages(0)
                    .currentPage(page)
                    .pageSize(size)
                    .hasNext(false)
                    .hasPrevious(false)
                    .first(true)
                    .last(true)
                    .numberOfElements(0)
                    .build();
        }
    }
    
    /**
     * Testa dados sem filtro de loccod
     * 
     * @return Lista de dados de teste
     */
    public List<Object[]> testStocks() {
        return stockService.testStocks();
    }
    
    /**
     * Testa dados com loccod = 1
     * 
     * @return Lista de dados de teste
     */
    public List<Object[]> testStocksWithLoccod1() {
        return stockService.testStocksWithLoccod1();
    }
    
    /**
     * Testa dados com PIVOT simplificado
     * 
     * @return Lista de dados de teste
     */
    public List<Object[]> testStocksWithPivot() {
        return stockService.testStocksWithPivot();
    }
}
