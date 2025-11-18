package com.sysconard.business.repository.sale;

import com.sysconard.business.entity.sale.SaleDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Repository para operações de acesso a dados da entidade SaleDetail.
 * Estende JpaRepository para operações CRUD básicas e define métodos customizados.
 * Utiliza queries otimizadas para verificação de duplicatas em lote.
 */
@Repository
public interface SaleDetailRepository extends JpaRepository<SaleDetail, UUID> {
    
    /**
     * Verifica se existe uma venda detalhada com a chave composta especificada.
     * Chave composta: saleCode + productRefCode + itemSequence
     * 
     * @param saleCode Código da venda
     * @param productRefCode Código de referência do produto
     * @param itemSequence Sequência do item na venda
     * @return true se existe, false caso contrário
     */
    boolean existsBySaleCodeAndProductRefCodeAndItemSequence(
            String saleCode, 
            String productRefCode, 
            Integer itemSequence
    );
    
    /**
     * Busca vendas detalhadas existentes pela chave composta.
     * Utilizado para verificação em lote de vendas existentes.
     * 
     * @param saleCode Código da venda
     * @param productRefCode Código de referência do produto
     * @param itemSequence Sequência do item na venda
     * @return Lista de vendas encontradas
     */
    @Query("SELECT sd FROM SaleDetail sd WHERE sd.saleCode = :saleCode " +
           "AND sd.productRefCode = :productRefCode AND sd.itemSequence = :itemSequence")
    List<SaleDetail> findBySaleCodeAndProductRefCodeAndItemSequence(
            @Param("saleCode") String saleCode,
            @Param("productRefCode") String productRefCode,
            @Param("itemSequence") Integer itemSequence
    );
    
    /**
     * Verifica quais vendas já existem no banco baseado na chave composta.
     * Retorna uma lista de tuplas (saleCode, productRefCode, itemSequence) que já existem.
     * Utilizado para verificação em lote mais eficiente.
     * 
     * @param saleCodes Lista de códigos de venda para verificar
     * @param productRefCodes Lista de códigos de referência de produtos para verificar
     * @param itemSequences Lista de sequências de itens para verificar
     * @return Lista de tuplas (saleCode, productRefCode, itemSequence) que já existem
     */
    @Query("SELECT sd.saleCode, sd.productRefCode, sd.itemSequence FROM SaleDetail sd " +
           "WHERE sd.saleCode IN :saleCodes " +
           "AND sd.productRefCode IN :productRefCodes " +
           "AND sd.itemSequence IN :itemSequences")
    List<Object[]> findExistingSaleDetails(
            @Param("saleCodes") Set<String> saleCodes,
            @Param("productRefCodes") Set<String> productRefCodes,
            @Param("itemSequences") Set<Integer> itemSequences
    );
    
    /**
     * Verifica se uma combinação específica de chave composta já existe.
     * Versão otimizada para verificação individual.
     * 
     * @param saleCode Código da venda
     * @param productRefCode Código de referência do produto
     * @param itemSequence Sequência do item na venda
     * @return true se existe, false caso contrário
     */
    @Query("SELECT COUNT(sd) > 0 FROM SaleDetail sd WHERE sd.saleCode = :saleCode " +
           "AND sd.productRefCode = :productRefCode AND sd.itemSequence = :itemSequence")
    boolean existsByCompositeKey(
            @Param("saleCode") String saleCode,
            @Param("productRefCode") String productRefCode,
            @Param("itemSequence") Integer itemSequence
    );
}

