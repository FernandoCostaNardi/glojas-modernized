package com.sysconard.business.controller.salestargetconfig;

import com.sysconard.business.dto.salestargetconfig.SalesTargetConfigRequest;
import com.sysconard.business.dto.salestargetconfig.SalesTargetConfigResponse;
import com.sysconard.business.dto.salestargetconfig.UpdateSalesTargetConfigRequest;
import com.sysconard.business.exception.salestargetconfig.SalesTargetConfigNotFoundException;
import com.sysconard.business.service.salestargetconfig.SalesTargetConfigService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller REST para operações com configurações de metas e comissões.
 * Expõe endpoints para criação, atualização, busca e exclusão de configurações.
 * Segue princípios de Clean Code com responsabilidades bem definidas.
 * 
 * @author Sysconard Business API
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/v1/sales-targets-config")
@RequiredArgsConstructor
@Validated
public class SalesTargetConfigController {
    
    private final SalesTargetConfigService salesTargetConfigService;
    
    /**
     * Cria uma nova configuração de metas e comissões.
     * 
     * @param request Dados da configuração a ser criada
     * @return Dados da configuração criada
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<SalesTargetConfigResponse> create(@Valid @RequestBody SalesTargetConfigRequest request) {
        log.info("Recebida requisição para criar configuração de metas e comissões: loja={}, competência={}", 
                request.storeCode(), request.competenceDate());
        
        try {
            SalesTargetConfigResponse response = salesTargetConfigService.create(request);
            
            log.info("Configuração criada com sucesso: id={}", response.getId());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            log.error("Erro ao criar configuração de metas e comissões: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Atualiza uma configuração de metas e comissões existente.
     * 
     * @param id ID da configuração a ser atualizada
     * @param request Dados atualizados da configuração
     * @return Dados da configuração atualizada
     */
    @PutMapping("/{id}")
    public ResponseEntity<SalesTargetConfigResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateSalesTargetConfigRequest request) {
        log.info("Recebida requisição para atualizar configuração de metas e comissões: id={}", id);
        
        try {
            SalesTargetConfigResponse response = salesTargetConfigService.update(id, request);
            
            log.info("Configuração atualizada com sucesso: id={}", response.getId());
            
            return ResponseEntity.ok(response);
            
        } catch (SalesTargetConfigNotFoundException e) {
            log.warn("Configuração não encontrada com ID: {}", id);
            return ResponseEntity.notFound().build();
            
        } catch (Exception e) {
            log.error("Erro ao atualizar configuração de metas e comissões: id={}, error={}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Busca configurações de metas e comissões cadastradas.
     * Suporta filtros opcionais por loja e/ou competência.
     * Se nenhum filtro for fornecido, retorna todas as configurações.
     * 
     * @param storeCode Código da loja (opcional)
     * @param competenceDate Data de competência no formato MM/YYYY (opcional)
     * @return Lista de configurações que atendem aos critérios de filtro
     */
    @GetMapping
    public ResponseEntity<List<SalesTargetConfigResponse>> findAll(
            @RequestParam(required = false) String storeCode,
            @RequestParam(required = false) String competenceDate) {
        
        log.info("Recebida requisição para buscar configurações de metas e comissões: loja={}, competência={}", 
                storeCode, competenceDate);
        
        try {
            // Validar formato de competenceDate se fornecido
            if (competenceDate != null && !competenceDate.trim().isEmpty()) {
                if (!competenceDate.matches("^(0[1-9]|1[0-2])/\\d{4}$")) {
                    log.warn("Formato inválido de competência: {}", competenceDate);
                    return ResponseEntity.badRequest().build();
                }
            }
            
            List<SalesTargetConfigResponse> configs;
            
            // Se ambos os parâmetros forem null/vazios, retornar todas as configurações
            boolean hasFilters = (storeCode != null && !storeCode.trim().isEmpty()) || 
                                (competenceDate != null && !competenceDate.trim().isEmpty());
            
            if (hasFilters) {
                configs = salesTargetConfigService.findByFilters(storeCode, competenceDate);
            } else {
                configs = salesTargetConfigService.findAll();
            }
            
            log.info("Configurações encontradas: {} registros", 
                    configs != null ? configs.size() : 0);
            
            return ResponseEntity.ok(configs);
            
        } catch (Exception e) {
            log.error("Erro ao buscar configurações de metas e comissões: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Busca uma configuração de metas e comissões específica pelo ID.
     * 
     * @param id ID da configuração (UUID)
     * @return Dados da configuração
     */
    @GetMapping("/{id}")
    public ResponseEntity<SalesTargetConfigResponse> findById(@PathVariable UUID id) {
        log.info("Recebida requisição para buscar configuração de metas e comissões por ID: {}", id);
        
        try {
            SalesTargetConfigResponse response = salesTargetConfigService.findById(id);
            
            log.info("Configuração encontrada: id={}, loja={}, competência={}", 
                    response.getId(), response.getStoreCode(), response.getCompetenceDate());
            
            return ResponseEntity.ok(response);
            
        } catch (SalesTargetConfigNotFoundException e) {
            log.warn("Configuração não encontrada com ID: {}", id);
            return ResponseEntity.notFound().build();
            
        } catch (Exception e) {
            log.error("Erro ao buscar configuração de metas e comissões por ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Deleta uma configuração de metas e comissões pelo ID.
     * 
     * @param id ID da configuração a ser deletada
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        log.info("Recebida requisição para deletar configuração de metas e comissões: id={}", id);
        
        try {
            salesTargetConfigService.delete(id);
            
            log.info("Configuração deletada com sucesso: id={}", id);
            
            return ResponseEntity.noContent().build();
            
        } catch (SalesTargetConfigNotFoundException e) {
            log.warn("Configuração não encontrada com ID: {}", id);
            return ResponseEntity.notFound().build();
            
        } catch (Exception e) {
            log.error("Erro ao deletar configuração de metas e comissões: id={}, error={}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

