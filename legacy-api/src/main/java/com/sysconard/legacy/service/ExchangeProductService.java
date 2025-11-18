package com.sysconard.legacy.service;

import com.sysconard.legacy.dto.ExchangeProductDTO;
import com.sysconard.legacy.dto.ExchangeProductRequestDTO;
import com.sysconard.legacy.repository.IncomingItemsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Serviço responsável pela lógica de negócio dos produtos de trocas.
 * Processa requisições e executa queries para retornar dados de produtos de trocas.
 * 
 * @author Sysconard Legacy API
 * @version 2.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExchangeProductService {
    
    private final IncomingItemsRepository incomingItemsRepository;
    
    /**
     * Busca produtos de trocas baseado em números de nota e chaves NFE.
     * 
     * @param request DTO com parâmetros da requisição
     * @return Lista de DTOs com os dados dos produtos de trocas
     */
    public List<ExchangeProductDTO> findExchangeProducts(ExchangeProductRequestDTO request) {
        log.debug("Iniciando busca de produtos de trocas: documentNumbers={}, nfeKeys={}", 
                 request.getDocumentNumbers() != null ? request.getDocumentNumbers().size() : 0, 
                 request.getNfeKeys() != null ? request.getNfeKeys().size() : 0);
        
        // Validar requisição
        validateRequest(request);
        
        try {
            // Converter documentNumbers de String para Integer (removendo zeros à esquerda)
            List<Integer> documentNumbersInt = convertDocumentNumbersToInteger(request.getDocumentNumbers());
            
            // Preparar listas para o repository (null se vazias)
            List<Integer> documentNumbersParam = (documentNumbersInt == null || documentNumbersInt.isEmpty()) 
                    ? null 
                    : documentNumbersInt;
            List<String> nfeKeysParam = (request.getNfeKeys() == null || request.getNfeKeys().isEmpty()) 
                    ? null 
                    : request.getNfeKeys();
            
            log.debug("Parâmetros preparados - documentNumbers: {}, nfeKeys: {}", 
                     documentNumbersParam != null ? documentNumbersParam.size() : 0,
                     nfeKeysParam != null ? nfeKeysParam.size() : 0);
            
            // Executar query nativa
            List<Object[]> results = incomingItemsRepository.findExchangeProducts(
                    documentNumbersParam,
                    nfeKeysParam
            );
            
            log.info("Produtos de trocas encontrados: {}", results.size());
            
            // Converter resultados para DTOs, filtrando nulos
            List<ExchangeProductDTO> products = results.stream()
                    .map(this::convertToDTO)
                    .filter(dto -> dto != null)
                    .collect(Collectors.toList());
            
            int failedConversions = results.size() - products.size();
            if (failedConversions > 0) {
                log.warn("{} linhas não puderam ser convertidas para DTO", failedConversions);
            }
            
            log.debug("Conversão para DTOs concluída: {} itens processados de {} resultados", 
                     products.size(), results.size());
            
            return products;
            
        } catch (IllegalArgumentException e) {
            log.error("Erro de validação nos parâmetros: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Erro ao buscar produtos de trocas: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao buscar produtos de trocas: " + e.getMessage(), e);
        }
    }
    
    /**
     * Valida os parâmetros da requisição.
     * Pelo menos uma das listas (documentNumbers OU nfeKeys) deve estar preenchida.
     * 
     * @param request DTO com parâmetros da requisição
     * @throws IllegalArgumentException se os parâmetros forem inválidos
     */
    private void validateRequest(ExchangeProductRequestDTO request) {
        boolean hasDocumentNumbers = request.getDocumentNumbers() != null 
                && !request.getDocumentNumbers().isEmpty();
        boolean hasNfeKeys = request.getNfeKeys() != null 
                && !request.getNfeKeys().isEmpty();
        
        if (!hasDocumentNumbers && !hasNfeKeys) {
            throw new IllegalArgumentException(
                    "Pelo menos uma das listas deve estar preenchida: documentNumbers ou nfeKeys");
        }
    }
    
    /**
     * Converte lista de números de documento (String) para Integer, removendo zeros à esquerda.
     * 
     * @param documentNumbers Lista de números de documento como String
     * @return Lista de números de documento como Integer
     */
    private List<Integer> convertDocumentNumbersToInteger(List<String> documentNumbers) {
        if (documentNumbers == null || documentNumbers.isEmpty()) {
            return null;
        }
        
        return documentNumbers.stream()
                .filter(num -> num != null && !num.trim().isEmpty())
                .map(num -> {
                    try {
                        // Remove zeros à esquerda convertendo para Integer
                        return Integer.parseInt(num.trim());
                    } catch (NumberFormatException e) {
                        log.warn("Erro ao converter número de documento para Integer: {}", num);
                        return null;
                    }
                })
                .filter(num -> num != null)
                .collect(Collectors.toList());
    }
    
    /**
     * Converte um array de objetos retornado pela query para ExchangeProductDTO.
     * Ordem dos campos: [LOJCOD, ENTCOD, REFPLU, ITEDATMOV, QUANTIDADE, ITEVLREMBAS, DOCNUMDOC, DOCCHVNFE]
     * 
     * @param row Array de objetos com os dados da query
     * @return DTO com os dados formatados
     */
    private ExchangeProductDTO convertToDTO(Object[] row) {
        if (row == null || row.length < 8) {
            log.warn("Linha de resultado inválida ou incompleta. Esperado: 8 campos, recebido: {}", 
                    row != null ? row.length : 0);
            return null;
        }
        
        try {
            return ExchangeProductDTO.builder()
                    .storeCode(formatId(row[0]))  // LOJCOD
                    .documentCode(formatId(row[1]))  // ENTCOD
                    .productRefCode(convertToString(row[2]))  // REFPLU
                    .exchangeDate(convertToDate(row[3]))  // ITEDATMOV
                    .quantity(convertToInteger(row[4]))  // QUANTIDADE
                    .unitValue(convertToBigDecimal(row[5]))  // ITEVLREMBAS
                    .documentNumber(convertToString(row[6]))  // DOCNUMDOC
                    .nfeKey(convertToString(row[7]))  // DOCCHVNFE
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

