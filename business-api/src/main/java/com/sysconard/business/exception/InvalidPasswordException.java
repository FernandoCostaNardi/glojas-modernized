package com.sysconard.business.exception;

/**
 * Exceção lançada quando uma senha é inválida.
 */
public class InvalidPasswordException extends RuntimeException {
    
    public InvalidPasswordException(String message) {
        super(message);
    }
    
    public InvalidPasswordException(String field, String reason) {
        super("Senha inválida: " + field + " - " + reason);
    }
}
