package com.sysconard.legacy.repository;

import com.sysconard.legacy.entity.Documento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository para operações com a entidade Documento.
 * Contém queries nativas otimizadas para relatórios de vendas.
 * 
 * @author Sysconard Legacy API
 * @version 1.0
 */
@Repository
public interface DocumentRepository extends JpaRepository<Documento, Long> {
    
    /**
     * Busca dados otimizados para relatório de vendas por loja com agregação no SQL.
     * Aplica filtro de data diretamente no banco para melhor performance.
     * Retorna dados já agregados por loja com valores calculados de TROCA, PDV e DANFE.
     *
     * @param storeCodes Lista de códigos de loja para filtrar
     * @param startDate Data de início do período (formato timestamp)
     * @param endDate Data de fim do período (formato timestamp)
     * @param danfeOrigin Lista de códigos de origem para DANFE
     * @param pdvOrigin Lista de códigos de origem para PDV
     * @param exchangeOrigin Lista de códigos de origem para trocas
     * @param sellOperation Lista de códigos de operação para vendas
     * @param exchangeOperation Lista de códigos de operação para trocas
     * @return Lista de arrays Object[] com dados agregados: [LOJFAN, LOJCOD, TROCA, PDV, DANFE]
     */
    @Query(value = "SELECT " +
                   "J.LOJFAN, " +
                   "J.LOJCOD, " +
                   "SUM(CASE " +
                   "    WHEN D.ORICOD IN (:exchangeOrigin) " +
                   "     AND D.OPECOD IN (:exchangeOperation) " +
                   "    THEN D.DOCVLRTOT " +
                   "    ELSE 0 " +
                   "END) AS TROCA, " +
                   "SUM(CASE " +
                   "    WHEN D.ORICOD IN (:pdvOrigin) " +
                   "     AND D.OPECOD IN (:sellOperation) " +
                   "    THEN D.DOCVLRTOT " +
                   "    ELSE 0 " +
                   "END) AS PDV, " +
                   "SUM(CASE " +
                   "    WHEN D.ORICOD IN (:danfeOrigin) " +
                   "     AND D.OPECOD IN (:sellOperation) " +
                   "     AND D.DOCSTANFE = 'A' " +
                   "    THEN D.DOCVLRTOT " +
                   "    ELSE 0 " +
                   "END) AS DANFE " +
                   "FROM LOJA J " +
                   "LEFT JOIN DOCUMENTO D ON D.LOJCOD = J.LOJCOD " +
                   "    AND D.DOCSTA = 'E' " +
                   "    AND D.DOCDATEMI >= CAST(:startDate AS DATETIME) " +
                   "    AND D.DOCDATEMI < CAST(:endDate AS DATETIME) " +
                   "    AND ( " +
                   "        (D.ORICOD IN (:danfeOrigin) AND D.OPECOD IN (:sellOperation)) " +
                   "        OR " +
                   "        (D.ORICOD IN (:pdvOrigin) AND D.OPECOD IN (:sellOperation)) " +
                   "        OR " +
                   "        (D.ORICOD IN (:exchangeOrigin) AND D.OPECOD IN (:exchangeOperation) AND D.DOCSTANFE = 'A') " +
                   "    ) " +
                   "WHERE J.LOJCOD IN (:storeCodes) " +
                   "GROUP BY J.LOJFAN, J.LOJCOD " +
                   "ORDER BY J.LOJFAN", nativeQuery = true)
    List<Object[]> findStoreSalesOptimizedData(@Param("storeCodes") List<String> storeCodes,
                                              @Param("startDate") String startDate,
                                              @Param("endDate") String endDate,
                                              @Param("danfeOrigin") List<String> danfeOrigin,
                                              @Param("pdvOrigin") List<String> pdvOrigin,
                                              @Param("exchangeOrigin") List<String> exchangeOrigin,
                                              @Param("sellOperation") List<String> sellOperation,
                                              @Param("exchangeOperation") List<String> exchangeOperation);

