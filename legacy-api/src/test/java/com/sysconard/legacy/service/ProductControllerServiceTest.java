package com.sysconard.legacy.service;

import com.sysconard.legacy.dto.ProductConnectionResponse;
import com.sysconard.legacy.dto.ProductPageResponse;
import com.sysconard.legacy.dto.ProductRegisteredDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Testes unitários para o ProductControllerService
 * 
 * @author Sysconard Legacy API
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
class ProductControllerServiceTest {

    @Mock
    private ProductService productService;
    
    private ProductControllerService productControllerService;
    
    @BeforeEach
    void setUp() {
        productControllerService = new ProductControllerService(productService);
    }
    
    @Test
    void shouldReturnProductPageResponseSuccessfully() {
        // Given
        String secao = "Eletrônicos";
        String grupo = "Smartphones";
        String marca = "Apple";
        String descricao = "iPhone";
        int page = 0;
        int size = 20;
        String sortBy = "codigo";
        String sortDir = "asc";
        
        List<ProductRegisteredDTO> products = Arrays.asList(
            new ProductRegisteredDTO(1L, "Eletrônicos", "Smartphones", "Apple", "Apple", "IPHONE13", "123", "iPhone 13", "8517.12.00"),
            new ProductRegisteredDTO(2L, "Eletrônicos", "Smartphones", "Apple", "Apple", "IPHONE14", "124", "iPhone 14", "8517.12.00")
        );
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductRegisteredDTO> productPage = new PageImpl<>(products, pageable, 2);
        
        when(productService.findProductsWithFilters(secao, grupo, marca, descricao, page, size, sortBy, sortDir))
            .thenReturn(productPage);
        
        // When
        ProductPageResponse response = productControllerService.getRegisteredProducts(
            secao, grupo, marca, descricao, page, size, sortBy, sortDir
        );
        
        // Then
        assertThat(response).isNotNull();
        assertThat(response.getContent()).hasSize(2);
        assertThat(response.getTotalElements()).isEqualTo(2);
        assertThat(response.getTotalPages()).isEqualTo(1);
        assertThat(response.getCurrentPage()).isEqualTo(0);
        assertThat(response.getPageSize()).isEqualTo(20);
        assertThat(response.isHasNext()).isFalse();
        assertThat(response.isHasPrevious()).isFalse();
        
        // Verificar filtros
        assertThat(response.getFilters()).isNotNull();
        assertThat(response.getFilters().get("secao")).isEqualTo("Eletrônicos");
        assertThat(response.getFilters().get("grupo")).isEqualTo("Smartphones");
        assertThat(response.getFilters().get("marca")).isEqualTo("Apple");
        assertThat(response.getFilters().get("descricao")).isEqualTo("iPhone");
        
        // Verificar sorting
        assertThat(response.getSorting()).isNotNull();
        assertThat(response.getSorting().get("sortBy")).isEqualTo("codigo");
        assertThat(response.getSorting().get("sortDir")).isEqualTo("asc");
    }
    
    @Test
    void shouldReturnProductPageResponseWithNullFilters() {
        // Given
        List<ProductRegisteredDTO> products = Arrays.asList(
            new ProductRegisteredDTO(1L, "Eletrônicos", "Smartphones", "Apple", "Apple", "IPHONE13", "123", "iPhone 13", "8517.12.00")
        );
        
        Pageable pageable = PageRequest.of(0, 20);
        Page<ProductRegisteredDTO> productPage = new PageImpl<>(products, pageable, 1);
        
        when(productService.findProductsWithFilters(null, null, null, null, 0, 20, "codigo", "asc"))
            .thenReturn(productPage);
        
        // When
        ProductPageResponse response = productControllerService.getRegisteredProducts(
            null, null, null, null, 0, 20, "codigo", "asc"
        );
        
        // Then
        assertThat(response).isNotNull();
        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getFilters().get("secao")).isNull();
        assertThat(response.getFilters().get("grupo")).isNull();
        assertThat(response.getFilters().get("marca")).isNull();
        assertThat(response.getFilters().get("descricao")).isNull();
    }
    
    @Test
    void shouldReturnSuccessConnectionResponse() {
        // Given
        long totalProducts = 1500L;
        when(productService.countTotalProducts()).thenReturn(totalProducts);
        
        // When
        ProductConnectionResponse response = productControllerService.testConnection();
        
        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo("SUCCESS");
        assertThat(response.getMessage()).isEqualTo("SQL Server conectado com sucesso!");
        assertThat(response.getDatabase()).isEqualTo("SysacME");
        assertThat(response.getTotalProducts()).isEqualTo(1500L);
        assertThat(response.getDriver()).isEqualTo("Microsoft SQL Server JDBC Driver 6.4.0.jre8");
        assertThat(response.getJava()).isEqualTo(System.getProperty("java.version"));
        assertThat(response.getTimestamp()).isNotNull();
        assertThat(response.getError()).isNull();
    }
    
    @Test
    void shouldReturnErrorConnectionResponse() {
        // Given
        RuntimeException exception = new RuntimeException("Database connection failed");
        when(productService.countTotalProducts()).thenThrow(exception);
        
        // When
        ProductConnectionResponse response = productControllerService.testConnection();
        
        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo("ERROR");
        assertThat(response.getMessage()).isEqualTo("Erro na conexão: Database connection failed");
        assertThat(response.getError()).isEqualTo("RuntimeException");
        assertThat(response.getTimestamp()).isNotNull();
        assertThat(response.getDatabase()).isNull();
        assertThat(response.getTotalProducts()).isEqualTo(0);
        assertThat(response.getDriver()).isNull();
        assertThat(response.getJava()).isNull();
    }
}
