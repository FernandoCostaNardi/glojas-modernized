package com.sysconard.legacy.service;

import com.sysconard.legacy.dto.StoreSalesReportByDayDTO;
import com.sysconard.legacy.dto.StoreSalesReportRequestDTO;
import com.sysconard.legacy.repository.DocumentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para StoreSalesService - funcionalidade por dia.
 * 
 * @author Sysconard Legacy API
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
class StoreSalesServiceByDayTest {
    
    @Mock
    private DocumentRepository documentRepository;
    
    @InjectMocks
    private StoreSalesService storeSalesService;
    
    private StoreSalesReportRequestDTO validRequest;
    
    @BeforeEach
    void setUp() {
        validRequest = StoreSalesReportRequestDTO.builder()
            .startDate("2025-01-15")
            .endDate("2025-01-17")
            .storeCodes(Arrays.asList("000002", "000003"))
            .danfeOrigin(Arrays.asList("015", "002"))
            .pdvOrigin(Arrays.asList("009"))
            .exchangeOrigin(Arrays.asList("051", "065"))
            .sellOperation(Arrays.asList("000999", "000007", "000001"))
            .exchangeOperation(Arrays.asList("000015", "000048"))
            .build();
    }
    
    @Test
    void shouldGenerateReportByDaySuccessfully() {
        // Given
        Object[] day1Data = {"Loja Centro", "000002", java.sql.Date.valueOf("2025-01-15"), 
                           new BigDecimal("1000.00"), new BigDecimal("2000.00"), new BigDecimal("500.00")};
        Object[] day2Data = {"Loja Centro", "000002", java.sql.Date.valueOf("2025-01-16"), 
                           new BigDecimal("1200.00"), new BigDecimal("1800.00"), new BigDecimal("600.00")};
        Object[] day3Data = {"Loja Norte", "000003", java.sql.Date.valueOf("2025-01-15"), 
                           new BigDecimal("1500.00"), new BigDecimal("2500.00"), new BigDecimal("750.00")};
        
        List<Object[]> mockAggregatedData = Arrays.<Object[]>asList(day1Data, day2Data, day3Data);
        
        when(documentRepository.findStoreSalesByDayOptimizedData(
            eq(validRequest.getStoreCodes()),
            eq("2025-01-15T00:00:00.000"),
            eq("2025-01-17T23:59:59.997"),
            eq(validRequest.getDanfeOrigin()), eq(validRequest.getPdvOrigin()), eq(validRequest.getExchangeOrigin()),
            eq(validRequest.getSellOperation()), eq(validRequest.getExchangeOperation())))
            .thenReturn(mockAggregatedData);
        
        // When
        List<StoreSalesReportByDayDTO> result = storeSalesService.getStoreSalesReportByDay(validRequest);
        
        // Then
        assertNotNull(result);
        assertEquals(3, result.size());
        
        // Verificar ordenação por loja e depois por data
        StoreSalesReportByDayDTO dto1 = result.get(0); // Loja Centro - 2025-01-15
        assertEquals("000002", dto1.getStoreCode());
        assertEquals("Loja Centro", dto1.getStoreName());
        assertEquals(LocalDate.of(2025, 1, 15), dto1.getReportDate());
        assertEquals(new BigDecimal("500.00"), dto1.getDanfe());
        assertEquals(new BigDecimal("2000.00"), dto1.getPdv());
        assertEquals(new BigDecimal("1000.00"), dto1.getTroca3());
        
        StoreSalesReportByDayDTO dto2 = result.get(1); // Loja Centro - 2025-01-16
        assertEquals("000002", dto2.getStoreCode());
        assertEquals("Loja Centro", dto2.getStoreName());
        assertEquals(LocalDate.of(2025, 1, 16), dto2.getReportDate());
        assertEquals(new BigDecimal("600.00"), dto2.getDanfe());
        assertEquals(new BigDecimal("1800.00"), dto2.getPdv());
        assertEquals(new BigDecimal("1200.00"), dto2.getTroca3());
        
        StoreSalesReportByDayDTO dto3 = result.get(2); // Loja Norte - 2025-01-15
        assertEquals("000003", dto3.getStoreCode());
        assertEquals("Loja Norte", dto3.getStoreName());
        assertEquals(LocalDate.of(2025, 1, 15), dto3.getReportDate());
        assertEquals(new BigDecimal("750.00"), dto3.getDanfe());
        assertEquals(new BigDecimal("2500.00"), dto3.getPdv());
        assertEquals(new BigDecimal("1500.00"), dto3.getTroca3());
        
        verify(documentRepository, times(1)).findStoreSalesByDayOptimizedData(any(), any(), any(), any(), any(), any(), any(), any());
    }
    
    @Test
    void shouldReturnEmptyListWhenNoDataFound() {
        // Given
        when(documentRepository.findStoreSalesByDayOptimizedData(any(), any(), any(), any(), any(), any(), any(), any()))
            .thenReturn(Collections.emptyList());
        
        // When
        List<StoreSalesReportByDayDTO> result = storeSalesService.getStoreSalesReportByDay(validRequest);
        
        // Then
        assertNotNull(result);
        assertEquals(0, result.size());
        
        verify(documentRepository, times(1)).findStoreSalesByDayOptimizedData(any(), any(), any(), any(), any(), any(), any(), any());
    }
    
