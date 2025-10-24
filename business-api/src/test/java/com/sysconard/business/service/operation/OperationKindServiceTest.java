package com.sysconard.business.service.operation;

import com.sysconard.business.client.LegacyApiClient;
import com.sysconard.business.dto.operation.OperationKindDto;
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
 * Testes unitários para OperationKindService.
 * Seguindo princípios de Clean Code com testes focados e bem estruturados.
 */
@ExtendWith(MockitoExtension.class)
class OperationKindServiceTest {
    
    @Mock
    private LegacyApiClient legacyApiClient;
    
    @Mock
    private OperationService operationService;
    
    @InjectMocks
    private OperationKindService operationKindService;
    
    private List<OperationKindDto> allKinds;
    private List<String> registeredCodes;
    
    @BeforeEach
    void setUp() {
        allKinds = Arrays.asList(
            new OperationKindDto("000001", "Venda à Vista"),
            new OperationKindDto("000002", "Venda a Prazo"),
            new OperationKindDto("000003", "Troca"),
            new OperationKindDto("000004", "Devolução")
        );
        
        registeredCodes = Arrays.asList("000001", "000002");
    }
    
    /**
     * Teste para verificar se o método getAllOperationKinds() retorna apenas os tipos disponíveis.
     */
    @Test
    void shouldReturnOnlyAvailableOperationKinds() {
        // Given
        when(legacyApiClient.getOperationKinds()).thenReturn(allKinds);
        when(operationService.getAllOperationCodes()).thenReturn(registeredCodes);
        
        // When
        List<OperationKindDto> availableKinds = operationKindService.getAllOperationKinds();
        
        // Then
        assertThat(availableKinds).isNotNull();
        assertThat(availableKinds).hasSize(2);
        
        // Verificar se contém apenas os tipos não cadastrados
        assertThat(availableKinds.stream().map(OperationKindDto::id))
            .containsExactlyInAnyOrder("000003", "000004");
        
        // Verificar se não contém os tipos já cadastrados
        assertThat(availableKinds.stream().map(OperationKindDto::id))
            .doesNotContain("000001", "000002");
        
        verify(legacyApiClient).getOperationKinds();
        verify(operationService).getAllOperationCodes();
    }
    
    /**
     * Teste para verificar se o método getAllOperationKinds() retorna todos os tipos quando nenhum está cadastrado.
     */
    @Test
    void shouldReturnAllOperationKindsWhenNoneAreRegistered() {
        // Given
        when(legacyApiClient.getOperationKinds()).thenReturn(allKinds);
        when(operationService.getAllOperationCodes()).thenReturn(Arrays.asList());
        
        // When
        List<OperationKindDto> availableKinds = operationKindService.getAllOperationKinds();
        
        // Then
        assertThat(availableKinds).isNotNull();
        assertThat(availableKinds).hasSize(4);
        assertThat(availableKinds).containsExactlyInAnyOrderElementsOf(allKinds);
        
        verify(legacyApiClient).getOperationKinds();
        verify(operationService).getAllOperationCodes();
    }
    
    /**
     * Teste para verificar se o método getAllOperationKinds() retorna lista vazia quando todos estão cadastrados.
     */
    @Test
    void shouldReturnEmptyListWhenAllOperationKindsAreRegistered() {
        // Given
        when(legacyApiClient.getOperationKinds()).thenReturn(allKinds);
        when(operationService.getAllOperationCodes()).thenReturn(
            Arrays.asList("000001", "000002", "000003", "000004")
        );
        
        // When
        List<OperationKindDto> availableKinds = operationKindService.getAllOperationKinds();
        
        // Then
        assertThat(availableKinds).isNotNull();
        assertThat(availableKinds).isEmpty();
        
        verify(legacyApiClient).getOperationKinds();
        verify(operationService).getAllOperationCodes();
    }
    
    /**
     * Teste para verificar se o método getAllOperationKinds() retorna lista vazia quando Legacy API retorna null.
     */
    @Test
    void shouldReturnEmptyListWhenLegacyApiReturnsNull() {
        // Given
        when(legacyApiClient.getOperationKinds()).thenReturn(null);
        when(operationService.getAllOperationCodes()).thenReturn(registeredCodes);
        
        // When
        List<OperationKindDto> availableKinds = operationKindService.getAllOperationKinds();
        
        // Then
        assertThat(availableKinds).isNotNull();
        assertThat(availableKinds).isEmpty();
        
        verify(legacyApiClient).getOperationKinds();
        verify(operationService).getAllOperationCodes();
    }
    
    /**
     * Teste para verificar se o método getAllOperationKinds() retorna lista vazia quando Legacy API retorna lista vazia.
     */
    @Test
    void shouldReturnEmptyListWhenLegacyApiReturnsEmptyList() {
        // Given
        when(legacyApiClient.getOperationKinds()).thenReturn(Arrays.asList());
        when(operationService.getAllOperationCodes()).thenReturn(registeredCodes);
        
        // When
        List<OperationKindDto> availableKinds = operationKindService.getAllOperationKinds();
        
        // Then
        assertThat(availableKinds).isNotNull();
        assertThat(availableKinds).isEmpty();
        
        verify(legacyApiClient).getOperationKinds();
        verify(operationService).getAllOperationCodes();
    }
}
