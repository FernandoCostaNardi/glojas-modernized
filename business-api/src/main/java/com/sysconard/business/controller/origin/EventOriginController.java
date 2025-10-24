package com.sysconard.business.controller.origin;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sysconard.business.dto.origin.CreateEventOriginRequest;
import com.sysconard.business.dto.origin.EventOriginResponse;
import com.sysconard.business.dto.origin.EventOriginSearchRequest;
import com.sysconard.business.dto.origin.EventOriginSearchResponse;
import com.sysconard.business.dto.origin.UpdateEventOriginRequest;
import com.sysconard.business.enums.EventSource;
import com.sysconard.business.service.origin.EventOriginService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller para gerenciamento de EventOrigin.
 * Implementa endpoints REST para CRUD de EventOrigin seguindo princípios de Clean Code.
 * Utiliza injeção de dependência via construtor e logging estruturado.
 */
@Slf4j
@RestController
@RequestMapping("/event-origins")
@RequiredArgsConstructor
@Validated
@CrossOrigin(origins = "http://localhost:3000")
public class EventOriginController {
    
    private final EventOriginService eventOriginService;
    
    /**
     * Cria um novo EventOrigin.
     * 
     * @param request Dados do EventOrigin a ser criado
     * @return EventOrigin criado
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('origin:create')")
    public ResponseEntity<EventOriginResponse> createEventOrigin(
            @Valid @RequestBody CreateEventOriginRequest request) {
        
        log.info("Recebida requisição para criar EventOrigin: eventSource={}, sourceCode={}", 
                request.eventSource(), request.sourceCode());
        
        EventOriginResponse response = eventOriginService.createEventOrigin(request);
        
        log.info("EventOrigin criado com sucesso: id={}", response.id());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * Atualiza um EventOrigin existente.
     * 
     * @param id ID do EventOrigin a ser atualizado
     * @param request Dados de atualização
     * @return EventOrigin atualizado
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('origin:update')")
    public ResponseEntity<EventOriginResponse> updateEventOrigin(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateEventOriginRequest request) {
        
        log.info("Recebida requisição para atualizar EventOrigin: id={}, eventSource={}, sourceCode={}", 
                id, request.eventSource(), request.sourceCode());
        
        EventOriginResponse response = eventOriginService.updateEventOrigin(id, request);
        
        log.info("EventOrigin atualizado com sucesso: id={}", response.id());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Busca um EventOrigin por ID.
     * 
     * @param id ID do EventOrigin
     * @return EventOrigin encontrado
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('origin:read')")
    public ResponseEntity<EventOriginResponse> getEventOriginById(@PathVariable UUID id) {
        
        log.debug("Recebida requisição para buscar EventOrigin por ID: {}", id);
        
        EventOriginResponse response = eventOriginService.findEventOriginById(id);
        
        log.debug("EventOrigin encontrado: id={}, sourceCode={}", 
                response.id(), response.sourceCode());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Busca EventOrigins com filtros e paginação.
     * 
     * @param eventSource Filtro por fonte do evento (opcional)
     * @param page Número da página (padrão: 0)
     * @param size Tamanho da página (padrão: 20)
     * @param sortBy Campo para ordenação (padrão: "sourceCode")
     * @param sortDir Direção da ordenação (asc/desc, padrão: "asc")
     * @return Lista paginada de EventOrigins com totalizadores
     */
    @GetMapping
    @PreAuthorize("hasAuthority('origin:read')")
    public ResponseEntity<EventOriginSearchResponse> getEventOrigins(
            @RequestParam(required = false) EventSource eventSource,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "sourceCode") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        log.debug("Recebida requisição para buscar EventOrigins: eventSource={}, page={}, size={}, sortBy={}, sortDir={}",
                eventSource, page, size, sortBy, sortDir);
        
        EventOriginSearchRequest request = new EventOriginSearchRequest(
                eventSource, null, page, size, sortBy, sortDir);
        
        EventOriginSearchResponse response = eventOriginService.findEventOriginsWithFilters(request);
        
        log.debug("EventOrigins encontrados: {} de {}", 
                response.getEventOrigins().size(), response.getCounts().getTotalEventOrigins());
        
        return ResponseEntity.ok(response);
    }
}
