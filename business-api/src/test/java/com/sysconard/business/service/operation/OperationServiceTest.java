package com.sysconard.business.service.operation;

import com.sysconard.business.repository.operation.OperationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testes unitários para OperationService.
 * Seguindo princípios de Clean Code com testes focados e bem estruturados.
 */
@ExtendWith(MockitoExtension.class)
class OperationServiceTest {
    
    @Mock
    private OperationRepository operationRepository;
    
    @InjectMocks
    private OperationService operationService;
    
    private List<String> expectedCodes;
    
    @BeforeEach
    void setUp() {
        expectedCodes = Arrays.asList("000001", "000002", "000003");
    }
    
    /**
     * Teste para verificar se o método getAllOperationCodes() retorna todos os códigos corretamente.
     */
    @Test
    void shouldGetAllOperationCodesSuccessfully() {
        // Given
        when(operationRepository.findAllOperationCodes()).thenReturn(expectedCodes);
        
        // When
        List<String> codes = operationService.getAllOperationCodes();
        
        // Then
        assertThat(codes).isNotNull();
        assertThat(codes).hasSize(3);
        assertThat(codes).containsExactlyInAnyOrder("000001", "000002", "000003");
        verify(operationRepository).findAllOperationCodes();
    }
    
    /**
     * Teste para verificar se o método getAllOperationCodes() retorna lista vazia quando não há operações.
     */
    @Test
    void shouldReturnEmptyListWhenNoOperationsExist() {
        // Given
        when(operationRepository.findAllOperationCodes()).thenReturn(Arrays.asList());
        
        // When
        List<String> codes = operationService.getAllOperationCodes();
        
        // Then
        assertThat(codes).isNotNull();
        assertThat(codes).isEmpty();
        verify(operationRepository).findAllOperationCodes();
    }
    
    /**
     * Teste para verificar se o método getAllOperationCodes() retorna null quando repository retorna null.
     */
    @Test
    void shouldHandleNullResponseFromRepository() {
        // Given
        when(operationRepository.findAllOperationCodes()).thenReturn(null);
        
        // When
        List<String> codes = operationService.getAllOperationCodes();
        
        // Then
        assertThat(codes).isNull();
        verify(operationRepository).findAllOperationCodes();
    }
}
