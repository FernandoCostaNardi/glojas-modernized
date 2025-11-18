package com.sysconard.legacy.controller;

import com.sysconard.legacy.dto.ExchangeProductDTO;
import com.sysconard.legacy.dto.ExchangeProductRequestDTO;
import com.sysconard.legacy.service.ExchangeProductControllerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para operações com produtos de trocas.
 * Expõe endpoints para buscar produtos de trocas baseado em listas de códigos.
 * 
 * @author Sysconard Legacy API
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/exchanges/products")
@RequiredArgsConstructor
public class ExchangeProductController {
    
    private final ExchangeProductControllerService exchangeProductControllerService;
    
    /**
     * Busca produtos de trocas baseado em números de nota e chaves NFE.
     * 
     * @param request DTO com parâmetros da requisição (documentNumbers, nfeKeys)
     * @return Lista de produtos de trocas
     */
    @PostMapping
    public ResponseEntity<List<ExchangeProductDTO>> getExchangeProducts(@RequestBody ExchangeProductRequestDTO request) {
        log.debug("Recebida requisição para buscar produtos de trocas: documentNumbers={}, nfeKeys={}", 
                 request.getDocumentNumbers() != null ? request.getDocumentNumbers().size() : 0, 
                 request.getNfeKeys() != null ? request.getNfeKeys().size() : 0);
        
        try {
            List<ExchangeProductDTO> products = exchangeProductControllerService.getExchangeProducts(request);
            
            log.info("Requisição de busca de produtos de trocas processada com sucesso. Total: {}", 
                    products != null ? products.size() : 0);
            
            return ResponseEntity.ok(products);
            
        } catch (IllegalArgumentException e) {
            log.warn("Erro de validação na requisição: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
            
        } catch (Exception e) {
            log.error("Erro ao processar requisição de busca de produtos de trocas: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

