package com.sysconard.business.service.email;

import com.sysconard.business.dto.email.DailySalesEmailData;
import com.sysconard.business.dto.email.MonthlySalesEmailData;
import com.sysconard.business.dto.sell.DailySalesReportResponse;
import com.sysconard.business.dto.sell.MonthlySalesReportResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/**
 * Serviço responsável pelo envio de emails do sistema.
 * Implementa formatação HTML e envio de notificações de vendas.
 * Segue os princípios de Clean Code com responsabilidades bem definidas.
 * 
 * @author Business API
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {
    
    private final JavaMailSender mailSender;
    
    @Value("${spring.mail.default-from:fcostanardi@gmail.com}")
    private String defaultFrom;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static final NumberFormat CURRENCY_FORMATTER = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
    
    /**
     * Envia relatório de vendas diárias para uma lista de destinatários.
     * 
     * @param recipients Lista de endereços de email dos destinatários
     * @param emailData Dados do relatório de vendas
     * @throws RuntimeException se houver erro no envio
     */
    public void sendDailySalesReport(List<String> recipients, DailySalesEmailData emailData) {
        if (recipients == null || recipients.isEmpty()) {
            log.warn("Nenhum destinatário fornecido para envio de relatório de vendas");
            return;
        }
        
        if (!emailData.hasSalesData()) {
            log.warn("Nenhum dado de vendas disponível para envio de relatório");
            return;
        }
        
        log.info("Enviando relatório de vendas diárias para {} destinatários", recipients.size());
        
        try {
            String htmlContent = buildDailySalesHtmlContent(emailData);
            String subject = String.format("Relatório de Vendas Diárias - %s", 
                    emailData.date().format(DATE_FORMATTER));
            
            for (String recipient : recipients) {
                try {
                    sendEmail(recipient, subject, htmlContent);
                    log.info("Email enviado com sucesso para: {}", recipient);
                } catch (Exception e) {
                    log.error("Erro ao enviar email para {}: {}", recipient, e.getMessage(), e);
                    // Continua para o próximo destinatário
                }
            }
            
            log.info("Processo de envio de emails concluído");
            
        } catch (Exception e) {
            log.error("Erro geral no envio de relatório de vendas: {}", e.getMessage(), e);
            throw new RuntimeException("Erro no envio de relatório de vendas", e);
        }
    }
    
    /**
     * Envia um email individual.
     * 
     * @param to Endereço do destinatário
     * @param subject Assunto do email
     * @param htmlContent Conteúdo HTML do email
     * @throws MessagingException se houver erro no envio
     */
    private void sendEmail(String to, String subject, String htmlContent) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        
        helper.setFrom(defaultFrom);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true); // true indica que é HTML
        
        mailSender.send(message);
    }
    
    /**
     * Constrói o conteúdo HTML do relatório de vendas diárias.
     * 
     * @param emailData Dados do relatório
     * @return String com HTML formatado
     */
    public String buildDailySalesHtmlContent(DailySalesEmailData emailData) {
        StringBuilder html = new StringBuilder();
        
        // Cabeçalho HTML
        html.append("<!DOCTYPE html>")
            .append("<html>")
            .append("<head>")
            .append("<meta charset=\"UTF-8\">")
            .append("<style>")
            .append("body { font-family: Arial, sans-serif; margin: 20px; background-color: #f9fafb; }")
            .append(".container { background-color: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }")
            .append("h2 { color: #dc2626; margin-bottom: 20px; text-align: center; }")
            .append("table { border-collapse: collapse; width: 100%; margin: 20px 0; }")
            .append("th, td { border: 1px solid #d1d5db; padding: 12px; text-align: right; }")
            .append("th { background-color: #dc2626; color: white; font-weight: bold; }")
            .append("tr:nth-child(even) { background-color: #f9fafb; }")
            .append(".total-row { font-weight: bold; background-color: #fee2e2; }")
            .append(".summary { background-color: #f3f4f6; padding: 15px; border-radius: 4px; margin: 20px 0; }")
            .append(".footer { margin-top: 30px; font-size: 12px; color: #6b7280; text-align: center; }")
            .append("</style>")
            .append("</head>")
            .append("<body>");
        
        // Container principal
        html.append("<div class=\"container\">");
        
        // Título
        html.append("<h2>Relatório de Vendas Diárias - ")
            .append(emailData.date().format(DATE_FORMATTER))
            .append("</h2>");
        
        // Resumo da sincronização
        html.append("<div class=\"summary\">")
            .append("<strong>Resumo da Sincronização:</strong><br>")
            .append("• Registros criados: ").append(emailData.syncStats().created()).append("<br>")
            .append("• Registros atualizados: ").append(emailData.syncStats().updated()).append("<br>")
            .append("• Lojas processadas: ").append(emailData.syncStats().storesProcessed()).append("<br>")
            .append("• Total de lojas com vendas: ").append(emailData.getStoreCount())
            .append("</div>");
        
        // Tabela de vendas
        html.append("<table>")
            .append("<thead>")
            .append("<tr>")
            .append("<th>Loja</th>")
            .append("<th>PDV</th>")
            .append("<th>DANFE</th>")
            .append("<th>Troca</th>")
            .append("<th>Total</th>")
            .append("</tr>")
            .append("</thead>")
            .append("<tbody>");
        
        // Calcular totais
        BigDecimal totalPdv = BigDecimal.ZERO;
        BigDecimal totalDanfe = BigDecimal.ZERO;
        BigDecimal totalExchange = BigDecimal.ZERO;
        BigDecimal totalGeral = BigDecimal.ZERO;
        
        // Linhas de dados
        for (DailySalesReportResponse sale : emailData.salesData()) {
            html.append("<tr>")
                .append("<td style=\"text-align: left;\">").append(sale.storeName()).append("</td>")
                .append("<td>").append(formatCurrency(sale.pdv())).append("</td>")
                .append("<td>").append(formatCurrency(sale.danfe())).append("</td>")
                .append("<td>").append(formatCurrency(sale.exchange())).append("</td>")
                .append("<td>").append(formatCurrency(sale.total())).append("</td>")
                .append("</tr>");
            
            // Acumular totais
            totalPdv = totalPdv.add(sale.pdv());
            totalDanfe = totalDanfe.add(sale.danfe());
            totalExchange = totalExchange.add(sale.exchange());
            totalGeral = totalGeral.add(sale.total());
        }
        
        // Linha de totais
        html.append("</tbody>")
            .append("<tfoot>")
            .append("<tr class=\"total-row\">")
            .append("<td style=\"text-align: left;\"><strong>TOTAL GERAL</strong></td>")
            .append("<td><strong>").append(formatCurrency(totalPdv)).append("</strong></td>")
            .append("<td><strong>").append(formatCurrency(totalDanfe)).append("</strong></td>")
            .append("<td><strong>").append(formatCurrency(totalExchange)).append("</strong></td>")
            .append("<td><strong>").append(formatCurrency(totalGeral)).append("</strong></td>")
            .append("</tr>")
            .append("</tfoot>")
            .append("</table>");
        
        // Rodapé
        html.append("<div class=\"footer\">")
            .append("<p>Sincronização executada em: ")
            .append(emailData.syncStats().processedAt().format(DATETIME_FORMATTER))
            .append("</p>")
            .append("<p>Sistema Glojas - Relatório Automático de Vendas Diárias</p>")
            .append("</div>");
        
        // Fechar container e HTML
        html.append("</div>")
            .append("</body>")
            .append("</html>");
        
        return html.toString();
    }
    
    /**
     * Envia relatório de vendas mensais para uma lista de destinatários.
     * 
     * @param recipients Lista de endereços de email dos destinatários
     * @param emailData Dados do relatório de vendas mensais
     * @throws RuntimeException se houver erro no envio
     */
    public void sendMonthlySalesEmail(List<String> recipients, MonthlySalesEmailData emailData) {
        if (recipients == null || recipients.isEmpty()) {
            log.warn("Nenhum destinatário fornecido para envio de relatório mensal");
            return;
        }
        
        if (!emailData.hasData()) {
            log.warn("Nenhum dado de vendas mensais disponível para envio de relatório");
            return;
        }
        
        log.info("Enviando relatório de vendas mensais para {} destinatários", recipients.size());
        
        try {
            String htmlContent = buildMonthlySalesHtmlContent(emailData);
            String subject = buildMonthlySalesSubject(emailData);
            
            for (String recipient : recipients) {
                try {
                    sendEmail(recipient, subject, htmlContent);
                    log.info("Email mensal enviado com sucesso para: {}", recipient);
                } catch (Exception e) {
                    log.error("Erro ao enviar email mensal para {}: {}", recipient, e.getMessage(), e);
                    // Continua para o próximo destinatário
                }
            }
            
            log.info("Processo de envio de emails mensais concluído");
            
        } catch (Exception e) {
            log.error("Erro geral no envio de relatório de vendas mensais: {}", e.getMessage(), e);
            throw new RuntimeException("Erro no envio de relatório de vendas mensais", e);
        }
    }
    
    /**
     * Constrói o assunto do email de vendas mensais.
     * 
     * @param emailData Dados do relatório
     * @return String com o assunto formatado
     */
    private String buildMonthlySalesSubject(MonthlySalesEmailData emailData) {
        if (emailData.isMonthClosure()) {
            return String.format("Fechamento Mensal - %s - Glojas", emailData.monthName());
        } else {
            return String.format("Atualização Mensal - %s - Glojas", emailData.monthName());
        }
    }
    
    /**
     * Constrói o conteúdo HTML do relatório de vendas mensais.
     * 
     * @param emailData Dados do relatório
     * @return String com HTML formatado
     */
    public String buildMonthlySalesHtmlContent(MonthlySalesEmailData emailData) {
        StringBuilder html = new StringBuilder();
        
        // Cabeçalho HTML
        html.append("<!DOCTYPE html>")
            .append("<html>")
            .append("<head>")
            .append("<meta charset=\"UTF-8\">")
            .append("<style>")
            .append("body { font-family: Arial, sans-serif; margin: 20px; background-color: #f9fafb; }")
            .append(".container { background-color: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }")
            .append("h2 { color: #2563eb; margin-bottom: 20px; text-align: center; }")
            .append("table { border-collapse: collapse; width: 100%; margin: 20px 0; }")
            .append("th, td { border: 1px solid #d1d5db; padding: 12px; text-align: right; }")
            .append("th { background-color: #2563eb; color: white; font-weight: bold; }")
            .append("tr:nth-child(even) { background-color: #f9fafb; }")
            .append(".total-row { font-weight: bold; background-color: #dbeafe; }")
            .append(".summary { background-color: #f3f4f6; padding: 15px; border-radius: 4px; margin: 20px 0; }")
            .append(".footer { margin-top: 30px; font-size: 12px; color: #6b7280; text-align: center; }")
            .append(".progress-container { display: flex; align-items: center; gap: 10px; }")
            .append(".progress-bar-bg { width: 80px; height: 8px; background: #e5e7eb; border-radius: 4px; overflow: hidden; }")
            .append(".progress-bar { height: 100%; background: #2563eb; border-radius: 4px; }")
            .append("</style>")
            .append("</head>")
            .append("<body>");
        
        // Container principal
        html.append("<div class=\"container\">");
        
        // Título
        String titlePrefix = emailData.isMonthClosure() ? "Fechamento Mensal" : "Atualização Mensal";
        html.append("<h2>").append(titlePrefix).append(" - ").append(emailData.monthName()).append("</h2>");
        
        // Resumo da sincronização
        if (emailData.syncResponse().isPresent()) {
            var syncResponse = emailData.syncResponse().get();
            html.append("<div class=\"summary\">")
                .append("<strong>Resumo da Sincronização:</strong><br>")
                .append("• Registros criados: ").append(syncResponse.created()).append("<br>")
                .append("• Registros atualizados: ").append(syncResponse.updated()).append("<br>")
                .append("• Lojas processadas: ").append(syncResponse.storesProcessed()).append("<br>")
                .append("• Meses processados: ").append(syncResponse.monthsProcessed()).append("<br>")
                .append("• Total de lojas com vendas: ").append(emailData.getStoreCount())
                .append("</div>");
        }
        
        // Tabela de vendas mensais (3 colunas)
        html.append("<table>")
            .append("<thead>")
            .append("<tr>")
            .append("<th style=\"text-align: left;\">Loja</th>")
            .append("<th>Total</th>")
            .append("<th>% do Total</th>")
            .append("</tr>")
            .append("</thead>")
            .append("<tbody>");
        
        // Calcular total geral para percentuais
        double grandTotal = emailData.getGrandTotal();
        
        // Linhas de dados
        for (MonthlySalesReportResponse sale : emailData.salesData()) {
            double percentage = grandTotal > 0 ? (sale.total().doubleValue() / grandTotal) * 100 : 0;
            
            html.append("<tr>")
                .append("<td style=\"text-align: left;\">").append(sale.storeName()).append("</td>")
                .append("<td>").append(formatCurrency(sale.total().doubleValue())).append("</td>")
                .append("<td>")
                .append("<div class=\"progress-container\">")
                .append("<div class=\"progress-bar-bg\">")
                .append("<div class=\"progress-bar\" style=\"width: ").append(Math.min(percentage, 100)).append("%\"></div>")
                .append("</div>")
                .append("<span>").append(String.format("%.2f", percentage)).append("%</span>")
                .append("</div>")
                .append("</td>")
                .append("</tr>");
        }
        
        // Linha de totais
        html.append("</tbody>")
            .append("<tfoot>")
            .append("<tr class=\"total-row\">")
            .append("<td style=\"text-align: left;\"><strong>TOTAL GERAL</strong></td>")
            .append("<td><strong>").append(formatCurrency(grandTotal)).append("</strong></td>")
            .append("<td><strong>100,00%</strong></td>")
            .append("</tr>")
            .append("</tfoot>")
            .append("</table>");
        
        // Rodapé
        html.append("<div class=\"footer\">")
            .append("<p>Período: ").append(emailData.startDate().format(DATE_FORMATTER))
            .append(" a ").append(emailData.endDate().format(DATE_FORMATTER)).append("</p>")
            .append("<p>Sistema Glojas - Relatório Automático de Vendas Mensais</p>")
            .append("</div>");
        
        // Fechar container e HTML
        html.append("</div>")
            .append("</body>")
            .append("</html>");
        
        return html.toString();
    }
    
    /**
     * Formata um valor BigDecimal como moeda brasileira.
     * 
     * @param value Valor a ser formatado
     * @return String formatada como moeda
     */
    public String formatCurrency(BigDecimal value) {
        if (value == null) {
            return "R$ 0,00";
        }
        return CURRENCY_FORMATTER.format(value);
    }
    
    /**
     * Formata um valor double como moeda brasileira.
     * 
     * @param value Valor a ser formatado
     * @return String formatada como moeda
     */
    public String formatCurrency(double value) {
        return CURRENCY_FORMATTER.format(value);
    }
}
