package com.sysconard.business.service.sync;

import com.sysconard.business.dto.sync.YearlySalesSyncRequest;
import com.sysconard.business.dto.sync.YearlySalesSyncResponse;
import com.sysconard.business.entity.sell.YearSell;
import com.sysconard.business.repository.sell.MonthlySellRepository;
import com.sysconard.business.repository.sell.YearSellRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Serviço responsável pela sincronização de vendas anuais.
 * Implementa a lógica de negócio para agregar dados de monthly_sells por ano e loja,
 * validar registros existentes e persistir em year_sells.
 * Segue os princípios de Clean Code com responsabilidades bem definidas.
 * 
 * @author Business API
 * @version 1.0
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class YearlySalesSyncService {
    
    private final MonthlySellRepository monthlySellRepository;
    private final YearSellRepository yearSellRepository;
    
    /**
     * Executa a sincronização completa de vendas anuais para o ano especificado.
     * Agrega vendas mensais por loja e ano, sempre atualizando registros existentes.
     * 
     * @param request Requisição com ano de sincronização
     * @return Resposta com estatísticas do processo
     * @throws RuntimeException se houver erro na sincronização
     */
    public YearlySalesSyncResponse syncYearlySales(YearlySalesSyncRequest request) {
        log.info("Iniciando sincronização de vendas anuais: year={}", request.year());
        
        try {
            // Passo 1: Buscar dados agregados de vendas mensais
            List<YearSell> aggregatedData = fetchMonthlySalesData(request.year());
            
            if (aggregatedData.isEmpty()) {
                log.warn("Nenhum dado de venda mensal encontrado para o ano: {}", request.year());
                return buildEmptyResponse(request);
            }
            
            // Passo 2: Processar cada registro individualmente (upsert)
            UpsertResult result = processUpsertData(aggregatedData);
            
            // Passo 3: Calcular estatísticas
            int storesProcessed = (int) aggregatedData.stream()
                    .map(YearSell::getStoreCode)
                    .distinct()
                    .count();
            
            log.info("Sincronização de vendas anuais concluída: criados={}, atualizados={}, lojas={}", 
                    result.created(), result.updated(), storesProcessed);
            
            return YearlySalesSyncResponse.builder()
                    .created(result.created())
                    .updated(result.updated())
                    .processedAt(LocalDateTime.now())
                    .year(request.year())
                    .storesProcessed(storesProcessed)
                    .build();
            
        } catch (Exception e) {
            log.error("Erro durante sincronização de vendas anuais: {}", e.getMessage(), e);
            throw new RuntimeException("Erro na sincronização de vendas anuais: " + e.getMessage(), e);
        }
    }
    
    /**
     * Busca dados agregados de vendas mensais por ano e loja.
     * 
     * @param year Ano para agregação
     * @return Lista de entidades YearSell com dados agregados
     */
    private List<YearSell> fetchMonthlySalesData(Integer year) {
        log.info("Buscando dados agregados de vendas mensais para o ano: {}", year);
        
        // Buscar dados agregados usando query SQL otimizada
        List<Object[]> rawData = monthlySellRepository.findYearlyAggregatedSalesByYear(year);
        
        log.info("Dados agregados obtidos: {} registros", rawData.size());
        
        if (rawData.isEmpty()) {
            log.warn("Nenhum dado encontrado na tabela monthly_sells para o ano {}", year);
            return List.of();
        }
        
        // Log dos primeiros registros para debug
        log.debug("Primeiros registros encontrados:");
        for (int i = 0; i < Math.min(3, rawData.size()); i++) {
            Object[] record = rawData.get(i);
            log.debug("Registro {}: storeId={}, storeCode={}, storeName={}, year={}, total={}", 
                    i, record[0], record[1], record[2], record[3], record[4]);
        }
        
        // Mapear para entidades YearSell
        List<YearSell> result = rawData.stream()
                .map(this::mapToYearSell)
                .collect(Collectors.toList());
        
        log.info("Mapeamento concluído: {} entidades YearSell criadas", result.size());
        
        return result;
    }
    
    /**
     * Mapeia um registro agregado para entidade YearSell.
     * 
     * @param rawData Array de objetos retornado pela query SQL
     * @return Entidade YearSell
     */
    private YearSell mapToYearSell(Object[] rawData) {
        UUID storeId = (UUID) rawData[0];
        String storeCode = (String) rawData[1];
        String storeName = (String) rawData[2];
        Integer year = (Integer) rawData[3];
        BigDecimal total = (BigDecimal) rawData[4];
        
        return YearSell.builder()
                .storeId(storeId)
                .storeCode(storeCode)
                .storeName(storeName)
                .year(year)
                .total(total != null ? total : BigDecimal.ZERO)
                .build();
    }
    
    /**
     * Processa dados usando upsert (insert ou update) individual para cada registro.
     * Evita problemas de duplicata processando um registro por vez.
     * 
     * @param aggregatedData Dados agregados para processar
     * @return Resultado do processamento upsert
     */
    private UpsertResult processUpsertData(List<YearSell> aggregatedData) {
        log.debug("Processando dados com upsert individual");
        
        int created = 0;
        int updated = 0;
        
        for (YearSell aggregated : aggregatedData) {
            try {
                // Buscar registro existente para esta loja e ano
                Optional<YearSell> existingOpt = yearSellRepository.findByStoreIdAndYear(
                        aggregated.getStoreId(), 
                        aggregated.getYear()
                );
                
                if (existingOpt.isPresent()) {
                    // Atualizar registro existente
                    YearSell existing = existingOpt.get();
                    log.debug("Atualizando registro existente: storeId={}, year={}, total antigo={}, total novo={}", 
                            existing.getStoreId(), existing.getYear(), existing.getTotal(), aggregated.getTotal());
                    
                    updateExistingRecord(existing, aggregated);
                    yearSellRepository.save(existing);
                    updated++;
                    
                } else {
                    // Criar novo registro
                    log.debug("Criando novo registro: storeId={}, year={}, total={}", 
                            aggregated.getStoreId(), aggregated.getYear(), aggregated.getTotal());
                    
                    yearSellRepository.save(aggregated);
                    created++;
                }
                
            } catch (Exception e) {
                log.error("Erro ao processar registro: storeId={}, year={}, error={}", 
                        aggregated.getStoreId(), aggregated.getYear(), e.getMessage(), e);
                throw new RuntimeException("Erro ao processar registro: " + e.getMessage(), e);
            }
        }
        
        log.debug("Processamento upsert concluído: criados={}, atualizados={}", created, updated);
        
        return new UpsertResult(created, updated);
    }
    
    /**
     * Atualiza campos de um registro existente com dados agregados.
     * 
     * @param existing Registro existente no banco
     * @param aggregated Dados agregados para atualizar
     */
    private void updateExistingRecord(YearSell existing, YearSell aggregated) {
        existing.setStoreName(aggregated.getStoreName());
        existing.setTotal(aggregated.getTotal());
    }
    
    
    /**
     * Constrói resposta vazia para casos sem dados.
     * 
     * @param request Requisição original
     * @return Resposta com valores zerados
     */
    private YearlySalesSyncResponse buildEmptyResponse(YearlySalesSyncRequest request) {
        return YearlySalesSyncResponse.builder()
                .created(0)
                .updated(0)
                .processedAt(LocalDateTime.now())
                .year(request.year())
                .storesProcessed(0)
                .build();
    }
    
    /**
     * Record interno para resultado de upsert.
     */
    private record UpsertResult(
        int created,
        int updated
    ) {}
}
