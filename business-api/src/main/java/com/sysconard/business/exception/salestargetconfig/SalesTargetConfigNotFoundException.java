package com.sysconard.business.exception.salestargetconfig;

import java.util.UUID;

/**
 * Exceção lançada quando uma configuração de metas e comissões não é encontrada no sistema.
 * 
 * @author Sysconard Business API
 * @version 1.0
 */
public class SalesTargetConfigNotFoundException extends RuntimeException {
    
    public SalesTargetConfigNotFoundException(UUID id) {
        super("Configuração de metas e comissões não encontrada com ID: " + id);
    }
}

