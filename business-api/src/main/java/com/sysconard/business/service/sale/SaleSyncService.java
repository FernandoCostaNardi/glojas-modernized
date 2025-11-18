package com.sysconard.business.service.sale;

import com.sysconard.business.client.LegacyApiClient;
import com.sysconard.business.dto.sale.SaleItemLegacyDTO;
import com.sysconard.business.dto.sale.SaleSyncRequest;
import com.sysconard.business.dto.sale.SaleSyncResponse;
import com.sysconard.business.dto.store.StoreResponseDto;
import com.sysconard.business.entity.product.Product;
import com.sysconard.business.entity.sale.SaleDetail;
import com.sysconard.business.enums.EventSource;
import com.sysconard.business.enums.OperationSource;
import com.sysconard.business.repository.operation.OperationRepository;
import com.sysconard.business.repository.origin.EventOriginRepository;
import com.sysconard.business.repository.product.ProductRepository;
import com.sysconard.business.repository.sale.SaleDetailRepository;
import com.sysconard.business.repository.store.StoreRepository;
import com.sysconard.business.entity.store.Store;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Serviço responsável pela sincronização de vendas da Legacy API.
 * Implementa a lógica de negócio para sincronização de produtos e vendas detalhadas.
 * Segue os princípios de Clean Code com responsabilidades bem definidas.
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class SaleSyncService {
    
    private final LegacyApiClient legacyApiClient;
    private final ProductRepository productRepository;
    private final SaleDetailRepository saleDetailRepository;
    private final OperationRepository operationRepository;
    private final EventOriginRepository eventOriginRepository;
    private final StoreRepository storeRepository; // Adicionar esta linha
    
    /**
     * Sincroniza vendas da Legacy API para o banco de dados.
     * Processa produtos únicos e vendas detalhadas com validação de duplicatas.
     * 
     * @param request Requisição de sincronização com datas inicial e final
     * @return Resposta com estatísticas da sincronização
     * @throws RuntimeException se houver erro na comunicação com a Legacy API
     */
    public SaleSyncResponse syncSales(SaleSyncRequest request) {
        log.info("Iniciando sincronização de vendas - startDate: {}, endDate: {}", 
                request.startDate(), request.endDate());
        
        try {
            // 1. Preparar parâmetros
            LocalDateTime startDateTime = request.startDate().atStartOfDay();
            LocalDateTime endDateTime = request.endDate().atTime(23, 59, 59);
            
            List<String> storeCodes = prepareStoreCodes();
            List<String> originCodes = prepareOriginCodes(); // Mudou de String para List<String>
            List<String> operationCodes = prepareOperationCodes();
            
            log.info("Parâmetros preparados - storeCodes: {}, originCodes: {}, operationCodes: {}", 
                    storeCodes.size(), originCodes.size(), operationCodes.size());
            
            // 2. Chamar Legacy API
            List<SaleItemLegacyDTO> saleItems = legacyApiClient.getSaleItemsDetails(
                    startDateTime,
                    endDateTime,
                    storeCodes,
                    originCodes, // Agora é List<String>
                    operationCodes
            );
            
            log.info("Itens de venda recebidos da Legacy API: {}", 
                    saleItems != null ? saleItems.size() : 0);
            
            if (saleItems == null || saleItems.isEmpty()) {
                log.warn("Nenhum item de venda encontrado na Legacy API");
                return new SaleSyncResponse(0, 0, 0, 0, 0, LocalDateTime.now());
            }
            
            // 3. Processar produtos únicos
            int productsInserted = processProducts(saleItems);
            int productsSkipped = saleItems.stream()
                    .map(SaleItemLegacyDTO::getProductRefCode)
                    .collect(Collectors.toSet())
                    .size() - productsInserted;
            
            log.info("Produtos processados - inseridos: {}, ignorados: {}", 
                    productsInserted, productsSkipped);
            
            // 4. Processar vendas com validação de duplicatas
            int salesInserted = processSales(saleItems);
            int salesSkipped = saleItems.size() - salesInserted;
            
            log.info("Vendas processadas - inseridas: {}, ignoradas: {}", 
                    salesInserted, salesSkipped);
            
            // 5. Retornar resposta com estatísticas
            SaleSyncResponse response = new SaleSyncResponse(
                    saleItems.size(),
                    productsInserted,
                    productsSkipped,
                    salesInserted,
                    salesSkipped,
                    LocalDateTime.now()
            );
            
            log.info("Sincronização concluída com sucesso - totalItemsReceived: {}, productsInserted: {}, salesInserted: {}", 
                    response.totalItemsReceived(), response.productsInserted(), response.salesInserted());
            
            return response;
            
        } catch (Exception e) {
            log.error("Erro ao sincronizar vendas da Legacy API: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao sincronizar vendas: " + e.getMessage(), e);
        }
    }
    
    /**
     * Prepara lista de códigos de lojas buscando todas as stores do banco de dados local.
     * 
     * @return Lista de códigos de lojas
     */
    private List<String> prepareStoreCodes() {
        log.debug("Buscando códigos de lojas do banco de dados local");
        
        List<Store> stores = storeRepository.findAll();
        
        if (stores == null || stores.isEmpty()) {
            log.warn("Nenhuma loja encontrada no banco de dados local");
            return List.of();
        }
        
        log.debug("Total de lojas encontradas no banco de dados: {}", stores.size());
        
        List<String> storeCodes = stores.stream()
                .map(Store::getCode)
                .filter(Objects::nonNull)
                .filter(code -> !code.trim().isEmpty())
                .collect(Collectors.toList());
        
        log.debug("Códigos de lojas encontrados: {} (de {} lojas)", storeCodes.size(), stores.size());
        
        if (storeCodes.isEmpty() && !stores.isEmpty()) {
            log.warn("Lojas foram encontradas no banco de dados, mas nenhuma possui código válido. Total de lojas: {}", stores.size());
        }
        
        return storeCodes;
    }
    
    /**
     * Prepara lista de códigos de origem dos tipos PDV e DANFE.
     * 
     * @return Lista de códigos de origem
     */
    private List<String> prepareOriginCodes() {
        log.debug("Buscando códigos de origem PDV e DANFE");
        
        List<com.sysconard.business.entity.origin.EventOrigin> pdvOrigins = 
                eventOriginRepository.findByEventSource(EventSource.PDV);
        List<com.sysconard.business.entity.origin.EventOrigin> danfeOrigins = 
                eventOriginRepository.findByEventSource(EventSource.DANFE);
        
        List<String> allOriginCodes = new ArrayList<>();
        
        if (pdvOrigins != null) {
            allOriginCodes.addAll(pdvOrigins.stream()
                    .map(com.sysconard.business.entity.origin.EventOrigin::getSourceCode)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList()));
        }
        
        if (danfeOrigins != null) {
            allOriginCodes.addAll(danfeOrigins.stream()
                    .map(com.sysconard.business.entity.origin.EventOrigin::getSourceCode)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList()));
        }
        
        log.debug("Códigos de origem encontrados: {} (PDV: {}, DANFE: {})", 
                allOriginCodes.size(), 
                pdvOrigins != null ? pdvOrigins.size() : 0,
                danfeOrigins != null ? danfeOrigins.size() : 0);
        
        return allOriginCodes;
    }
    
    /**
     * Prepara lista de códigos de operações buscando todas as operations com operationSource = SELL.
     * 
     * @return Lista de códigos de operações
     */
    private List<String> prepareOperationCodes() {
        log.debug("Buscando códigos de operações com operationSource = SELL");
        
        List<com.sysconard.business.entity.operation.Operation> operations = 
                operationRepository.findByOperationSource(OperationSource.SELL);
        
        if (operations == null || operations.isEmpty()) {
            log.warn("Nenhuma operação SELL encontrada no banco");
            return List.of();
        }
        
        List<String> operationCodes = operations.stream()
                .map(com.sysconard.business.entity.operation.Operation::getCode)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        
        log.debug("Códigos de operações encontrados: {}", operationCodes.size());
        
        return operationCodes;
    }
    
    /**
     * Processa produtos únicos da lista de itens de venda.
     * Extrai productRefCodes únicos, verifica quais não existem e insere os novos.
     * 
     * @param saleItems Lista de itens de venda da Legacy API
     * @return Quantidade de produtos inseridos
     */
    private int processProducts(List<SaleItemLegacyDTO> saleItems) {
        log.debug("Processando produtos únicos");
        
        // Extrair productRefCodes únicos
        Set<String> uniqueProductRefCodes = saleItems.stream()
                .map(SaleItemLegacyDTO::getProductRefCode)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        
        log.debug("ProductRefCodes únicos encontrados: {}", uniqueProductRefCodes.size());
        
        if (uniqueProductRefCodes.isEmpty()) {
            return 0;
        }
        
        // Buscar produtos existentes no banco
        Set<String> existingProductRefCodes = new HashSet<>(
                productRepository.findExistingProductRefCodes(uniqueProductRefCodes)
        );
        
        log.debug("Produtos já existentes no banco: {}", existingProductRefCodes.size());
        
        // Filtrar produtos novos
        Set<String> newProductRefCodes = uniqueProductRefCodes.stream()
                .filter(refCode -> !existingProductRefCodes.contains(refCode))
                .collect(Collectors.toSet());
        
        log.debug("Produtos novos para inserir: {}", newProductRefCodes.size());
        
        if (newProductRefCodes.isEmpty()) {
            return 0;
        }
        
        // Criar mapa de productRefCode para primeiro item (para pegar dados do produto)
        Map<String, SaleItemLegacyDTO> productDataMap = saleItems.stream()
                .filter(item -> item.getProductRefCode() != null)
                .collect(Collectors.toMap(
                        SaleItemLegacyDTO::getProductRefCode,
                        item -> item,
                        (existing, replacement) -> existing // Manter o primeiro encontrado
                ));
        
        // Criar produtos novos
        List<Product> newProducts = newProductRefCodes.stream()
                .map(refCode -> {
                    SaleItemLegacyDTO item = productDataMap.get(refCode);
                    return Product.builder()
                            .productRefCode(item.getProductRefCode())
                            .productCode(item.getProductCode())
                            .section(item.getSection())
                            .group(item.getGroup())
                            .subgroup(item.getSubgroup())
                            .brand(item.getBrand())
                            .productDescription(item.getProductDescription())
                            .build();
                })
                .collect(Collectors.toList());
        
        // Inserir produtos novos em lote
        productRepository.saveAll(newProducts);
        
        log.info("Produtos inseridos com sucesso: {}", newProducts.size());
        
        return newProducts.size();
    }
    
    /**
     * Processa vendas detalhadas da lista de itens de venda.
     * Verifica duplicatas por chave composta (saleCode + productRefCode + itemSequence) e insere as novas.
     * 
     * @param saleItems Lista de itens de venda da Legacy API
     * @return Quantidade de vendas inseridas
     */
    private int processSales(List<SaleItemLegacyDTO> saleItems) {
        log.debug("Processando vendas detalhadas");
        
        // Criar conjunto de chaves compostas para verificação
        Set<String> compositeKeys = saleItems.stream()
                .map(item -> createCompositeKey(
                        item.getSaleCode(),
                        item.getProductRefCode(),
                        item.getItemSequence()
                ))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        
        log.debug("Chaves compostas únicas encontradas: {}", compositeKeys.size());
        
        // Verificar quais vendas já existem no banco
        Set<String> existingKeys = new HashSet<>();
        
        for (SaleItemLegacyDTO item : saleItems) {
            if (item.getSaleCode() != null && 
                item.getProductRefCode() != null && 
                item.getItemSequence() != null) {
                
                boolean exists = saleDetailRepository.existsBySaleCodeAndProductRefCodeAndItemSequence(
                        item.getSaleCode(),
                        item.getProductRefCode(),
                        item.getItemSequence()
                );
                
                if (exists) {
                    existingKeys.add(createCompositeKey(
                            item.getSaleCode(),
                            item.getProductRefCode(),
                            item.getItemSequence()
                    ));
                }
            }
        }
        
        log.debug("Vendas já existentes no banco: {}", existingKeys.size());
        
        // Filtrar vendas novas
        List<SaleItemLegacyDTO> newSaleItems = saleItems.stream()
                .filter(item -> {
                    String key = createCompositeKey(
                            item.getSaleCode(),
                            item.getProductRefCode(),
                            item.getItemSequence()
                    );
                    return key != null && !existingKeys.contains(key);
                })
                .collect(Collectors.toList());
        
        log.debug("Vendas novas para inserir: {}", newSaleItems.size());
        
        if (newSaleItems.isEmpty()) {
            return 0;
        }
        
        // Criar vendas detalhadas
        List<SaleDetail> newSaleDetails = newSaleItems.stream()
                .map(this::mapToSaleDetail)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        
        // Inserir vendas novas em lote
        saleDetailRepository.saveAll(newSaleDetails);
        
        log.info("Vendas inseridas com sucesso: {}", newSaleDetails.size());
        
        return newSaleDetails.size();
    }
    
    /**
     * Mapeia um SaleItemLegacyDTO para uma entidade SaleDetail.
     * 
     * @param item DTO do item de venda da Legacy API
     * @return Entidade SaleDetail
     */
    private SaleDetail mapToSaleDetail(SaleItemLegacyDTO item) {
        try {
            // Converter OffsetDateTime para LocalDateTime
            LocalDateTime saleDate = item.getSaleDate() != null 
                    ? item.getSaleDate().toLocalDateTime() 
                    : null;
            
            return SaleDetail.builder()
                    .saleDate(saleDate)
                    .saleCode(item.getSaleCode())
                    .itemSequence(item.getItemSequence())
                    .collaboratorCode(item.getEmployeeCode())
                    .storeCode(item.getStoreCode())
                    .productRefCode(item.getProductRefCode())
                    .ncm(item.getNcm())
                    .quantity(item.getQuantity())
                    .unitPrice(item.getUnitPrice() != null ? item.getUnitPrice() : BigDecimal.ZERO)
                    .totalPrice(item.getTotalPrice() != null ? item.getTotalPrice() : BigDecimal.ZERO)
                    .build();
        } catch (Exception e) {
            log.error("Erro ao mapear item de venda para SaleDetail: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Cria uma chave composta para identificação única de uma venda.
     * Formato: saleCode|productRefCode|itemSequence
     * 
     * @param saleCode Código da venda
     * @param productRefCode Código de referência do produto
     * @param itemSequence Sequência do item
     * @return Chave composta ou null se algum parâmetro for null
     */
    private String createCompositeKey(String saleCode, String productRefCode, Integer itemSequence) {
        if (saleCode == null || productRefCode == null || itemSequence == null) {
            return null;
        }
        return saleCode + "|" + productRefCode + "|" + itemSequence;
    }
}

