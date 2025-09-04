package com.sysconard.business.exception;

import java.util.UUID;

/**
 * Exceção lançada quando um usuário não é encontrado no sistema.
 */
public class UserNotFoundException extends RuntimeException {
    
    public UserNotFoundException(String message) {
        super(message);
    }
    
    public UserNotFoundException(UUID userId) {
        super("Usuário com ID " + userId + " não encontrado");
    }
    
    public UserNotFoundException(String field, String value) {
        super("Usuário com " + field + " '" + value + "' não encontrado");
    }
}
