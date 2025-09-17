package com.sysconard.legacy.service;

import com.sysconard.legacy.dto.OperationDTO;
import com.sysconard.legacy.entity.Operation;
import com.sysconard.legacy.repository.OperationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Serviço responsável pela lógica de negócio das operações
 * 
 * Segue os princípios de Clean Code:
 * - Responsabilidade única: lógica de negócio das operações
 * - Injeção de dependência via construtor
 * - Conversão de entidades para DTOs
 * - Tratamento de erros adequado
 * - Logging para monitoramento
 * 
 * @author Sysconard Legacy API
 * @version 1.0
 */
@Slf4j
@Service
public class OperationService {
    
    private final OperationRepository operationRepository;
    
    @Autowired
    public OperationService(OperationRepository operationRepository) {
        this.operationRepository = operationRepository;
    }
    
    /**
     * Busca todas as operações cadastradas no sistema
     * 
     * @return Lista de operações convertidas para DTO
     */
    public List<OperationDTO> findAllOperations() {
        log.debug("Buscando todas as operações cadastradas");
        
        List<Operation> operations = operationRepository.findAll();
        
        List<OperationDTO> operationDTOs = operations.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        log.debug("Operações encontradas: {}", operationDTOs.size());
        
        return operationDTOs;
    }
    
    /**
     * Busca uma operação específica pelo ID
     * 
     * @param id ID da operação a ser buscada
     * @return DTO da operação encontrada
     * @throws IllegalArgumentException se o ID for inválido
     * @throws RuntimeException se a operação não for encontrada
     */
    public OperationDTO findOperationById(Long id) {
        log.debug("Buscando operação com ID: {}", id);
        
        // Validação do ID
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID da operação deve ser válido (maior que 0)");
        }
        
        Optional<Operation> operationOptional = operationRepository.findById(id);
        
        if (operationOptional.isPresent()) {
            OperationDTO operationDTO = convertToDTO(operationOptional.get());
            log.debug("Operação encontrada: ID={}, Description={}", operationDTO.getId(), operationDTO.getDescription());
            return operationDTO;
        } else {
            log.warn("Operação não encontrada com ID: {}", id);
            throw new RuntimeException("Operação não encontrada com ID: " + id);
        }
    }
    
    /**
     * Converte uma entidade Operation para OperationDTO
     * 
     * @param operation Entidade a ser convertida
     * @return DTO convertido
     */
    private OperationDTO convertToDTO(Operation operation) {
        return OperationDTO.builder()
                .id(formatOperationId(operation.getId()))
                .description(operation.getDescription())
                .build();
    }
    
    /**
     * Formata o ID da operação com 6 dígitos, preenchendo com zeros à esquerda
     * 
     * @param id ID da operação
     * @return ID formatado com 6 dígitos (ex: 000001)
     */
    private String formatOperationId(Long id) {
        if (id == null) {
            return null;
        }
        return String.format("%06d", id);
    }
}
