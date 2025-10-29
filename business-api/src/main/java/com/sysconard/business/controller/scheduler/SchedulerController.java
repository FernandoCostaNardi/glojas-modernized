package com.sysconard.business.controller.scheduler;

import com.sysconard.business.service.scheduler.DailySalesSchedulerService;
import com.sysconard.business.service.scheduler.MonthlySalesSchedulerService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

/**
 * Controller REST para operações de agendamento e execução manual de processos.
 * Fornece endpoints para execução manual de sincronização e envio de emails.
 * 
 * @author Business API
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/scheduler")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class SchedulerController {
    
    private final DailySalesSchedulerService dailySalesSchedulerService;
    private final MonthlySalesSchedulerService monthlySalesSchedulerService;
    
    /**
     * Endpoint para execução manual do processo de sincronização e envio de emails.
     * Útil para testes e execução sob demanda.
     * 
     * @param date Data específica para execução (opcional, usa dia anterior se não informada)
     * @return Resposta com status da execução
     */
    @PostMapping("/daily-sales/execute")
    @PreAuthorize("hasAuthority('SYSTEM_ADMIN')")
    public ResponseEntity<Map<String, Object>> executeDailySalesSyncAndEmail(
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) 
            LocalDate date) {
        
        log.info("Recebida solicitação de execução manual do processo de vendas diárias: date={}", date);
        
        try {
            dailySalesSchedulerService.executeManualSyncAndEmail(date);
            
            LocalDate targetDate = date != null ? date : LocalDate.now().minusDays(1);
            
            Map<String, Object> response = Map.of(
                "success", true,
                "message", "Processo executado com sucesso",
                "targetDate", targetDate.toString(),
                "executedAt", java.time.LocalDateTime.now().toString()
            );
            
            log.info("Execução manual concluída com sucesso para data: {}", targetDate);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Erro durante execução manual: {}", e.getMessage(), e);
            
            Map<String, Object> response = Map.of(
                "success", false,
                "message", "Erro durante execução: " + e.getMessage(),
                "executedAt", java.time.LocalDateTime.now().toString()
            );
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * Endpoint para execução manual do processo de sincronização mensal e envio de emails.
     * Útil para testes e execução sob demanda.
     * 
     * @return Resposta com status da execução
     */
    @PostMapping("/monthly-sales/execute")
    @PreAuthorize("hasAuthority('SYSTEM_ADMIN')")
    public ResponseEntity<Map<String, Object>> executeMonthlySalesSyncAndEmail() {
        
        log.info("Recebida solicitação de execução manual do processo de vendas mensais");
        
        try {
            monthlySalesSchedulerService.executeManualMonthlySalesSync();
            
            Map<String, Object> response = Map.of(
                "success", true,
                "message", "Processo de sincronização mensal executado com sucesso",
                "executedAt", java.time.LocalDateTime.now().toString()
            );
            
            log.info("Execução manual de vendas mensais concluída com sucesso");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Erro durante execução manual de vendas mensais: {}", e.getMessage(), e);
            
            Map<String, Object> response = Map.of(
                "success", false,
                "message", "Erro durante execução: " + e.getMessage(),
                "executedAt", java.time.LocalDateTime.now().toString()
            );
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * Endpoint para verificar status do scheduler.
     * 
     * @return Status do scheduler
     */
    @GetMapping("/status")
    @PreAuthorize("hasAuthority('SYSTEM_ADMIN')")
    public ResponseEntity<Map<String, Object>> getSchedulerStatus() {
        log.debug("Verificando status do scheduler");
        
        Map<String, Object> status = Map.of(
            "dailySchedulerEnabled", true,
            "monthlySchedulerEnabled", true,
            "lastCheck", java.time.LocalDateTime.now().toString(),
            "dailyNextExecution", "01:00 AM (configurado no cron)",
            "monthlyNextExecution", "01:30 AM (configurado no cron)",
            "dailyCronExpression", "0 0 1 * * *",
            "monthlyCronExpression", "0 30 1 * * *"
        );
        
        return ResponseEntity.ok(status);
    }
}
