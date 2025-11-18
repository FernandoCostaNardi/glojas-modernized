package com.sysconard.legacy.controller;

import com.sysconard.legacy.dto.ExchangeDTO;
import com.sysconard.legacy.dto.ExchangeRequestDTO;
import com.sysconard.legacy.service.ExchangeControllerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para operações com trocas realizadas.
 * Expõe endpoints para buscar trocas de um período específico.
 * 
 * @author Sysconard Legacy API
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/exchanges")
@RequiredArgsConstructor
public class ExchangeController {
    
    private final ExchangeControllerService exchangeControllerService;
    
    /**
     * Busca trocas realizadas em um período específico.
     * 
     * @param request DTO com parâmetros da requisição (datas, códigos de origem e operação)
     * @return Lista de trocas realizadas
     */
    @PostMapping
    public ResponseEntity<List<ExchangeDTO>> getExchanges(@RequestBody ExchangeRequestDTO request) {
        log.debug("Recebida requisição para buscar trocas: startDate={}, endDate={}", 
                 request.getStartDate(), request.getEndDate());
        
        try {
            List<ExchangeDTO> exchanges = exchangeControllerService.getExchanges(request);
            
            log.info("Requisição de busca de trocas processada com sucesso. Total: {}", 
                    exchanges != null ? exchanges.size() : 0);
            
            return ResponseEntity.ok(exchanges);
            
        } catch (IllegalArgumentException e) {
            log.warn("Erro de validação na requisição: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
            
        } catch (Exception e) {
            log.error("Erro ao processar requisição de busca de trocas: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

