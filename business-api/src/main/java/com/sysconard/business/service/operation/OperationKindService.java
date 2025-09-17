package com.sysconard.business.service.operation;

import com.sysconard.business.client.LegacyApiClient;
import com.sysconard.business.dto.operation.OperationKindDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Serviço de negócio para operações com tipos de operação (OperationKind).
 * Orquestra chamadas para a Legacy API e aplica regras de negócio.
 * 
 * @author Business API
 * @version 1.0
 */
@Slf4j
@Service
public class OperationKindService {
    
    private final LegacyApiClient legacyApiClient;
    
    public OperationKindService(LegacyApiClient legacyApiClient) {
        this.legacyApiClient = legacyApiClient;
    }
    
    /**
     * Busca todos os tipos de operação disponíveis na Legacy API.
     * 
     * @return Lista de tipos de operação
     */
    public List<OperationKindDto> getAllOperationKinds() {
        log.info("Iniciando busca de tipos de operação na Legacy API");
        
        try {
            // Buscar dados na Legacy API
            List<OperationKindDto> operationKinds = legacyApiClient.getOperationKinds();
            
            log.info("Tipos de operação encontrados: {} itens", 
                    operationKinds != null ? operationKinds.size() : 0);
            
            return operationKinds != null ? operationKinds : Collections.emptyList();
            
        } catch (Exception e) {
            log.error("Erro ao buscar tipos de operação na Legacy API", e);
            throw new RuntimeException("Erro ao buscar tipos de operação: " + e.getMessage(), e);
        }
    }
    
    /**
     * Testa conectividade com a Legacy API para operações.
     * 
     * @return Resultado do teste de conexão
     */
    public Map<String, Object> testLegacyApiConnection() {
        log.info("Testando conectividade com Legacy API para operações");
        
        try {
            return legacyApiClient.testConnection();
        } catch (Exception e) {
            log.error("Erro no teste de conectividade", e);
            return Map.of(
                    "status", "ERROR",
                    "message", "Erro ao testar conectividade: " + e.getMessage(),
                    "timestamp", LocalDateTime.now().toString()
            );
        }
    }
}
