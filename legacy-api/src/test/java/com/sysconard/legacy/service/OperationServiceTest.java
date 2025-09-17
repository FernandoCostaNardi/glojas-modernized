package com.sysconard.legacy.service;

import com.sysconard.legacy.dto.OperationDTO;
import com.sysconard.legacy.entity.Operation;
import com.sysconard.legacy.repository.OperationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Testes unitários para OperationService
 * 
 * @author Sysconard Legacy API
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
class OperationServiceTest {
    
    @Mock
    private OperationRepository operationRepository;
    
    @InjectMocks
    private OperationService operationService;
    
    private Operation operation1;
    private Operation operation2;
    
    @BeforeEach
    void setUp() {
        operation1 = new Operation();
        operation1.setId(1L);
        operation1.setDescription("Operação de Teste 1");
        
        operation2 = new Operation();
        operation2.setId(123L);
        operation2.setDescription("Operação de Teste 2");
    }
    
    @Test
    void shouldFormatOperationIdWithSixDigits() {
        // Given
        when(operationRepository.findById(1L)).thenReturn(Optional.of(operation1));
        
        // When
        OperationDTO result = operationService.findOperationById(1L);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("000001");
        assertThat(result.getDescription()).isEqualTo("Operação de Teste 1");
    }
    
    @Test
    void shouldFormatOperationIdWithSixDigitsForLargerNumbers() {
        // Given
        when(operationRepository.findById(123L)).thenReturn(Optional.of(operation2));
        
        // When
        OperationDTO result = operationService.findOperationById(123L);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("000123");
        assertThat(result.getDescription()).isEqualTo("Operação de Teste 2");
    }
    
    @Test
    void shouldFormatAllOperationsWithSixDigits() {
        // Given
        List<Operation> operations = Arrays.asList(operation1, operation2);
        when(operationRepository.findAll()).thenReturn(operations);
        
        // When
        List<OperationDTO> results = operationService.findAllOperations();
        
        // Then
        assertThat(results).hasSize(2);
        
        OperationDTO result1 = results.get(0);
        assertThat(result1.getId()).isEqualTo("000001");
        
        OperationDTO result2 = results.get(1);
        assertThat(result2.getId()).isEqualTo("000123");
    }
}
