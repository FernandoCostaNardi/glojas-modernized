package com.sysconard.business.exception.operation;

/**
 * Exceção lançada quando uma operação com o mesmo código já existe no sistema.
 * Utilizada em operações de criação e atualização para garantir unicidade do código.
 */
public class OperationAlreadyExistsException extends RuntimeException {
    
    private final String code;
    
    /**
     * Construtor que recebe o código da operação que já existe.
     * 
     * @param code Código da operação que já existe
     */
    public OperationAlreadyExistsException(String code) {
        super(String.format("Operação com código '%s' já existe no sistema", code));
        this.code = code;
    }
    
    /**
     * Construtor que recebe o código e uma mensagem customizada.
     * 
     * @param code Código da operação que já existe
     * @param message Mensagem customizada de erro
     */
    public OperationAlreadyExistsException(String code, String message) {
        super(message);
        this.code = code;
    }
    
    /**
     * Retorna o código da operação que já existe.
     * 
     * @return Código da operação
     */
    public String getCode() {
        return code;
    }
}
