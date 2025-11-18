package com.sysconard.legacy.service;

import com.sysconard.legacy.dto.SaleItemDetailDTO;
import com.sysconard.legacy.dto.SaleItemDetailRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Serviço de orquestração para o controller de detalhes de itens de venda.
 * Atua como camada intermediária entre o controller e o service de negócio.
 * 
 * @author Sysconard Legacy API
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SaleItemControllerService {
    
    private final SaleItemService saleItemService;
    
    /**
     * Busca detalhes de itens de venda para o controller.
     * 
     * @param request DTO com parâmetros da requisição
     * @return Lista de DTOs com os detalhes dos itens de venda
     */
    public List<SaleItemDetailDTO> getSaleItemDetails(SaleItemDetailRequestDTO request) {
        log.debug("Iniciando busca de detalhes de itens de venda para o controller");
        
        try {
            List<SaleItemDetailDTO> saleItemDetails = saleItemService.findSaleItemDetails(request);
            
            log.info("Busca de detalhes de itens de venda para o controller processada com sucesso. Total: {}", 
                    saleItemDetails != null ? saleItemDetails.size() : 0);
            
            return saleItemDetails;
            
        } catch (IllegalArgumentException e) {
            log.error("Erro de validação ao buscar detalhes de itens de venda para o controller: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Erro ao buscar detalhes de itens de venda para o controller: {}", e.getMessage(), e);
            throw e;
        }
    }
}

