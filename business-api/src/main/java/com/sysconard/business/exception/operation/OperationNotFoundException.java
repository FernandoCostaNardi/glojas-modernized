package com.sysconard.business.exception.operation;

import java.util.UUID;

/**
 * Exceção lançada quando uma operação não é encontrada no sistema.
 * Utilizada em operações de busca, atualização e exclusão.
 */
public class OperationNotFoundException extends RuntimeException {
    
    private final UUID operationId;
    
    /**
     * Construtor que recebe o ID da operação não encontrada.
     * 
     * @param operationId ID da operação que não foi encontrada
     */
    public OperationNotFoundException(UUID operationId) {
        super(String.format("Operação com ID %s não foi encontrada", operationId));
        this.operationId = operationId;
    }
    
    /**
     * Construtor que recebe o código da operação não encontrada.
     * 
     * @param code Código da operação que não foi encontrada
     */
    public OperationNotFoundException(String code) {
        super(String.format("Operação com código '%s' não foi encontrada", code));
        this.operationId = null;
    }
    
    /**
     * Retorna o ID da operação não encontrada.
     * 
     * @return ID da operação ou null se não disponível
     */
    public UUID getOperationId() {
        return operationId;
    }
}
