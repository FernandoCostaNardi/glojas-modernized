package com.sysconard.legacy.service;

import com.sysconard.legacy.dto.ExchangeDTO;
import com.sysconard.legacy.dto.ExchangeRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Serviço de orquestração para o controller de trocas.
 * Atua como camada intermediária entre o controller e o service de negócio.
 * 
 * @author Sysconard Legacy API
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExchangeControllerService {
    
    private final ExchangeService exchangeService;
    
    /**
     * Busca trocas realizadas para o controller.
     * 
     * @param request DTO com parâmetros da requisição
     * @return Lista de DTOs com os dados das trocas
     */
    public List<ExchangeDTO> getExchanges(ExchangeRequestDTO request) {
        log.debug("Iniciando busca de trocas para o controller");
        
        try {
            List<ExchangeDTO> exchanges = exchangeService.findExchanges(request);
            
            log.info("Busca de trocas para o controller processada com sucesso. Total: {}", 
                    exchanges != null ? exchanges.size() : 0);
            
            return exchanges;
            
        } catch (IllegalArgumentException e) {
            log.error("Erro de validação ao buscar trocas para o controller: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Erro ao buscar trocas para o controller: {}", e.getMessage(), e);
            throw e;
        }
    }
}

