package com.sysconard.business.exception;

/**
 * Exceção lançada quando tenta criar um EmailNotifier com email já existente
 * Seguindo princípios de Clean Code com responsabilidade única
 */
public class EmailAlreadyExistsException extends RuntimeException {
    
    /**
     * Construtor com mensagem de erro
     * @param message Mensagem de erro
     */
    public EmailAlreadyExistsException(String message) {
        super(message);
    }
    
    /**
     * Construtor com mensagem de erro e causa
     * @param message Mensagem de erro
     * @param cause Causa da exceção
     */
    public EmailAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