    @Test
    void shouldReturnEmptyListWhenRepositoryThrowsException() {
        // Given
        when(documentRepository.findStoreSalesByDayOptimizedData(any(), any(), any(), any(), any(), any(), any(), any()))
            .thenThrow(new RuntimeException("Database connection error"));
        
        // When
        List<StoreSalesReportByDayDTO> result = storeSalesService.getStoreSalesReportByDay(validRequest);
        
        // Then
        assertNotNull(result);
        assertEquals(0, result.size());
        
        verify(documentRepository, times(1)).findStoreSalesByDayOptimizedData(any(), any(), any(), any(), any(), any(), any(), any());
    }
    
    @Test
    void shouldThrowExceptionWhenRequestIsNull() {
        // When & Then
        assertThatThrownBy(() -> storeSalesService.getStoreSalesReportByDay(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Request não pode ser nulo");
    }
    
    @Test
    void shouldThrowExceptionWhenStoreCodesIsEmpty() {
        // Given
        validRequest.setStoreCodes(Collections.emptyList());
        
        // When & Then
        assertThatThrownBy(() -> storeSalesService.getStoreSalesReportByDay(validRequest))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Lista de códigos de loja não pode ser nula ou vazia");
    }
    
    @Test
    void shouldThrowExceptionWhenStartDateIsInvalid() {
        // Given
        validRequest.setStartDate("invalid-date-format");
        
        // When & Then
        assertThatThrownBy(() -> storeSalesService.getStoreSalesReportByDay(validRequest))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Data de início deve estar no formato YYYY-MM-DD");
    }
    
    @Test
    void shouldHandleDifferentDateTypesFromDatabase() {
        // Given - Testando diferentes tipos de data que podem vir do SQL Server
        Object[] stringDateData = {"Loja Teste", "000001", "2025-01-15", 
                                 new BigDecimal("100.00"), new BigDecimal("200.00"), new BigDecimal("50.00")};
        Object[] sqlDateData = {"Loja Teste", "000001", java.sql.Date.valueOf("2025-01-16"), 
                              new BigDecimal("150.00"), new BigDecimal("250.00"), new BigDecimal("75.00")};
        
        List<Object[]> mockAggregatedData = Arrays.<Object[]>asList(stringDateData, sqlDateData);
        
        when(documentRepository.findStoreSalesByDayOptimizedData(any(), any(), any(), any(), any(), any(), any(), any()))
            .thenReturn(mockAggregatedData);
        
        // When
        List<StoreSalesReportByDayDTO> result = storeSalesService.getStoreSalesReportByDay(validRequest);
        
        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        
        // Verificar que ambas as datas foram convertidas corretamente
        assertEquals(LocalDate.of(2025, 1, 15), result.get(0).getReportDate());
        assertEquals(LocalDate.of(2025, 1, 16), result.get(1).getReportDate());
    }
    
    @Test
    void shouldCalculateTotalCorrectly() {
        // Given
        Object[] storeData = {"Loja Teste", "000001", java.sql.Date.valueOf("2025-01-15"), 
                            new BigDecimal("100.00"), new BigDecimal("200.00"), new BigDecimal("50.00")};
        List<Object[]> mockAggregatedData = Arrays.<Object[]>asList(storeData);
        
        when(documentRepository.findStoreSalesByDayOptimizedData(any(), any(), any(), any(), any(), any(), any(), any()))
            .thenReturn(mockAggregatedData);
        
        // When
        List<StoreSalesReportByDayDTO> result = storeSalesService.getStoreSalesReportByDay(validRequest);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        
        StoreSalesReportByDayDTO dto = result.get(0);
        BigDecimal expectedTotal = new BigDecimal("100.00").add(new BigDecimal("200.00")).add(new BigDecimal("50.00"));
        assertEquals(expectedTotal, dto.getTotal());
    }
    
    @Test
    void shouldFormatDatesCorrectlyForSqlServer() {
        // Given
        Object[] storeData = {"Loja Teste", "000001", java.sql.Date.valueOf("2025-01-15"), 
                            BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO};
        List<Object[]> mockAggregatedData = Arrays.<Object[]>asList(storeData);
        
        when(documentRepository.findStoreSalesByDayOptimizedData(
            eq(validRequest.getStoreCodes()),
            eq("2025-01-15T00:00:00.000"), // Verificar formato correto ISO 8601
            eq("2025-01-17T23:59:59.997"), // Verificar formato correto ISO 8601
            any(), any(), any(), any(), any()
        )).thenReturn(mockAggregatedData);
        
        // When
        storeSalesService.getStoreSalesReportByDay(validRequest);
        
        // Then - Se chegou até aqui sem exceção, o formato das datas está correto
        // O teste verifica implicitamente que as datas foram formatadas corretamente
    }
}
