package com.sysconard.business.exception.sell;

/**
 * Exceção específica para operações de relatório de vendas por loja.
 * Utilizada quando há problemas na comunicação com a Legacy API ou
 * no processamento dos dados de vendas.
 * 
 * @author Business API
 * @version 1.0
 */
public class StoreReportException extends RuntimeException {
    
    /**
     * Construtor com mensagem
     * 
     * @param message Mensagem de erro
     */
    public StoreReportException(String message) {
        super(message);
    }
    
    /**
     * Construtor com mensagem e causa
     * 
     * @param message Mensagem de erro
     * @param cause Causa raiz do erro
     */
    public StoreReportException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Construtor com causa
     * 
     * @param cause Causa raiz do erro
     */
    public StoreReportException(Throwable cause) {
        super(cause);
    }
}
