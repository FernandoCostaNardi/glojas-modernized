package com.sysconard.business.controller.sale;

import com.sysconard.business.dto.sale.SaleSyncRequest;
import com.sysconard.business.dto.sale.SaleSyncResponse;
import com.sysconard.business.service.sale.SaleSyncService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Controller REST para sincronização de vendas.
 * Expõe endpoints para sincronização de vendas com Legacy API.
 * Segue princípios de Clean Code com responsabilidades bem definidas.
 * 
 * @author Sysconard Business API
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/v1/sales")
@RequiredArgsConstructor
@Validated
public class SaleSyncController {
    
    private final SaleSyncService saleSyncService;
    
    /**
     * Sincroniza vendas da Legacy API para o banco de dados.
     * Busca vendas no período especificado e processa produtos únicos e vendas detalhadas.
     * 
     * @param request Requisição de sincronização com datas inicial e final
     * @return Resposta com estatísticas da sincronização
     */
    @PostMapping("/sync")
    public ResponseEntity<SaleSyncResponse> syncSales(@Valid @RequestBody SaleSyncRequest request) {
        log.info("Recebida requisição para sincronizar vendas - startDate: {}, endDate: {}", 
                request.startDate(), request.endDate());
        
        try {
            SaleSyncResponse response = saleSyncService.syncSales(request);
            
            log.info("Sincronização concluída com sucesso - totalItemsReceived: {}, productsInserted: {}, salesInserted: {}", 
                    response.totalItemsReceived(), response.productsInserted(), response.salesInserted());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Erro ao sincronizar vendas: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

