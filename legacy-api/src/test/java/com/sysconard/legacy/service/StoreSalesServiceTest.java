package com.sysconard.legacy.service;

import com.sysconard.legacy.dto.StoreSalesReportDTO;
import com.sysconard.legacy.dto.StoreSalesReportRequestDTO;
import com.sysconard.legacy.repository.DocumentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

/**
 * Testes unitários para StoreSalesService.
 * Verifica a funcionalidade da implementação otimizada com filtro de data no SQL.
 * 
 * @author Sysconard Legacy API
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
class StoreSalesServiceTest {
    
    @Mock
    private DocumentRepository documentRepository;
    
    @InjectMocks
    private StoreSalesService storeSalesService;
    
    private StoreSalesReportRequestDTO validRequest;
    
    @BeforeEach
    void setUp() {
        validRequest = StoreSalesReportRequestDTO.builder()
            .startDate("2025-01-15")
            .endDate("2025-01-15")
            .storeCodes(Arrays.asList("000002", "000003", "000004"))
            .danfeOrigin(Arrays.asList("015", "002"))
            .pdvOrigin(Arrays.asList("009"))
            .exchangeOrigin(Arrays.asList("051", "065"))
            .sellOperation(Arrays.asList("000999", "000007", "000001"))
            .exchangeOperation(Arrays.asList("000015", "000048"))
            .build();
    }
    
    @Test
    void shouldGenerateStoreSalesReportSuccessfully() {
        // Given
        Object[] store1Data = {"Loja Centro", "000002", new BigDecimal("1000.00"), new BigDecimal("2000.00"), new BigDecimal("500.00")};
        Object[] store2Data = {"Loja Norte", "000003", new BigDecimal("1500.00"), new BigDecimal("2500.00"), new BigDecimal("750.00")};
        Object[] store3Data = {"Loja Sul", "000004", new BigDecimal("800.00"), new BigDecimal("1200.00"), new BigDecimal("300.00")};
        
        List<Object[]> mockAggregatedData = Arrays.<Object[]>asList(store1Data, store2Data, store3Data);
        
        when(documentRepository.findStoreSalesOptimizedData(
            eq(validRequest.getStoreCodes()),
            eq("2025-01-15T00:00:00.000"),
            eq("2025-01-15T23:59:59.997"),
            eq(validRequest.getDanfeOrigin()),
            eq(validRequest.getPdvOrigin()),
            eq(validRequest.getExchangeOrigin()),
            eq(validRequest.getSellOperation()),
            eq(validRequest.getExchangeOperation())
        )).thenReturn(mockAggregatedData);
        
        // When
        List<StoreSalesReportDTO> result = storeSalesService.getStoreSalesReport(validRequest);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(3);
        
        // Verificar dados da Loja Centro
        StoreSalesReportDTO store1 = result.stream()
            .filter(dto -> "000002".equals(dto.getStoreCode()))
            .findFirst()
            .orElse(null);
        assertThat(store1).isNotNull();
        assertThat(store1.getStoreName()).isEqualTo("Loja Centro");
        assertThat(store1.getStoreCode()).isEqualTo("000002");
        assertThat(store1.getTroca3()).isEqualTo(new BigDecimal("1000.00"));
        assertThat(store1.getPdv()).isEqualTo(new BigDecimal("2000.00"));
        assertThat(store1.getDanfe()).isEqualTo(new BigDecimal("500.00"));
        
        // Verificar dados da Loja Norte
        StoreSalesReportDTO store2 = result.stream()
            .filter(dto -> "000003".equals(dto.getStoreCode()))
            .findFirst()
            .orElse(null);
        assertThat(store2).isNotNull();
        assertThat(store2.getStoreName()).isEqualTo("Loja Norte");
        assertThat(store2.getStoreCode()).isEqualTo("000003");
        assertThat(store2.getTroca3()).isEqualTo(new BigDecimal("1500.00"));
        assertThat(store2.getPdv()).isEqualTo(new BigDecimal("2500.00"));
        assertThat(store2.getDanfe()).isEqualTo(new BigDecimal("750.00"));
        
        // Verificar dados da Loja Sul
        StoreSalesReportDTO store3 = result.stream()
            .filter(dto -> "000004".equals(dto.getStoreCode()))
            .findFirst()
            .orElse(null);
        assertThat(store3).isNotNull();
        assertThat(store3.getStoreName()).isEqualTo("Loja Sul");
        assertThat(store3.getStoreCode()).isEqualTo("000004");
        assertThat(store3.getTroca3()).isEqualTo(new BigDecimal("800.00"));
        assertThat(store3.getPdv()).isEqualTo(new BigDecimal("1200.00"));
        assertThat(store3.getDanfe()).isEqualTo(new BigDecimal("300.00"));
    }
    
    @Test
    void shouldHandleStoresWithNoData() {
        // Given - Apenas uma loja retorna dados, as outras não
        Object[] store1Data = {"Loja Centro", "000002", new BigDecimal("1000.00"), new BigDecimal("2000.00"), new BigDecimal("500.00")};
        List<Object[]> mockAggregatedData = Arrays.<Object[]>asList(store1Data);
        
        when(documentRepository.findStoreSalesOptimizedData(any(), any(), any(), any(), any(), any(), any(), any()))
            .thenReturn(mockAggregatedData);
        
        // When
        List<StoreSalesReportDTO> result = storeSalesService.getStoreSalesReport(validRequest);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(3); // Todas as lojas solicitadas devem aparecer
        
        // Verificar que a loja com dados está presente
        StoreSalesReportDTO storeWithData = result.stream()
            .filter(dto -> "000002".equals(dto.getStoreCode()))
            .findFirst()
            .orElse(null);
        assertThat(storeWithData).isNotNull();
        assertThat(storeWithData.getTroca3()).isEqualTo(new BigDecimal("1000.00"));
        
        // Verificar que as lojas sem dados aparecem com valores zerados
        StoreSalesReportDTO storeWithoutData = result.stream()
            .filter(dto -> "000003".equals(dto.getStoreCode()))
            .findFirst()
            .orElse(null);
        assertThat(storeWithoutData).isNotNull();
        assertThat(storeWithoutData.getStoreName()).isEqualTo("Loja 000003");
        assertThat(storeWithoutData.getTroca3()).isEqualTo(BigDecimal.ZERO);
        assertThat(storeWithoutData.getPdv()).isEqualTo(BigDecimal.ZERO);
        assertThat(storeWithoutData.getDanfe()).isEqualTo(BigDecimal.ZERO);
    }
    
    @Test
    void shouldReturnEmptyListWhenRepositoryThrowsException() {
        // Given
        when(documentRepository.findStoreSalesOptimizedData(any(), any(), any(), any(), any(), any(), any(), any()))
            .thenThrow(new RuntimeException("Database connection error"));
        
        // When
        List<StoreSalesReportDTO> result = storeSalesService.getStoreSalesReport(validRequest);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }
    
    @Test
    void shouldThrowExceptionForNullRequest() {
        // When & Then
        assertThatThrownBy(() -> storeSalesService.getStoreSalesReport(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Request não pode ser nulo");
    }
    
    @Test
    void shouldThrowExceptionForInvalidStartDate() {
        // Given
        validRequest.setStartDate(null);
        
        // When & Then
        assertThatThrownBy(() -> storeSalesService.getStoreSalesReport(validRequest))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Data de início não pode ser nula ou vazia");
    }
    
    @Test
    void shouldThrowExceptionForInvalidEndDate() {
        // Given
        validRequest.setEndDate("invalid-date");
        
        // When & Then
        assertThatThrownBy(() -> storeSalesService.getStoreSalesReport(validRequest))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Data de fim deve estar no formato YYYY-MM-DD");
    }
    
    @Test
    void shouldThrowExceptionForEmptyStoreCodes() {
        // Given
        validRequest.setStoreCodes(Arrays.asList());
        
        // When & Then
        assertThatThrownBy(() -> storeSalesService.getStoreSalesReport(validRequest))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Lista de códigos de loja não pode ser nula ou vazia");
    }
    
    @Test
    void shouldFormatDatesCorrectlyForSqlServer() {
        // Given
        Object[] storeData = {"Loja Teste", "000001", BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO};
        List<Object[]> mockAggregatedData = Arrays.<Object[]>asList(storeData);
        
        when(documentRepository.findStoreSalesOptimizedData(
            eq(validRequest.getStoreCodes()),
            eq("2025-01-15T00:00:00.000"), // Verificar formato correto ISO 8601
            eq("2025-01-15T23:59:59.997"), // Verificar formato correto ISO 8601
            any(), any(), any(), any(), any()
        )).thenReturn(mockAggregatedData);
        
        // When
        storeSalesService.getStoreSalesReport(validRequest);
        
        // Then - Se chegou até aqui sem exceção, o formato das datas está correto
        // O teste verifica implicitamente que as datas foram formatadas corretamente
    }
    
    @Test
    void shouldThrowExceptionForInvalidDateFormat() {
        // Given
        validRequest.setStartDate("invalid-date-format");
        
        // When & Then
        assertThatThrownBy(() -> storeSalesService.getStoreSalesReport(validRequest))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Data de início deve estar no formato YYYY-MM-DD");
    }
    
    @Test
    void shouldThrowExceptionForNullDate() {
        // Given
        validRequest.setStartDate(null);
        
        // When & Then
        assertThatThrownBy(() -> storeSalesService.getStoreSalesReport(validRequest))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Data de início não pode ser nula ou vazia");
    }
}
