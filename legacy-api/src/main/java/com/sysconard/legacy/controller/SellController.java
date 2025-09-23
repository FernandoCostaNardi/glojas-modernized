package com.sysconard.legacy.controller;

import com.sysconard.legacy.dto.StoreSalesReportDTO;
import com.sysconard.legacy.dto.StoreSalesReportRequestDTO;
import com.sysconard.legacy.dto.StoreSalesReportByDayDTO;
import com.sysconard.legacy.service.StoreSalesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Controller REST para operações relacionadas a vendas.
 * Expõe endpoints para relatórios de vendas por loja.
 * 
 * @author Sysconard Legacy API
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
public class SellController {
    
    private final StoreSalesService storeSalesService;
    
    /**
     * Endpoint para obter relatório de vendas por loja.
     * Retorna agregações de DANFE, PDV e TROCA3 para as lojas especificadas.
     * 
     * @param request DTO com todos os parâmetros necessários para o relatório
     * @return Lista de DTOs com os dados agregados por loja
     */
    @PostMapping("/store-report")
    public ResponseEntity<List<StoreSalesReportDTO>> getStoreSalesReport(
            @Valid @RequestBody StoreSalesReportRequestDTO request) {
        
        log.info("Solicitando relatório de vendas por loja: startDate={}, endDate={}, storeCodes={}", 
                request.getStartDate(), request.getEndDate(), request.getStoreCodes());
        
        try {
            // Geração do relatório usando o DTO
            List<StoreSalesReportDTO> report = storeSalesService.getStoreSalesReport(request);
            
            log.info("Relatório de vendas gerado com sucesso: {} lojas", report.size());
            
            return ResponseEntity.ok(report);
            
        } catch (IllegalArgumentException e) {
            log.error("Erro de validação nos parâmetros: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
            
        } catch (Exception e) {
            log.error("Erro interno ao gerar relatório de vendas: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Endpoint para obter relatório de vendas por loja e por dia.
     * Retorna agregações de DANFE, PDV e TROCA3 para as lojas especificadas,
     * com dados separados por dia dentro do período solicitado.
     * 
     * @param request DTO com todos os parâmetros necessários para o relatório
     * @return Lista de DTOs com os dados agregados por loja e por dia
     */
    @PostMapping("/store-report-by-day")
    public ResponseEntity<List<StoreSalesReportByDayDTO>> getStoreSalesReportByDay(
            @Valid @RequestBody StoreSalesReportRequestDTO request) {
        
        log.info("Solicitando relatório de vendas por loja e por dia: startDate={}, endDate={}, storeCodes={}", 
                request.getStartDate(), request.getEndDate(), request.getStoreCodes());
        
        try {
            // Geração do relatório por dia usando o DTO
            List<StoreSalesReportByDayDTO> report = storeSalesService.getStoreSalesReportByDay(request);
            
            log.info("Relatório de vendas por dia gerado com sucesso: {} registros", report.size());
            
            return ResponseEntity.ok(report);
            
        } catch (IllegalArgumentException e) {
            log.error("Erro de validação nos parâmetros: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
            
        } catch (Exception e) {
            log.error("Erro interno ao gerar relatório de vendas por dia: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
