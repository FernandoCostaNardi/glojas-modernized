package com.sysconard.business.scheduler;

import com.sysconard.business.dto.sync.DailySalesSyncRequest;
import com.sysconard.business.dto.sync.DailySalesSyncResponse;
import com.sysconard.business.service.sync.DailySalesSyncService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Scheduler responsável pela execução automática da sincronização de vendas diárias.
 * Executa diariamente às 01:00 AM para sincronizar os dados do dia anterior.
 * Pode ser habilitado/desabilitado via configuração no application.yml.
 * 
 * @author Business API
 * @version 1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(
    name = "sync.daily-sales.schedule.enabled", 
    havingValue = "true", 
    matchIfMissing = false
)
public class DailySalesScheduler {
    
    private final DailySalesSyncService dailySalesSyncService;
    
    /**
     * Executa a sincronização automática de vendas diárias.
     * Agenda: Todo dia às 01:00 AM (cron: "0 0 1 * * *")
     * Sincroniza sempre os dados do dia anterior.
     */
    @Scheduled(cron = "${sync.daily-sales.schedule.cron:0 0 1 * * *}")
    public void syncYesterdayData() {
        log.info("Iniciando sincronização automática de vendas diárias");
        
        LocalDate yesterday = LocalDate.now().minusDays(1);
        
        try {
            // Criar requisição para o dia anterior
            DailySalesSyncRequest request = DailySalesSyncRequest.builder()
                    .startDate(yesterday)
                    .endDate(yesterday)
                    .build();
            
            log.info("Executando sincronização automática para data: {}", yesterday);
            
            // Executar sincronização
            DailySalesSyncResponse response = dailySalesSyncService.syncDailySales(request);
            
            log.info("Sincronização automática concluída com sucesso: " +
                    "data={}, criados={}, atualizados={}, lojas={}", 
                    yesterday, response.created(), response.updated(), response.storesProcessed());
            
            // Log de métricas para monitoramento
            logSyncMetrics(response, yesterday);
            
        } catch (Exception e) {
            log.error("Erro durante sincronização automática para data {}: {}", 
                     yesterday, e.getMessage(), e);
            
            // Aqui poderia ser implementado:
            // - Envio de alertas por email/slack
            // - Tentativas de retry
            // - Métricas de erro para monitoramento
            logSyncError(yesterday, e);
        }
    }
    
    /**
     * Executa sincronização semanal para correção de inconsistências.
     * Agenda: Todo domingo às 02:00 AM (cron: "0 0 2 * * SUN")
     * Sincroniza os últimos 7 dias para garantir consistência.
     */
    @Scheduled(cron = "${sync.daily-sales.weekly.cron:0 0 2 * * SUN}")
    @ConditionalOnProperty(
        name = "sync.daily-sales.weekly.enabled", 
        havingValue = "true", 
        matchIfMissing = false
    )
    public void syncWeeklyCorrection() {
        log.info("Iniciando sincronização semanal de correção");
        
        LocalDate endDate = LocalDate.now().minusDays(1);
        LocalDate startDate = endDate.minusDays(6); // Últimos 7 dias
        
        try {
            DailySalesSyncRequest request = DailySalesSyncRequest.builder()
                    .startDate(startDate)
                    .endDate(endDate)
                    .build();
            
            log.info("Executando sincronização semanal para período: {} a {}", startDate, endDate);
            
            DailySalesSyncResponse response = dailySalesSyncService.syncDailySales(request);
            
            log.info("Sincronização semanal concluída: " +
                    "período={} a {}, criados={}, atualizados={}, lojas={}", 
                    startDate, endDate, response.created(), response.updated(), response.storesProcessed());
            
        } catch (Exception e) {
            log.error("Erro durante sincronização semanal para período {} a {}: {}", 
                     startDate, endDate, e.getMessage(), e);
            
            logSyncError(startDate, e);
        }
    }
    
    /**
     * Registra métricas de sucesso da sincronização para monitoramento.
     * 
     * @param response Resposta da sincronização
     * @param date Data processada
     */
    private void logSyncMetrics(DailySalesSyncResponse response, LocalDate date) {
        log.info("SYNC_METRICS: date={}, created={}, updated={}, total_processed={}, stores={}, success=true",
                date, response.created(), response.updated(), 
                response.getTotalProcessed(), response.storesProcessed());
    }
    
    /**
     * Registra métricas de erro da sincronização para monitoramento.
     * 
     * @param date Data que deveria ter sido processada
     * @param error Erro ocorrido
     */
    private void logSyncError(LocalDate date, Exception error) {
        log.error("SYNC_METRICS: date={}, success=false, error_type={}, error_message={}", 
                 date, error.getClass().getSimpleName(), error.getMessage());
    }
}
