package com.sysconard.business.controller.store;

import com.sysconard.business.dto.store.CreateStoreRequest;
import com.sysconard.business.dto.store.StoreResponse;
import com.sysconard.business.dto.store.UpdateStoreRequest;
import com.sysconard.business.service.store.StoreService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Controller para gerenciamento de lojas do sistema.
 * Implementa endpoints REST para criação, atualização, busca e listagem de lojas.
 * Segue princípios de Clean Code com responsabilidades bem definidas.
 */
@Slf4j
@RestController
@RequestMapping("/stores")
@RequiredArgsConstructor
@Validated
@CrossOrigin(origins = "http://localhost:3000")
public class StoreController {
    
    private final StoreService storeService;
    
    /**
     * Cria uma nova loja no sistema.
     * Acesso permitido apenas para usuários com permissão store:create.
     * 
     * @param request Dados da loja a ser criada
     * @return Resposta com os dados da loja criada
     */
    @PostMapping
    @PreAuthorize("hasAuthority('store:create')")
    public ResponseEntity<StoreResponse> createStore(@Valid @RequestBody CreateStoreRequest request) {
        log.info("Recebida requisição para criar loja: {}", request.getCode());
        
        try {
            StoreResponse response = storeService.createStore(request);
            
            log.info("Loja criada com sucesso: {} (ID: {})", response.getCode(), response.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            log.error("Erro ao criar loja: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Atualiza uma loja existente no sistema.
     * Acesso permitido apenas para usuários com permissão store:update.
     * 
     * @param storeId ID da loja a ser atualizada
     * @param request Dados da loja a ser atualizada
     * @return Resposta com os dados da loja atualizada
     */
    @PutMapping("/{storeId}")
    @PreAuthorize("hasAuthority('store:update')")
    public ResponseEntity<StoreResponse> updateStore(
            @PathVariable UUID storeId,
            @Valid @RequestBody UpdateStoreRequest request) {
        
        log.info("Recebida requisição para atualizar loja: {} (ID: {})", request.getName(), storeId);
        
        try {
            StoreResponse response = storeService.updateStore(storeId, request);
            
            log.info("Loja atualizada com sucesso: {} (ID: {})", response.getCode(), response.getId());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Erro ao atualizar loja {}: {}", storeId, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Busca uma loja pelo ID.
     * Acesso permitido para usuários com permissão store:read.
     * 
     * @param storeId ID da loja
     * @return Dados da loja
     */
    @GetMapping("/{storeId}")
    @PreAuthorize("hasAuthority('store:read')")
    public ResponseEntity<StoreResponse> getStoreById(@PathVariable UUID storeId) {
        log.debug("Recebida requisição para buscar loja: {}", storeId);
        
        try {
            StoreResponse response = storeService.getStoreById(storeId);
            
            log.debug("Loja encontrada: {} (ID: {})", response.getCode(), response.getId());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Erro ao buscar loja {}: {}", storeId, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Busca todas as lojas cadastradas no sistema com paginação.
     * Acesso permitido para usuários com permissão store:read.
     * 
     * @param page Número da página (baseado em 0)
     * @param size Tamanho da página
     * @param sortBy Campo para ordenação
     * @param sortDir Direção da ordenação (asc ou desc)
     * @return Página de lojas
     */
    @GetMapping
    @PreAuthorize("hasAuthority('store:read')")
    public ResponseEntity<Page<StoreResponse>> getAllStores(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "code") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        log.info("Recebida requisição para buscar todas as lojas com paginação: page={}, size={}, sortBy={}, sortDir={}",
                page, size, sortBy, sortDir);
        
        try {
            Page<StoreResponse> stores = storeService.getAllStores(page, size, sortBy, sortDir);
            
            log.info("Retornando {} lojas da página {} de {} total",
                    stores.getContent().size(), stores.getNumber() + 1, stores.getTotalPages());
            
            return ResponseEntity.ok(stores);
            
        } catch (Exception e) {
            log.error("Erro ao buscar lojas: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Busca lojas com filtros e paginação.
     * Acesso permitido para usuários com permissão store:read.
     * 
     * @param code Filtro por código (busca parcial)
     * @param name Filtro por nome (busca parcial)
     * @param city Filtro por cidade (busca parcial)
     * @param status Filtro por status (true = ativa, false = inativa)
     * @param page Número da página (baseado em 0)
     * @param size Tamanho da página
     * @param sortBy Campo para ordenação
     * @param sortDir Direção da ordenação (asc ou desc)
     * @return Página de lojas com filtros aplicados
     */
    @GetMapping("/search")
    @PreAuthorize("hasAuthority('store:read')")
    public ResponseEntity<Page<StoreResponse>> searchStores(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Boolean status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "code") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        log.info("Recebida requisição para buscar lojas com filtros: code={}, name={}, city={}, status={}, page={}, size={}, sortBy={}, sortDir={}",
                code, name, city, status, page, size, sortBy, sortDir);
        
        try {
            Page<StoreResponse> response = storeService.findStoresWithFilters(
                    code, name, city, status, page, size, sortBy, sortDir);
            
            log.info("Retornando {} lojas da página {} de {} total",
                    response.getContent().size(), response.getNumber() + 1, response.getTotalPages());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Erro ao buscar lojas: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Remove uma loja do sistema.
     * Acesso permitido apenas para usuários com permissão store:delete.
     * 
     * @param storeId ID da loja a ser removida
     */
    @DeleteMapping("/{storeId}")
    @PreAuthorize("hasAuthority('store:delete')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteStore(@PathVariable UUID storeId) {
        log.info("Recebida requisição para remover loja: {}", storeId);
        
        try {
            storeService.deleteStore(storeId);
            
            log.info("Loja removida com sucesso: {}", storeId);
            
        } catch (Exception e) {
            log.error("Erro ao remover loja {}: {}", storeId, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Endpoint de health check para o StoreController.
     * 
     * @return Status de saúde do controller
     */
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        log.debug("Health check do StoreController");
        return ResponseEntity.ok("StoreController está funcionando");
    }
}
