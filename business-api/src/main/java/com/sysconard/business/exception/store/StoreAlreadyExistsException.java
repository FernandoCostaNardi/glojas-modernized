package com.sysconard.business.exception.store;

/**
 * Exceção lançada quando uma loja com o mesmo código já existe no sistema.
 * Utilizada em operações de criação e atualização de lojas.
 */
public class StoreAlreadyExistsException extends RuntimeException {
    
    /**
     * Construtor que recebe o código da loja que já existe.
     * 
     * @param code Código da loja que já existe
     */
    public StoreAlreadyExistsException(String code) {
        super("Loja com código '" + code + "' já existe no sistema");
    }
    
    /**
     * Construtor que recebe uma mensagem customizada.
     * 
     * @param message Mensagem de erro
     */
    public StoreAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
