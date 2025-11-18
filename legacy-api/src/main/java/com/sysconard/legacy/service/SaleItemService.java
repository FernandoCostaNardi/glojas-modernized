package com.sysconard.legacy.service;

import com.sysconard.legacy.dto.SaleItemDetailDTO;
import com.sysconard.legacy.dto.SaleItemDetailRequestDTO;
import com.sysconard.legacy.repository.SaleItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Serviço responsável pela lógica de negócio dos detalhes de itens de venda.
 * Processa requisições e executa queries para retornar detalhes de vendas.
 * 
 * @author Sysconard Legacy API
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SaleItemService {
    
    private final SaleItemRepository saleItemRepository;
    
    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    
    /**
     * Busca detalhes de itens de venda de um período específico.
     * 
     * @param request DTO com parâmetros da requisição
     * @return Lista de DTOs com os detalhes dos itens de venda
     */
    public List<SaleItemDetailDTO> findSaleItemDetails(SaleItemDetailRequestDTO request) {
        log.debug("Iniciando busca de detalhes de itens de venda: startDate={}, endDate={}", 
                 request.getStartDate(), request.getEndDate());
        
        // Validar requisição
        validateRequest(request);
        
        try {
            // Formatar datas para o formato datetime do SQL Server
            String startDateFormatted = formatDateForSqlServer(request.getStartDate(), true);
            String endDateFormatted = formatDateForSqlServer(request.getEndDate(), false);
            
            log.debug("Datas formatadas: startDate={}, endDate={}", startDateFormatted, endDateFormatted);
            log.debug("Parâmetros: originCodes={}, operationCodes={}, storeCodes={}", 
                     request.getOriginCodes().size(), 
                     request.getOperationCodes().size(), 
                     request.getStoreCodes().size());
            
            // Executar query nativa
            List<Object[]> results = saleItemRepository.findSaleItemDetails(
                    request.getOriginCodes(),
                    request.getOperationCodes(),
                    request.getStoreCodes(),
                    startDateFormatted,
                    endDateFormatted
            );
            
            log.info("Itens de venda encontrados: {}", results.size());
            
            // Converter resultados para DTOs, filtrando nulos
            List<SaleItemDetailDTO> saleItemDetails = results.stream()
                    .map(this::convertToDTO)
                    .filter(dto -> dto != null)
                    .collect(Collectors.toList());
            
            int failedConversions = results.size() - saleItemDetails.size();
            if (failedConversions > 0) {
                log.warn("{} linhas não puderam ser convertidas para DTO", failedConversions);
            }
            
            log.debug("Conversão para DTOs concluída: {} itens processados de {} resultados", 
                     saleItemDetails.size(), results.size());
            
            return saleItemDetails;
            
        } catch (IllegalArgumentException e) {
            log.error("Erro de validação nos parâmetros: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Erro ao buscar detalhes de itens de venda: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao buscar detalhes de itens de venda: " + e.getMessage(), e);
        }
    }
    
    /**
     * Valida os parâmetros da requisição.
     * 
     * @param request DTO com parâmetros da requisição
     * @throws IllegalArgumentException se os parâmetros forem inválidos
     */
    private void validateRequest(SaleItemDetailRequestDTO request) {
        if (request.getStartDate() == null || request.getStartDate().trim().isEmpty()) {
            throw new IllegalArgumentException("Data inicial (startDate) é obrigatória");
        }
        
        if (request.getEndDate() == null || request.getEndDate().trim().isEmpty()) {
            throw new IllegalArgumentException("Data final (endDate) é obrigatória");
        }
        
        if (request.getOriginCodes() == null || request.getOriginCodes().isEmpty()) {
            throw new IllegalArgumentException("Lista de códigos de origem (originCodes) não pode ser nula ou vazia");
        }
        
        if (request.getOperationCodes() == null || request.getOperationCodes().isEmpty()) {
            throw new IllegalArgumentException("Lista de códigos de operação (operationCodes) não pode ser nula ou vazia");
        }
        
        if (request.getStoreCodes() == null || request.getStoreCodes().isEmpty()) {
            throw new IllegalArgumentException("Lista de códigos de lojas (storeCodes) não pode ser nula ou vazia");
        }
        
        // Validar formato das datas
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
            Date startDate = sdf.parse(request.getStartDate());
            Date endDate = sdf.parse(request.getEndDate());
            
            if (startDate.after(endDate)) {
                throw new IllegalArgumentException("Data inicial não pode ser posterior à data final");
            }
        } catch (ParseException e) {
            throw new IllegalArgumentException("Formato de data inválido. Use o formato: yyyy-MM-dd'T'HH:mm:ss (ex: 2025-11-13T00:00:00)");
        }
    }
    
    /**
     * Formata uma data para o formato datetime do SQL Server.
     * Usa formato ISO 8601 com 'T' e milissegundos, que é o formato aceito pelo SQL Server.
     * 
     * @param dateString Data no formato ISO (ex: "2025-11-13T00:00:00")
     * @param isStartDate true para data de início, false para data de fim
     * @return String formatada para SQL Server datetime (formato ISO 8601)
     */
    private String formatDateForSqlServer(String dateString, boolean isStartDate) {
        try {
            // Se já está no formato ISO 8601 completo, apenas ajusta os milissegundos
            if (dateString.contains("T")) {
                // Remove milissegundos se existirem
                String dateWithoutMs = dateString.split("\\.")[0];
                
                // Se não tem hora, adiciona
                if (!dateWithoutMs.contains(":")) {
                    if (isStartDate) {
                        return dateWithoutMs + "T00:00:00.000";
                    } else {
                        return dateWithoutMs + "T23:59:59.997";
                    }
                }
                
                // Se já tem hora, apenas adiciona milissegundos
                if (isStartDate) {
                    return dateWithoutMs + ".000";
                } else {
                    // Para data final, ajusta para 23:59:59.997
                    String[] parts = dateWithoutMs.split("T");
                    if (parts.length == 2) {
                        return parts[0] + "T23:59:59.997";
                    }
                    return dateWithoutMs + ".997";
                }
            }
            
            // Se não tem 'T', assume formato YYYY-MM-DD
            if (isStartDate) {
                return dateString + "T00:00:00.000";
            } else {
                return dateString + "T23:59:59.997";
            }
        } catch (Exception e) {
            log.error("Erro ao formatar data: {}", dateString, e);
            throw new IllegalArgumentException("Formato de data inválido: " + dateString);
        }
    }
    
    /**
     * Converte um array de objetos retornado pela query para SaleItemDetailDTO.
     * 
     * @param row Array de objetos com os dados da query
     * @return DTO com os dados formatados
     */
    private SaleItemDetailDTO convertToDTO(Object[] row) {
        if (row == null || row.length < 16) {
            log.warn("Linha de resultado inválida ou incompleta. Esperado: 16 campos, recebido: {}", 
                    row != null ? row.length : 0);
            return null;
        }
        
        try {
            return SaleItemDetailDTO.builder()
                    .saleDate(convertToDate(row[0]))  // DOCDATEMI
                    .saleCode(formatId(row[1]))  // SAICOD
                    .itemSequence(convertToInteger(row[2]))  // ITSSEQ
                    .employeeCode(formatId(row[3]))  // FUNCOD
                    .productRefCode(convertToString(row[4]))  // REFPLU
                    .storeCode(formatId(row[5]))  // LOJCOD
                    .productCode(formatId(row[6]))  // PROCOD
                    .brand(convertToString(row[7]))  // MARDES
                    .section(convertToString(row[8]))  // SECDES
                    .group(convertToString(row[9]))  // GRPDES
                    .subgroup(convertToString(row[10]))  // SBGDES
                    .productDescription(convertToString(row[11]))  // PRODES
                    .ncm(convertToString(row[12]))  // PRONCM
                    .quantity(convertToInteger(row[13]))  // ITSQTDTOT
                    .unitPrice(convertToBigDecimal(row[14]))  // Unitario
                    .totalPrice(convertToBigDecimal(row[15]))  // ITSTOTFAT
                    .build();
        } catch (Exception e) {
            log.error("Erro ao converter linha para DTO. Tamanho do array: {}, Erro: {}", 
                     row != null ? row.length : 0, e.getMessage(), e);
            // Log dos primeiros elementos para debug
            if (row != null && row.length > 0) {
                log.debug("Primeiros elementos do array: [0]={}, [1]={}, [2]={}", 
                         row[0], row.length > 1 ? row[1] : "N/A", row.length > 2 ? row[2] : "N/A");
            }
            return null;
        }
    }
    
    /**
     * Formata um ID para 6 dígitos com zeros à esquerda.
     * 
     * @param id Objeto que representa o ID (pode ser Long, String, etc.)
     * @return String formatada com 6 dígitos
     */
    private String formatId(Object id) {
        if (id == null) {
            return null;
        }
        
        try {
            Long idValue;
            if (id instanceof Long) {
                idValue = (Long) id;
            } else if (id instanceof Number) {
                idValue = ((Number) id).longValue();
            } else if (id instanceof String) {
                String str = id.toString().trim();
                if (str.isEmpty()) {
                    return null;
                }
                idValue = Long.parseLong(str);
            } else {
                idValue = Long.parseLong(id.toString());
            }
            
            return String.format("%06d", idValue);
        } catch (Exception e) {
            log.warn("Erro ao formatar ID: {}", id, e);
            return id.toString();
        }
    }
    
    /**
     * Converte um objeto para Date de forma segura.
     * 
     * @param obj Objeto a ser convertido
     * @return Date ou null se não puder converter
     */
    private Date convertToDate(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Date) {
            return (Date) obj;
        }
        if (obj instanceof java.sql.Timestamp) {
            return new Date(((java.sql.Timestamp) obj).getTime());
        }
        if (obj instanceof java.sql.Date) {
            return new Date(((java.sql.Date) obj).getTime());
        }
        return null;
    }
    
    /**
     * Converte um objeto para Integer de forma segura.
     * 
     * @param obj Objeto a ser convertido
     * @return Integer ou null se não puder converter
     */
    private Integer convertToInteger(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Integer) {
            return (Integer) obj;
        }
        if (obj instanceof Number) {
            return ((Number) obj).intValue();
        }
        try {
            return Integer.parseInt(obj.toString().trim());
        } catch (Exception e) {
            log.warn("Erro ao converter para Integer: {}", obj);
            return null;
        }
    }
    
    /**
     * Converte um objeto para String de forma segura.
     * 
     * @param obj Objeto a ser convertido
     * @return String ou null se o objeto for null
     */
    private String convertToString(Object obj) {
        if (obj == null) {
            return null;
        }
        String str = obj.toString().trim();
        return str.isEmpty() ? null : str;
    }
    
    /**
     * Converte um objeto para BigDecimal de forma segura.
     * 
     * @param obj Objeto a ser convertido
     * @return BigDecimal ou null se não puder converter
     */
    private BigDecimal convertToBigDecimal(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof BigDecimal) {
            return (BigDecimal) obj;
        }
        if (obj instanceof Number) {
            return BigDecimal.valueOf(((Number) obj).doubleValue());
        }
        try {
            return new BigDecimal(obj.toString().trim());
        } catch (Exception e) {
            log.warn("Erro ao converter para BigDecimal: {}", obj);
            return null;
        }
    }
}

