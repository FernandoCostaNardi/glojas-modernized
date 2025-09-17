package com.sysconard.legacy.controller;

import com.sysconard.legacy.dto.OperationDTO;
import com.sysconard.legacy.service.OperationControllerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller para operações com operações do sistema
 * 
 * Segue os princípios de Clean Code:
 * - Responsabilidade única: apenas roteamento HTTP
 * - Injeção de dependência via construtor
 * - Delegação de lógica para o serviço
 * - Documentação clara
 * - Tratamento adequado de códigos HTTP
 * 
 * @author Sysconard Legacy API
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/operations")
public class OperationController {

    private final OperationControllerService operationControllerService;
    
    @Autowired
    public OperationController(OperationControllerService operationControllerService) {
        this.operationControllerService = operationControllerService;
    }

    /**
     * Busca todas as operações cadastradas no sistema
     * 
     * @return Lista de operações com status 200 OK
     */
    @GetMapping
    public ResponseEntity<List<OperationDTO>> getAllOperations() {
        log.debug("Recebida requisição para buscar todas as operações");
        
        try {
            List<OperationDTO> operations = operationControllerService.getAllOperations();
            log.info("Requisição de busca de operações processada com sucesso. Total: {}", operations.size());
            return ResponseEntity.ok(operations);
        } catch (Exception e) {
            log.error("Erro ao processar requisição de busca de operações: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Busca uma operação específica pelo ID
     * 
     * @param id ID da operação a ser buscada
     * @return Operação encontrada com status 200 OK, ou 404 Not Found se não encontrada
     */
    @GetMapping("/{id}")
    public ResponseEntity<OperationDTO> getOperationById(@PathVariable Long id) {
        log.debug("Recebida requisição para buscar operação com ID: {}", id);
        
        try {
            OperationDTO operation = operationControllerService.getOperationById(id);
            log.info("Requisição de busca de operação por ID processada com sucesso: ID={}", id);
            return ResponseEntity.ok(operation);
        } catch (IllegalArgumentException e) {
            log.warn("ID inválido fornecido na requisição: {}", id);
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            if (e.getMessage().contains("não encontrada")) {
                log.warn("Operação não encontrada com ID: {}", id);
                return ResponseEntity.notFound().build();
            } else {
                log.error("Erro ao processar requisição de busca de operação por ID {}: {}", id, e.getMessage(), e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } catch (Exception e) {
            log.error("Erro inesperado ao processar requisição de busca de operação por ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
