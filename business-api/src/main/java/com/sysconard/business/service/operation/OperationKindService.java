package com.sysconard.business.service.operation;

import com.sysconard.business.client.LegacyApiClient;
import com.sysconard.business.dto.operation.OperationKindDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    private final OperationService operationService;
    
    public OperationKindService(LegacyApiClient legacyApiClient, OperationService operationService) {
        this.legacyApiClient = legacyApiClient;
        this.operationService = operationService;
    }
    
    /**
     * Busca todos os tipos de operação disponíveis para cadastro.
     * Retorna apenas os tipos de operação da Legacy API que ainda não foram cadastrados no sistema.
     * A comparação é feita entre o campo 'id' do OperationKindDto e o campo 'code' da entidade Operation.
     * 
     * @return Lista de tipos de operação disponíveis (não cadastrados)
     */
    public List<OperationKindDto> getAllOperationKinds() {
        log.info("Iniciando busca de tipos de operação disponíveis (não cadastrados)");
        
        try {
            // 1. Buscar todos os OperationKinds da Legacy API
            List<OperationKindDto> allOperationKinds = legacyApiClient.getOperationKinds();
            
            // 2. Buscar todos os códigos de operações já cadastradas
            List<String> registeredCodes = operationService.getAllOperationCodes();
            Set<String> registeredCodesSet = new HashSet<>(registeredCodes);
            
            // 3. Filtrar apenas os OperationKinds que NÃO foram cadastrados
            List<OperationKindDto> availableOperationKinds = allOperationKinds.stream()
                    .filter(kind -> !registeredCodesSet.contains(kind.id()))
                    .collect(Collectors.toList());
            
            log.info("Total de tipos de operação na Legacy API: {}", allOperationKinds.size());
            log.info("Total de operações já cadastradas: {}", registeredCodes.size());
            log.info("Total de tipos de operação disponíveis: {}", availableOperationKinds.size());
            
            return availableOperationKinds;
            
        } catch (Exception e) {
            log.error("Erro ao buscar tipos de operação disponíveis", e);
            throw new RuntimeException("Erro ao buscar tipos de operação: " + e.getMessage(), e);
        }
    }
    
}
