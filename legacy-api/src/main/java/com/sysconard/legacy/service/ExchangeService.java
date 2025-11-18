package com.sysconard.legacy.service;

import com.sysconard.legacy.dto.ExchangeDTO;
import com.sysconard.legacy.dto.ExchangeRequestDTO;
import com.sysconard.legacy.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Serviço responsável pela lógica de negócio das trocas realizadas.
 * Processa requisições e executa queries para retornar dados de trocas.
 * 
 * @author Sysconard Legacy API
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExchangeService {
    
    private final DocumentRepository documentRepository;
    
    private static final String DATE_INPUT_FORMAT = "yyyy-MM-dd";
    
    /**
     * Busca trocas realizadas em um período específico.
     * 
     * @param request DTO com parâmetros da requisição
     * @return Lista de DTOs com os dados das trocas
     */
    public List<ExchangeDTO> findExchanges(ExchangeRequestDTO request) {
        log.debug("Iniciando busca de trocas: startDate={}, endDate={}", 
                 request.getStartDate(), request.getEndDate());
        
        // Validar requisição
        validateRequest(request);
        
        try {
            // Converter datas de YYYY-MM-DD para YYYY-MM-DDTHH:mm:ss
            String startDateFull = convertDateToFullFormat(request.getStartDate(), true);
            String endDateFull = convertDateToFullFormat(request.getEndDate(), false);
            
            log.debug("Datas convertidas: startDate={}, endDate={}", startDateFull, endDateFull);
            log.debug("Parâmetros: originCodes={}, operationCodes={}", 
                     request.getOriginCodes().size(), 
                     request.getOperationCodes().size());
            
            // Formatar datas para o formato datetime do SQL Server
            String startDateFormatted = formatDateForSqlServer(startDateFull, true);
            String endDateFormatted = formatDateForSqlServer(endDateFull, false);
            
            log.debug("Datas formatadas para SQL Server: startDate={}, endDate={}", 
                     startDateFormatted, endDateFormatted);
            
            // Executar query nativa
            List<Object[]> results = documentRepository.findExchanges(
                    request.getOriginCodes(),
                    request.getOperationCodes(),
                    startDateFormatted,
                    endDateFormatted
            );
            
            log.info("Trocas encontradas: {}", results.size());
            
            // Converter resultados para DTOs, filtrando nulos
            List<ExchangeDTO> exchanges = results.stream()
                    .map(this::convertToDTO)
                    .filter(dto -> dto != null)
                    .collect(Collectors.toList());
            
            int failedConversions = results.size() - exchanges.size();
            if (failedConversions > 0) {
                log.warn("{} linhas não puderam ser convertidas para DTO", failedConversions);
            }
            
            log.debug("Conversão para DTOs concluída: {} itens processados de {} resultados", 
                     exchanges.size(), results.size());
            
            return exchanges;
            
        } catch (IllegalArgumentException e) {
            log.error("Erro de validação nos parâmetros: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Erro ao buscar trocas: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao buscar trocas: " + e.getMessage(), e);
        }
    }
    
    /**
     * Valida os parâmetros da requisição.
     * 
     * @param request DTO com parâmetros da requisição
     * @throws IllegalArgumentException se os parâmetros forem inválidos
     */
    private void validateRequest(ExchangeRequestDTO request) {
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
        
        // Validar formato das datas (YYYY-MM-DD)
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_INPUT_FORMAT);
            sdf.setLenient(false);
            Date startDate = sdf.parse(request.getStartDate());
            Date endDate = sdf.parse(request.getEndDate());
            
            if (startDate.after(endDate)) {
                throw new IllegalArgumentException("Data inicial não pode ser posterior à data final");
            }
        } catch (ParseException e) {
            throw new IllegalArgumentException("Formato de data inválido. Use o formato: YYYY-MM-DD (ex: 2025-11-07)");
        }
    }
    
    /**
     * Converte uma data do formato YYYY-MM-DD para o formato completo YYYY-MM-DDTHH:mm:ss.
     * 
     * @param dateString Data no formato YYYY-MM-DD
     * @param isStartDate true para data de início (adiciona T00:00:00), false para data de fim (adiciona T23:59:59)
     * @return String no formato YYYY-MM-DDTHH:mm:ss
     */
    private String convertDateToFullFormat(String dateString, boolean isStartDate) {
        if (dateString == null || dateString.trim().isEmpty()) {
            throw new IllegalArgumentException("Data não pode ser nula ou vazia");
        }
        
        String dateTrimmed = dateString.trim();
        
        // Se já está no formato completo, retorna como está
        if (dateTrimmed.contains("T")) {
            return dateTrimmed;
        }
        
        // Converte YYYY-MM-DD para YYYY-MM-DDTHH:mm:ss
        if (isStartDate) {
            return dateTrimmed + "T00:00:00";
        } else {
            return dateTrimmed + "T23:59:59";
        }
    }
    
    /**
     * Formata uma data para o formato datetime do SQL Server.
     * Usa formato ISO 8601 com 'T' e milissegundos, que é o formato aceito pelo SQL Server.
     * 
     * @param dateString Data no formato ISO (ex: "2025-11-07T00:00:00")
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
     * Converte um array de objetos retornado pela query para ExchangeDTO.
     * Ordem dos campos: [ORICOD, OPECOD, LOJCOD, DOCCOD, FUNCOD, DOCNUMDOC, DOCCHVNFE, DOCDATEMI, DOCOBS]
     * 
     * @param row Array de objetos com os dados da query
     * @return DTO com os dados formatados
     */
    private ExchangeDTO convertToDTO(Object[] row) {
        if (row == null || row.length < 9) {
            log.warn("Linha de resultado inválida ou incompleta. Esperado: 9 campos, recebido: {}", 
                    row != null ? row.length : 0);
            return null;
        }
        
        try {
            return ExchangeDTO.builder()
                    .originCode(convertToString(row[0]))  // ORICOD
                    .operationCode(convertToString(row[1]))  // OPECOD
                    .storeCode(formatId(row[2]))  // LOJCOD
                    .documentCode(formatId(row[3]))  // DOCCOD
                    .employeeCode(formatId(row[4]))  // FUNCOD
                    .documentNumber(convertToString(row[5]))  // DOCNUMDOC
                    .nfeKey(convertToString(row[6]))  // DOCCHVNFE
                    .issueDate(convertToDate(row[7]))  // DOCDATEMI
                    .observation(convertToString(row[8]))  // DOCOBS
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
}

