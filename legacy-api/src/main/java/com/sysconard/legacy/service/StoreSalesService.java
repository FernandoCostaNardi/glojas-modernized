package com.sysconard.legacy.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.sysconard.legacy.dto.StoreSalesReportDTO;
import com.sysconard.legacy.dto.StoreSalesReportRequestDTO;
import com.sysconard.legacy.dto.StoreSalesReportByDayDTO;
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
    
    /**
     * Gera relatório de vendas por loja e por dia com agregações de DANFE, PDV e TROCA3.
     * Usa query otimizada com filtro de data no banco para melhor performance.
     * Retorna dados agregados por loja e por dia.
     * 
     * @param request DTO com todos os parâmetros necessários para o relatório
     * @return Lista de DTOs com os dados agregados por loja e por dia
     */
    public List<StoreSalesReportByDayDTO> getStoreSalesReportByDay(StoreSalesReportRequestDTO request) {
        // Validação dos parâmetros
        validateRequest(request);
        
        log.debug("Gerando relatório de vendas por loja e por dia: startDate={}, endDate={}, storeCodes={}", 
                 request.getStartDate(), request.getEndDate(), request.getStoreCodes());

        log.debug("Executando query otimizada com filtro de data no banco e agrupamento por dia");
        
        try {
            // Formatar datas para o formato datetime do SQL Server (ISO 8601)
            String startDateFormatted = formatDateForSqlServer(request.getStartDate(), true);
            String endDateFormatted = formatDateForSqlServer(request.getEndDate(), false);
            
            // Busca dados já agregados com filtro de data no SQL e agrupamento por dia
            List<Object[]> aggregatedData = documentRepository.findStoreSalesByDayOptimizedData(
                request.getStoreCodes(),
                startDateFormatted, endDateFormatted,
                request.getDanfeOrigin(), request.getPdvOrigin(), request.getExchangeOrigin(),
                request.getSellOperation(), request.getExchangeOperation());
            
            log.debug("Dados agregados por dia obtidos: {} registros", aggregatedData.size());
            
            // Converter datas string para LocalDate
            LocalDate startDateParsed = parseStringToLocalDate(request.getStartDate());
            LocalDate endDateParsed = parseStringToLocalDate(request.getEndDate());
            
            // Processar dados agregados por loja e por dia
            List<StoreSalesReportByDayDTO> report = processAggregatedDataByDay(
                aggregatedData, request.getStoreCodes(), startDateParsed, endDateParsed);
            
            log.debug("Relatório por dia gerado com sucesso: {} registros processados", report.size());
            
            return report;
            
        } catch (IllegalArgumentException e) {
            // Erro de validação de parâmetros - re-lançar para ser tratado pelo controller
            log.error("Erro de validação nos parâmetros do relatório por dia: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Erro ao executar query de relatório de vendas por dia: {}", e.getMessage(), e);
            
            // Verificar se é erro de conversão de data
            if (e.getMessage() != null && e.getMessage().contains("conversão de um tipo de dados")) {
                log.error("Erro de conversão de data - verificar formato das datas: startDate={}, endDate={}", 
                         request.getStartDate(), request.getEndDate());
            }
            
            // Retorna lista vazia em caso de erro, em vez de falhar
            log.warn("Retornando relatório vazio devido ao erro na query por dia");
            return new ArrayList<>();
        }
    }
    
    /**
     * Processa dados já agregados pela query otimizada por loja e por dia.
     * Garante que todas as lojas solicitadas apareçam em todas as datas do período,
     * mesmo que não tenham vendas (valores zerados).
     * 
     * @param aggregatedData Dados já agregados pela query SQL: [LOJFAN, LOJCOD, DATA, TROCA, PDV, DANFE]
     * @param requestedStoreCodes Lista de códigos de loja solicitados
     * @param startDate Data de início do período solicitado
     * @param endDate Data de fim do período solicitado
     * @return Lista de DTOs com dados por loja e por dia
     */
    private List<StoreSalesReportByDayDTO> processAggregatedDataByDay(List<Object[]> aggregatedData, List<String> requestedStoreCodes, LocalDate startDate, LocalDate endDate) {
        log.debug("Processando {} registros com dados agregados por loja e por dia", aggregatedData.size());

        // Mapa para armazenar dados reais retornados pela query
        Map<String, StoreSalesReportByDayDTO> realDataMap = new HashMap<>();
        Map<String, String> storeNamesMap = new HashMap<>();
        
        // Processar dados retornados pela query e indexar por chave composta
        for (Object[] row : aggregatedData) {
            String storeName = toSafeString(row[0]);
            String storeCode = toSafeString(row[1]);
            LocalDate reportDate = convertToLocalDate(row[2]);
            BigDecimal troca = convertToBigDecimal(row[3]);
            BigDecimal pdv = convertToBigDecimal(row[4]);
            BigDecimal danfe = convertToBigDecimal(row[5]);
            
            if (storeCode != null && reportDate != null) {
                String dataKey = createDataKey(storeCode, reportDate);
                
                StoreSalesReportByDayDTO dto = StoreSalesReportByDayDTO.builder()
                    .storeName(storeName != null ? storeName : "Loja " + storeCode)
                    .storeCode(storeCode)
                    .reportDate(reportDate)
                    .troca3(troca)
                    .pdv(pdv)
                    .danfe(danfe)
                    .build();
                
                realDataMap.put(dataKey, dto);
                
                // Armazenar nome da loja para usar em registros zerados
                if (storeName != null) {
                    storeNamesMap.put(storeCode, storeName);
                }
            }
        }
        
        log.debug("Dados reais indexados: {} registros", realDataMap.size());
        log.debug("Gerando combinações para período solicitado: {} a {}", startDate, endDate);
        
        // Gerar todas as combinações de loja + data esperadas usando o período do request
        List<StoreSalesReportByDayDTO> result = generateAllStoreAndDateCombinations(
            requestedStoreCodes, startDate, endDate, storeNamesMap, realDataMap);
        
        // Ordenar por nome da loja e depois por data
        result.sort(Comparator.comparing(StoreSalesReportByDayDTO::getStoreName)
                              .thenComparing(StoreSalesReportByDayDTO::getReportDate));
        
        log.debug("Processamento por dia concluído: {} registros no resultado final (incluindo zeros)", result.size());
        return result;
    }
    
    /**
     * Cria uma chave composta para indexação de dados por loja e data.
     * Formato: "storeCode_YYYY-MM-DD"
     * 
     * @param storeCode Código da loja
     * @param date Data do relatório
     * @return String representando a chave composta
     */
    private String createDataKey(String storeCode, LocalDate date) {
        if (storeCode == null || date == null) {
            return null;
        }
        return storeCode + "_" + date.toString();
    }
    
    
    /**
     * Gera todas as combinações de loja + data do período solicitado.
     * Para cada combinação, usa dados reais se disponíveis, senão cria com valores zerados.
     * 
     * @param requestedStoreCodes Lista de códigos de loja solicitados
     * @param startDate Data de início do período
     * @param endDate Data de fim do período
     * @param storeNamesMap Mapa com nomes das lojas
     * @param realDataMap Mapa com dados reais indexados por chave
     * @return Lista completa de DTOs com todas as combinações
     */
    private List<StoreSalesReportByDayDTO> generateAllStoreAndDateCombinations(
            List<String> requestedStoreCodes, LocalDate startDate, LocalDate endDate,
            Map<String, String> storeNamesMap, Map<String, StoreSalesReportByDayDTO> realDataMap) {
        
        log.debug("Gerando combinações para {} lojas no período {} a {}", 
                 requestedStoreCodes.size(), startDate, endDate);
        
        List<StoreSalesReportByDayDTO> result = new ArrayList<>();
        
        // Validar parâmetros
        if (requestedStoreCodes == null || requestedStoreCodes.isEmpty()) {
            log.warn("Nenhuma loja solicitada para gerar combinações");
            return result;
        }
        
        if (startDate == null || endDate == null) {
            log.warn("Período de datas inválido para gerar combinações");
            return result;
        }
        
        // Gerar todas as datas do período
        List<LocalDate> dateRange = generateDateRange(startDate, endDate);
        
        // Para cada loja solicitada
        for (String storeCode : requestedStoreCodes) {
            String storeName = storeNamesMap.getOrDefault(storeCode, "Loja " + storeCode);
            
            // Para cada data do período
            for (LocalDate date : dateRange) {
                String dataKey = createDataKey(storeCode, date);
                
                // Verificar se temos dados reais para esta combinação
                StoreSalesReportByDayDTO realData = realDataMap.get(dataKey);
                
                StoreSalesReportByDayDTO dto;
                if (realData != null) {
                    // Usar dados reais
                    dto = realData;
                } else {
                    // Criar com valores zerados
                    dto = StoreSalesReportByDayDTO.builder()
                        .storeName(storeName)
                        .storeCode(storeCode)
                        .reportDate(date)
                        .troca3(BigDecimal.ZERO)
                        .pdv(BigDecimal.ZERO)
                        .danfe(BigDecimal.ZERO)
                        .build();
                }
                
                result.add(dto);
            }
        }
        
        log.debug("Geradas {} combinações de loja + data", result.size());
        return result;
    }
    
    /**
     * Converte uma string de data no formato YYYY-MM-DD para LocalDate.
     * 
     * @param dateString String de data no formato YYYY-MM-DD
     * @return LocalDate convertido
     * @throws IllegalArgumentException se a data for inválida
     */
    private LocalDate parseStringToLocalDate(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
            throw new IllegalArgumentException("Data não pode ser nula ou vazia");
        }
        
        try {
            return LocalDate.parse(dateString.trim());
        } catch (Exception e) {
            throw new IllegalArgumentException("Data inválida: " + dateString + ". Use o formato YYYY-MM-DD");
        }
    }
    
    /**
     * Gera uma lista de datas consecutivas entre startDate e endDate (inclusivos).
     * 
     * @param startDate Data de início (inclusiva)
     * @param endDate Data de fim (inclusiva)
     * @return Lista de LocalDate do período
     */
    private List<LocalDate> generateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            return new ArrayList<>();
        }
        
        if (startDate.isAfter(endDate)) {
            log.warn("Data de início {} é posterior à data de fim {}", startDate, endDate);
            return new ArrayList<>();
        }
        
        List<LocalDate> dates = new ArrayList<>();
        LocalDate currentDate = startDate;
        
        while (!currentDate.isAfter(endDate)) {
            dates.add(currentDate);
            currentDate = currentDate.plusDays(1);
        }
        
        log.debug("Período de datas gerado: {} dias de {} a {}", dates.size(), startDate, endDate);
        return dates;
    }
    
    /**
     * Converte um objeto para LocalDate de forma segura.
     * Trata diferentes tipos retornados pelo SQL Server.
     * 
     * @param dateValue Objeto que pode ser Date, String ou null
     * @return LocalDate ou null se inválido
     */
    private LocalDate convertToLocalDate(Object dateValue) {
        if (dateValue == null) {
            return null;
        }
        
        try {
            if (dateValue instanceof java.sql.Date) {
                return ((java.sql.Date) dateValue).toLocalDate();
            }
            
            if (dateValue instanceof java.util.Date) {
                return ((java.util.Date) dateValue).toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDate();
            }
            
            if (dateValue instanceof String) {
                String dateStr = (String) dateValue;
                // Remover horário se presente (formato YYYY-MM-DD HH:mm:ss)
                if (dateStr.contains(" ")) {
                    dateStr = dateStr.substring(0, dateStr.indexOf(" "));
                }
                return LocalDate.parse(dateStr);
            }
            
            // Tentar converter toString() como último recurso
            String dateStr = dateValue.toString();
            if (dateStr.contains(" ")) {
                dateStr = dateStr.substring(0, dateStr.indexOf(" "));
            }
            return LocalDate.parse(dateStr);
            
        } catch (Exception e) {
            log.warn("Erro ao converter data '{}' (tipo: {}) para LocalDate: {}", 
                    dateValue, dateValue.getClass().getSimpleName(), e.getMessage());
            return null;
        }
    }
}
