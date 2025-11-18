package com.sysconard.business.service.exchange;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Componente responsável por processar observações de trocas e extrair informações.
 * Extrai número da nova venda e chave NFE da venda a partir de padrões específicos na observação.
 */
@Slf4j
@Component
public class ExchangeObservationProcessor {
    
    // Padrão para extrair número de nota: "DEVOLUCAO NOTA(S) FISCAL(IS): 1/000054118" ou "CANCELAMENTO/ESTORNO DEVOLUCAO NOTA(S) FISCAL(IS): 1/000011863"
    private static final Pattern NOTE_NUMBER_PATTERN = Pattern.compile(
        "(?:DEVOLUCAO|CANCELAMENTO/ESTORNO DEVOLUCAO)\\s+NOTA\\(S\\)\\s+FISCAL\\(IS\\):\\s*\\d+/(\\d+)",
        Pattern.CASE_INSENSITIVE
    );
    
    // Padrão para extrair chave NFE: "[REFERENTE A TROCA: CHAVE: 23250816571889000440592300701510399804480711]"
    private static final Pattern NFE_KEY_PATTERN = Pattern.compile(
        "\\[REFERENTE\\s+A\\s+TROCA:\\s*CHAVE:\\s*(\\d+)\\]",
        Pattern.CASE_INSENSITIVE
    );
    
    // Padrão para ignorar: "[REFERENTE A DEVOLUÇÃO: C.O.O: 113 ECF: 001]"
    private static final Pattern IGNORE_PATTERN = Pattern.compile(
        "\\[REFERENTE\\s+A\\s+DEVOLUÇÃO:.*\\]",
        Pattern.CASE_INSENSITIVE
    );
    
    /**
     * Record para resultado do processamento da observação.
     * 
     * @param newSaleNumber Número da nova venda extraído (pode ser null)
     * @param newSaleNfeKey Chave NFE da venda extraída (pode ser null)
     */
    public record ProcessedObservationResult(
        String newSaleNumber,
        String newSaleNfeKey
    ) {
    }
    
    /**
     * Processa a observação e extrai informações de nova venda e chave NFE.
     * 
     * @param observation Observação a ser processada
     * @return Resultado do processamento com número da nova venda e chave NFE extraídos
     */
    public ProcessedObservationResult processObservation(String observation) {
        if (observation == null || observation.trim().isEmpty()) {
            log.debug("Observação vazia ou nula, retornando resultado vazio");
            return new ProcessedObservationResult(null, null);
        }
        
        // Verificar se deve ser ignorado
        if (IGNORE_PATTERN.matcher(observation).find()) {
            log.debug("Observação corresponde ao padrão de ignorar: {}", observation);
            return new ProcessedObservationResult(null, null);
        }
        
        String newSaleNumber = extractNewSaleNumber(observation);
        String newSaleNfeKey = extractNewSaleNfeKey(observation);
        
        log.debug("Observação processada - newSaleNumber: {}, newSaleNfeKey: {}", 
                 newSaleNumber, newSaleNfeKey);
        
        return new ProcessedObservationResult(newSaleNumber, newSaleNfeKey);
    }
    
    /**
     * Extrai o número da nova venda da observação.
     * 
     * @param observation Observação a ser processada
     * @return Número da nova venda extraído ou null se não encontrado
     */
    private String extractNewSaleNumber(String observation) {
        Matcher matcher = NOTE_NUMBER_PATTERN.matcher(observation);
        
        if (matcher.find()) {
            String number = matcher.group(1);
            log.debug("Número da nova venda extraído: {}", number);
            return number;
        }
        
        return null;
    }
    
    /**
     * Extrai a chave NFE da venda da observação.
     * 
     * @param observation Observação a ser processada
     * @return Chave NFE extraída ou null se não encontrada
     */
    private String extractNewSaleNfeKey(String observation) {
        Matcher matcher = NFE_KEY_PATTERN.matcher(observation);
        
        if (matcher.find()) {
            String nfeKey = matcher.group(1);
            log.debug("Chave NFE da venda extraída: {}", nfeKey);
            return nfeKey;
        }
        
        return null;
    }
}

