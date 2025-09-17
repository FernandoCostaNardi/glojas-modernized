package com.sysconard.legacy.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.sysconard.legacy.dto.ProductConnectionResponse;
import com.sysconard.legacy.dto.ProductPageResponse;
import com.sysconard.legacy.dto.ProductRegisteredDTO;
import com.sysconard.legacy.service.ProductControllerService;

/**
 * Testes de integração para o ProductController
 * 
 * @author Sysconard Legacy API
 * @version 1.0
 */
@WebMvcTest(ProductController.class)
class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private ProductControllerService productControllerService;
    
    @Test
    void shouldReturnProductPageResponseWhenCallingRegisteredEndpoint() throws Exception {
        // Given
        List<ProductRegisteredDTO> products = Arrays.asList(
            new ProductRegisteredDTO(1L, "Eletrônicos", "Smartphones", "Apple", "Apple", "IPHONE13", "123", "iPhone 13", "8517.12.00"),
            new ProductRegisteredDTO(2L, "Eletrônicos", "Smartphones", "Apple", "Apple", "IPHONE14", "124", "iPhone 14", "8517.12.00")
        );
        
        ProductPageResponse expectedResponse = ProductPageResponse.builder()
                .content(products)
                .totalElements(2)
                .totalPages(1)
                .currentPage(0)
                .pageSize(20)
                .hasNext(false)
                .hasPrevious(false)
                .build();
        
        when(productControllerService.getRegisteredProducts("Eletrônicos", "Smartphones", "Apple", "iPhone", 0, 20, "codigo", "asc"))
            .thenReturn(expectedResponse);
        
        // When & Then
        mockMvc.perform(get("/products/registered")
                .param("secao", "Eletrônicos")
                .param("grupo", "Smartphones")
                .param("marca", "Apple")
                .param("descricao", "iPhone")
                .param("page", "0")
                .param("size", "20")
                .param("sortBy", "codigo")
                .param("sortDir", "asc")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].codigo").value(1))
                .andExpect(jsonPath("$.content[0].secao").value("Eletrônicos"))
                .andExpect(jsonPath("$.content[0].grupo").value("Smartphones"))
                .andExpect(jsonPath("$.content[0].marca").value("Apple"))
                .andExpect(jsonPath("$.content[0].descricao").value("iPhone 13"))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.currentPage").value(0))
                .andExpect(jsonPath("$.pageSize").value(20))
                .andExpect(jsonPath("$.hasNext").value(false))
                .andExpect(jsonPath("$.hasPrevious").value(false))
                .andExpect(jsonPath("$.filters.secao").value("Eletrônicos"))
                .andExpect(jsonPath("$.filters.grupo").value("Smartphones"))
                .andExpect(jsonPath("$.filters.marca").value("Apple"))
                .andExpect(jsonPath("$.filters.descricao").value("iPhone"))
                .andExpect(jsonPath("$.sorting.sortBy").value("codigo"))
                .andExpect(jsonPath("$.sorting.sortDir").value("asc"));
    }
    
    @Test
    void shouldReturnProductPageResponseWithDefaultParameters() throws Exception {
        // Given
        List<ProductRegisteredDTO> products = Arrays.asList(
            new ProductRegisteredDTO(1L, "Eletrônicos", "Smartphones", "Apple", "Apple", "IPHONE13", "123", "iPhone 13", "8517.12.00")
        );
        
        ProductPageResponse expectedResponse = ProductPageResponse.builder()
                .content(products)
                .totalElements(1)
                .totalPages(1)
                .currentPage(0)
                .pageSize(20)
                .hasNext(false)
                .hasPrevious(false)
                .build();
        
        when(productControllerService.getRegisteredProducts(null, null, null, null, 0, 20, "codigo", "asc"))
            .thenReturn(expectedResponse);
        
        // When & Then
        mockMvc.perform(get("/products/registered")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].codigo").value(1))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.currentPage").value(0))
                .andExpect(jsonPath("$.pageSize").value(20));
    }
    
    @Test
    void shouldReturnSuccessConnectionResponseWhenCallingTestConnectionEndpoint() throws Exception {
        // Given
        ProductConnectionResponse expectedResponse = ProductConnectionResponse.builder()
                .status("SUCCESS")
                .message("SQL Server conectado com sucesso!")
                .database("SysacME")
                .totalProducts(1500L)
                .driver("Microsoft SQL Server JDBC Driver 6.4.0.jre8")
                .java(System.getProperty("java.version"))
                .timestamp(LocalDateTime.now())
                .build();
        
        when(productControllerService.testConnection()).thenReturn(expectedResponse);
        
        // When & Then
        mockMvc.perform(get("/products/test-connection")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("SQL Server conectado com sucesso!"))
                .andExpect(jsonPath("$.database").value("SysacME"))
                .andExpect(jsonPath("$.totalProducts").value(1500))
                .andExpect(jsonPath("$.driver").value("Microsoft SQL Server JDBC Driver 6.4.0.jre8"))
                .andExpect(jsonPath("$.java").value(System.getProperty("java.version")))
                .andExpect(jsonPath("$.timestamp").exists());
    }
    
    @Test
    void shouldReturnErrorConnectionResponseWhenCallingTestConnectionEndpoint() throws Exception {
        // Given
        ProductConnectionResponse expectedResponse = ProductConnectionResponse.builder()
                .status("ERROR")
                .message("Erro na conexão: Database connection failed")
                .error("RuntimeException")
                .timestamp(LocalDateTime.now())
                .build();
        
        when(productControllerService.testConnection()).thenReturn(expectedResponse);
        
        // When & Then
        mockMvc.perform(get("/products/test-connection")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("ERROR"))
                .andExpect(jsonPath("$.message").value("Erro na conexão: Database connection failed"))
                .andExpect(jsonPath("$.error").value("RuntimeException"))
                .andExpect(jsonPath("$.timestamp").exists());
    }
}
