package com.sysconard.legacy.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.sysconard.legacy.dto.StoreSalesReportDTO;
import com.sysconard.legacy.dto.StoreSalesReportRequestDTO;
import com.sysconard.legacy.repository.DocumentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Serviço responsável pela lógica de negócio dos relatórios de vendas por loja.
 * Processa dados agregados de documentos para gerar relatórios de performance.
 * 
 * @author Sysconard Legacy API
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StoreSalesService {
    
    private final DocumentRepository documentRepository;
    
    /**
     * Gera relatório de vendas por loja com agregações de DANFE, PDV e TROCA3.
     * Usa query otimizada com filtro de data no banco para melhor performance.
     * 
     * @param request DTO com todos os parâmetros necessários para o relatório
     * @return Lista de DTOs com os dados agregados por loja
     */
    public List<StoreSalesReportDTO> getStoreSalesReport(StoreSalesReportRequestDTO request) {
        // Validação dos parâmetros
        validateRequest(request);
        
        log.debug("Gerando relatório de vendas por loja: startDate={}, endDate={}, storeCodes={}", 
                 request.getStartDate(), request.getEndDate(), request.getStoreCodes());

        log.debug("Executando query otimizada com filtro de data no banco");
        
        try {
            // Formatar datas para o formato datetime do SQL Server (ISO 8601)
            String startDateFormatted = formatDateForSqlServer(request.getStartDate(), true);
            String endDateFormatted = formatDateForSqlServer(request.getEndDate(), false);
            
            // Busca dados já agregados com filtro de data no SQL
            List<Object[]> aggregatedData = documentRepository.findStoreSalesOptimizedData(
                request.getStoreCodes(),
                startDateFormatted, endDateFormatted,
                request.getDanfeOrigin(), request.getPdvOrigin(), request.getExchangeOrigin(),
                request.getSellOperation(), request.getExchangeOperation());
            
            log.debug("Dados agregados obtidos: {} lojas", aggregatedData.size());
            
            // Processar dados agregados e garantir que todas as lojas apareçam
            List<StoreSalesReportDTO> report = processAggregatedData(aggregatedData, request.getStoreCodes());
            
            log.debug("Relatório gerado com sucesso: {} lojas processadas", report.size());
            
            return report;
            
        } catch (IllegalArgumentException e) {
            // Erro de validação de parâmetros - re-lançar para ser tratado pelo controller
            log.error("Erro de validação nos parâmetros do relatório: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Erro ao executar query de relatório de vendas: {}", e.getMessage(), e);
            
            // Verificar se é erro de conversão de data
            if (e.getMessage() != null && e.getMessage().contains("conversão de um tipo de dados")) {
                log.error("Erro de conversão de data - verificar formato das datas: startDate={}, endDate={}", 
                         request.getStartDate(), request.getEndDate());
            }
            
            // Retorna lista vazia em caso de erro, em vez de falhar
            log.warn("Retornando relatório vazio devido ao erro na query");
            return new ArrayList<>();
        }
    }
    
    /**
     * Formata uma data para o formato datetime do SQL Server.
     * Usa formato ISO 8601 para garantir compatibilidade.
     * 
     * @param dateString Data no formato YYYY-MM-DD
     * @param isStartDate true para data de início (00:00:00), false para data de fim (23:59:59)
     * @return String formatada para SQL Server datetime
     */
    private String formatDateForSqlServer(String dateString, boolean isStartDate) {
        if (dateString == null || dateString.trim().isEmpty()) {
            throw new IllegalArgumentException("Data não pode ser nula ou vazia");
        }
        
        // Validar formato básico YYYY-MM-DD
        if (!dateString.matches("\\d{4}-\\d{2}-\\d{2}")) {
            throw new IllegalArgumentException("Data deve estar no formato YYYY-MM-DD: " + dateString);
        }
        
        if (isStartDate) {
            return dateString + "T00:00:00.000";
        } else {
            return dateString + "T23:59:59.997";
        }
    }
    
    /**
     * Converte um objeto para BigDecimal de forma segura.
     * Trata valores nulos retornando BigDecimal.ZERO.
     * 
     * @param value Valor a ser convertido
     * @return BigDecimal com o valor ou ZERO se nulo
     */
    private BigDecimal convertToBigDecimal(Object value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }
        if (value instanceof Number) {
            return BigDecimal.valueOf(((Number) value).doubleValue());
        }
        return BigDecimal.ZERO;
    }
    
    /**
     * Processa dados já agregados pela query otimizada e garante que todas as lojas apareçam.
     * 
     * @param aggregatedData Dados já agregados pela query SQL: [LOJFAN, LOJCOD, TROCA, PDV, DANFE]
     * @param requestedStoreCodes Lista de códigos de loja solicitados
     * @return Lista de DTOs com todas as lojas solicitadas
     */
    private List<StoreSalesReportDTO> processAggregatedData(List<Object[]> aggregatedData, List<String> requestedStoreCodes) {
        log.debug("Processando {} lojas com dados agregados", aggregatedData.size());

        // Mapa para armazenar dados das lojas que retornaram da query
        Map<String, StoreSalesReportDTO> storeDataMap = new HashMap<>();
        
        // Processar dados retornados pela query
        for (Object[] row : aggregatedData) {
            String storeName = toSafeString(row[0]);
            String storeCode = toSafeString(row[1]);
            BigDecimal troca = convertToBigDecimal(row[2]);
            BigDecimal pdv = convertToBigDecimal(row[3]);
            BigDecimal danfe = convertToBigDecimal(row[4]);
            
            if (storeCode != null) {
                storeDataMap.put(storeCode, StoreSalesReportDTO.builder()
                    .storeName(storeName != null ? storeName : "Loja " + storeCode)
                    .storeCode(storeCode)
                    .troca3(troca)
                    .pdv(pdv)
                    .danfe(danfe)
                    .build());
            }
        }
        
        // Garantir que todas as lojas solicitadas apareçam no resultado
        List<StoreSalesReportDTO> result = new ArrayList<>();
        for (String storeCode : requestedStoreCodes) {
            StoreSalesReportDTO dto = storeDataMap.get(storeCode);
            if (dto == null) {
                // Loja não retornou dados da query, criar com valores zerados
                dto = StoreSalesReportDTO.builder()
                    .storeName("Loja " + storeCode)
                    .storeCode(storeCode)
                    .troca3(BigDecimal.ZERO)
                    .pdv(BigDecimal.ZERO)
                    .danfe(BigDecimal.ZERO)
                    .build();
            }
            result.add(dto);
        }
        
        // Ordenar por nome da loja
        result.sort(Comparator.comparing(StoreSalesReportDTO::getStoreName));
        
        log.debug("Processamento concluído: {} lojas no resultado final", result.size());
        return result;
    }

    
    /**
     * Converte qualquer tipo de objeto para String de forma segura.
     * Trata especificamente os tipos retornados pelo SQL Server legado:
     * Character, String, Number, etc.
     * 
     * @param value Objeto a ser convertido para String
     * @return String representando o valor ou null se inválido
     */
    private String toSafeString(Object value) {
        if (value == null) {
            return null;
        }
        
        try {
            // Character (comum em campos CHAR(1) do SQL Server)
            if (value instanceof Character) {
                return value.toString();
            }
            
            // String (já é string)
            if (value instanceof String) {
                return (String) value;
            }
            
            // Números (BigDecimal, Integer, etc.)
            if (value instanceof Number) {
                return value.toString();
            }
            
            // Fallback seguro para qualquer outro tipo
            String result = value.toString();
            log.debug("Conversão automática de tipo '{}' para String: '{}'", 
                     value.getClass().getSimpleName(), result);
            return result;
            
        } catch (Exception e) {
            log.warn("Erro ao converter objeto '{}' (tipo: {}) para String: {}", 
                     value, value.getClass().getSimpleName(), e.getMessage());
            return null;
        }
    }

    
    /**
     * Valida o DTO de entrada do método.
     * 
     * @param request DTO com todos os parâmetros necessários
     */
    private void validateRequest(StoreSalesReportRequestDTO request) {
        if (request == null) {
            throw new IllegalArgumentException("Request não pode ser nulo");
        }
        
        // Validação das datas
        if (request.getStartDate() == null || request.getStartDate().trim().isEmpty()) {
            throw new IllegalArgumentException("Data de início não pode ser nula ou vazia");
        }
        if (request.getEndDate() == null || request.getEndDate().trim().isEmpty()) {
            throw new IllegalArgumentException("Data de fim não pode ser nula ou vazia");
        }
        
        // Validação básica do formato das datas
        if (!request.getStartDate().matches("\\d{4}-\\d{2}-\\d{2}")) {
            throw new IllegalArgumentException("Data de início deve estar no formato YYYY-MM-DD");
        }
        if (!request.getEndDate().matches("\\d{4}-\\d{2}-\\d{2}")) {
            throw new IllegalArgumentException("Data de fim deve estar no formato YYYY-MM-DD");
        }
        
        // Validação das listas
        if (request.getStoreCodes() == null || request.getStoreCodes().isEmpty()) {
            throw new IllegalArgumentException("Lista de códigos de loja não pode ser nula ou vazia");
        }
        if (request.getDanfeOrigin() == null || request.getDanfeOrigin().isEmpty()) {
            throw new IllegalArgumentException("Lista de códigos de origem DANFE não pode ser nula ou vazia");
        }
        if (request.getPdvOrigin() == null || request.getPdvOrigin().isEmpty()) {
            throw new IllegalArgumentException("Lista de códigos de origem PDV não pode ser nula ou vazia");
        }
        if (request.getExchangeOrigin() == null || request.getExchangeOrigin().isEmpty()) {
            throw new IllegalArgumentException("Lista de códigos de origem de troca não pode ser nula ou vazia");
        }
        if (request.getSellOperation() == null || request.getSellOperation().isEmpty()) {
            throw new IllegalArgumentException("Lista de códigos de operação de venda não pode ser nula ou vazia");
        }
        if (request.getExchangeOperation() == null || request.getExchangeOperation().isEmpty()) {
            throw new IllegalArgumentException("Lista de códigos de operação de troca não pode ser nula ou vazia");
        }
        
        log.debug("Request validado com sucesso");
    }
}
