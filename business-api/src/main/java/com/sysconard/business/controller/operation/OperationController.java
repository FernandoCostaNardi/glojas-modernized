package com.sysconard.business.controller.operation;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sysconard.business.dto.operation.CreateOperationRequest;
import com.sysconard.business.dto.operation.OperationResponse;
import com.sysconard.business.dto.operation.OperationSearchRequest;
import com.sysconard.business.dto.operation.OperationSearchResponse;
import com.sysconard.business.dto.operation.UpdateOperationRequest;
import com.sysconard.business.service.operation.OperationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller para gerenciamento de operações do sistema.
 * Implementa endpoints REST para criação, atualização, busca e listagem de operações.
 * Segue princípios de Clean Code com responsabilidades bem definidas.
 */
@Slf4j
@RestController
@RequestMapping("/operations")
@RequiredArgsConstructor
@Validated
@CrossOrigin(origins = "http://localhost:3000")
public class OperationController {
    
    private final OperationService operationService;
    
    /**
     * Cria uma nova operação no sistema.
     * Acesso permitido apenas para usuários com permissão operation:create.
     * 
     * @param request Dados da operação a ser criada
     * @return Resposta com os dados da operação criada
     */
    @PostMapping
    @PreAuthorize("hasAuthority('operation:create')")
    public ResponseEntity<OperationResponse> createOperation(@Valid @RequestBody CreateOperationRequest request) {
        log.info("Recebida requisição para criar operação: {}", request.getCode());
        
        try {
            OperationResponse response = operationService.createOperation(request);
            
            log.info("Operação criada com sucesso: {} (ID: {})", response.getCode(), response.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            log.error("Erro ao criar operação: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Atualiza uma operação existente no sistema.
     * Acesso permitido apenas para usuários com permissão operation:update.
     * 
     * @param operationId ID da operação a ser atualizada
     * @param request Dados da operação a ser atualizada
     * @return Resposta com os dados da operação atualizada
     */
    @PutMapping("/{operationId}")
    @PreAuthorize("hasAuthority('operation:update')")
    public ResponseEntity<OperationResponse> updateOperation(
            @PathVariable UUID operationId,
            @Valid @RequestBody UpdateOperationRequest request) {
        
        log.info("Recebida requisição para atualizar operação: {} (ID: {})", request.getCode(), operationId);
        
        try {
            OperationResponse response = operationService.updateOperation(operationId, request);
            
            log.info("Operação atualizada com sucesso: {} (ID: {})", response.getCode(), response.getId());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Erro ao atualizar operação {}: {}", operationId, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Busca uma operação pelo ID.
     * Acesso permitido para usuários com permissão operation:read.
     * 
     * @param operationId ID da operação
     * @return Dados da operação
     */
    @GetMapping("/{operationId}")
    @PreAuthorize("hasAuthority('operation:read')")
    public ResponseEntity<OperationResponse> getOperationById(@PathVariable UUID operationId) {
        log.debug("Recebida requisição para buscar operação: {}", operationId);
        
        try {
            OperationResponse response = operationService.getOperationById(operationId);
            
            log.debug("Operação encontrada: {} (ID: {})", response.getCode(), response.getId());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Erro ao buscar operação {}: {}", operationId, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Busca operações com filtros e paginação.
     * Acesso permitido para usuários com permissão operation:read.
     * 
     * @param code Filtro por código (busca parcial)
     * @param page Número da página (baseado em 0)
     * @param size Tamanho da página
     * @param sortBy Campo para ordenação
     * @param sortDir Direção da ordenação (asc ou desc)
     * @return Resposta com operações, paginação e totalizadores
     */
    @GetMapping
    @PreAuthorize("hasAuthority('operation:read')")
    public ResponseEntity<OperationSearchResponse> getAllOperations(
            @RequestParam(required = false) String code,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "code") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        log.info("Recebida requisição para buscar operações com filtros: code={}, page={}, size={}, sortBy={}, sortDir={}",
                code, page, size, sortBy, sortDir);
        
        try {
            // Criar request de busca
            OperationSearchRequest searchRequest = OperationSearchRequest.builder()
                    .code(code)
                    .page(page)
                    .size(size)
                    .sortBy(sortBy)
                    .sortDir(sortDir)
                    .build();
            
            OperationSearchResponse response = operationService.findOperationsWithFilters(searchRequest);
            
            log.info("Retornando {} operações da página {} de {} total",
                    response.getOperations().size(), response.getCurrentPage() + 1, response.getTotalPages());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Erro ao buscar operações: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Endpoint de health check para o OperationController.
     * 
     * @return Status de saúde do controller
     */
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        log.debug("Health check do OperationController");
        return ResponseEntity.ok("OperationController está funcionando");
    }
}
