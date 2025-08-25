package com.sysconard.legacy.controller;

import com.sysconard.legacy.dto.ProductRegisteredDTO;
import com.sysconard.legacy.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.HashMap;

/**
 * Controller para operações com produtos
 * 
 * @author Sysconard Legacy API
 * @version 1.0
 */
@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    /**
     * Busca produtos cadastrados com filtros, paginação e ordenação
     * 
     * @param secao Filtro por seção (opcional)
     * @param grupo Filtro por grupo (opcional)
     * @param marca Filtro por marca (opcional)
     * @param descricao Filtro por descrição (opcional)
     * @param page Número da página (0-based, padrão: 0)
     * @param size Tamanho da página (padrão: 20)
     * @param sortBy Campo para ordenação (padrão: codigo)
     * @param sortDir Direção da ordenação (asc/desc, padrão: asc)
     * @return Página com produtos cadastrados
     */
    @GetMapping("/registered")
    public ResponseEntity<Map<String, Object>> getRegisteredProducts(
            @RequestParam(required = false) String secao,
            @RequestParam(required = false) String grupo,
            @RequestParam(required = false) String marca,
            @RequestParam(required = false) String descricao,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "codigo") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        // Buscar dados
        Page<ProductRegisteredDTO> result = productService.findProductsWithFilters(
            secao, grupo, marca, descricao, page, size, sortBy, sortDir
        );

        // Montar resposta
        Map<String, Object> response = new HashMap<>();
        response.put("content", result.getContent());
        response.put("totalElements", result.getTotalElements());
        response.put("totalPages", result.getTotalPages());
        response.put("currentPage", result.getNumber());
        response.put("pageSize", result.getSize());
        response.put("hasNext", result.hasNext());
        response.put("hasPrevious", result.hasPrevious());
        // Criar map de filtros (compatível com Java 8)
        Map<String, String> filters = new HashMap<>();
        filters.put("secao", secao);
        filters.put("grupo", grupo);
        filters.put("marca", marca);
        filters.put("descricao", descricao);
        response.put("filters", filters);
        
        // Criar map de sorting (compatível com Java 8)
        Map<String, String> sorting = new HashMap<>();
        sorting.put("sortBy", sortBy);
        sorting.put("sortDir", sortDir);
        response.put("sorting", sorting);

        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint de teste para verificar conexão com SQL Server
     * 
     * @return Confirmação de conexão ou erro
     */
    @GetMapping("/test-connection")
    public ResponseEntity<Map<String, Object>> testConnection() {
        try {
            // Teste simples - contar produtos cadastrados
            long count = productService.countTotalProducts();
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "SUCCESS");
            response.put("message", "SQL Server conectado com sucesso!");
            response.put("database", "SysacME");
            response.put("totalProducts", count);
            response.put("driver", "Microsoft SQL Server JDBC Driver 6.4.0.jre8");
            response.put("java", System.getProperty("java.version"));
            response.put("timestamp", java.time.LocalDateTime.now().toString());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "ERROR");
            errorResponse.put("message", "Erro na conexão: " + e.getMessage());
            errorResponse.put("error", e.getClass().getSimpleName());
            errorResponse.put("timestamp", java.time.LocalDateTime.now().toString());
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}
