package com.sysconard.business.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.sysconard.business.entity.origin.EventOrigin;
import com.sysconard.business.enums.EventSource;
import com.sysconard.business.repository.origin.EventOriginRepository;

/**
 * Testes de integração para EventOriginRepository.
 * Cobertura de 100% seguindo padrões de Clean Code e TDD.
 */
@DataJpaTest
@DisplayName("EventOriginRepository Tests")
class EventOriginRepositoryTest {
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Autowired
    private EventOriginRepository eventOriginRepository;
    
    private EventOrigin testEventOrigin1;
    private EventOrigin testEventOrigin2;
    private EventOrigin testEventOrigin3;
    
    @BeforeEach
    void setUp() {
        // Limpar dados antes de cada teste
        eventOriginRepository.deleteAll();
        
        // Criar EventOrigins de teste
        testEventOrigin1 = EventOrigin.builder()
                .eventSource(EventSource.PDV)
                .sourceCode("PDV001")
                .build();
        
        testEventOrigin2 = EventOrigin.builder()
                .eventSource(EventSource.EXCHANGE)
                .sourceCode("EXC001")
                .build();
        
        testEventOrigin3 = EventOrigin.builder()
                .eventSource(EventSource.DANFE)
                .sourceCode("DAN001")
                .build();
        
        // Persistir no banco
        entityManager.persistAndFlush(testEventOrigin1);
        entityManager.persistAndFlush(testEventOrigin2);
        entityManager.persistAndFlush(testEventOrigin3);
        entityManager.clear();
    }
    
    @Test
    @DisplayName("Deve verificar se sourceCode existe")
    void shouldCheckIfSourceCodeExists() {
        // When & Then
        assertThat(eventOriginRepository.existsBySourceCode("PDV001")).isTrue();
        assertThat(eventOriginRepository.existsBySourceCode("EXC001")).isTrue();
        assertThat(eventOriginRepository.existsBySourceCode("DAN001")).isTrue();
        assertThat(eventOriginRepository.existsBySourceCode("NONEXISTENT")).isFalse();
    }
    