    /**
     * Busca dados otimizados para relatório de vendas por loja e por dia com agregação no SQL.
     * Aplica filtro de data diretamente no banco e agrupa por loja e data.
     * Retorna dados agregados por loja e por dia com valores calculados de TROCA, PDV e DANFE.
     *
     * @param storeCodes Lista de códigos de loja para filtrar
     * @param startDate Data de início do período (formato timestamp)
     * @param endDate Data de fim do período (formato timestamp)
     * @param danfeOrigin Lista de códigos de origem para DANFE
     * @param pdvOrigin Lista de códigos de origem para PDV
     * @param exchangeOrigin Lista de códigos de origem para trocas
     * @param sellOperation Lista de códigos de operação para vendas
     * @param exchangeOperation Lista de códigos de operação para trocas
     * @return Lista de arrays Object[] com dados agregados por loja e dia: [LOJFAN, LOJCOD, DATA, TROCA, PDV, DANFE]
     */
    @Query(value = "SELECT " +
                   "J.LOJFAN, " +
                   "J.LOJCOD, " +
                   "CAST(D.DOCDATEMI AS DATE) AS DATA, " +
                   "SUM(CASE " +
                   "    WHEN D.ORICOD IN (:exchangeOrigin) " +
                   "     AND D.OPECOD IN (:exchangeOperation) " +
                   "    THEN D.DOCVLRTOT " +
                   "    ELSE 0 " +
                   "END) AS TROCA, " +
                   "SUM(CASE " +
                   "    WHEN D.ORICOD IN (:pdvOrigin) " +
                   "     AND D.OPECOD IN (:sellOperation) " +
                   "    THEN D.DOCVLRTOT " +
                   "    ELSE 0 " +
                   "END) AS PDV, " +
                   "SUM(CASE " +
                   "    WHEN D.ORICOD IN (:danfeOrigin) " +
                   "     AND D.OPECOD IN (:sellOperation) " +
                   "     AND D.DOCSTANFE = 'A' " +
                   "    THEN D.DOCVLRTOT " +
                   "    ELSE 0 " +
                   "END) AS DANFE " +
                   "FROM LOJA J " +
                   "LEFT JOIN DOCUMENTO D ON D.LOJCOD = J.LOJCOD " +
                   "    AND D.DOCSTA = 'E' " +
                   "    AND D.DOCDATEMI >= CAST(:startDate AS DATETIME) " +
                   "    AND D.DOCDATEMI < CAST(:endDate AS DATETIME) " +
                   "    AND ( " +
                   "        (D.ORICOD IN (:danfeOrigin) AND D.OPECOD IN (:sellOperation)) " +
                   "        OR " +
                   "        (D.ORICOD IN (:pdvOrigin) AND D.OPECOD IN (:sellOperation)) " +
                   "        OR " +
                   "        (D.ORICOD IN (:exchangeOrigin) AND D.OPECOD IN (:exchangeOperation) AND D.DOCSTANFE = 'A') " +
                   "    ) " +
                   "WHERE J.LOJCOD IN (:storeCodes) " +
                   "GROUP BY J.LOJFAN, J.LOJCOD, CAST(D.DOCDATEMI AS DATE) " +
                   "ORDER BY J.LOJFAN, CAST(D.DOCDATEMI AS DATE)", nativeQuery = true)
    List<Object[]> findStoreSalesByDayOptimizedData(@Param("storeCodes") List<String> storeCodes,
                                                   @Param("startDate") String startDate,
                                                   @Param("endDate") String endDate,
                                                   @Param("danfeOrigin") List<String> danfeOrigin,
                                                   @Param("pdvOrigin") List<String> pdvOrigin,
                                                   @Param("exchangeOrigin") List<String> exchangeOrigin,
                                                   @Param("sellOperation") List<String> sellOperation,
                                                   @Param("exchangeOperation") List<String> exchangeOperation);

    /**
     * Busca trocas realizadas em um período específico.
     * Aplica filtros de origem, operação, status e período de datas.
     * Retorna dados detalhados de cada troca realizada.
     *
     * @param originCodes Lista de códigos de origem (ORICOD) para filtrar
     * @param operationCodes Lista de códigos de operação (OPECOD) para filtrar
     * @param startDate Data de início do período (formato timestamp: YYYY-MM-DDTHH:mm:ss)
     * @param endDate Data de fim do período (formato timestamp: YYYY-MM-DDTHH:mm:ss)
     * @return Lista de arrays Object[] com dados das trocas: [ORICOD, OPECOD, LOJCOD, DOCCOD, FUNCOD, DOCNUMDOC, DOCCHVNFE, DOCDATEMI, DOCOBS]
     */
    @Query(value = "SELECT D.ORICOD, D.OPECOD, D.LOJCOD, D.DOCCOD, D.FUNCOD, " +
                   "D.DOCNUMDOC, D.DOCCHVNFE, D.DOCDATEMI, D.DOCOBS " +
                   "FROM DOCUMENTO D " +
                   "WHERE D.ORICOD IN (:originCodes) " +
                   "  AND D.OPECOD IN (:operationCodes) " +
                   "  AND D.DOCSTA = 'E' " +
                   "  AND D.DOCSTANFE = 'A' " +
                   "  AND D.DOCDATEMI >= CAST(:startDate AS DATETIME) " +
                   "  AND D.DOCDATEMI < CAST(:endDate AS DATETIME)",
           nativeQuery = true)
    List<Object[]> findExchanges(@Param("originCodes") List<String> originCodes,
                                 @Param("operationCodes") List<String> operationCodes,
                                 @Param("startDate") String startDate,
                                 @Param("endDate") String endDate);
}
