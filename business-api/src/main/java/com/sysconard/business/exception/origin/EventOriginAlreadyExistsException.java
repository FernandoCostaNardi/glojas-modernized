package com.sysconard.business.exception.origin;

/**
 * Exceção lançada quando um EventOrigin já existe.
 * Segue padrões de Clean Code com mensagens descritivas.
 */
public class EventOriginAlreadyExistsException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Construtor com sourceCode do EventOrigin.
     * 
     * @param sourceCode Código da fonte que já existe
     */
    public EventOriginAlreadyExistsException(String sourceCode) {
        super(String.format("EventOrigin com sourceCode '%s' já existe", sourceCode));
    }
    
    /**
     * Construtor com mensagem customizada.
     * 
     * @param message Mensagem de erro
     */
    public EventOriginAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
