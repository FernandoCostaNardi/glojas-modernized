package com.sysconard.business.service.store;

import com.sysconard.business.client.LegacyApiClient;
import com.sysconard.business.dto.store.StoreResponseDto;
import com.sysconard.business.entity.store.Store;
import com.sysconard.business.repository.store.StoreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para o StoreService.
 * Valida a lógica de negócio para gestão de lojas.
 * Segue princípios de Clean Code com testes bem estruturados.
 */
@ExtendWith(MockitoExtension.class)
class StoreServiceTest {
    
    @Mock
    private StoreRepository storeRepository;
    
    @Mock
    private LegacyApiClient legacyApiClient;
    
    @InjectMocks
    private StoreService storeService;
    
    private List<Store> registeredStores;
    private List<StoreResponseDto> legacyStores;
    
    @BeforeEach
    void setUp() {
        // Setup lojas cadastradas
        registeredStores = Arrays.asList(
            Store.builder()
                .id(UUID.randomUUID())
                .code("000001")
                .name("Loja Centro")
                .city("São Paulo")
                .status(true)
                .build(),
            Store.builder()
                .id(UUID.randomUUID())
                .code("000002")
                .name("Loja Norte")
                .city("São Paulo")
                .status(true)
                .build()
        );
        
        // Setup lojas da Legacy API
        legacyStores = Arrays.asList(
            StoreResponseDto.builder()
                .id("000001")
                .name("Loja Centro")
                .city("São Paulo")
                .build(),
            StoreResponseDto.builder()
                .id("000002")
                .name("Loja Norte")
                .city("São Paulo")
                .build(),
            StoreResponseDto.builder()
                .id("000003")
                .name("Loja Sul")
                .city("São Paulo")
                .build(),
            StoreResponseDto.builder()
                .id("000004")
                .name("Loja Oeste")
                .city("São Paulo")
                .build()
        );
    }
    
    @Test
    void shouldReturnUnregisteredStoresSuccessfully() {
        // Given
        when(storeRepository.findAll()).thenReturn(registeredStores);
        when(legacyApiClient.getStores()).thenReturn(legacyStores);
        
        // When
        List<StoreResponseDto> result = storeService.getUnregisteredStores();
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result).extracting(StoreResponseDto::getId)
                .containsExactly("000003", "000004");
        assertThat(result).extracting(StoreResponseDto::getName)
                .containsExactly("Loja Sul", "Loja Oeste");
        
        verify(storeRepository).findAll();
        verify(legacyApiClient).getStores();
    }
    
    @Test
    void shouldReturnEmptyListWhenAllLegacyStoresAreRegistered() {
        // Given
        List<StoreResponseDto> allRegisteredLegacyStores = Arrays.asList(
            StoreResponseDto.builder()
                .id("000001")
                .name("Loja Centro")
                .city("São Paulo")
                .build(),
            StoreResponseDto.builder()
                .id("000002")
                .name("Loja Norte")
                .city("São Paulo")
                .build()
        );
        
        when(storeRepository.findAll()).thenReturn(registeredStores);
        when(legacyApiClient.getStores()).thenReturn(allRegisteredLegacyStores);
        
        // When
        List<StoreResponseDto> result = storeService.getUnregisteredStores();
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
        
        verify(storeRepository).findAll();
        verify(legacyApiClient).getStores();
    }
    
    @Test
    void shouldReturnAllLegacyStoresWhenNoStoresAreRegistered() {
        // Given
        when(storeRepository.findAll()).thenReturn(Arrays.asList());
        when(legacyApiClient.getStores()).thenReturn(legacyStores);
        
        // When
        List<StoreResponseDto> result = storeService.getUnregisteredStores();
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(4);
        assertThat(result).extracting(StoreResponseDto::getId)
                .containsExactly("000001", "000002", "000003", "000004");
        
        verify(storeRepository).findAll();
        verify(legacyApiClient).getStores();
    }
    
    @Test
    void shouldReturnEmptyListWhenLegacyApiReturnsNull() {
        // Given
        when(storeRepository.findAll()).thenReturn(registeredStores);
        when(legacyApiClient.getStores()).thenReturn(null);
        
        // When
        List<StoreResponseDto> result = storeService.getUnregisteredStores();
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
        
        verify(storeRepository).findAll();
        verify(legacyApiClient).getStores();
    }
    
    @Test
    void shouldReturnEmptyListWhenLegacyApiReturnsEmptyList() {
        // Given
        when(storeRepository.findAll()).thenReturn(registeredStores);
        when(legacyApiClient.getStores()).thenReturn(Arrays.asList());
        
        // When
        List<StoreResponseDto> result = storeService.getUnregisteredStores();
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
        
        verify(storeRepository).findAll();
        verify(legacyApiClient).getStores();
    }
    
    @Test
    void shouldThrowRuntimeExceptionWhenLegacyApiClientFails() {
        // Given
        when(storeRepository.findAll()).thenReturn(registeredStores);
        when(legacyApiClient.getStores()).thenThrow(new RuntimeException("Legacy API error"));
        
        // When & Then
        assertThatThrownBy(() -> storeService.getUnregisteredStores())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Erro ao buscar lojas não cadastradas")
                .hasMessageContaining("Legacy API error");
        
        verify(storeRepository).findAll();
        verify(legacyApiClient).getStores();
    }
    
    @Test
    void shouldHandleMixedCaseStoreCodes() {
        // Given
        List<Store> storesWithMixedCodes = Arrays.asList(
            Store.builder()
                .id(UUID.randomUUID())
                .code("000001")
                .name("Loja Centro")
                .city("São Paulo")
                .status(true)
                .build()
        );
        
        List<StoreResponseDto> legacyStoresWithMixedCodes = Arrays.asList(
            StoreResponseDto.builder()
                .id("000001")
                .name("Loja Centro")
                .city("São Paulo")
                .build(),
            StoreResponseDto.builder()
                .id("000002")
                .name("Loja Norte")
                .city("São Paulo")
                .build()
        );
        
        when(storeRepository.findAll()).thenReturn(storesWithMixedCodes);
        when(legacyApiClient.getStores()).thenReturn(legacyStoresWithMixedCodes);
        
        // When
        List<StoreResponseDto> result = storeService.getUnregisteredStores();
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo("000002");
        
        verify(storeRepository).findAll();
        verify(legacyApiClient).getStores();
    }
}
