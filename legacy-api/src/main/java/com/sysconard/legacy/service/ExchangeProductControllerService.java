package com.sysconard.legacy.service;

import com.sysconard.legacy.dto.ExchangeProductDTO;
import com.sysconard.legacy.dto.ExchangeProductRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Serviço de orquestração para o controller de produtos de trocas.
 * Atua como camada intermediária entre o controller e o service de negócio.
 * 
 * @author Sysconard Legacy API
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExchangeProductControllerService {
    
    private final ExchangeProductService exchangeProductService;
    
    /**
     * Busca produtos de trocas para o controller.
     * 
     * @param request DTO com parâmetros da requisição
     * @return Lista de DTOs com os dados dos produtos de trocas
     */
    public List<ExchangeProductDTO> getExchangeProducts(ExchangeProductRequestDTO request) {
        log.debug("Iniciando busca de produtos de trocas para o controller");
        
        try {
            List<ExchangeProductDTO> products = exchangeProductService.findExchangeProducts(request);
            
            log.info("Busca de produtos de trocas para o controller processada com sucesso. Total: {}", 
                    products != null ? products.size() : 0);
            
            return products;
            
        } catch (IllegalArgumentException e) {
            log.error("Erro de validação ao buscar produtos de trocas para o controller: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Erro ao buscar produtos de trocas para o controller: {}", e.getMessage(), e);
            throw e;
        }
    }
}

