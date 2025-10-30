package com.sysconard.business.controller;

import com.sysconard.business.dto.StockPageResponseDTO;
import com.sysconard.business.dto.StockSearchRequest;
import com.sysconard.business.service.StockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

/**
 * Controller REST para operações de estoque na Business API
 * Expõe endpoints que consomem a Legacy API e aplicam regras de negócio
 * 
 * @author Business API
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/stocks")
public class StockController {
    
    private final StockService stockService;
    
    public StockController(StockService stockService) {
        this.stockService = stockService;
    }
    
    /**
     * Busca estoque com filtros, paginação e ordenação
     * Requer permissão STOCK para acessar
     * 
     * @param refplu Filtro por refplu (opcional)
     * @param marca Filtro por marca (opcional)
     * @param descricao Filtro por descrição (opcional)
     * @param hasStock Filtrar apenas produtos com estoque total > 0 (padrão: true)
     * @param page Número da página (0-based, padrão: 0)
     * @param size Tamanho da página (padrão: 15, máximo: 100)
     * @param sortBy Campo para ordenação - aceita: refplu, marca, descricao, loj1-loj14, total (padrão: refplu)
     * @param sortDir Direção da ordenação (asc/desc, padrão: asc)
     * @return Estoque com metadados da Business API
     */
    @GetMapping
    @PreAuthorize("hasAuthority('stock:read')")
    public ResponseEntity<StockPageResponseDTO> getStocks(
            @RequestParam(required = false) String refplu,
            @RequestParam(required = false) String marca,
            @RequestParam(required = false) String descricao,
            @RequestParam(required = false, defaultValue = "true") Boolean hasStock,
            @RequestParam(defaultValue = "0") @Min(0) Integer page,
            @RequestParam(defaultValue = "15") @Min(1) @Max(100) Integer size,
            @RequestParam(defaultValue = "refplu") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        log.info("Recebida requisição para buscar estoque - página: {}, tamanho: {}, hasStock: {}", page, size, hasStock);
        
        // Validar direção de ordenação
        if (!sortDir.equalsIgnoreCase("asc") && !sortDir.equalsIgnoreCase("desc")) {
            sortDir = "asc";
        }
        
        // Criar request object
        StockSearchRequest request = new StockSearchRequest(
                refplu, marca, descricao, hasStock, page, size, sortBy, sortDir.toLowerCase());
        
        StockPageResponseDTO response = stockService.getStocks(request);
        
        // Retornar status HTTP baseado no resultado
        if ("ERROR".equals(response.getStatus())) {
            log.warn("Erro na busca de estoque: {}", response.getMessage());
            return ResponseEntity.status(500).body(response);
        }
        
        log.info("Busca de estoque concluída com sucesso. Total encontrado: {}", 
                response.getPagination().getTotalElements());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Endpoint de health check específico para estoque
     * 
     * @return Status de saúde do serviço de estoque
     */
    @GetMapping("/health")
    public ResponseEntity<java.util.Map<String, Object>> healthCheck() {
        log.debug("Health check do StockController");
        
        return ResponseEntity.ok(java.util.Map.of(
                "status", "UP",
                "service", "StockController",
                "timestamp", java.time.LocalDateTime.now().toString(),
                "version", "1.0"
        ));
    }
}
