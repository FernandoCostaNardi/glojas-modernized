package com.sysconard.legacy.controller;

import com.sysconard.legacy.dto.StockPageResponse;
import com.sysconard.legacy.service.StockControllerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller para operações com estoque
 * 
 * Segue os princípios de Clean Code:
 * - Responsabilidade única: apenas roteamento HTTP
 * - Injeção de dependência via construtor
 * - Delegação de lógica para o serviço
 * - Documentação clara
 * 
 * @author Sysconard Legacy API
 * @version 1.0
 */
@RestController
@RequestMapping("/stocks")
public class StockController {

    private final StockControllerService stockControllerService;
    
    @Autowired
    public StockController(StockControllerService stockControllerService) {
        this.stockControllerService = stockControllerService;
    }

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
     * @return Página com itens de estoque
     */
    @GetMapping
    public ResponseEntity<StockPageResponse> getStocks(
            @RequestParam(required = false) String refplu,
            @RequestParam(required = false) String marca,
            @RequestParam(required = false) String descricao,
            @RequestParam(required = false, defaultValue = "true") Boolean hasStock,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size,
            @RequestParam(defaultValue = "refplu") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        StockPageResponse response = stockControllerService.getStocks(
            refplu, marca, descricao, hasStock, page, size, sortBy, sortDir
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> testConnection() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "OK");
        response.put("message", "Legacy API Stock Controller funcionando");
        response.put("timestamp", LocalDateTime.now());
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/test-data")
    public ResponseEntity<Map<String, Object>> testData() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Testar dados sem filtro de loccod
            List<Object[]> testStocks = stockControllerService.testStocks();
            response.put("testStocks", testStocks);
            response.put("testStocksCount", testStocks.size());
            
            // Testar dados com loccod = 1
            List<Object[]> testStocksWithLoccod1 = stockControllerService.testStocksWithLoccod1();
            response.put("testStocksWithLoccod1", testStocksWithLoccod1);
            response.put("testStocksWithLoccod1Count", testStocksWithLoccod1.size());
            
            // Testar dados com PIVOT simplificado
            List<Object[]> testStocksWithPivot = stockControllerService.testStocksWithPivot();
            response.put("testStocksWithPivot", testStocksWithPivot);
            response.put("testStocksWithPivotCount", testStocksWithPivot.size());
            
            response.put("status", "OK");
            response.put("message", "Dados de teste carregados");
            response.put("timestamp", LocalDateTime.now());
            
        } catch (Exception e) {
            response.put("status", "ERROR");
            response.put("message", "Erro ao carregar dados de teste: " + e.getMessage());
            response.put("timestamp", LocalDateTime.now());
        }
        
        return ResponseEntity.ok(response);
    }
}
