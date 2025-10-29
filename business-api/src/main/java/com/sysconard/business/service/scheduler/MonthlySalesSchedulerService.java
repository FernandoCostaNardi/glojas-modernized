package com.sysconard.business.service.scheduler;

import com.sysconard.business.dto.email.MonthlySalesEmailData;
import com.sysconard.business.dto.sell.MonthlySalesReportResponse;
import com.sysconard.business.dto.sync.MonthlySalesSyncRequest;
import com.sysconard.business.dto.sync.MonthlySalesSyncResponse;
import com.sysconard.business.entity.EmailNotifier;
import com.sysconard.business.repository.EmailNotifierRepository;
import com.sysconard.business.service.email.EmailService;
import com.sysconard.business.service.sell.MonthlySalesReportService;
import com.sysconard.business.service.sync.MonthlySalesSyncService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Serviço responsável pelo agendamento e execução da sincronização de vendas mensais.
 * Executa diariamente às 01:30 AM com lógica especial para o primeiro dia do mês.
 * 
 * Lógica de datas:
 * - Dia 1 do mês: processa fechamento do mês anterior
 * - Outros dias: processa atualização do mês atual
 * 
 * @author Business API
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MonthlySalesSchedulerService {
    
    private final MonthlySalesSyncService monthlySalesSyncService;
    private final MonthlySalesReportService monthlySalesReportService;
    private final EmailNotifierRepository emailNotifierRepository;
    private final EmailService emailService;
    
    @Value("${sync.monthly-sales.schedule.enabled:true}")
    private boolean scheduleEnabled;
    
    /**
     * Executa a sincronização agendada de vendas mensais.
     * Executado diariamente às 01:30 AM via @Scheduled.
     */
    @Scheduled(cron = "${sync.monthly-sales.schedule.cron}")
    public void executeScheduledMonthlySalesSync() {
        if (!scheduleEnabled) {
            log.debug("Scheduler de vendas mensais desabilitado");
            return;
        }
        
        log.info("=== Iniciando processo agendado de sincronização mensal ===");
        
        try {
            // 1. Calcular datas com lógica especial
            LocalDate today = LocalDate.now();
            boolean isFirstDayOfMonth = today.getDayOfMonth() == 1;
            LocalDate startDate, endDate;
            
            if (isFirstDayOfMonth) {
                // Primeiro dia do mês: processar fechamento do mês anterior
                LocalDate previousMonth = today.minusMonths(1);
                startDate = previousMonth.withDayOfMonth(1);
                endDate = previousMonth.withDayOfMonth(previousMonth.lengthOfMonth());
                log.info("Primeiro dia do mês - processando fechamento: {} a {}", startDate, endDate);
            } else {
                // Outros dias: processar atualização do mês atual
                startDate = today.withDayOfMonth(1);
                endDate = today.withDayOfMonth(today.lengthOfMonth());
                log.info("Processando atualização mensal: {} a {}", startDate, endDate);
            }
            
            // 2. Sincronizar vendas mensais
            MonthlySalesSyncRequest syncRequest = new MonthlySalesSyncRequest(startDate, endDate);
            MonthlySalesSyncResponse syncResponse = monthlySalesSyncService.syncMonthlySales(syncRequest);
            log.info("Sincronização mensal concluída: {} criados, {} atualizados", 
                    syncResponse.created(), syncResponse.updated());
            
            // 3. Buscar destinatários com notificação mensal ativada
            List<EmailNotifier> recipients = emailNotifierRepository.findByDailyMonthNotifierTrue();
            if (recipients.isEmpty()) {
                log.info("Nenhum destinatário cadastrado para notificação mensal");
                return;
            }
            log.info("Encontrados {} destinatários para notificação mensal", recipients.size());
            
            // 4. Buscar dados de vendas mensais para o período
            List<MonthlySalesReportResponse> salesData = 
                    monthlySalesReportService.getMonthlySalesReport(startDate, endDate);
            
            if (salesData.isEmpty()) {
                log.warn("Nenhum dado de vendas encontrado para o período {} a {}", startDate, endDate);
                return;
            }
            
            // 5. Preparar dados do email
            String monthName = formatMonthYear(isFirstDayOfMonth ? startDate : today);
            MonthlySalesEmailData emailData = new MonthlySalesEmailData(
                startDate,
                endDate,
                monthName,
                isFirstDayOfMonth,
                salesData,
                Optional.of(syncResponse)
            );
            
            // 6. Enviar emails para todos os destinatários
            List<String> emailAddresses = recipients.stream()
                    .map(EmailNotifier::getEmail)
                    .toList();
            
            emailService.sendMonthlySalesEmail(emailAddresses, emailData);
            log.info("Emails mensais enviados com sucesso para {} destinatários", emailAddresses.size());
            
            log.info("=== Processo agendado de sincronização mensal concluído ===");
            
        } catch (Exception e) {
            log.error("Erro no processo agendado de sincronização mensal: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Formata uma data para o padrão "MMMM yyyy" em português brasileiro.
     * 
     * @param date Data a ser formatada
     * @return String formatada (ex: "Outubro 2024")
     */
    private String formatMonthYear(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("MMMM yyyy", new Locale("pt", "BR")));
    }
    
    /**
     * Executa manualmente o processo de sincronização mensal.
     * Útil para testes e execução sob demanda.
     */
    public void executeManualMonthlySalesSync() {
        log.info("Executando sincronização mensal manual");
        executeScheduledMonthlySalesSync();
    }
}
