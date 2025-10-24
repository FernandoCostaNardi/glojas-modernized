package com.sysconard.business.service.operation;

import com.sysconard.business.dto.operation.CreateOperationRequest;
import com.sysconard.business.dto.operation.OperationResponse;
import com.sysconard.business.dto.operation.OperationSearchRequest;
import com.sysconard.business.dto.operation.OperationSearchResponse;
import com.sysconard.business.dto.operation.UpdateOperationRequest;
import com.sysconard.business.entity.operation.Operation;
import com.sysconard.business.enums.OperationSource;
import com.sysconard.business.exception.operation.OperationAlreadyExistsException;
import com.sysconard.business.exception.operation.OperationNotFoundException;
import com.sysconard.business.repository.operation.OperationRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Serviço responsável pela gestão de operações no sistema.
 * Implementa a lógica de negócio para criação, atualização, busca e listagem de operações.
 * Segue os princípios de Clean Code com responsabilidades bem definidas.
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class OperationService {
    
    private final OperationRepository operationRepository;
    
    /**
     * Cria uma nova operação no sistema.
     * 
     * @param request Dados da operação a ser criada
     * @return Resposta com os dados da operação criada
     * @throws OperationAlreadyExistsException se o código já existir
     */
    public OperationResponse createOperation(CreateOperationRequest request) {
        log.info("Iniciando criação de operação: {}", request.getCode());
        
        // Validar se o código já existe
        if (operationRepository.existsByCode(request.getCode())) {
            log.warn("Tentativa de criar operação com código já existente: {}", request.getCode());
            throw new OperationAlreadyExistsException(request.getCode());
        }
        
        // Criar nova operação
        Operation operation = Operation.builder()
                .code(request.getCode())
                .description(request.getDescription())
                .operationSource(request.getOperationSource())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        // Salvar operação
        Operation savedOperation = operationRepository.save(operation);
        
        log.info("Operação criada com sucesso: {} (ID: {})", savedOperation.getCode(), savedOperation.getId());
        
        // Mapear para resposta
        OperationResponse response = mapToResponse(savedOperation);
        
        log.info("Resposta preparada para operação criada: {}", savedOperation.getCode());
        
        return response;
    }
    
    /**
     * Atualiza uma operação existente no sistema.
     * 
     * @param operationId ID da operação a ser atualizada
     * @param request Dados da operação a ser atualizada
     * @return Resposta com os dados da operação atualizada
     * @throws OperationNotFoundException se a operação não existir
     * @throws OperationAlreadyExistsException se o novo código já existir
     */
    public OperationResponse updateOperation(UUID operationId, UpdateOperationRequest request) {
        log.info("Iniciando atualização de operação: {} (ID: {})", request.getCode(), operationId);
        
        // Buscar operação existente
        Operation existingOperation = operationRepository.findById(operationId)
                .orElseThrow(() -> new OperationNotFoundException(operationId));
        
        // Validar código se fornecido
        if (request.getCode() != null && !request.getCode().trim().isEmpty()) {
            if (operationRepository.existsByCodeAndIdNot(request.getCode(), operationId)) {
                log.warn("Tentativa de atualizar operação com código já existente: {}", request.getCode());
                throw new OperationAlreadyExistsException(request.getCode());
            }
            existingOperation.setCode(request.getCode());
        }
        
        // Atualizar descrição se fornecida
        if (request.getDescription() != null && !request.getDescription().trim().isEmpty()) {
            existingOperation.setDescription(request.getDescription());
        }
        
        // Atualizar operationSource se fornecido
        if (request.getOperationSource() != null) {
            existingOperation.setOperationSource(request.getOperationSource());
        }
        
        // Atualizar timestamp
        existingOperation.setUpdatedAt(LocalDateTime.now());
        
        // Salvar operação atualizada
        Operation updatedOperation = operationRepository.save(existingOperation);
        
        log.info("Operação atualizada com sucesso: {} (ID: {})", updatedOperation.getCode(), updatedOperation.getId());
        
        // Mapear para resposta
        OperationResponse response = mapToResponse(updatedOperation);
        
        log.info("Resposta preparada para operação atualizada: {}", updatedOperation.getCode());
        
        return response;
    }
    
    /**
     * Busca uma operação pelo ID.
     * 
     * @param operationId ID da operação
     * @return Resposta com os dados da operação
     * @throws OperationNotFoundException se a operação não existir
     */
    @Transactional(readOnly = true)
    public OperationResponse getOperationById(UUID operationId) {
        log.debug("Buscando operação por ID: {}", operationId);
        
        Operation operation = operationRepository.findById(operationId)
                .orElseThrow(() -> new OperationNotFoundException(operationId));
        
        log.debug("Operação encontrada: {} (ID: {})", operation.getCode(), operation.getId());
        
        return mapToResponse(operation);
    }
    
    /**
     * Busca operações com filtros e paginação.
     * 
     * @param request Requisição com filtros e parâmetros de paginação
     * @return Resposta com operações encontradas, informações de paginação e totalizadores
     */
    @Transactional(readOnly = true)
    public OperationSearchResponse findOperationsWithFilters(OperationSearchRequest request) {
        log.info("Buscando operações com filtros: code={}, operationSource={}, page={}, size={}, sortBy={}, sortDir={}",
                request.getCode(), request.getOperationSource(), request.getPage(), request.getSize(), request.getSortBy(), request.getSortDir());
        
        try {
            // Criar Pageable com ordenação
            Sort sort = Sort.by(Sort.Direction.fromString(request.getSortDir()), request.getSortBy());
            Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);
            
            // Buscar operações com filtros
            Page<Operation> operationsPage = operationRepository.findOperationsWithFilters(
                    request.getCode(), 
                    request.getOperationSource() != null ? request.getOperationSource().name() : null,
                    pageable);
            
            // Mapear para DTOs
            List<OperationResponse> operations = operationsPage.getContent().stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
            
            // Buscar totalizadores
            long totalSell = operationRepository.countSellOperations();
            long totalExchange = operationRepository.countExchangeOperations();
            long totalOperations = operationRepository.countTotalOperations();
            
            // Criar contadores
            OperationSearchResponse.OperationCounts counts = OperationSearchResponse.OperationCounts.builder()
                    .totalSell(totalSell)
                    .totalExchange(totalExchange)
                    .totalOperations(totalOperations)
                    .build();
            
            // Criar informações de paginação
            OperationSearchResponse.PaginationInfo paginationInfo = OperationSearchResponse.PaginationInfo.builder()
                    .currentPage(operationsPage.getNumber())
                    .pageSize(operationsPage.getSize())
                    .totalPages(operationsPage.getTotalPages())
                    .totalElements(operationsPage.getTotalElements())
                    .hasNext(operationsPage.hasNext())
                    .hasPrevious(operationsPage.hasPrevious())
                    .build();
            
            // Criar resposta
            OperationSearchResponse response = OperationSearchResponse.builder()
                    .operations(operations)
                    .pagination(paginationInfo)
                    .counts(counts)
                    .totalElements(operationsPage.getTotalElements())
                    .totalPages(operationsPage.getTotalPages())
                    .currentPage(operationsPage.getNumber())
                    .pageSize(operationsPage.getSize())
                    .hasNext(operationsPage.hasNext())
                    .hasPrevious(operationsPage.hasPrevious())
                    .build();
            
            log.info("Encontradas {} operações de {} total, página {} de {}",
                    operations.size(), operationsPage.getTotalElements(),
                    operationsPage.getNumber() + 1, operationsPage.getTotalPages());
            
            log.debug("Totalizadores: Venda={}, Troca={}, Total={}", totalSell, totalExchange, totalOperations);
            
            return response;
            
        } catch (Exception e) {
            log.error("Erro ao buscar operações com filtros: {}", e.getMessage(), e);
            throw new RuntimeException("Erro interno ao buscar operações", e);
        }
    }
    
    /**
     * Mapeia uma entidade Operation para OperationResponse.
     * 
     * @param operation Entidade Operation
     * @return OperationResponse mapeada
     */
    private OperationResponse mapToResponse(Operation operation) {
        return OperationResponse.builder()
                .id(operation.getId())
                .code(operation.getCode())
                .description(operation.getDescription())
                .operationSource(operation.getOperationSource())
                .createdAt(operation.getCreatedAt())
                .updatedAt(operation.getUpdatedAt())
                .build();
    }

    // criar metodo para buscar todas as operações do tipo sell
    public List<OperationResponse> findAllSellOperations() {
        return operationRepository.findByOperationSource(OperationSource.SELL).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // criar metodo para buscar todas as operações do tipo exchange
    public List<OperationResponse> findAllExchangeOperations() {
        return operationRepository.findByOperationSource(OperationSource.EXCHANGE).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Busca todos os códigos de operações cadastradas no sistema.
     * Utilizado pelo OperationKindService para filtrar tipos de operação disponíveis.
     * 
     * @return Lista com todos os códigos de operações cadastradas
     */
    @Transactional(readOnly = true)
    public List<String> getAllOperationCodes() {
        log.debug("Buscando todos os códigos de operações cadastradas");
        List<String> codes = operationRepository.findAllOperationCodes();
        log.debug("Total de códigos encontrados: {}", codes.size());
        return codes;
    }
}
