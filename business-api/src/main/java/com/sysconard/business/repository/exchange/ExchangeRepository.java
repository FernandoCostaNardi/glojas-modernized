package com.sysconard.business.repository.exchange;

import com.sysconard.business.entity.exchange.Exchange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository para operações de acesso a dados da entidade Exchange.
 * Estende JpaRepository para operações CRUD básicas e JpaSpecificationExecutor para buscas com Specification.
 */
@Repository
public interface ExchangeRepository extends JpaRepository<Exchange, UUID>, JpaSpecificationExecutor<Exchange> {
    
    /**
     * Busca uma troca pelo código do documento e código da loja.
     * 
     * @param documentCode Código do documento
     * @param storeCode Código da loja
     * @return Optional contendo a troca se encontrada
     */
    Optional<Exchange> findByDocumentCodeAndStoreCode(String documentCode, String storeCode);
    
    /**
     * Verifica se existe uma troca com o código do documento e código da loja especificados.
     * 
     * @param documentCode Código do documento
     * @param storeCode Código da loja
     * @return true se existe, false caso contrário
     */
    boolean existsByDocumentCodeAndStoreCode(String documentCode, String storeCode);
    
    /**
     * Busca trocas por período de data de emissão.
     * 
     * @param start Data de início do período
     * @param end Data de fim do período
     * @return Lista de trocas encontradas no período
     */
    List<Exchange> findByIssueDateBetween(LocalDateTime start, LocalDateTime end);
}

