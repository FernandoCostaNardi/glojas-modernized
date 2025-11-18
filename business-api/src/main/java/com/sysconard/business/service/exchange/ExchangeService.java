package com.sysconard.business.service.exchange;

import com.sysconard.business.dto.exchange.CreateExchangeRequest;
import com.sysconard.business.dto.exchange.ExchangeResponse;
import com.sysconard.business.dto.exchange.ExchangeSearchRequest;
import com.sysconard.business.dto.exchange.UpdateExchangeRequest;
import com.sysconard.business.entity.exchange.Exchange;
import com.sysconard.business.repository.exchange.ExchangeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Serviço responsável pela gestão de trocas no sistema.
 * Implementa a lógica de negócio para CRUD completo de trocas.
 * Segue os princípios de Clean Code com responsabilidades bem definidas.
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ExchangeService {
    
    private final ExchangeRepository exchangeRepository;
    
    /**
     * Cria uma nova troca.
     * 
     * @param request Requisição com dados da troca
     * @return Resposta com a troca criada
     * @throws IllegalArgumentException se já existir uma troca com o mesmo documentCode e storeCode
     */
    public ExchangeResponse createExchange(CreateExchangeRequest request) {
        log.info("Criando nova troca: documentCode={}, storeCode={}", 
                request.documentCode(), request.storeCode());
        
        // Verificar se já existe
        if (exchangeRepository.existsByDocumentCodeAndStoreCode(
                request.documentCode(), request.storeCode())) {
            throw new IllegalArgumentException(
                    String.format("Já existe uma troca com documentCode=%s e storeCode=%s", 
                            request.documentCode(), request.storeCode()));
        }
        
        Exchange exchange = Exchange.builder()
                .documentCode(request.documentCode())
                .storeCode(request.storeCode())
                .operationCode(request.operationCode())
                .originCode(request.originCode())
                .employeeCode(request.employeeCode())
                .documentNumber(request.documentNumber())
                .nfeKey(request.nfeKey())
                .issueDate(request.issueDate())
                .observation(request.observation())
                .newSaleNumber(request.newSaleNumber())
                .newSaleNfeKey(request.newSaleNfeKey())
                .build();
        
        Exchange saved = exchangeRepository.save(exchange);
        log.info("Troca criada com sucesso: id={}", saved.getId());
        
        return toResponse(saved);
    }
    
    /**
     * Atualiza uma troca existente.
     * 
     * @param id ID da troca a ser atualizada
     * @param request Requisição com dados a serem atualizados
     * @return Resposta com a troca atualizada
     * @throws IllegalArgumentException se a troca não for encontrada
     */
    public ExchangeResponse updateExchange(UUID id, UpdateExchangeRequest request) {
        log.info("Atualizando troca: id={}", id);
        
        Exchange exchange = exchangeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Troca não encontrada com id: " + id));
        
        // Atualizar apenas campos fornecidos
        if (request.documentCode() != null) {
            exchange.setDocumentCode(request.documentCode());
        }
        if (request.storeCode() != null) {
            exchange.setStoreCode(request.storeCode());
        }
        if (request.operationCode() != null) {
            exchange.setOperationCode(request.operationCode());
        }
        if (request.originCode() != null) {
            exchange.setOriginCode(request.originCode());
        }
        if (request.employeeCode() != null) {
            exchange.setEmployeeCode(request.employeeCode());
        }
        if (request.documentNumber() != null) {
            exchange.setDocumentNumber(request.documentNumber());
        }
        if (request.nfeKey() != null) {
            exchange.setNfeKey(request.nfeKey());
        }
        if (request.issueDate() != null) {
            exchange.setIssueDate(request.issueDate());
        }
        if (request.observation() != null) {
            exchange.setObservation(request.observation());
        }
        if (request.newSaleNumber() != null) {
            exchange.setNewSaleNumber(request.newSaleNumber());
        }
        if (request.newSaleNfeKey() != null) {
            exchange.setNewSaleNfeKey(request.newSaleNfeKey());
        }
        
        Exchange saved = exchangeRepository.save(exchange);
        log.info("Troca atualizada com sucesso: id={}", saved.getId());
        
        return toResponse(saved);
    }
    
    /**
     * Busca uma troca por ID.
     * 
     * @param id ID da troca
     * @return Resposta com a troca encontrada
     * @throws IllegalArgumentException se a troca não for encontrada
     */
    @Transactional(readOnly = true)
    public ExchangeResponse getExchangeById(UUID id) {
        log.debug("Buscando troca por ID: {}", id);
        
        Exchange exchange = exchangeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Troca não encontrada com id: " + id));
        
        return toResponse(exchange);
    }
    
    /**
     * Lista todas as trocas com paginação.
     * 
     * @param pageable Configuração de paginação
     * @return Página com trocas
     */
    @Transactional(readOnly = true)
    public Page<ExchangeResponse> getAllExchanges(Pageable pageable) {
        log.debug("Listando todas as trocas - page: {}, size: {}", 
                pageable.getPageNumber(), pageable.getPageSize());
        
        return exchangeRepository.findAll(pageable)
                .map(this::toResponse);
    }
    
    /**
     * Busca trocas com filtros e paginação.
     * 
     * @param request Requisição com filtros
     * @param pageable Configuração de paginação
     * @return Página com trocas filtradas
     */
    @Transactional(readOnly = true)
    public Page<ExchangeResponse> searchExchanges(ExchangeSearchRequest request, Pageable pageable) {
        log.debug("Buscando trocas com filtros - page: {}, size: {}", 
                pageable.getPageNumber(), pageable.getPageSize());
        
        Specification<Exchange> spec = buildSpecification(request);
        
        return exchangeRepository.findAll(spec, pageable)
                .map(this::toResponse);
    }
    
    /**
     * Deleta uma troca por ID.
     * 
     * @param id ID da troca a ser deletada
     * @throws IllegalArgumentException se a troca não for encontrada
     */
    public void deleteExchange(UUID id) {
        log.info("Deletando troca: id={}", id);
        
        if (!exchangeRepository.existsById(id)) {
            throw new IllegalArgumentException("Troca não encontrada com id: " + id);
        }
        
        exchangeRepository.deleteById(id);
        log.info("Troca deletada com sucesso: id={}", id);
    }
    
    /**
     * Constrói uma Specification para busca com filtros.
     * 
     * @param request Requisição com filtros
     * @return Specification construída
     */
    private Specification<Exchange> buildSpecification(ExchangeSearchRequest request) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (request.documentCode() != null && !request.documentCode().trim().isEmpty()) {
                predicates.add(cb.equal(root.get("documentCode"), request.documentCode()));
            }
            if (request.storeCode() != null && !request.storeCode().trim().isEmpty()) {
                predicates.add(cb.equal(root.get("storeCode"), request.storeCode()));
            }
            if (request.operationCode() != null && !request.operationCode().trim().isEmpty()) {
                predicates.add(cb.equal(root.get("operationCode"), request.operationCode()));
            }
            if (request.originCode() != null && !request.originCode().trim().isEmpty()) {
                predicates.add(cb.equal(root.get("originCode"), request.originCode()));
            }
            if (request.employeeCode() != null && !request.employeeCode().trim().isEmpty()) {
                predicates.add(cb.equal(root.get("employeeCode"), request.employeeCode()));
            }
            if (request.startDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("issueDate"), request.startDate()));
            }
            if (request.endDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("issueDate"), request.endDate()));
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
    
    /**
     * Converte uma entidade Exchange para ExchangeResponse.
     * 
     * @param exchange Entidade a ser convertida
     * @return DTO de resposta
     */
    private ExchangeResponse toResponse(Exchange exchange) {
        return new ExchangeResponse(
                exchange.getId(),
                exchange.getDocumentCode(),
                exchange.getStoreCode(),
                exchange.getOperationCode(),
                exchange.getOriginCode(),
                exchange.getEmployeeCode(),
                exchange.getDocumentNumber(),
                exchange.getNfeKey(),
                exchange.getIssueDate(),
                exchange.getObservation(),
                exchange.getNewSaleNumber(),
                exchange.getNewSaleNfeKey(),
                exchange.getCreatedAt(),
                exchange.getUpdatedAt()
        );
    }
}

