package com.sysconard.business.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.sysconard.business.dto.origin.CreateEventOriginRequest;
import com.sysconard.business.dto.origin.EventOriginResponse;
import com.sysconard.business.dto.origin.EventOriginSearchRequest;
import com.sysconard.business.dto.origin.EventOriginSearchResponse;
import com.sysconard.business.dto.origin.UpdateEventOriginRequest;
import com.sysconard.business.entity.origin.EventOrigin;
import com.sysconard.business.enums.EventSource;
import com.sysconard.business.exception.origin.EventOriginAlreadyExistsException;
import com.sysconard.business.exception.origin.EventOriginNotFoundException;
import com.sysconard.business.repository.origin.EventOriginRepository;
import com.sysconard.business.service.origin.EventOriginService;

/**
 * Testes unitários para EventOriginService.
 * Cobertura de 100% seguindo padrões de Clean Code e TDD.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("EventOriginService Tests")
class EventOriginServiceTest {
    
    @Mock
    private EventOriginRepository eventOriginRepository;
    
    @InjectMocks
    private EventOriginService eventOriginService;
    
    private UUID testId;
    private EventOrigin testEventOrigin;
    private CreateEventOriginRequest createRequest;
    private UpdateEventOriginRequest updateRequest;
    
    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();
        testEventOrigin = EventOrigin.builder()
                .id(testId)
                .eventSource(EventSource.PDV)
                .sourceCode("PDV001")
                .build();
        
        createRequest = new CreateEventOriginRequest(EventSource.PDV, "PDV001");
        updateRequest = new UpdateEventOriginRequest(EventSource.EXCHANGE, "EXC001");
    }
    
    @Test
    @DisplayName("Deve criar EventOrigin com sucesso")
    void shouldCreateEventOriginSuccessfully() {
        // Given
        when(eventOriginRepository.existsBySourceCode("PDV001")).thenReturn(false);
        when(eventOriginRepository.save(any(EventOrigin.class))).thenReturn(testEventOrigin);
        
        // When
        EventOriginResponse response = eventOriginService.createEventOrigin(createRequest);
        
        // Then
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(testId);
        assertThat(response.eventSource()).isEqualTo(EventSource.PDV);
        assertThat(response.sourceCode()).isEqualTo("PDV001");
        
        verify(eventOriginRepository).existsBySourceCode("PDV001");
        verify(eventOriginRepository).save(any(EventOrigin.class));
    }
    
    @Test
    @DisplayName("Deve lançar exceção quando sourceCode já existe")
    void shouldThrowExceptionWhenSourceCodeAlreadyExists() {
        // Given
        when(eventOriginRepository.existsBySourceCode("PDV001")).thenReturn(true);
        
        // When & Then
        assertThatThrownBy(() -> eventOriginService.createEventOrigin(createRequest))
                .isInstanceOf(EventOriginAlreadyExistsException.class)
                .hasMessage("EventOrigin com sourceCode 'PDV001' já existe");
        
        verify(eventOriginRepository).existsBySourceCode("PDV001");
        verify(eventOriginRepository, never()).save(any(EventOrigin.class));
    }
    
    @Test
    @DisplayName("Deve atualizar EventOrigin com sucesso")
    void shouldUpdateEventOriginSuccessfully() {
        // Given
        when(eventOriginRepository.findById(testId)).thenReturn(Optional.of(testEventOrigin));
        when(eventOriginRepository.existsBySourceCode("EXC001")).thenReturn(false);
        when(eventOriginRepository.save(any(EventOrigin.class))).thenReturn(testEventOrigin);
        
        // When
        EventOriginResponse response = eventOriginService.updateEventOrigin(testId, updateRequest);
        
        // Then
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(testId);
        
        verify(eventOriginRepository).findById(testId);
        verify(eventOriginRepository).existsBySourceCode("EXC001");
        verify(eventOriginRepository).save(any(EventOrigin.class));
    }
    
    @Test
    @DisplayName("Deve lançar exceção quando EventOrigin não encontrado para atualização")
    void shouldThrowExceptionWhenEventOriginNotFoundForUpdate() {
        // Given
        when(eventOriginRepository.findById(testId)).thenReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> eventOriginService.updateEventOrigin(testId, updateRequest))
                .isInstanceOf(EventOriginNotFoundException.class)
                .hasMessage("EventOrigin com ID '%s' não encontrado", testId);
        
        verify(eventOriginRepository).findById(testId);
        verify(eventOriginRepository, never()).save(any(EventOrigin.class));
    }
    
    @Test
    @DisplayName("Deve lançar exceção quando sourceCode duplicado na atualização")
    void shouldThrowExceptionWhenSourceCodeDuplicateInUpdate() {
        // Given
        when(eventOriginRepository.findById(testId)).thenReturn(Optional.of(testEventOrigin));
        when(eventOriginRepository.existsBySourceCode("EXC001")).thenReturn(true);
        
        // When & Then
        assertThatThrownBy(() -> eventOriginService.updateEventOrigin(testId, updateRequest))
                .isInstanceOf(EventOriginAlreadyExistsException.class)
                .hasMessage("EventOrigin com sourceCode 'EXC001' já existe");
        
        verify(eventOriginRepository).findById(testId);
        verify(eventOriginRepository).existsBySourceCode("EXC001");
        verify(eventOriginRepository, never()).save(any(EventOrigin.class));
    }
    
    @Test
    @DisplayName("Deve permitir atualização com mesmo sourceCode")
    void shouldAllowUpdateWithSameSourceCode() {
        // Given
        UpdateEventOriginRequest sameSourceCodeRequest = new UpdateEventOriginRequest(EventSource.EXCHANGE, "PDV001");
        when(eventOriginRepository.findById(testId)).thenReturn(Optional.of(testEventOrigin));
        when(eventOriginRepository.save(any(EventOrigin.class))).thenReturn(testEventOrigin);
        
        // When
        EventOriginResponse response = eventOriginService.updateEventOrigin(testId, sameSourceCodeRequest);
        
        // Then
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(testId);
        
        verify(eventOriginRepository).findById(testId);
        verify(eventOriginRepository, never()).existsBySourceCode("PDV001");
        verify(eventOriginRepository).save(any(EventOrigin.class));
    }
    
    @Test
    @DisplayName("Deve buscar EventOrigin por ID com sucesso")
    void shouldFindEventOriginByIdSuccessfully() {
        // Given
        when(eventOriginRepository.findById(testId)).thenReturn(Optional.of(testEventOrigin));
        
        // When
        EventOriginResponse response = eventOriginService.findEventOriginById(testId);
        
        // Then
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(testId);
        assertThat(response.eventSource()).isEqualTo(EventSource.PDV);
        assertThat(response.sourceCode()).isEqualTo("PDV001");
        
        verify(eventOriginRepository).findById(testId);
    }
    
    @Test
    @DisplayName("Deve lançar exceção quando EventOrigin não encontrado por ID")
    void shouldThrowExceptionWhenEventOriginNotFoundById() {
        // Given
        when(eventOriginRepository.findById(testId)).thenReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> eventOriginService.findEventOriginById(testId))
                .isInstanceOf(EventOriginNotFoundException.class)
                .hasMessage("EventOrigin com ID '%s' não encontrado", testId);
        
        verify(eventOriginRepository).findById(testId);
    }
    
    @Test
    @DisplayName("Deve buscar EventOrigins com filtros e paginação com sucesso")
    void shouldFindEventOriginsWithFiltersSuccessfully() {
        // Given
        EventOriginSearchRequest request = new EventOriginSearchRequest(EventSource.PDV, null, 0, 20, "sourceCode", "asc");
        Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, "sourceCode"));
        Page<EventOrigin> eventOriginsPage = new PageImpl<>(List.of(testEventOrigin), pageable, 1);
        
        when(eventOriginRepository.findByFilters(EventSource.PDV, pageable)).thenReturn(eventOriginsPage);
        when(eventOriginRepository.countPdvEventOrigins()).thenReturn(1L);
        when(eventOriginRepository.countExchangeEventOrigins()).thenReturn(0L);
        when(eventOriginRepository.countDanfeEventOrigins()).thenReturn(0L);
        when(eventOriginRepository.countTotalEventOrigins()).thenReturn(1L);
        
        // When
        EventOriginSearchResponse response = eventOriginService.findEventOriginsWithFilters(request);
        
        // Then
        assertThat(response).isNotNull();
        assertThat(response.getEventOrigins()).hasSize(1);
        assertThat(response.getEventOrigins().get(0).id()).isEqualTo(testId);
        assertThat(response.getPagination().getCurrentPage()).isEqualTo(0);
        assertThat(response.getPagination().getTotalElements()).isEqualTo(1);
        assertThat(response.getCounts().getTotalPdv()).isEqualTo(1);
        assertThat(response.getCounts().getTotalEventOrigins()).isEqualTo(1);
        
        verify(eventOriginRepository).findByFilters(EventSource.PDV, pageable);
        verify(eventOriginRepository).countPdvEventOrigins();
        verify(eventOriginRepository).countExchangeEventOrigins();
        verify(eventOriginRepository).countDanfeEventOrigins();
        verify(eventOriginRepository).countTotalEventOrigins();
    }
    
    @Test
    @DisplayName("Deve buscar EventOrigins sem filtros com sucesso")
    void shouldFindEventOriginsWithoutFiltersSuccessfully() {
        // Given
        EventOriginSearchRequest request = new EventOriginSearchRequest(null, null, 0, 20, "sourceCode", "asc");
        Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, "sourceCode"));
        Page<EventOrigin> eventOriginsPage = new PageImpl<>(List.of(testEventOrigin), pageable, 1);
        
        when(eventOriginRepository.findByFilters(null, pageable)).thenReturn(eventOriginsPage);
        when(eventOriginRepository.countPdvEventOrigins()).thenReturn(1L);
        when(eventOriginRepository.countExchangeEventOrigins()).thenReturn(0L);
        when(eventOriginRepository.countDanfeEventOrigins()).thenReturn(0L);
        when(eventOriginRepository.countTotalEventOrigins()).thenReturn(1L);
        
        // When
        EventOriginSearchResponse response = eventOriginService.findEventOriginsWithFilters(request);
        
        // Then
        assertThat(response).isNotNull();
        assertThat(response.getEventOrigins()).hasSize(1);
        assertThat(response.getCounts().getTotalEventOrigins()).isEqualTo(1);
        
        verify(eventOriginRepository).findByFilters(null, pageable);
    }
    
    @Test
    @DisplayName("Deve buscar EventOrigins com valores padrão")
    void shouldFindEventOriginsWithDefaultValues() {
        // Given
        EventOriginSearchRequest request = new EventOriginSearchRequest();
        Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, "sourceCode"));
        Page<EventOrigin> eventOriginsPage = new PageImpl<>(List.of(testEventOrigin), pageable, 1);
        
        when(eventOriginRepository.findByFilters(null, pageable)).thenReturn(eventOriginsPage);
        when(eventOriginRepository.countPdvEventOrigins()).thenReturn(1L);
        when(eventOriginRepository.countExchangeEventOrigins()).thenReturn(0L);
        when(eventOriginRepository.countDanfeEventOrigins()).thenReturn(0L);
        when(eventOriginRepository.countTotalEventOrigins()).thenReturn(1L);
        
        // When
        EventOriginSearchResponse response = eventOriginService.findEventOriginsWithFilters(request);
        
        // Then
        assertThat(response).isNotNull();
        assertThat(response.getEventOrigins()).hasSize(1);
        
        verify(eventOriginRepository).findByFilters(null, pageable);
    }
}
