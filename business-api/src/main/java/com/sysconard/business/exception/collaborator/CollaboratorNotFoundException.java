package com.sysconard.business.exception.collaborator;

import java.util.UUID;

/**
 * Exceção lançada quando um colaborador não é encontrado no sistema.
 * 
 * @author Sysconard Business API
 * @version 1.0
 */
public class CollaboratorNotFoundException extends RuntimeException {
    
    public CollaboratorNotFoundException(UUID id) {
        super("Colaborador não encontrado com ID: " + id);
    }
    
    public CollaboratorNotFoundException(String code) {
        super("Colaborador não encontrado com código: " + code);
    }
}

