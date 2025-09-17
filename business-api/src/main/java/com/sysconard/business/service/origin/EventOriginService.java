package com.sysconard.business.service.origin;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Serviço para operações de negócio relacionadas a EventOrigin.
 * Implementa lógica de negócio seguindo princípios de Clean Code com logging estruturado.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class EventOriginService {
    
    private final EventOriginRepository eventOriginRepository;
    
    /**
     * Cria um novo EventOrigin no sistema.
     * 
     * @param request Dados do EventOrigin a ser criado
     * @return Dados do EventOrigin criado
     * @throws EventOriginAlreadyExistsException se o sourceCode já existir
     */
    public EventOriginResponse createEventOrigin(CreateEventOriginRequest request) {
        log.info("Criando EventOrigin: eventSource={}, sourceCode={}", 
                request.eventSource(), request.sourceCode());
        
        // 1. Verificação de existência
        if (eventOriginRepository.existsBySourceCode(request.sourceCode())) {
            log.warn("Tentativa de criar EventOrigin com sourceCode duplicado: {}", request.sourceCode());
            throw new EventOriginAlreadyExistsException(request.sourceCode());
        }
        
        // 2. Criação da entidade
        EventOrigin eventOrigin = EventOrigin.builder()
                .eventSource(request.eventSource())
                .sourceCode(request.sourceCode())
                .build();
        
        // 3. Persistência
        EventOrigin savedEventOrigin = eventOriginRepository.save(eventOrigin);
        
        log.info("EventOrigin criado com sucesso: id={}, sourceCode={}", 
                savedEventOrigin.getId(), savedEventOrigin.getSourceCode());
        
        // 4. Mapeamento para resposta
        return new EventOriginResponse(
                savedEventOrigin.getId(),
                savedEventOrigin.getEventSource(),
                savedEventOrigin.getSourceCode()
        );
    }
    
    /**
     * Atualiza um EventOrigin existente.
     * 
     * @param id ID do EventOrigin a ser atualizado
     * @param request Dados de atualização
     * @return Dados do EventOrigin atualizado
     * @throws EventOriginNotFoundException se o EventOrigin não for encontrado
     * @throws EventOriginAlreadyExistsException se o novo sourceCode já existir
     */
    public EventOriginResponse updateEventOrigin(UUID id, UpdateEventOriginRequest request) {
        log.info("Atualizando EventOrigin: id={}, eventSource={}, sourceCode={}", 
                id, request.eventSource(), request.sourceCode());
        
        // 1. Busca do EventOrigin existente
        EventOrigin existingEventOrigin = eventOriginRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("EventOrigin não encontrado para atualização: id={}", id);
                    return new EventOriginNotFoundException(id);
                });
        
        // 2. Verificação de sourceCode duplicado (se mudou)
        if (!existingEventOrigin.getSourceCode().equals(request.sourceCode()) &&
            eventOriginRepository.existsBySourceCode(request.sourceCode())) {
            log.warn("Tentativa de atualizar EventOrigin com sourceCode duplicado: {}", request.sourceCode());
            throw new EventOriginAlreadyExistsException(request.sourceCode());
        }
        
        // 3. Atualização dos campos
        existingEventOrigin.setEventSource(request.eventSource());
        existingEventOrigin.setSourceCode(request.sourceCode());
        
        // 4. Persistência
        EventOrigin updatedEventOrigin = eventOriginRepository.save(existingEventOrigin);
        
        log.info("EventOrigin atualizado com sucesso: id={}, sourceCode={}", 
                updatedEventOrigin.getId(), updatedEventOrigin.getSourceCode());
        
        // 5. Mapeamento para resposta
        return new EventOriginResponse(
                updatedEventOrigin.getId(),
                updatedEventOrigin.getEventSource(),
                updatedEventOrigin.getSourceCode()
        );
    }
    
    /**
     * Busca um EventOrigin por ID.
     * 
     * @param id ID do EventOrigin
     * @return Dados do EventOrigin
     * @throws EventOriginNotFoundException se o EventOrigin não for encontrado
     */
    @Transactional(readOnly = true)
    public EventOriginResponse findEventOriginById(UUID id) {
        log.debug("Buscando EventOrigin por ID: {}", id);
        
        EventOrigin eventOrigin = eventOriginRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("EventOrigin não encontrado: id={}", id);
                    return new EventOriginNotFoundException(id);
                });
        
        log.debug("EventOrigin encontrado: id={}, sourceCode={}", 
                eventOrigin.getId(), eventOrigin.getSourceCode());
        
        return new EventOriginResponse(
                eventOrigin.getId(),
                eventOrigin.getEventSource(),
                eventOrigin.getSourceCode()
        );
    }
    
    /**
     * Busca EventOrigins com filtros e paginação.
     * 
     * @param request Parâmetros de busca e paginação
     * @return Resposta com EventOrigins, paginação e totalizadores
     */
    @Transactional(readOnly = true)
    public EventOriginSearchResponse findEventOriginsWithFilters(EventOriginSearchRequest request) {
        log.debug("Buscando EventOrigins com filtros: eventSource={}, page={}, size={}, sortBy={}, sortDir={}",
                request.eventSource(), request.page(), request.size(), request.sortBy(), request.sortDir());
        
        // 1. Configuração de paginação
        Pageable pageable = PageRequest.of(
                request.page(),
                request.size(),
                Sort.by(Sort.Direction.fromString(request.sortDir()), request.sortBy())
        );
        
        // 2. Busca de dados
        Page<EventOrigin> eventOriginsPage = eventOriginRepository.findByFilters(
                request.eventSource(),
                pageable
        );
        
        // 3. Busca de totalizadores
        long totalPdv = eventOriginRepository.countPdvEventOrigins();
        long totalExchange = eventOriginRepository.countExchangeEventOrigins();
        long totalDanfe = eventOriginRepository.countDanfeEventOrigins();
        long totalEventOrigins = eventOriginRepository.countTotalEventOrigins();
        
        // 4. Conversão para DTOs
        var eventOriginResponses = eventOriginsPage.getContent().stream()
                .map(eventOrigin -> new EventOriginResponse(
                        eventOrigin.getId(),
                        eventOrigin.getEventSource(),
                        eventOrigin.getSourceCode()
                ))
                .toList();
        
        // 5. Construção da resposta
        EventOriginSearchResponse.PaginationInfo pagination = EventOriginSearchResponse.PaginationInfo.builder()
                .currentPage(eventOriginsPage.getNumber())
                .totalPages(eventOriginsPage.getTotalPages())
                .totalElements(eventOriginsPage.getTotalElements())
                .pageSize(eventOriginsPage.getSize())
                .hasNext(eventOriginsPage.hasNext())
                .hasPrevious(eventOriginsPage.hasPrevious())
                .build();
        
        EventOriginSearchResponse.EventOriginCounts counts = EventOriginSearchResponse.EventOriginCounts.builder()
                .totalPdv(totalPdv)
                .totalExchange(totalExchange)
                .totalDanfe(totalDanfe)
                .totalEventOrigins(totalEventOrigins)
                .build();
        
        log.debug("EventOrigins encontrados: {} de {}", eventOriginResponses.size(), totalEventOrigins);
        
        return EventOriginSearchResponse.builder()
                .eventOrigins(eventOriginResponses)
                .pagination(pagination)
                .counts(counts)
                .build();
    }

    // criar metodo para buscar todos os EventOrigins do tipo danfe
    public List<EventOriginResponse> findAllDanfeEventOrigins() {
        return eventOriginRepository.findByEventSource(EventSource.DANFE).stream()
                .map(eventOrigin -> new EventOriginResponse(
                        eventOrigin.getId(),
                        eventOrigin.getEventSource(),
                        eventOrigin.getSourceCode()
                ))
                .toList();
    }

    // criar metodo para buscar todos os EventOrigins do tipo pdv
    public List<EventOriginResponse> findAllPdvEventOrigins() {
        return eventOriginRepository.findByEventSource(EventSource.PDV).stream()
                .map(eventOrigin -> new EventOriginResponse(
                        eventOrigin.getId(),
                        eventOrigin.getEventSource(),
                        eventOrigin.getSourceCode()
                ))
                .toList();
    }

    // criar metodo para buscar todos os EventOrigins do tipo exchange
    public List<EventOriginResponse> findAllExchangeEventOrigins() {
        return eventOriginRepository.findByEventSource(EventSource.EXCHANGE).stream()
                .map(eventOrigin -> new EventOriginResponse(
                        eventOrigin.getId(),
                        eventOrigin.getEventSource(),
                        eventOrigin.getSourceCode()
                ))
                .toList();
    }

    
}
