package com.sysconard.business.service.scheduler;

import com.sysconard.business.dto.email.DailySalesEmailData;
import com.sysconard.business.dto.sell.DailySalesReportResponse;
import com.sysconard.business.dto.sync.DailySalesSyncRequest;
import com.sysconard.business.dto.sync.DailySalesSyncResponse;
import com.sysconard.business.entity.EmailNotifier;
import com.sysconard.business.repository.EmailNotifierRepository;
import com.sysconard.business.repository.sell.DailySellRepository;
import com.sysconard.business.service.email.EmailService;
import com.sysconard.business.service.sync.DailySalesSyncService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Serviço responsável pela execução agendada de sincronização e envio de emails.
 * Executa diariamente às 01:00 AM para sincronizar vendas do dia anterior
 * e enviar relatórios por email para destinatários cadastrados.
 * Segue os princípios de Clean Code com responsabilidades bem definidas.
 * 
 * @author Business API
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DailySalesSchedulerService {
    
    private final DailySalesSyncService dailySalesSyncService;
    private final EmailNotifierRepository emailNotifierRepository;
    private final DailySellRepository dailySellRepository;
    private final EmailService emailService;
    
    @Value("${sync.daily-sales.schedule.enabled:true}")
    private boolean schedulerEnabled;
    
    /**
     * Executa o processo agendado de sincronização e envio de emails.
     * Executado diariamente às 01:00 AM conforme configuração do cron.
     */
    @Scheduled(cron = "${sync.daily-sales.schedule.cron:0 0 1 * * *}")
    public void executeDailySalesSyncAndEmail() {
        log.info("=== Iniciando processo agendado de sincronização e envio de emails ===");
        
        if (!schedulerEnabled) {
            log.info("Scheduler de vendas diárias está desabilitado. Pulando execução.");
            return;
        }
        
        try {
            // Passo 1: Calcular data anterior
            LocalDate previousDay = LocalDate.now().minusDays(1);
            log.info("Sincronizando vendas do dia: {}", previousDay);
            
            // Passo 2: Executar sincronização
            DailySalesSyncResponse syncResponse = executeSync(previousDay);
            log.info("Sincronização concluída: {} criados, {} atualizados, {} lojas processadas", 
                    syncResponse.created(), syncResponse.updated(), syncResponse.storesProcessed());
            
            // Passo 3: Buscar destinatários
            List<String> recipients = getRecipients();
            if (recipients.isEmpty()) {
                log.info("Nenhum destinatário cadastrado para notificação. Processo concluído.");
                return;
            }
            log.info("Encontrados {} destinatários para notificação", recipients.size());
            
            // Passo 4: Buscar dados de vendas
            List<DailySalesReportResponse> salesData = getSalesData(previousDay);
            if (salesData.isEmpty()) {
                log.warn("Nenhum dado de vendas encontrado para o dia {}. Enviando relatório vazio.", previousDay);
            }
            
            // Passo 5: Preparar dados do email
            DailySalesEmailData emailData = new DailySalesEmailData(previousDay, salesData, syncResponse);
            
            // Passo 6: Enviar emails
            emailService.sendDailySalesReport(recipients, emailData);
            
            log.info("=== Processo agendado concluído com sucesso ===");
            
        } catch (Exception e) {
            log.error("Erro durante execução do processo agendado: {}", e.getMessage(), e);
            // Não relançar a exceção para não quebrar o scheduler
        }
    }
    
    /**
     * Executa a sincronização de vendas para o dia especificado.
     * 
     * @param date Data para sincronização
     * @return Resposta da sincronização
     */
    private DailySalesSyncResponse executeSync(LocalDate date) {
        log.debug("Executando sincronização para a data: {}", date);
        
        try {
            DailySalesSyncRequest request = new DailySalesSyncRequest(date, date);
            return dailySalesSyncService.syncDailySales(request);
        } catch (Exception e) {
            log.error("Erro durante sincronização para data {}: {}", date, e.getMessage(), e);
            throw new RuntimeException("Falha na sincronização de vendas", e);
        }
    }
    
    /**
     * Busca todos os destinatários cadastrados para notificação de vendas diárias.
     * 
     * @return Lista de endereços de email dos destinatários
     */
    private List<String> getRecipients() {
        log.debug("Buscando destinatários para notificação de vendas diárias");
        
        try {
            List<EmailNotifier> notifiers = emailNotifierRepository.findByDailySellNotifierTrue();
            
            List<String> recipients = notifiers.stream()
                    .map(EmailNotifier::getEmail)
                    .collect(Collectors.toList());
            
            log.debug("Destinatários encontrados: {}", recipients);
            return recipients;
            
        } catch (Exception e) {
            log.error("Erro ao buscar destinatários: {}", e.getMessage(), e);
            return List.of(); // Retorna lista vazia para não quebrar o processo
        }
    }
    
    /**
     * Busca dados de vendas agregadas para o dia especificado.
     * 
     * @param date Data para busca dos dados
     * @return Lista de vendas agregadas por loja
     */
    private List<DailySalesReportResponse> getSalesData(LocalDate date) {
        log.debug("Buscando dados de vendas para a data: {}", date);
        
        try {
            List<DailySalesReportResponse> salesData = dailySellRepository
                    .findAggregatedSalesByDateRange(date, date);
            
            log.debug("Dados de vendas encontrados: {} lojas", salesData.size());
            return salesData;
            
        } catch (Exception e) {
            log.error("Erro ao buscar dados de vendas para data {}: {}", date, e.getMessage(), e);
            return List.of(); // Retorna lista vazia para não quebrar o processo
        }
    }
    
    /**
     * Método para execução manual do processo (para testes).
     * Pode ser chamado via endpoint ou teste unitário.
     * 
     * @param date Data específica para execução (opcional, usa dia anterior se null)
     */
    public void executeManualSyncAndEmail(LocalDate date) {
        log.info("=== Executando processo manual de sincronização e envio de emails ===");
        
        LocalDate targetDate = date != null ? date : LocalDate.now().minusDays(1);
        log.info("Data alvo para execução manual: {}", targetDate);
        
        try {
            // Executar sincronização
            DailySalesSyncResponse syncResponse = executeSync(targetDate);
            log.info("Sincronização manual concluída: {} criados, {} atualizados", 
                    syncResponse.created(), syncResponse.updated());
            
            // Buscar destinatários
            List<String> recipients = getRecipients();
            log.info("Destinatários para execução manual: {}", recipients);
            
            // Buscar dados de vendas
            List<DailySalesReportResponse> salesData = getSalesData(targetDate);
            log.info("Dados de vendas para execução manual: {} lojas", salesData.size());
            
            // Preparar e enviar email
            if (!recipients.isEmpty()) {
                DailySalesEmailData emailData = new DailySalesEmailData(targetDate, salesData, syncResponse);
                emailService.sendDailySalesReport(recipients, emailData);
                log.info("Email enviado com sucesso para {} destinatários", recipients.size());
            } else {
                log.warn("Nenhum destinatário encontrado para execução manual");
            }
            
            log.info("=== Processo manual concluído com sucesso ===");
            
        } catch (Exception e) {
            log.error("Erro durante execução manual: {}", e.getMessage(), e);
            throw e; // Relança exceção para execução manual
        }
    }
}
