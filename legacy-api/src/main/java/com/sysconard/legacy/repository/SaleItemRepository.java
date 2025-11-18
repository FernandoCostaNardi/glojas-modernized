package com.sysconard.legacy.repository;

import com.sysconard.legacy.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository para operações de acesso a dados de itens de venda.
 * Contém queries nativas para buscar detalhes de itens de venda.
 * 
 * @author Sysconard Legacy API
 * @version 1.0
 */
@Repository
public interface SaleItemRepository extends JpaRepository<Product, Long> {
    
    /**
     * Busca detalhes de itens de venda de um período específico.
     * Retorna dados detalhados de cada item vendido com informações do produto e da venda.
     * 
     * @param originCodes Lista de códigos de origem (ORICOD) para PDV e DANFE
     * @param operationCodes Lista de códigos de operação (OPECOD) do tipo SELL
     * @param storeCodes Lista de códigos de lojas (LOJCOD)
     * @param startDate Data inicial do período (formato datetime SQL Server)
     * @param endDate Data final do período (formato datetime SQL Server)
     * @return Lista de arrays Object[] com os dados dos itens de venda
     */
    @Query(value = "SELECT " +
                   "    d.DOCDATEMI, " +
                   "    its.SAICOD, " +
                   "    its.ITSSEQ, " +
                   "    d.FUNCOD, " +
                   "    its.REFPLU, " +
                   "    its.LOJCOD, " +
                   "    prd.PROCOD, " +
                   "    m.MARDES, " +
                   "    s.SECDES, " +
                   "    grop.GRPDES, " +
                   "    sub.SBGDES, " +
                   "    prd.PRODES, " +
                   "    prd.PRONCM, " +
                   "    CONVERT(INT, its.ITSQTDTOT) AS ITSQTDTOT, " +
                   "    CAST(its.ITSTOTFAT / NULLIF(its.ITSQTDTOT, 0) AS DECIMAL(10,2)) AS Unitario, " +
                   "    CAST(its.ITSTOTFAT AS DECIMAL(10,2)) AS ITSTOTFAT " +
                   "FROM DOCUMENTO AS d " +
                   "INNER LOOP JOIN ITEM_SAIDA AS its " +
                   "    ON its.SAICOD = d.DOCCOD " +
                   "   AND its.LOJCOD = d.LOJCOD " +
                   "INNER JOIN REFERENCIA AS ref " +
                   "    ON ref.REFPLU = its.REFPLU " +
                   "INNER JOIN PRODUTO AS prd " +
                   "    ON prd.PROCOD = ref.PROCOD " +
                   "INNER JOIN MARCA AS m " +
                   "    ON m.MARCOD = prd.MARCOD " +
                   "INNER JOIN SECAO AS s " +
                   "    ON s.SECCOD = prd.SECCOD " +
                   "INNER JOIN GRUPO AS grop " +
                   "    ON grop.SECCOD = prd.SECCOD " +
                   "   AND grop.GRPCOD = prd.GRPCOD " +
                   "INNER JOIN SUBGRUPO AS sub " +
                   "    ON sub.SECCOD = prd.SECCOD " +
                   "   AND sub.GRPCOD = prd.GRPCOD " +
                   "   AND sub.SBGCOD = prd.SBGCOD " +
                   "WHERE " +
                   "    d.DOCSTA = 'E' " +
                   "    AND d.DOCSTANFE = 'A' " +
                   "    AND d.ORICOD IN (:originCodes) " +
                   "    AND d.OPECOD IN (:operationCodes) " +
                   "    AND its.LOJCOD IN (:storeCodes) " +
                   "    AND d.DOCDATEMI >= CAST(:startDate AS DATETIME) " +
                   "    AND d.DOCDATEMI < CAST(:endDate AS DATETIME)",
           nativeQuery = true)
    List<Object[]> findSaleItemDetails(
            @Param("originCodes") List<String> originCodes,
            @Param("operationCodes") List<String> operationCodes,
            @Param("storeCodes") List<String> storeCodes,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate
    );
}

