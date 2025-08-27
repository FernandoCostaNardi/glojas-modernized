package com.sysconard.business.controller;

import com.sysconard.business.dto.ProductsBusinessResponseDTO;
import com.sysconard.business.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import java.util.Map;

/**
 * Controller REST para operações de produtos na Business API
 * Expõe endpoints que consomem a Legacy API e aplicam regras de negócio
 * 
 * @author Business API
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/products")
public class ProductController {
    
    private final ProductService productService;
    
    public ProductController(ProductService productService) {
        this.productService = productService;
    }
    
    /**
     * Busca produtos registrados com filtros, paginação e ordenação
     * Consome a Legacy API e aplica regras de negócio da Business layer
     * 
     * @param secao Filtro por seção (opcional)
     * @param grupo Filtro por grupo (opcional)
     * @param marca Filtro por marca (opcional)
     * @param descricao Filtro por descrição (opcional)
     * @param page Número da página (0-based, padrão: 0)
     * @param size Tamanho da página (padrão: 20, máximo: 100)
     * @param sortBy Campo para ordenação (padrão: codigo)
     * @param sortDir Direção da ordenação (asc/desc, padrão: asc)
     * @return Produtos registrados com metadados da Business API
     */
    @GetMapping("/registered")
    public ResponseEntity<ProductsBusinessResponseDTO> getRegisteredProducts(
            @RequestParam(required = false) String secao,
            @RequestParam(required = false) String grupo,
            @RequestParam(required = false) String marca,
            @RequestParam(required = false) String descricao,
            @RequestParam(defaultValue = "0") @Min(0) Integer page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) Integer size,
            @RequestParam(defaultValue = "codigo") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        log.info("Recebida requisição para buscar produtos registrados - página: {}, tamanho: {}", page, size);
        
        // Validar direção de ordenação
        if (!sortDir.equalsIgnoreCase("asc") && !sortDir.equalsIgnoreCase("desc")) {
            sortDir = "asc";
        }
        
        ProductsBusinessResponseDTO response = productService.getRegisteredProducts(
                secao, grupo, marca, descricao, page, size, sortBy, sortDir.toLowerCase());
        
        // Retornar status HTTP baseado no resultado
        if ("ERROR".equals(response.getStatus())) {
            log.warn("Erro na busca de produtos: {}", response.getMessage());
            return ResponseEntity.status(500).body(response);
        }
        
        log.info("Busca de produtos concluída com sucesso. Total encontrado: {}", 
                response.getPagination().getTotalElements());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Endpoint para testar conectividade com a Legacy API
     * Útil para diagnósticos e monitoramento
     * 
     * @return Status da conexão com a Legacy API
     */
    @GetMapping("/test-legacy-connection")
    public ResponseEntity<Map<String, Object>> testLegacyConnection() {
        log.info("Testando conectividade com Legacy API");
        
        Map<String, Object> testResult = productService.testLegacyApiConnection();
        
        // Determinar status HTTP baseado no resultado
        String status = (String) testResult.get("status");
        if ("ERROR".equals(status)) {
            return ResponseEntity.status(503).body(testResult); // Service Unavailable
        }
        
        return ResponseEntity.ok(testResult);
    }
    
    /**
     * Endpoint de health check específico para produtos
     * 
     * @return Status de saúde do serviço de produtos
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        log.debug("Health check do ProductController");
        
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "ProductController",
                "timestamp", java.time.LocalDateTime.now().toString(),
                "version", "1.0"
        ));
    }
}
