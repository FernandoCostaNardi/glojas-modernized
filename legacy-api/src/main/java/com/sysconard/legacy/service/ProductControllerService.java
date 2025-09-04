package com.sysconard.legacy.service;

import com.sysconard.legacy.dto.ProductConnectionResponse;
import com.sysconard.legacy.dto.ProductPageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Serviço responsável pela lógica de negócio do ProductController
 * 
 * @author Sysconard Legacy API
 * @version 1.0
 */
@Service
public class ProductControllerService {
    
    private final ProductService productService;
    
    @Autowired
    public ProductControllerService(ProductService productService) {
        this.productService = productService;
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
     * @return ProductPageResponse com produtos cadastrados
     */
    public ProductPageResponse getRegisteredProducts(
            String secao, String grupo, String marca, String descricao,
            int page, int size, String sortBy, String sortDir) {
        
        // Buscar dados do serviço
        Page<com.sysconard.legacy.dto.ProductRegisteredDTO> result = productService.findProductsWithFilters(
            secao, grupo, marca, descricao, page, size, sortBy, sortDir
        );
        
        // Criar map de filtros (compatível com Java 8)
        Map<String, String> filters = new HashMap<>();
        filters.put("secao", secao);
        filters.put("grupo", grupo);
        filters.put("marca", marca);
        filters.put("descricao", descricao);
        
        // Criar map de sorting (compatível com Java 8)
        Map<String, String> sorting = new HashMap<>();
        sorting.put("sortBy", sortBy);
        sorting.put("sortDir", sortDir);
        
        // Construir resposta usando Builder pattern
        return ProductPageResponse.builder()
                .content(result.getContent())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .currentPage(result.getNumber())
                .pageSize(result.getSize())
                .hasNext(result.hasNext())
                .hasPrevious(result.hasPrevious())
                .filters(filters)
                .sorting(sorting)
                .build();
    }
    
    /**
     * Testa a conexão com SQL Server através da contagem de produtos
     * 
     * @return ProductConnectionResponse com resultado do teste
     */
    public ProductConnectionResponse testConnection() {
        try {
            // Teste simples - contar produtos cadastrados
            long count = productService.countTotalProducts();
            
            return ProductConnectionResponse.builder()
                    .status("SUCCESS")
                    .message("SQL Server conectado com sucesso!")
                    .database("SysacME")
                    .totalProducts(count)
                    .driver("Microsoft SQL Server JDBC Driver 6.4.0.jre8")
                    .java(System.getProperty("java.version"))
                    .timestamp(LocalDateTime.now())
                    .build();
                    
        } catch (Exception e) {
            return ProductConnectionResponse.builder()
                    .status("ERROR")
                    .message("Erro na conexão: " + e.getMessage())
                    .error(e.getClass().getSimpleName())
                    .timestamp(LocalDateTime.now())
                    .build();
        }
    }
}
