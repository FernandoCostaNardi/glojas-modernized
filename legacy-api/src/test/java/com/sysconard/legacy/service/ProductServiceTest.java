package com.sysconard.legacy.service;

import com.sysconard.legacy.dto.ProductRegisteredDTO;
import com.sysconard.legacy.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

/**
 * Testes unitários para o ProductService
 * 
 * @author Sysconard Legacy API
 * @version 2.0
 */
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    private ProductService productService;

    @BeforeEach
    void setUp() {
        productService = new ProductService(productRepository);
    }

    @Test
    void shouldFindProductsWithFiltersSuccessfully() {
        // Given
        String secao = "Eletrônicos";
        String grupo = "Smartphones";
        String marca = "Apple";
        String descricao = "iPhone";
        int page = 0;
        int size = 20;
        String sortBy = "codigo";
        String sortDir = "asc";

        List<Object[]> rows = Arrays.asList(
            createProductRow(1L, "Eletrônicos", "Smartphones", "Apple", "Apple", "IPHONE13", "123", "iPhone 13", "8517.12.00"),
            createProductRow(2L, "Eletrônicos", "Smartphones", "Apple", "Apple", "IPHONE14", "124", "iPhone 14", "8517.12.00")
        );

        when(productRepository.findProductsWithFilters(
            eq(secao), eq(grupo), eq(marca), eq(descricao),
            eq("%" + secao + "%"), eq("%" + grupo + "%"), eq("%" + marca + "%"), eq("%" + descricao + "%"),
            eq(sortBy), eq(0), eq(size)
        )).thenReturn(rows);

        when(productRepository.countProductsWithFilters(
            eq(secao), eq(grupo), eq(marca), eq(descricao),
            eq("%" + secao + "%"), eq("%" + grupo + "%"), eq("%" + marca + "%"), eq("%" + descricao + "%")
        )).thenReturn(2L);

        // When
        Page<ProductRegisteredDTO> result = productService.findProductsWithFilters(
            secao, grupo, marca, descricao, page, size, sortBy, sortDir
        );

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getTotalPages()).isEqualTo(1);
        assertThat(result.getNumber()).isEqualTo(0);
        assertThat(result.getSize()).isEqualTo(20);

        // Verificar primeiro produto
        ProductRegisteredDTO firstProduct = result.getContent().get(0);
        assertThat(firstProduct.getCodigo()).isEqualTo(1L);
        assertThat(firstProduct.getSecao()).isEqualTo("Eletrônicos");
        assertThat(firstProduct.getGrupo()).isEqualTo("Smartphones");
        assertThat(firstProduct.getMarca()).isEqualTo("Apple");
        assertThat(firstProduct.getDescricao()).isEqualTo("iPhone 13");
    }

    @Test
    void shouldFindProductsWithNullFilters() {
        // Given
        List<Object[]> rows = Arrays.asList(
            createProductRow(1L, "Eletrônicos", "Smartphones", "Apple", "Apple", "IPHONE13", "123", "iPhone 13", "8517.12.00")
        );

        when(productRepository.findProductsWithFilters(
            isNull(), isNull(), isNull(), isNull(),
            isNull(), isNull(), isNull(), isNull(),
            eq("codigo"), eq(0), eq(20)
        )).thenReturn(rows);

        when(productRepository.countProductsWithFilters(
            isNull(), isNull(), isNull(), isNull(),
            isNull(), isNull(), isNull(), isNull()
        )).thenReturn(1L);

        // When
        Page<ProductRegisteredDTO> result = productService.findProductsWithFilters(
            null, null, null, null, 0, 20, "codigo", "asc"
        );

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    void shouldFindProductsWithDescSorting() {
        // Given
        List<Object[]> rows = Arrays.asList(
            createProductRow(2L, "Eletrônicos", "Smartphones", "Apple", "Apple", "IPHONE14", "124", "iPhone 14", "8517.12.00"),
            createProductRow(1L, "Eletrônicos", "Smartphones", "Apple", "Apple", "IPHONE13", "123", "iPhone 13", "8517.12.00")
        );

        when(productRepository.findProductsWithFilters(
            any(), any(), any(), any(),
            any(), any(), any(), any(),
            eq("codigo"), eq(0), eq(20)
        )).thenReturn(rows);

        when(productRepository.countProductsWithFilters(
            any(), any(), any(), any(),
            any(), any(), any(), any()
        )).thenReturn(2L);

        // When
        Page<ProductRegisteredDTO> result = productService.findProductsWithFilters(
            "Eletrônicos", "Smartphones", "Apple", "iPhone", 0, 20, "codigo", "desc"
        );

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).getCodigo()).isEqualTo(2L);
        assertThat(result.getContent().get(1).getCodigo()).isEqualTo(1L);
    }

    @Test
    void shouldCountTotalProductsSuccessfully() {
        // Given
        long expectedCount = 1500L;
        when(productRepository.countTotalProducts()).thenReturn(expectedCount);

        // When
        long result = productService.countTotalProducts();

        // Then
        assertThat(result).isEqualTo(expectedCount);
    }

    @Test
    void shouldThrowExceptionForInvalidPage() {
        // When & Then
        assertThatThrownBy(() -> 
            productService.findProductsWithFilters("Eletrônicos", "Smartphones", "Apple", "iPhone", -1, 20, "codigo", "asc")
        ).isInstanceOf(IllegalArgumentException.class)
         .hasMessage("Número da página deve ser >= 0");
    }

    @Test
    void shouldThrowExceptionForInvalidSize() {
        // When & Then
        assertThatThrownBy(() -> 
            productService.findProductsWithFilters("Eletrônicos", "Smartphones", "Apple", "iPhone", 0, 0, "codigo", "asc")
        ).isInstanceOf(IllegalArgumentException.class)
         .hasMessage("Tamanho da página deve ser > 0");
    }

    @Test
    void shouldThrowExceptionForSizeTooLarge() {
        // When & Then
        assertThatThrownBy(() -> 
            productService.findProductsWithFilters("Eletrônicos", "Smartphones", "Apple", "iPhone", 0, 1001, "codigo", "asc")
        ).isInstanceOf(IllegalArgumentException.class)
         .hasMessage("Tamanho da página não pode ser > 1000");
    }

    @Test
    void shouldThrowExceptionForEmptySortBy() {
        // When & Then
        assertThatThrownBy(() -> 
            productService.findProductsWithFilters("Eletrônicos", "Smartphones", "Apple", "iPhone", 0, 20, "", "asc")
        ).isInstanceOf(IllegalArgumentException.class)
         .hasMessage("Campo de ordenação não pode ser vazio");
    }

    @Test
    void shouldThrowExceptionForEmptySortDir() {
        // When & Then
        assertThatThrownBy(() -> 
            productService.findProductsWithFilters("Eletrônicos", "Smartphones", "Apple", "iPhone", 0, 20, "codigo", "")
        ).isInstanceOf(IllegalArgumentException.class)
         .hasMessage("Direção de ordenação não pode ser vazia");
    }

    @Test
    void shouldThrowExceptionForInvalidSortDir() {
        // When & Then
        assertThatThrownBy(() -> 
            productService.findProductsWithFilters("Eletrônicos", "Smartphones", "Apple", "iPhone", 0, 20, "codigo", "invalid")
        ).isInstanceOf(IllegalArgumentException.class)
         .hasMessage("Direção de ordenação deve ser 'asc' ou 'desc'");
    }



    /**
     * Cria uma linha de dados de produto para testes
     */
    private Object[] createProductRow(Long codigo, String secao, String grupo, String subgrupo, 
                                    String marca, String partNumber, String refplu, String descricao, String ncm) {
        return new Object[]{codigo, secao, grupo, subgrupo, marca, partNumber, refplu, descricao, ncm};
    }
}
