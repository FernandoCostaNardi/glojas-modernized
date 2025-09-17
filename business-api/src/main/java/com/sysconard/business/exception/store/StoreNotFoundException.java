package com.sysconard.business.exception.store;

import java.util.UUID;

/**
 * Exceção lançada quando uma loja não é encontrada no sistema.
 * Utilizada em operações de busca, atualização e exclusão de lojas.
 */
public class StoreNotFoundException extends RuntimeException {
    
    /**
     * Construtor que recebe o ID da loja não encontrada.
     * 
     * @param id ID da loja não encontrada
     */
    public StoreNotFoundException(UUID id) {
        super("Loja não encontrada com ID: " + id);
    }
    
    /**
     * Construtor que recebe o código da loja não encontrada.
     * 
     * @param code Código da loja não encontrada
     */
    public StoreNotFoundException(String code) {
        super("Loja não encontrada com código: " + code);
    }
    
    /**
     * Construtor que recebe uma mensagem customizada.
     * 
     * @param message Mensagem de erro
     */
    public StoreNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
