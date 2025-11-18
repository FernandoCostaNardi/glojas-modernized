package com.sysconard.business.exception.jobposition;

import java.util.UUID;

/**
 * Exceção lançada quando um cargo não é encontrado no sistema.
 * 
 * @author Sysconard Business API
 * @version 1.0
 */
public class JobPositionNotFoundException extends RuntimeException {
    
    public JobPositionNotFoundException(UUID id) {
        super("Cargo não encontrado com ID: " + id);
    }
    
    public JobPositionNotFoundException(String code) {
        super("Cargo não encontrado com código: " + code);
    }
}

