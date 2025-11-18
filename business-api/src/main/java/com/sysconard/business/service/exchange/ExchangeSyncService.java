package com.sysconard.business.service.exchange;

import com.sysconard.business.client.LegacyApiClient;
import com.sysconard.business.dto.exchange.ExchangeLegacyDTO;
import com.sysconard.business.dto.exchange.ExchangeSyncRequest;
import com.sysconard.business.dto.exchange.ExchangeSyncResponse;
import com.sysconard.business.entity.exchange.Exchange;
import com.sysconard.business.repository.exchange.ExchangeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Serviço responsável pela sincronização de trocas da Legacy API.
 * Implementa a lógica de negócio para sincronização de trocas com processamento de observações.
 * Segue os princípios de Clean Code com responsabilidades bem definidas.
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ExchangeSyncService {
    
    private final LegacyApiClient legacyApiClient;
    private final ExchangeRepository exchangeRepository;
    private final ExchangeObservationProcessor observationProcessor;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    /**
     * Sincroniza trocas da Legacy API para o banco de dados.
     * Processa observações para extrair informações de nova venda e chave NFE.
     * 
     * @param request Requisição de sincronização com datas inicial e final
     * @return Resposta com estatísticas da sincronização
     * @throws RuntimeException se houver erro na comunicação com a Legacy API
     */
    public ExchangeSyncResponse syncExchanges(ExchangeSyncRequest request) {
        log.info("Iniciando sincronização de trocas - startDate: {}, endDate: {}", 
                request.startDate(), request.endDate());
        
        try {
            // 1. Chamar Legacy API
            List<ExchangeLegacyDTO> legacyExchanges = legacyApiClient.getExchanges(
                    request.startDate(),
                    request.endDate(),
                    request.originCodes(),
                    request.operationCodes()
            );
            
            log.info("Trocas recebidas da Legacy API: {}", 
                    legacyExchanges != null ? legacyExchanges.size() : 0);
            
            if (legacyExchanges == null || legacyExchanges.isEmpty()) {
                log.warn("Nenhuma troca encontrada na Legacy API");
                return new ExchangeSyncResponse(
                        0, 
                        0, 
                        0, 
                        LocalDateTime.now(),
                        request.startDate().format(DATE_FORMATTER),
                        request.endDate().format(DATE_FORMATTER)
                );
            }
            
            // 2. Processar cada troca
            int created = 0;
            int updated = 0;
            int skipped = 0;
            
            for (ExchangeLegacyDTO legacyExchange : legacyExchanges) {
                try {
                    // Processar observação para extrair dados
                    ExchangeObservationProcessor.ProcessedObservationResult processed = 
                            observationProcessor.processObservation(legacyExchange.getObservation());
                    
                    // Verificar se já existe
                    Optional<Exchange> existingExchange = exchangeRepository
                            .findByDocumentCodeAndStoreCode(
                                    legacyExchange.getDocumentCode(),
                                    legacyExchange.getStoreCode()
                            );
                    
                    if (existingExchange.isPresent()) {
                        // Atualizar existente
                        Exchange exchange = existingExchange.get();
                        updateExchangeFromLegacy(exchange, legacyExchange, processed);
                        exchangeRepository.save(exchange);
                        updated++;
                        log.debug("Troca atualizada: documentCode={}, storeCode={}", 
                                legacyExchange.getDocumentCode(), legacyExchange.getStoreCode());
                    } else {
                        // Criar novo
                        Exchange exchange = createExchangeFromLegacy(legacyExchange, processed);
                        exchangeRepository.save(exchange);
                        created++;
                        log.debug("Troca criada: documentCode={}, storeCode={}", 
                                legacyExchange.getDocumentCode(), legacyExchange.getStoreCode());
                    }
                    
                } catch (Exception e) {
                    skipped++;
                    log.error("Erro ao processar troca: documentCode={}, storeCode={}, error={}", 
                            legacyExchange.getDocumentCode(), 
                            legacyExchange.getStoreCode(), 
                            e.getMessage(), 
                            e);
                }
            }
            
            log.info("Sincronização concluída - criadas: {}, atualizadas: {}, ignoradas: {}", 
                    created, updated, skipped);
            
            return new ExchangeSyncResponse(
                    created,
                    updated,
                    skipped,
                    LocalDateTime.now(),
                    request.startDate().format(DATE_FORMATTER),
                    request.endDate().format(DATE_FORMATTER)
            );
            
        } catch (Exception e) {
            log.error("Erro ao sincronizar trocas da Legacy API: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao sincronizar trocas: " + e.getMessage(), e);
        }
    }
    
    /**
     * Cria uma entidade Exchange a partir de um DTO da Legacy API.
     * 
     * @param legacyExchange DTO da Legacy API
     * @param processed Resultado do processamento da observação
     * @return Entidade Exchange criada
     */
    private Exchange createExchangeFromLegacy(
            ExchangeLegacyDTO legacyExchange,
            ExchangeObservationProcessor.ProcessedObservationResult processed) {
        
        return Exchange.builder()
                .documentCode(legacyExchange.getDocumentCode())
                .storeCode(legacyExchange.getStoreCode())
                .operationCode(legacyExchange.getOperationCode())
                .originCode(legacyExchange.getOriginCode())
                .employeeCode(legacyExchange.getEmployeeCode())
                .documentNumber(legacyExchange.getDocumentNumber())
                .nfeKey(legacyExchange.getNfeKey())
                .issueDate(convertToLocalDateTime(legacyExchange.getIssueDate()))
                .observation(legacyExchange.getObservation())
                .newSaleNumber(processed.newSaleNumber())
                .newSaleNfeKey(processed.newSaleNfeKey())
                .build();
    }
    
    /**
     * Atualiza uma entidade Exchange existente com dados da Legacy API.
     * 
     * @param exchange Entidade existente
     * @param legacyExchange DTO da Legacy API
     * @param processed Resultado do processamento da observação
     */
    private void updateExchangeFromLegacy(
            Exchange exchange,
            ExchangeLegacyDTO legacyExchange,
            ExchangeObservationProcessor.ProcessedObservationResult processed) {
        
        exchange.setOperationCode(legacyExchange.getOperationCode());
        exchange.setOriginCode(legacyExchange.getOriginCode());
        exchange.setEmployeeCode(legacyExchange.getEmployeeCode());
        exchange.setDocumentNumber(legacyExchange.getDocumentNumber());
        exchange.setNfeKey(legacyExchange.getNfeKey());
        exchange.setIssueDate(convertToLocalDateTime(legacyExchange.getIssueDate()));
        exchange.setObservation(legacyExchange.getObservation());
        exchange.setNewSaleNumber(processed.newSaleNumber());
        exchange.setNewSaleNfeKey(processed.newSaleNfeKey());
    }
    
    /**
     * Converte Date para LocalDateTime.
     * 
     * @param date Data a ser convertida
     * @return LocalDateTime convertido
     */
    private LocalDateTime convertToLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return LocalDateTime.ofInstant(date.toInstant(), java.time.ZoneId.systemDefault());
    }
}