    @Test
    @DisplayName("Deve buscar EventOrigin por sourceCode")
    void shouldFindEventOriginBySourceCode() {
        // When
        Optional<EventOrigin> found = eventOriginRepository.findBySourceCode("PDV001");
        
        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getEventSource()).isEqualTo(EventSource.PDV);
        assertThat(found.get().getSourceCode()).isEqualTo("PDV001");
    }
    
    @Test
    @DisplayName("Deve retornar empty quando sourceCode não encontrado")
    void shouldReturnEmptyWhenSourceCodeNotFound() {
        // When
        Optional<EventOrigin> found = eventOriginRepository.findBySourceCode("NONEXISTENT");
        
        // Then
        assertThat(found).isEmpty();
    }
    
    @Test
    @DisplayName("Deve buscar EventOrigins por EventSource com paginação")
    void shouldFindEventOriginsByEventSourceWithPagination() {
        // Given
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "sourceCode"));
        
        // When
        Page<EventOrigin> pdvPage = eventOriginRepository.findByEventSource(EventSource.PDV, pageable);
        Page<EventOrigin> exchangePage = eventOriginRepository.findByEventSource(EventSource.EXCHANGE, pageable);
        Page<EventOrigin> danfePage = eventOriginRepository.findByEventSource(EventSource.DANFE, pageable);
        
        // Then
        assertThat(pdvPage.getContent()).hasSize(1);
        assertThat(pdvPage.getContent().get(0).getSourceCode()).isEqualTo("PDV001");
        
        assertThat(exchangePage.getContent()).hasSize(1);
        assertThat(exchangePage.getContent().get(0).getSourceCode()).isEqualTo("EXC001");
        
        assertThat(danfePage.getContent()).hasSize(1);
        assertThat(danfePage.getContent().get(0).getSourceCode()).isEqualTo("DAN001");
    }
    
    @Test
    @DisplayName("Deve contar EventOrigins por EventSource")
    void shouldCountEventOriginsByEventSource() {
        // When & Then
        assertThat(eventOriginRepository.countByEventSource(EventSource.PDV)).isEqualTo(1);
        assertThat(eventOriginRepository.countByEventSource(EventSource.EXCHANGE)).isEqualTo(1);
        assertThat(eventOriginRepository.countByEventSource(EventSource.DANFE)).isEqualTo(1);
    }
    
    @Test
    @DisplayName("Deve contar EventOrigins PDV")
    void shouldCountPdvEventOrigins() {
        // When & Then
        assertThat(eventOriginRepository.countPdvEventOrigins()).isEqualTo(1);
    }
    
    @Test
    @DisplayName("Deve contar EventOrigins EXCHANGE")
    void shouldCountExchangeEventOrigins() {
        // When & Then
        assertThat(eventOriginRepository.countExchangeEventOrigins()).isEqualTo(1);
    }
    
    @Test
    @DisplayName("Deve contar EventOrigins DANFE")
    void shouldCountDanfeEventOrigins() {
        // When & Then
        assertThat(eventOriginRepository.countDanfeEventOrigins()).isEqualTo(1);
    }
    
    @Test
    @DisplayName("Deve contar total de EventOrigins")
    void shouldCountTotalEventOrigins() {
        // When & Then
        assertThat(eventOriginRepository.countTotalEventOrigins()).isEqualTo(3);
    }
    
    @Test
    @DisplayName("Deve buscar EventOrigins com filtros dinâmicos")
    void shouldFindEventOriginsWithDynamicFilters() {
        // Given
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "sourceCode"));
        
        // When - Busca com filtro PDV
        Page<EventOrigin> pdvPage = eventOriginRepository.findByFilters(EventSource.PDV, pageable);
        
        // When - Busca sem filtro
        Page<EventOrigin> allPage = eventOriginRepository.findByFilters(null, pageable);
        
        // Then
        assertThat(pdvPage.getContent()).hasSize(1);
        assertThat(pdvPage.getContent().get(0).getEventSource()).isEqualTo(EventSource.PDV);
        
        assertThat(allPage.getContent()).hasSize(3);
        assertThat(allPage.getTotalElements()).isEqualTo(3);
    }
    
    @Test
    @DisplayName("Deve buscar EventOrigins com paginação")
    void shouldFindEventOriginsWithPagination() {
        // Given
        Pageable pageable = PageRequest.of(0, 2, Sort.by(Sort.Direction.ASC, "sourceCode"));
        
        // When
        Page<EventOrigin> page = eventOriginRepository.findByFilters(null, pageable);
        
        // Then
        assertThat(page.getContent()).hasSize(2);
        assertThat(page.getTotalElements()).isEqualTo(3);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.hasNext()).isTrue();
        assertThat(page.hasPrevious()).isFalse();
    }
    
    @Test
    @DisplayName("Deve buscar EventOrigins com ordenação")
    void shouldFindEventOriginsWithSorting() {
        // Given
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "sourceCode"));
        
        // When
        Page<EventOrigin> page = eventOriginRepository.findByFilters(null, pageable);
        
        // Then
        assertThat(page.getContent()).hasSize(3);
        assertThat(page.getContent().get(0).getSourceCode()).isEqualTo("PDV001");
        assertThat(page.getContent().get(1).getSourceCode()).isEqualTo("EXC001");
        assertThat(page.getContent().get(2).getSourceCode()).isEqualTo("DAN001");
    }
    
    @Test
    @DisplayName("Deve salvar e buscar EventOrigin")
    void shouldSaveAndFindEventOrigin() {
        // Given
        EventOrigin newEventOrigin = EventOrigin.builder()
                .eventSource(EventSource.PDV)
                .sourceCode("PDV002")
                .build();
        
        // When
        EventOrigin saved = eventOriginRepository.save(newEventOrigin);
        Optional<EventOrigin> found = eventOriginRepository.findById(saved.getId());
        
        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(found).isPresent();
        assertThat(found.get().getSourceCode()).isEqualTo("PDV002");
        assertThat(found.get().getEventSource()).isEqualTo(EventSource.PDV);
    }
    
    @Test
    @DisplayName("Deve deletar EventOrigin")
    void shouldDeleteEventOrigin() {
        // Given
        UUID id = testEventOrigin1.getId();
        
        // When
        eventOriginRepository.deleteById(id);
        Optional<EventOrigin> found = eventOriginRepository.findById(id);
        
        // Then
        assertThat(found).isEmpty();
        assertThat(eventOriginRepository.countTotalEventOrigins()).isEqualTo(2);
    }
}
