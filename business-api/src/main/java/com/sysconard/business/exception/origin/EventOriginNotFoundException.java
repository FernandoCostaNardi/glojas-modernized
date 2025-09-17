package com.sysconard.business.exception.origin;

import java.util.UUID;

/**
 * Exceção lançada quando um EventOrigin não é encontrado.
 * Segue padrões de Clean Code com mensagens descritivas.
 */
public class EventOriginNotFoundException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Construtor com ID do EventOrigin.
     * 
     * @param id ID do EventOrigin não encontrado
     */
    public EventOriginNotFoundException(UUID id) {
        super(String.format("EventOrigin com ID '%s' não encontrado", id));
    }
    
    /**
     * Construtor com sourceCode do EventOrigin.
     * 
     * @param sourceCode Código da fonte não encontrado
     */
    public EventOriginNotFoundException(String sourceCode) {
        super(String.format("EventOrigin com sourceCode '%s' não encontrado", sourceCode));
    }
    
    /**
     * Construtor com mensagem customizada.
     * 
     * @param message Mensagem de erro
     */
    public EventOriginNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
