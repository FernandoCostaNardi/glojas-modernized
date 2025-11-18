package com.sysconard.legacy.controller;

import com.sysconard.legacy.dto.SaleItemDetailDTO;
import com.sysconard.legacy.dto.SaleItemDetailRequestDTO;
import com.sysconard.legacy.service.SaleItemControllerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller REST para operações com detalhes de itens de venda.
 * Expõe endpoints para buscar detalhes de vendas de um período específico.
 * 
 * @author Sysconard Legacy API
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/sale-items")
@RequiredArgsConstructor
public class SaleItemController {
    
    private final SaleItemControllerService saleItemControllerService;
    
    /**
     * Busca detalhes de itens de venda de um período específico.
     * 
     * @param request DTO com parâmetros da requisição (datas, códigos de origem, operação e lojas)
     * @return Lista de detalhes de itens de venda
     */
    @PostMapping("/details")
    public ResponseEntity<List<SaleItemDetailDTO>> getSaleItemDetails(@RequestBody SaleItemDetailRequestDTO request) {
        log.debug("Recebida requisição para buscar detalhes de itens de venda: startDate={}, endDate={}", 
                 request.getStartDate(), request.getEndDate());
        
        try {
            List<SaleItemDetailDTO> saleItemDetails = saleItemControllerService.getSaleItemDetails(request);
            
            log.info("Requisição de busca de detalhes de itens de venda processada com sucesso. Total: {}", 
                    saleItemDetails != null ? saleItemDetails.size() : 0);
            
            return ResponseEntity.ok(saleItemDetails);
            
        } catch (IllegalArgumentException e) {
            log.warn("Erro de validação na requisição: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
            
        } catch (Exception e) {
            log.error("Erro ao processar requisição de busca de detalhes de itens de venda: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

