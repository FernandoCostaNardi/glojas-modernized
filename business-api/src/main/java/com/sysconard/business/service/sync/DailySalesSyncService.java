package com.sysconard.business.service.sync;

import com.sysconard.business.dto.sell.StoreReportByDayResponse;
import com.sysconard.business.dto.sell.StoreReportRequest;
import com.sysconard.business.dto.store.StoreResponseDto;
import com.sysconard.business.dto.sync.DailySalesSyncRequest;
import com.sysconard.business.dto.sync.DailySalesSyncResponse;
import com.sysconard.business.entity.sell.DailySell;
import com.sysconard.business.repository.sell.DailySellRepository;
import com.sysconard.business.service.sell.SellService;
import com.sysconard.business.service.store.StoreService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Serviço responsável pela sincronização de vendas diárias.
 * Implementa a lógica de negócio para buscar dados externos,
 * validar registros existentes e persistir em lote.
 * Segue os princípios de Clean Code com responsabilidades bem definidas.
 * 
 * @author Business API
 * @version 1.0
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class DailySalesSyncService {
    
    private final StoreService storeService;
    private final SellService sellService;
    private final DailySellRepository dailySellRepository;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    /**
     * Executa a sincronização completa de vendas diárias para o período especificado.
     * 
     * @param request Requisição com período de sincronização
     * @return Resposta com estatísticas do processo
     * @throws RuntimeException se houver erro na sincronização
     */
    public DailySalesSyncResponse syncDailySales(DailySalesSyncRequest request) {
        log.info("Iniciando sincronização de vendas diárias: startDate={}, endDate={}", 
                request.startDate(), request.endDate());
        
        try {
            // Passo 1: Obter dados externos
            List<DailySell> externalData = fetchExternalData(request.startDate(), request.endDate());
            
            if (externalData.isEmpty()) {
                log.warn("Nenhum dado externo encontrado para o período: {} a {}", 
                        request.startDate(), request.endDate());
                return buildEmptyResponse(request);
            }
            
            // Passo 2: Validação inteligente no banco
            SyncDataSeparation separation = separateCreateUpdateData(externalData, request.startDate(), request.endDate());
            
            // Passo 3: Persistência otimizada
            PersistenceResult result = persistData(separation);
            
            log.info("Sincronização concluída com sucesso: criados={}, atualizados={}, lojas={}", 
                    result.created(), result.updated(), separation.uniqueStores());
            
            return DailySalesSyncResponse.builder()
                    .created(result.created())
                    .updated(result.updated())
                    .processedAt(LocalDateTime.now())
                    .startDate(request.startDate().format(DATE_FORMATTER))
                    .endDate(request.endDate().format(DATE_FORMATTER))
                    .storesProcessed(separation.uniqueStores())
                    .build();
            
        } catch (Exception e) {
            log.error("Erro durante sincronização de vendas diárias: {}", e.getMessage(), e);
            throw new RuntimeException("Erro na sincronização de vendas diárias: " + e.getMessage(), e);
        }
    }
    
    /**
     * Busca dados externos de vendas diárias.
     * 
     * @param startDate Data de início
     * @param endDate Data de fim
     * @return Lista de entidades DailySell com dados externos
     */
    private List<DailySell> fetchExternalData(LocalDate startDate, LocalDate endDate) {
        log.debug("Buscando dados externos para período: {} a {}", startDate, endDate);
        
        // 1. Obter lojas ativas
        List<StoreResponseDto> activeStores = storeService.getAllActiveStores();
        
        if (activeStores.isEmpty()) {
            log.warn("Nenhuma loja ativa encontrada");
            return List.of();
        }
        
        List<String> storeCodes = activeStores.stream()
                .map(StoreResponseDto::getCode)
                .collect(Collectors.toList());
        
        log.debug("Lojas ativas encontradas: {}", storeCodes.size());
        
        // 2. Buscar relatório de vendas por dia
        StoreReportRequest reportRequest = StoreReportRequest.builder()
                .startDate(startDate)
                .endDate(endDate)
                .storeCodes(storeCodes)
                .build();
        
        List<StoreReportByDayResponse> reportData = sellService.getStoreReportByDay(reportRequest);
        
        log.debug("Registros de vendas obtidos da API externa: {}", reportData.size());
        
        // 3. Mapear para entidades DailySell
        return mapToDailySell(reportData, activeStores);
    }
    
    /**
     * Mapeia dados do relatório externo para entidades DailySell.
     * Utiliza nomes de lojas da fonte confiável (banco de dados) ao invés do relatório.
     * 
     * @param reportData Dados do relatório externo
     * @param activeStores Lista de lojas ativas
     * @return Lista de entidades DailySell
     */
    private List<DailySell> mapToDailySell(List<StoreReportByDayResponse> reportData, List<StoreResponseDto> activeStores) {
        // Criar mapa de códigos para UUIDs das lojas
        Map<String, UUID> storeCodeToIdMap = createStoreCodeToIdMap(activeStores);
        
        // Criar mapa de códigos para nomes das lojas (fonte confiável)
        Map<String, String> storeCodeToNameMap = createStoreCodeToNameMap(activeStores);
        
        log.debug("Mapa de nomes de lojas criado: {} lojas mapeadas", storeCodeToNameMap.size());
        
        // Validar que todos os códigos do relatório têm nomes no mapa
        long missingNames = reportData.stream()
                .filter(report -> !storeCodeToNameMap.containsKey(report.storeCode()))
                .count();
        
        if (missingNames > 0) {
            log.warn("Atenção: {} registros do relatório não possuem nome no mapa de lojas ativas", missingNames);
        }
        
        List<DailySell> result = reportData.stream()
                .filter(report -> storeCodeToIdMap.containsKey(report.storeCode()))
                .map(report -> mapSingleRecord(report, storeCodeToIdMap, storeCodeToNameMap))
                .collect(Collectors.toList());
        
        log.debug("Mapeamento concluído: {} registros processados", result.size());
        
        return result;
    }
    
    /**
     * Cria mapa de código de loja para UUID para lookup eficiente.
     */
    private Map<String, UUID> createStoreCodeToIdMap(List<StoreResponseDto> activeStores) {
        Map<String, UUID> codeToIdMap = new HashMap<>();
        
        for (StoreResponseDto store : activeStores) {
            try {
                UUID storeId = UUID.fromString(store.getId());
                codeToIdMap.put(store.getCode(), storeId);
            } catch (IllegalArgumentException e) {
                log.warn("ID de loja inválido ignorado: {} (código: {})", store.getId(), store.getCode());
            }
        }
        
        return codeToIdMap;
    }
    
    /**
     * Cria mapa de código de loja para nome da loja para lookup eficiente.
     * Utiliza a fonte confiável (banco de dados) ao invés do relatório da Legacy API.
     * 
     * @param activeStores Lista de lojas ativas
     * @return Map com código da loja como chave e nome da loja como valor
     */
    private Map<String, String> createStoreCodeToNameMap(List<StoreResponseDto> activeStores) {
        Map<String, String> codeToNameMap = new HashMap<>();
        
        for (StoreResponseDto store : activeStores) {
            if (store.getCode() != null && store.getName() != null) {
                codeToNameMap.put(store.getCode(), store.getName());
            } else {
                log.warn("Loja com código ou nome nulo ignorada: código={}, nome={}", 
                        store.getCode(), store.getName());
            }
        }
        
        return codeToNameMap;
    }
    
    /**
     * Mapeia um único registro do relatório para DailySell.
     * Utiliza o nome da loja da fonte confiável (banco de dados) ao invés do relatório.
     * 
     * @param report Dados do relatório externo
     * @param storeCodeToIdMap Mapa de código para UUID da loja
     * @param storeCodeToNameMap Mapa de código para nome da loja (fonte confiável)
     * @return Entidade DailySell mapeada
     */
    private DailySell mapSingleRecord(StoreReportByDayResponse report, 
                                     Map<String, UUID> storeCodeToIdMap,
                                     Map<String, String> storeCodeToNameMap) {
        UUID storeId = storeCodeToIdMap.get(report.storeCode());
        BigDecimal total = report.danfe().add(report.pdv()).subtract(report.troca());
        
        // Obter nome da loja da fonte confiável (banco de dados)
        // Se não encontrar, usar o nome do relatório como fallback (caso de erro)
        String storeName = storeCodeToNameMap.getOrDefault(report.storeCode(), report.storeName());
        
        // Log de validação: verificar se o nome do relatório difere do nome correto
        String reportStoreName = report.storeName();
        if (reportStoreName != null && !reportStoreName.equals(storeName)) {
            log.debug("Nome da loja corrigido: código={}, nome do relatório='{}', nome correto='{}'", 
                    report.storeCode(), reportStoreName, storeName);
        }
        
        return DailySell.builder()
                .storeId(storeId)
                .storeCode(report.storeCode())
                .storeName(storeName)
                .date(report.reportDate())
                .danfe(report.danfe())
                .pdv(report.pdv())
                .exchange(report.troca())
                .total(total)
                .build();
    }
    
    /**
     * Separa dados entre criação e atualização baseado no que já existe no banco.
     */
    private SyncDataSeparation separateCreateUpdateData(List<DailySell> externalData, LocalDate startDate, LocalDate endDate) {
        log.debug("Separando dados para criação vs atualização");
        
        // Buscar registros existentes no banco
        List<UUID> storeIds = externalData.stream()
                .map(DailySell::getStoreId)
                .distinct()
                .collect(Collectors.toList());
        
        List<DailySell> existingRecords = dailySellRepository.findExistingRecords(storeIds, startDate, endDate);
        
        // Criar mapa para lookup eficiente (chave: storeId + data)
        Map<String, DailySell> existingMap = existingRecords.stream()
                .collect(Collectors.toMap(
                    record -> record.getStoreId() + "|" + record.getDate(),
                    record -> record
                ));
        
        // Separar em listas
        List<DailySell> toCreate = new ArrayList<>();
        List<DailySell> toUpdate = new ArrayList<>();
        
        for (DailySell external : externalData) {
            String key = external.getStoreId() + "|" + external.getDate();
            
            if (existingMap.containsKey(key)) {
                // Atualizar registro existente
                DailySell existing = existingMap.get(key);
                updateExistingRecord(existing, external);
                toUpdate.add(existing);
            } else {
                // Criar novo registro
                toCreate.add(external);
            }
        }
        
        Set<String> uniqueStores = externalData.stream()
                .map(DailySell::getStoreCode)
                .collect(Collectors.toSet());
        
        log.debug("Dados separados: criar={}, atualizar={}, lojas únicas={}", 
                toCreate.size(), toUpdate.size(), uniqueStores.size());
        
        return new SyncDataSeparation(toCreate, toUpdate, uniqueStores.size());
    }
    
    /**
     * Atualiza campos de um registro existente com dados externos.
     * O nome da loja já vem correto do mapeamento (fonte confiável).
     * 
     * @param existing Registro existente no banco
     * @param external Dados externos já mapeados com nome correto
     */
    private void updateExistingRecord(DailySell existing, DailySell external) {
        // Validar se o nome está sendo atualizado corretamente
        if (!existing.getStoreName().equals(external.getStoreName())) {
            log.debug("Atualizando nome da loja: código={}, nome antigo='{}', nome novo='{}'", 
                    existing.getStoreCode(), existing.getStoreName(), external.getStoreName());
        }
        
        existing.setStoreName(external.getStoreName());
        existing.setDanfe(external.getDanfe());
        existing.setPdv(external.getPdv());
        existing.setExchange(external.getExchange());
        existing.setTotal(external.getTotal());
    }
    
    /**
     * Persiste dados em lote de forma otimizada.
     */
    private PersistenceResult persistData(SyncDataSeparation separation) {
        log.debug("Persistindo dados: criar={}, atualizar={}", 
                separation.toCreate().size(), separation.toUpdate().size());
        
        int created = 0;
        int updated = 0;
        
        // Batch insert para novos registros
        if (!separation.toCreate().isEmpty()) {
            List<DailySell> savedNew = dailySellRepository.saveAll(separation.toCreate());
            created = savedNew.size();
            log.debug("Registros criados: {}", created);
        }
        
        // Batch update para registros existentes
        if (!separation.toUpdate().isEmpty()) {
            List<DailySell> savedUpdated = dailySellRepository.saveAll(separation.toUpdate());
            updated = savedUpdated.size();
            log.debug("Registros atualizados: {}", updated);
        }
        
        return new PersistenceResult(created, updated);
    }
    
    /**
     * Constrói resposta vazia para casos sem dados.
     */
    private DailySalesSyncResponse buildEmptyResponse(DailySalesSyncRequest request) {
        return DailySalesSyncResponse.builder()
                .created(0)
                .updated(0)
                .processedAt(LocalDateTime.now())
                .startDate(request.startDate().format(DATE_FORMATTER))
                .endDate(request.endDate().format(DATE_FORMATTER))
                .storesProcessed(0)
                .build();
    }
    
    /**
     * Record interno para separação de dados.
     */
    private record SyncDataSeparation(
        List<DailySell> toCreate,
        List<DailySell> toUpdate,
        int uniqueStores
    ) {}
    
    /**
     * Record interno para resultado de persistência.
     */
    private record PersistenceResult(
        int created,
        int updated
    ) {}
}
