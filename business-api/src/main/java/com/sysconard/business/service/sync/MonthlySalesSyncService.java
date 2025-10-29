package com.sysconard.business.service.sync;

import com.sysconard.business.dto.sync.MonthlySalesSyncRequest;
import com.sysconard.business.dto.sync.MonthlySalesSyncResponse;
import com.sysconard.business.entity.sell.MonthlySell;
import com.sysconard.business.repository.sell.DailySellRepository;
import com.sysconard.business.repository.sell.MonthlySellRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Serviço responsável pela sincronização de vendas mensais.
 * Implementa a lógica de negócio para agregar dados de daily_sells por mês e loja,
 * validar registros existentes e persistir em monthly_sells.
 * Segue os princípios de Clean Code com responsabilidades bem definidas.
 * 
 * @author Business API
 * @version 1.0
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MonthlySalesSyncService {
    
    private final DailySellRepository dailySellRepository;
    private final MonthlySellRepository monthlySellRepository;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    /**
     * Executa a sincronização completa de vendas mensais para o período especificado.
     * Agrega vendas diárias por loja e mês, atualizando ou criando registros em monthly_sells.
     * 
     * @param request Requisição com período de sincronização
     * @return Resposta com estatísticas do processo
     * @throws RuntimeException se houver erro na sincronização
     */
    public MonthlySalesSyncResponse syncMonthlySales(MonthlySalesSyncRequest request) {
        log.info("Iniciando sincronização de vendas mensais: startDate={}, endDate={}", 
                request.startDate(), request.endDate());
        
        try {
            // Passo 1: Buscar dados agregados de vendas diárias
            List<MonthlySell> aggregatedData = fetchDailySalesData(request.startDate(), request.endDate());
            
            if (aggregatedData.isEmpty()) {
                log.warn("Nenhum dado de venda encontrado para o período: {} a {}", 
                        request.startDate(), request.endDate());
                return buildEmptyResponse(request);
            }
            
            // Passo 2: Separar dados para criação vs atualização
            SyncDataSeparation separation = separateCreateUpdateData(aggregatedData);
            
            // Passo 3: Persistir dados em lote
            PersistenceResult result = persistData(separation);
            
            // Passo 4: Calcular estatísticas
            int monthsProcessed = calculateMonthsProcessed(request.startDate(), request.endDate());
            int storesProcessed = (int) aggregatedData.stream()
                    .map(MonthlySell::getStoreCode)
                    .distinct()
                    .count();
            
            log.info("Sincronização de vendas mensais concluída: criados={}, atualizados={}, lojas={}, meses={}", 
                    result.created(), result.updated(), storesProcessed, monthsProcessed);
            
            return MonthlySalesSyncResponse.builder()
                    .created(result.created())
                    .updated(result.updated())
                    .processedAt(LocalDateTime.now())
                    .startDate(request.startDate().format(DATE_FORMATTER))
                    .endDate(request.endDate().format(DATE_FORMATTER))
                    .storesProcessed(storesProcessed)
                    .monthsProcessed(monthsProcessed)
                    .build();
            
        } catch (Exception e) {
            log.error("Erro durante sincronização de vendas mensais: {}", e.getMessage(), e);
            throw new RuntimeException("Erro na sincronização de vendas mensais: " + e.getMessage(), e);
        }
    }
    
    /**
     * Busca dados agregados de vendas diárias por mês e loja.
     * 
     * @param startDate Data de início
     * @param endDate Data de fim
     * @return Lista de entidades MonthlySell com dados agregados
     */
    private List<MonthlySell> fetchDailySalesData(LocalDate startDate, LocalDate endDate) {
        log.info("Buscando dados agregados de vendas diárias para período: {} a {}", startDate, endDate);
        
        // Buscar dados agregados usando query SQL otimizada
        List<Object[]> rawData = dailySellRepository.findMonthlyAggregatedSalesByDateRange(startDate, endDate);
        
        log.info("Dados agregados obtidos: {} registros", rawData.size());
        
        if (rawData.isEmpty()) {
            log.warn("Nenhum dado encontrado na tabela daily_sells para o período {} a {}", startDate, endDate);
            return List.of();
        }
        
        // Log dos primeiros registros para debug
        log.debug("Primeiros registros encontrados:");
        for (int i = 0; i < Math.min(3, rawData.size()); i++) {
            Object[] record = rawData.get(i);
            log.debug("Registro {}: storeId={}, storeCode={}, storeName={}, yearMonth={}, total={}", 
                    i, record[0], record[1], record[2], record[3], record[4]);
        }
        
        // Mapear para entidades MonthlySell
        List<MonthlySell> result = rawData.stream()
                .map(this::mapToMonthlySell)
                .collect(Collectors.toList());
        
        log.info("Mapeamento concluído: {} entidades MonthlySell criadas", result.size());
        
        return result;
    }
    
    /**
     * Mapeia um registro agregado para entidade MonthlySell.
     * 
     * @param rawData Array de objetos retornado pela query SQL
     * @return Entidade MonthlySell
     */
    private MonthlySell mapToMonthlySell(Object[] rawData) {
        UUID storeId = (UUID) rawData[0];
        String storeCode = (String) rawData[1];
        String storeName = (String) rawData[2];
        String yearMonth = (String) rawData[3];
        BigDecimal total = (BigDecimal) rawData[4];
        
        return MonthlySell.builder()
                .storeId(storeId)
                .storeCode(storeCode)
                .storeName(storeName)
                .yearMonth(yearMonth)
                .total(total != null ? total : BigDecimal.ZERO)
                .build();
    }
    
    /**
     * Separa dados entre criação e atualização baseado no que já existe no banco.
     * 
     * @param aggregatedData Dados agregados para processar
     * @return Separação entre dados para criar e atualizar
     */
    private SyncDataSeparation separateCreateUpdateData(List<MonthlySell> aggregatedData) {
        log.debug("Separando dados para criação vs atualização");
        
        // Extrair chaves para busca (storeId + yearMonth)
        List<UUID> storeIds = aggregatedData.stream()
                .map(MonthlySell::getStoreId)
                .distinct()
                .collect(Collectors.toList());
        
        List<String> yearMonths = aggregatedData.stream()
                .map(MonthlySell::getYearMonth)
                .distinct()
                .collect(Collectors.toList());
        
        // Buscar registros existentes
        List<MonthlySell> existingRecords = monthlySellRepository.findExistingRecords(storeIds, yearMonths);
        
        // Criar mapa para lookup eficiente (chave: storeId + yearMonth)
        Map<String, MonthlySell> existingMap = existingRecords.stream()
                .collect(Collectors.toMap(
                    record -> record.getStoreId() + "|" + record.getYearMonth(),
                    record -> record
                ));
        
        // Separar em listas
        List<MonthlySell> toCreate = new ArrayList<>();
        List<MonthlySell> toUpdate = new ArrayList<>();
        
        for (MonthlySell aggregated : aggregatedData) {
            String key = aggregated.getStoreId() + "|" + aggregated.getYearMonth();
            
            if (existingMap.containsKey(key)) {
                // Atualizar registro existente
                MonthlySell existing = existingMap.get(key);
                updateExistingRecord(existing, aggregated);
                toUpdate.add(existing);
            } else {
                // Criar novo registro
                toCreate.add(aggregated);
            }
        }
        
        log.debug("Dados separados: criar={}, atualizar={}", toCreate.size(), toUpdate.size());
        
        return new SyncDataSeparation(toCreate, toUpdate);
    }
    
    /**
     * Atualiza campos de um registro existente com dados agregados.
     * 
     * @param existing Registro existente no banco
     * @param aggregated Dados agregados para atualizar
     */
    private void updateExistingRecord(MonthlySell existing, MonthlySell aggregated) {
        existing.setStoreName(aggregated.getStoreName());
        existing.setTotal(aggregated.getTotal());
    }
    
    /**
     * Persiste dados em lote de forma otimizada.
     * 
     * @param separation Separação entre dados para criar e atualizar
     * @return Resultado da persistência
     */
    private PersistenceResult persistData(SyncDataSeparation separation) {
        log.info("Persistindo dados: criar={}, atualizar={}", 
                separation.toCreate().size(), separation.toUpdate().size());
        
        int created = 0;
        int updated = 0;
        
        // Batch insert para novos registros
        if (!separation.toCreate().isEmpty()) {
            log.info("Iniciando inserção de {} registros novos", separation.toCreate().size());
            
            // Log dos primeiros registros que serão criados
            for (int i = 0; i < Math.min(3, separation.toCreate().size()); i++) {
                MonthlySell record = separation.toCreate().get(i);
                log.debug("Criando registro {}: storeId={}, storeCode={}, yearMonth={}, total={}", 
                        i, record.getStoreId(), record.getStoreCode(), record.getYearMonth(), record.getTotal());
            }
            
            List<MonthlySell> savedNew = monthlySellRepository.saveAll(separation.toCreate());
            created = savedNew.size();
            log.info("Registros criados com sucesso: {}", created);
        }
        
        // Batch update para registros existentes
        if (!separation.toUpdate().isEmpty()) {
            log.info("Iniciando atualização de {} registros existentes", separation.toUpdate().size());
            
            List<MonthlySell> savedUpdated = monthlySellRepository.saveAll(separation.toUpdate());
            updated = savedUpdated.size();
            log.info("Registros atualizados com sucesso: {}", updated);
        }
        
        log.info("Persistência concluída: criados={}, atualizados={}", created, updated);
        
        return new PersistenceResult(created, updated);
    }
    
    /**
     * Calcula o número de meses processados no período.
     * 
     * @param startDate Data de início
     * @param endDate Data de fim
     * @return Número de meses no período
     */
    private int calculateMonthsProcessed(LocalDate startDate, LocalDate endDate) {
        YearMonth startMonth = YearMonth.from(startDate);
        YearMonth endMonth = YearMonth.from(endDate);
        
        int months = 0;
        YearMonth current = startMonth;
        
        while (!current.isAfter(endMonth)) {
            months++;
            current = current.plusMonths(1);
        }
        
        return months;
    }
    
    /**
     * Constrói resposta vazia para casos sem dados.
     * 
     * @param request Requisição original
     * @return Resposta com valores zerados
     */
    private MonthlySalesSyncResponse buildEmptyResponse(MonthlySalesSyncRequest request) {
        return MonthlySalesSyncResponse.builder()
                .created(0)
                .updated(0)
                .processedAt(LocalDateTime.now())
                .startDate(request.startDate().format(DATE_FORMATTER))
                .endDate(request.endDate().format(DATE_FORMATTER))
                .storesProcessed(0)
                .monthsProcessed(0)
                .build();
    }
    
    /**
     * Record interno para separação de dados.
     */
    private record SyncDataSeparation(
        List<MonthlySell> toCreate,
        List<MonthlySell> toUpdate
    ) {}
    
    /**
     * Record interno para resultado de persistência.
     */
    private record PersistenceResult(
        int created,
        int updated
    ) {}
}
