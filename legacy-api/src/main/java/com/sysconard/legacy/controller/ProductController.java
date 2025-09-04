package com.sysconard.legacy.controller;

import com.sysconard.legacy.dto.ProductConnectionResponse;
import com.sysconard.legacy.dto.ProductPageResponse;
import com.sysconard.legacy.service.ProductControllerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller para operações com produtos
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
@RequestMapping("/products")
public class ProductController {

    private final ProductControllerService productControllerService;
    
    @Autowired
    public ProductController(ProductControllerService productControllerService) {
        this.productControllerService = productControllerService;
    }

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
    public ResponseEntity<ProductPageResponse> getRegisteredProducts(
            @RequestParam(required = false) String secao,
            @RequestParam(required = false) String grupo,
            @RequestParam(required = false) String marca,
            @RequestParam(required = false) String descricao,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "codigo") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        ProductPageResponse response = productControllerService.getRegisteredProducts(
            secao, grupo, marca, descricao, page, size, sortBy, sortDir
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint de teste para verificar conexão com SQL Server
     * 
     * @return Confirmação de conexão ou erro
     */
    @GetMapping("/test-connection")
    public ResponseEntity<ProductConnectionResponse> testConnection() {
        ProductConnectionResponse response = productControllerService.testConnection();
        
        if ("ERROR".equals(response.getStatus())) {
            return ResponseEntity.status(500).body(response);
        }
        
        return ResponseEntity.ok(response);
    }
}
