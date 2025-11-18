package com.sysconard.business.controller.exchange;

import com.sysconard.business.dto.exchange.CreateExchangeRequest;
import com.sysconard.business.dto.exchange.ExchangeResponse;
import com.sysconard.business.dto.exchange.ExchangeSearchRequest;
import com.sysconard.business.dto.exchange.ExchangeSyncRequest;
import com.sysconard.business.dto.exchange.ExchangeSyncResponse;
import com.sysconard.business.dto.exchange.UpdateExchangeRequest;
import com.sysconard.business.service.exchange.ExchangeService;
import com.sysconard.business.service.exchange.ExchangeSyncService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Controller REST para operações com trocas.
 * Expõe endpoints para sincronização e CRUD completo de trocas.
 * 
 * @author Business API
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/v1/exchanges")
@RequiredArgsConstructor
public class ExchangeController {
    
    private final ExchangeService exchangeService;
    private final ExchangeSyncService exchangeSyncService;
    
    /**
     * Sincroniza trocas da Legacy API.
     * 
     * @param request Requisição com parâmetros de sincronização
     * @return Resposta com estatísticas da sincronização
     */
    @PostMapping("/sync")
    public ResponseEntity<ExchangeSyncResponse> syncExchanges(@Valid @RequestBody ExchangeSyncRequest request) {
        log.info("Recebida requisição de sincronização de trocas: startDate={}, endDate={}", 
                request.startDate(), request.endDate());
        
        try {
            ExchangeSyncResponse response = exchangeSyncService.syncExchanges(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("Erro de validação na requisição: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Erro ao sincronizar trocas: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Cria uma nova troca.
     * 
     * @param request Requisição com dados da troca
     * @return Resposta com a troca criada
     */
    @PostMapping
    public ResponseEntity<ExchangeResponse> createExchange(@Valid @RequestBody CreateExchangeRequest request) {
        log.info("Recebida requisição de criação de troca: documentCode={}, storeCode={}", 
                request.documentCode(), request.storeCode());
        
        try {
            ExchangeResponse response = exchangeService.createExchange(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            log.warn("Erro de validação na requisição: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Erro ao criar troca: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Busca uma troca por ID.
     * 
     * @param id ID da troca
     * @return Resposta com a troca encontrada
     */
    @GetMapping("/{id}")
    public ResponseEntity<ExchangeResponse> getExchangeById(@PathVariable UUID id) {
        log.debug("Recebida requisição de busca de troca por ID: {}", id);
        
        try {
            ExchangeResponse response = exchangeService.getExchangeById(id);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("Troca não encontrada: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Erro ao buscar troca: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Lista todas as trocas com paginação e filtros opcionais.
     * 
     * @param documentCode Filtro por código do documento (opcional)
     * @param storeCode Filtro por código da loja (opcional)
     * @param operationCode Filtro por código da operação (opcional)
     * @param originCode Filtro por código de origem (opcional)
     * @param employeeCode Filtro por código do colaborador (opcional)
     * @param startDate Filtro por data de início (opcional)
     * @param endDate Filtro por data de fim (opcional)
     * @param pageable Configuração de paginação
     * @return Página com trocas
     */
    @GetMapping
    public ResponseEntity<Page<ExchangeResponse>> getAllExchanges(
            @RequestParam(required = false) String documentCode,
            @RequestParam(required = false) String storeCode,
            @RequestParam(required = false) String operationCode,
            @RequestParam(required = false) String originCode,
            @RequestParam(required = false) String employeeCode,
            @RequestParam(required = false) java.time.LocalDateTime startDate,
            @RequestParam(required = false) java.time.LocalDateTime endDate,
            @PageableDefault(size = 20) Pageable pageable) {
        
        log.debug("Recebida requisição de listagem de trocas - page: {}, size: {}", 
                pageable.getPageNumber(), pageable.getPageSize());
        
        try {
            ExchangeSearchRequest searchRequest = new ExchangeSearchRequest(
                    documentCode,
                    storeCode,
                    operationCode,
                    originCode,
                    employeeCode,
                    startDate,
                    endDate
            );
            
            // Se há filtros, usar busca com filtros, senão listar todas
            boolean hasFilters = documentCode != null || storeCode != null || 
                                operationCode != null || originCode != null || 
                                employeeCode != null || startDate != null || endDate != null;
            
            Page<ExchangeResponse> response = hasFilters 
                    ? exchangeService.searchExchanges(searchRequest, pageable)
                    : exchangeService.getAllExchanges(pageable);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Erro ao listar trocas: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Atualiza uma troca existente.
     * 
     * @param id ID da troca a ser atualizada
     * @param request Requisição com dados a serem atualizados
     * @return Resposta com a troca atualizada
     */
    @PutMapping("/{id}")
    public ResponseEntity<ExchangeResponse> updateExchange(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateExchangeRequest request) {
        
        log.info("Recebida requisição de atualização de troca: id={}", id);
        
        try {
            ExchangeResponse response = exchangeService.updateExchange(id, request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("Erro de validação na requisição: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Erro ao atualizar troca: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Deleta uma troca por ID.
     * 
     * @param id ID da troca a ser deletada
     * @return Resposta vazia com status 204 (No Content)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExchange(@PathVariable UUID id) {
        log.info("Recebida requisição de exclusão de troca: id={}", id);
        
        try {
            exchangeService.deleteExchange(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            log.warn("Troca não encontrada: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Erro ao deletar troca: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

