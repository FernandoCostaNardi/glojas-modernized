package com.sysconard.legacy.service;

import com.sysconard.legacy.dto.OperationDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Serviço responsável pela lógica de negócio do OperationController
 * 
 * Segue os princípios de Clean Code:
 * - Responsabilidade única: coordenação entre controller e service
 * - Injeção de dependência via construtor
 * - Delegação de lógica para o service
 * - Tratamento de erros adequado
 * - Logging para monitoramento
 * 
 * @author Sysconard Legacy API
 * @version 1.0
 */
@Slf4j
@Service
public class OperationControllerService {
    
    private final OperationService operationService;
    
    @Autowired
    public OperationControllerService(OperationService operationService) {
        this.operationService = operationService;
    }
    
    /**
     * Busca todas as operações cadastradas no sistema
     * 
     * @return Lista de operações
     */
    public List<OperationDTO> getAllOperations() {
        log.debug("Iniciando busca de todas as operações");
        
        try {
            List<OperationDTO> operations = operationService.findAllOperations();
            log.info("Busca de operações concluída com sucesso. Total: {}", operations.size());
            return operations;
        } catch (Exception e) {
            log.error("Erro ao buscar operações: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Busca uma operação específica pelo ID
     * 
     * @param id ID da operação a ser buscada
     * @return Operação encontrada
     * @throws IllegalArgumentException se o ID for inválido
     * @throws RuntimeException se a operação não for encontrada
     */
    public OperationDTO getOperationById(Long id) {
        log.debug("Iniciando busca de operação com ID: {}", id);
        
        try {
            OperationDTO operation = operationService.findOperationById(id);
            log.info("Operação encontrada com sucesso: ID={}, Description={}", 
                    operation.getId(), operation.getDescription());
            return operation;
        } catch (IllegalArgumentException e) {
            log.warn("ID inválido fornecido: {}", id);
            throw e;
        } catch (RuntimeException e) {
            log.warn("Operação não encontrada com ID: {}", id);
            throw e;
        } catch (Exception e) {
            log.error("Erro inesperado ao buscar operação com ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Erro interno ao buscar operação", e);
        }
    }
}
