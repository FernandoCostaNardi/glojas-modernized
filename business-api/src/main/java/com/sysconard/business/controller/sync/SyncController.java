package com.sysconard.business.controller.sync;

import com.sysconard.business.dto.sync.DailySalesSyncRequest;
import com.sysconard.business.dto.sync.DailySalesSyncResponse;
import com.sysconard.business.dto.sync.MonthlySalesSyncRequest;
import com.sysconard.business.dto.sync.MonthlySalesSyncResponse;
import com.sysconard.business.dto.sync.YearlySalesSyncRequest;
import com.sysconard.business.dto.sync.YearlySalesSyncResponse;
import com.sysconard.business.service.sync.DailySalesSyncService;
import com.sysconard.business.service.sync.MonthlySalesSyncService;
import com.sysconard.business.service.sync.YearlySalesSyncService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Controller REST para operações de sincronização de dados.
 * Fornece endpoints para sincronização de vendas diárias e outras operações de sincronização.
 * 
 * @author Business API
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/sync")
@RequiredArgsConstructor
@Validated
public class SyncController {
    
    private final DailySalesSyncService dailySalesSyncService;
    private final MonthlySalesSyncService monthlySalesSyncService;
    private final YearlySalesSyncService yearlySalesSyncService;
    
    /**
     * Endpoint para sincronização manual de vendas diárias.
     * Executa a sincronização para o período especificado na requisição.
     * 
     * @param request DTO com período de sincronização (startDate e endDate)
     * @return Resposta com estatísticas da sincronização executada
     */
    @PostMapping("/daily-sales")
    @PreAuthorize("hasAuthority('sync:execute')")
    public ResponseEntity<DailySalesSyncResponse> syncDailySales(
            @Valid @RequestBody DailySalesSyncRequest request) {
        
        log.info("Recebida solicitação de sincronização de vendas diárias: startDate={}, endDate={}", 
                request.startDate(), request.endDate());
        
        try {
            DailySalesSyncResponse response = dailySalesSyncService.syncDailySales(request);
            
            log.info("Sincronização de vendas diárias concluída: criados={}, atualizados={}, lojas={}", 
                    response.created(), response.updated(), response.storesProcessed());
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            log.warn("Dados inválidos para sincronização: {}", e.getMessage());
            throw e; // Será tratado pelo GlobalExceptionHandler como 400 Bad Request
        } catch (Exception e) {
            log.error("Erro durante sincronização de vendas diárias: {}", e.getMessage(), e);
            throw e; // Será tratado pelo GlobalExceptionHandler
        }
    }
    
    /**
     * Endpoint para sincronização de vendas mensais.
     * Agrega vendas diárias por mês e loja, salvando na tabela monthly_sells.
     * Sempre atualiza registros existentes ao invés de duplicar.
     * 
     * @param request DTO com período de sincronização (startDate e endDate)
     * @return Resposta com estatísticas da sincronização executada
     */
    @PostMapping("/monthly-sales")
    @PreAuthorize("hasAuthority('sync:execute')")
    public ResponseEntity<MonthlySalesSyncResponse> syncMonthlySales(
            @Valid @RequestBody MonthlySalesSyncRequest request) {
        
        log.info("Recebida solicitação de sincronização de vendas mensais: startDate={}, endDate={}", 
                request.startDate(), request.endDate());
        
        try {
            MonthlySalesSyncResponse response = monthlySalesSyncService.syncMonthlySales(request);
            
            log.info("Sincronização de vendas mensais concluída: criados={}, atualizados={}, lojas={}, meses={}", 
                    response.created(), response.updated(), response.storesProcessed(), response.monthsProcessed());
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            log.warn("Dados inválidos para sincronização de vendas mensais: {}", e.getMessage());
            throw e; // Será tratado pelo GlobalExceptionHandler como 400 Bad Request
        } catch (Exception e) {
            log.error("Erro durante sincronização de vendas mensais: {}", e.getMessage(), e);
            throw e; // Será tratado pelo GlobalExceptionHandler
        }
    }
    
    /**
     * Endpoint para sincronização de vendas anuais.
     * Agrega vendas mensais por ano e loja, salvando na tabela year_sells.
     * Sempre atualiza registros existentes ao invés de duplicar.
     * 
     * @param request DTO com ano de sincronização
     * @return Resposta com estatísticas da sincronização executada
     */
    @PostMapping("/yearly-sales")
    @PreAuthorize("hasAuthority('sync:execute')")
    public ResponseEntity<YearlySalesSyncResponse> syncYearlySales(
            @Valid @RequestBody YearlySalesSyncRequest request) {
        
        log.info("Recebida solicitação de sincronização de vendas anuais: year={}", request.year());
        
        try {
            YearlySalesSyncResponse response = yearlySalesSyncService.syncYearlySales(request);
            
            log.info("Sincronização de vendas anuais concluída: criados={}, atualizados={}, lojas={}", 
                    response.created(), response.updated(), response.storesProcessed());
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            log.warn("Dados inválidos para sincronização de vendas anuais: {}", e.getMessage());
            throw e; // Será tratado pelo GlobalExceptionHandler como 400 Bad Request
        } catch (Exception e) {
            log.error("Erro durante sincronização de vendas anuais: {}", e.getMessage(), e);
            throw e; // Será tratado pelo GlobalExceptionHandler
        }
    }
    
    /**
     * Endpoint de status/health check para o serviço de sincronização.
     * Útil para monitoramento e verificação da disponibilidade do serviço.
     * 
     * @return Status do serviço de sincronização
     */
    @GetMapping("/status")
    @PreAuthorize("hasAuthority('sync:read')")
    public ResponseEntity<SyncStatusResponse> getSyncStatus() {
        log.debug("Verificando status do serviço de sincronização");
        
        try {
            SyncStatusResponse status = SyncStatusResponse.builder()
                    .serviceName("DailySales Sync Service")
                    .status("HEALTHY")
                    .version("1.0")
                    .lastCheck(java.time.LocalDateTime.now())
                    .build();
            
            return ResponseEntity.ok(status);
            
        } catch (Exception e) {
            log.error("Erro ao verificar status do serviço: {}", e.getMessage(), e);
            
            SyncStatusResponse status = SyncStatusResponse.builder()
                    .serviceName("DailySales Sync Service")
                    .status("UNHEALTHY")
                    .version("1.0")
                    .lastCheck(java.time.LocalDateTime.now())
                    .error(e.getMessage())
                    .build();
            
            return ResponseEntity.status(503).body(status);
        }
    }
    
    /**
     * Record para resposta de status do serviço de sincronização.
     */
    public record SyncStatusResponse(
        String serviceName,
        String status,
        String version,
        java.time.LocalDateTime lastCheck,
        String error
    ) {
        /**
         * Builder pattern para construção do objeto.
         */
        public static Builder builder() {
            return new Builder();
        }
        
        public static class Builder {
            private String serviceName;
            private String status;
            private String version;
            private java.time.LocalDateTime lastCheck;
            private String error;
            
            public Builder serviceName(String serviceName) {
                this.serviceName = serviceName;
                return this;
            }
            
            public Builder status(String status) {
                this.status = status;
                return this;
            }
            
            public Builder version(String version) {
                this.version = version;
                return this;
            }
            
            public Builder lastCheck(java.time.LocalDateTime lastCheck) {
                this.lastCheck = lastCheck;
                return this;
            }
            
            public Builder error(String error) {
                this.error = error;
                return this;
            }
            
            public SyncStatusResponse build() {
                return new SyncStatusResponse(serviceName, status, version, lastCheck, error);
            }
        }
    }
}
