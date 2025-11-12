package com.sysconard.legacy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sysconard.legacy.entity.Product;

import java.util.List;

/**
 * Repository para análise de estoque crítico.
 * Identifica produtos com estoque menor que a média de vendas mensal.
 * 
 * @author Sysconard Legacy API
 * @version 1.0
 */
@Repository
public interface CriticalStockRepository extends JpaRepository<Product, Long> {
    
    /**
     * Busca produtos com estoque crítico (estoque < média mensal de vendas).
     * 
     * @param refplu Filtro opcional por REFPLU
     * @param refpluFilter Filtro formatado com LIKE
     * @param sortColumn Coluna para ordenação
     * @param sortDirection Direção da ordenação (ASC ou DESC)
     * @param offset Offset para paginação
     * @param size Tamanho da página
     * @return Lista de arrays de objetos com dados do estoque crítico
     */
    @Query(value = "WITH " +
            "Vendas90 AS ( " +
            "    SELECT its.REFPLU, SUM(its.ITSQTDTOT) AS Total " +
            "    FROM ITEM_SAIDA its " +
            "    INNER JOIN DOCUMENTO d ON its.SAICOD = d.DOCCOD " +
            "    WHERE its.LOJCOD = d.LOJCOD " +
            "      AND d.DOCSTA = 'E' " +
            "      AND CAST(d.ORICOD AS INT) < 100 " +
            "      AND d.DOCDATEMI >= DATEADD(MONTH, -3, DATEADD(DAY, 1-DAY(GETDATE()), GETDATE())) " +
            "      AND d.DOCDATEMI < DATEADD(MONTH, -2, DATEADD(DAY, 1-DAY(GETDATE()), GETDATE())) " +
            "    GROUP BY its.REFPLU " +
            "), " +
            "Vendas60 AS ( " +
            "    SELECT its.REFPLU, SUM(its.ITSQTDTOT) AS Total " +
            "    FROM ITEM_SAIDA its " +
            "    INNER JOIN DOCUMENTO d ON its.SAICOD = d.DOCCOD " +
            "    WHERE its.LOJCOD = d.LOJCOD " +
            "      AND d.DOCSTA = 'E' " +
            "      AND CAST(d.ORICOD AS INT) < 100 " +
            "      AND d.DOCDATEMI >= DATEADD(MONTH, -2, DATEADD(DAY, 1-DAY(GETDATE()), GETDATE())) " +
            "      AND d.DOCDATEMI < DATEADD(MONTH, -1, DATEADD(DAY, 1-DAY(GETDATE()), GETDATE())) " +
            "    GROUP BY its.REFPLU " +
            "), " +
            "Vendas30 AS ( " +
            "    SELECT its.REFPLU, SUM(its.ITSQTDTOT) AS Total " +
            "    FROM ITEM_SAIDA its " +
            "    INNER JOIN DOCUMENTO d ON its.SAICOD = d.DOCCOD " +
            "    WHERE its.LOJCOD = d.LOJCOD " +
            "      AND d.DOCSTA = 'E' " +
            "      AND CAST(d.ORICOD AS INT) < 100 " +
            "      AND d.DOCDATEMI >= DATEADD(MONTH, -1, DATEADD(DAY, 1-DAY(GETDATE()), GETDATE())) " +
            "      AND d.DOCDATEMI < DATEADD(DAY, 1-DAY(GETDATE()), GETDATE()) " +
            "    GROUP BY its.REFPLU " +
            "), " +
            "VendasAtual AS ( " +
            "    SELECT its.REFPLU, SUM(its.ITSQTDTOT) AS Total " +
            "    FROM ITEM_SAIDA its " +
            "    INNER JOIN DOCUMENTO d ON its.SAICOD = d.DOCCOD " +
            "    WHERE its.LOJCOD = d.LOJCOD " +
            "      AND d.DOCSTA = 'E' " +
            "      AND CAST(d.ORICOD AS INT) < 100 " +
            "      AND d.DOCDATEMI >= DATEADD(DAY, 1-DAY(GETDATE()), GETDATE()) " +
            "      AND d.DOCDATEMI < DATEADD(MONTH, 1, DATEADD(DAY, 1-DAY(GETDATE()), GETDATE())) " +
            "    GROUP BY its.REFPLU " +
            "), " +
            "EstoqueTotal AS ( " +
            "    SELECT REFPLU, SUM(ESTTOT) AS Total " +
            "    FROM ESTOQUE " +
            "    WHERE LOCCOD = '001' " +
            "    GROUP BY REFPLU " +
            "), " +
            "BaseQuery AS ( " +
            "    SELECT " +
            "        g.GRPDES AS descricaoGrupo, " +
            "        pt.REFCOD AS codigoPartNumber, " +
            "        m.MARDES AS descricaoMarca, " +
            "        r.REFPLU AS refplu, " +
            "        p.PRODES AS descricaoProduto, " +
            "        ISNULL(c.CSTREP, 0) AS custoReposicao, " +
            "        ISNULL(pr.PRCVDA1, 0) AS precoVenda, " +
            "        ISNULL(v90.Total, 0) AS vendas90Dias, " +
            "        ISNULL(v60.Total, 0) AS vendas60Dias, " +
            "        ISNULL(v30.Total, 0) AS vendas30Dias, " +
            "        ISNULL(va.Total, 0) AS vendasMesAtual, " +
            "        ISNULL(est.Total, 0) AS estoque, " +
            "        (ISNULL(v90.Total, 0) + ISNULL(v60.Total, 0) + ISNULL(v30.Total, 0)) / 3.0 AS mediaMensal, " +
            "        ((ISNULL(v90.Total, 0) + ISNULL(v60.Total, 0) + ISNULL(v30.Total, 0)) / 3.0) - ISNULL(est.Total, 0) AS diferenca, " +
            "        ROW_NUMBER() OVER (" +
            "            ORDER BY " +
            "            CASE WHEN :sortColumn = 'descricaoGrupo' AND :sortDirection = 'ASC' THEN g.GRPDES END ASC, " +
            "            CASE WHEN :sortColumn = 'descricaoGrupo' AND :sortDirection = 'DESC' THEN g.GRPDES END DESC, " +
            "            CASE WHEN :sortColumn = 'codigoPartNumber' AND :sortDirection = 'ASC' THEN pt.REFCOD END ASC, " +
            "            CASE WHEN :sortColumn = 'codigoPartNumber' AND :sortDirection = 'DESC' THEN pt.REFCOD END DESC, " +
            "            CASE WHEN :sortColumn = 'descricaoMarca' AND :sortDirection = 'ASC' THEN m.MARDES END ASC, " +
            "            CASE WHEN :sortColumn = 'descricaoMarca' AND :sortDirection = 'DESC' THEN m.MARDES END DESC, " +
            "            CASE WHEN :sortColumn = 'refplu' AND :sortDirection = 'ASC' THEN r.REFPLU END ASC, " +
            "            CASE WHEN :sortColumn = 'refplu' AND :sortDirection = 'DESC' THEN r.REFPLU END DESC, " +
            "            CASE WHEN :sortColumn = 'descricaoProduto' AND :sortDirection = 'ASC' THEN p.PRODES END ASC, " +
            "            CASE WHEN :sortColumn = 'descricaoProduto' AND :sortDirection = 'DESC' THEN p.PRODES END DESC, " +
            "            CASE WHEN :sortColumn = 'custoReposicao' AND :sortDirection = 'ASC' THEN ISNULL(c.CSTREP, 0) END ASC, " +
            "            CASE WHEN :sortColumn = 'custoReposicao' AND :sortDirection = 'DESC' THEN ISNULL(c.CSTREP, 0) END DESC, " +
            "            CASE WHEN :sortColumn = 'precoVenda' AND :sortDirection = 'ASC' THEN ISNULL(pr.PRCVDA1, 0) END ASC, " +
            "            CASE WHEN :sortColumn = 'precoVenda' AND :sortDirection = 'DESC' THEN ISNULL(pr.PRCVDA1, 0) END DESC, " +
            "            CASE WHEN :sortColumn = 'vendas90Dias' AND :sortDirection = 'ASC' THEN ISNULL(v90.Total, 0) END ASC, " +
            "            CASE WHEN :sortColumn = 'vendas90Dias' AND :sortDirection = 'DESC' THEN ISNULL(v90.Total, 0) END DESC, " +
            "            CASE WHEN :sortColumn = 'vendas60Dias' AND :sortDirection = 'ASC' THEN ISNULL(v60.Total, 0) END ASC, " +
            "            CASE WHEN :sortColumn = 'vendas60Dias' AND :sortDirection = 'DESC' THEN ISNULL(v60.Total, 0) END DESC, " +
            "            CASE WHEN :sortColumn = 'vendas30Dias' AND :sortDirection = 'ASC' THEN ISNULL(v30.Total, 0) END ASC, " +
            "            CASE WHEN :sortColumn = 'vendas30Dias' AND :sortDirection = 'DESC' THEN ISNULL(v30.Total, 0) END DESC, " +
            "            CASE WHEN :sortColumn = 'vendasMesAtual' AND :sortDirection = 'ASC' THEN ISNULL(va.Total, 0) END ASC, " +
            "            CASE WHEN :sortColumn = 'vendasMesAtual' AND :sortDirection = 'DESC' THEN ISNULL(va.Total, 0) END DESC, " +
            "            CASE WHEN :sortColumn = 'estoque' AND :sortDirection = 'ASC' THEN ISNULL(est.Total, 0) END ASC, " +
            "            CASE WHEN :sortColumn = 'estoque' AND :sortDirection = 'DESC' THEN ISNULL(est.Total, 0) END DESC, " +
            "            CASE WHEN :sortColumn = 'mediaMensal' AND :sortDirection = 'ASC' THEN (ISNULL(v90.Total, 0) + ISNULL(v60.Total, 0) + ISNULL(v30.Total, 0)) / 3.0 END ASC, " +
            "            CASE WHEN :sortColumn = 'mediaMensal' AND :sortDirection = 'DESC' THEN (ISNULL(v90.Total, 0) + ISNULL(v60.Total, 0) + ISNULL(v30.Total, 0)) / 3.0 END DESC, " +
            "            CASE WHEN :sortColumn = 'diferenca' AND :sortDirection = 'ASC' THEN ((ISNULL(v90.Total, 0) + ISNULL(v60.Total, 0) + ISNULL(v30.Total, 0)) / 3.0) - ISNULL(est.Total, 0) END ASC, " +
            "            CASE WHEN :sortColumn = 'diferenca' AND :sortDirection = 'DESC' THEN ((ISNULL(v90.Total, 0) + ISNULL(v60.Total, 0) + ISNULL(v30.Total, 0)) / 3.0) - ISNULL(est.Total, 0) END DESC, " +
            "            p.PROCOD ASC " +
            "        ) AS RowNum " +
            "    FROM PRODUTO p " +
            "    INNER JOIN GRUPO g ON p.GRPCOD = g.GRPCOD AND p.SECCOD = g.SECCOD " +
            "    INNER JOIN MARCA m ON p.MARCOD = m.MARCOD " +
            "    INNER JOIN REFERENCIA r ON p.PROCOD = r.PROCOD " +
            "    LEFT JOIN REFERENCIA_FABRICANTE pt ON r.REFPLU = pt.REFPLU " +
            "    LEFT JOIN CUSTO c ON r.REFPLU = c.REFPLU AND c.LOJCOD = 4 " +
            "    LEFT JOIN PRECO pr ON r.REFPLU = pr.REFPLU AND pr.LOJCOD = 4 " +
            "    LEFT JOIN Vendas90 v90 ON r.REFPLU = v90.REFPLU " +
            "    LEFT JOIN Vendas60 v60 ON r.REFPLU = v60.REFPLU " +
            "    LEFT JOIN Vendas30 v30 ON r.REFPLU = v30.REFPLU " +
            "    LEFT JOIN VendasAtual va ON r.REFPLU = va.REFPLU " +
            "    LEFT JOIN EstoqueTotal est ON r.REFPLU = est.REFPLU " +
            "    WHERE (:refplu IS NULL OR r.REFPLU LIKE :refpluFilter) " +
            "      AND (:grupo IS NULL OR g.GRPDES LIKE :grupoFilter) " +
            "      AND (:marca IS NULL OR m.MARDES LIKE :marcaFilter) " +
            "      AND (:descricao IS NULL OR :descricaoWords IS NULL OR " +
            "           (UPPER(p.PRODES) LIKE '%' + REPLACE(:descricaoWords, '|', '%') + '%' OR " +
            "            UPPER(g.GRPDES) LIKE '%' + REPLACE(:descricaoWords, '|', '%') + '%' OR " +
            "            UPPER(m.MARDES) LIKE '%' + REPLACE(:descricaoWords, '|', '%') + '%')) " +
            "      AND (ISNULL(v90.Total, 0) > 0 OR ISNULL(v60.Total, 0) > 0 OR ISNULL(v30.Total, 0) > 0) " +
            "      AND ISNULL(est.Total, 0) < ((ISNULL(v90.Total, 0) + ISNULL(v60.Total, 0) + ISNULL(v30.Total, 0)) / 3.0) " +
            ") " +
            "SELECT " +
            "    descricaoGrupo, codigoPartNumber, descricaoMarca, refplu, descricaoProduto, " +
            "    custoReposicao, precoVenda, vendas90Dias, vendas60Dias, vendas30Dias, " +
            "    vendasMesAtual, estoque, mediaMensal, diferenca " +
            "FROM BaseQuery " +
            "WHERE RowNum > :offset AND RowNum <= (:offset + :size)",
           nativeQuery = true)
    List<Object[]> findCriticalStockWithFilters(
        @Param("refplu") String refplu,
        @Param("refpluFilter") String refpluFilter,
        @Param("descricao") String descricao,
        @Param("descricaoWords") String descricaoWords,
        @Param("grupo") String grupo,
        @Param("grupoFilter") String grupoFilter,
        @Param("marca") String marca,
        @Param("marcaFilter") String marcaFilter,
        @Param("sortColumn") String sortColumn,
        @Param("sortDirection") String sortDirection,
        @Param("offset") int offset,
        @Param("size") int size
    );
    
    /**
     * Conta o total de produtos com estoque crítico.
     * 
     * @param refplu Filtro opcional por REFPLU
     * @param refpluFilter Filtro formatado com LIKE
     * @return Total de registros
     */
    @Query(value = "WITH " +
            "Vendas90 AS ( " +
            "    SELECT its.REFPLU, SUM(its.ITSQTDTOT) AS Total " +
            "    FROM ITEM_SAIDA its " +
            "    INNER JOIN DOCUMENTO d ON its.SAICOD = d.DOCCOD " +
            "    WHERE its.LOJCOD = d.LOJCOD " +
            "      AND d.DOCSTA = 'E' " +
            "      AND CAST(d.ORICOD AS INT) < 100 " +
            "      AND d.DOCDATEMI >= DATEADD(MONTH, -3, DATEADD(DAY, 1-DAY(GETDATE()), GETDATE())) " +
            "      AND d.DOCDATEMI < DATEADD(MONTH, -2, DATEADD(DAY, 1-DAY(GETDATE()), GETDATE())) " +
            "    GROUP BY its.REFPLU " +
            "), " +
            "Vendas60 AS ( " +
            "    SELECT its.REFPLU, SUM(its.ITSQTDTOT) AS Total " +
            "    FROM ITEM_SAIDA its " +
            "    INNER JOIN DOCUMENTO d ON its.SAICOD = d.DOCCOD " +
            "    WHERE its.LOJCOD = d.LOJCOD " +
            "      AND d.DOCSTA = 'E' " +
            "      AND CAST(d.ORICOD AS INT) < 100 " +
            "      AND d.DOCDATEMI >= DATEADD(MONTH, -2, DATEADD(DAY, 1-DAY(GETDATE()), GETDATE())) " +
            "      AND d.DOCDATEMI < DATEADD(MONTH, -1, DATEADD(DAY, 1-DAY(GETDATE()), GETDATE())) " +
            "    GROUP BY its.REFPLU " +
            "), " +
            "Vendas30 AS ( " +
            "    SELECT its.REFPLU, SUM(its.ITSQTDTOT) AS Total " +
            "    FROM ITEM_SAIDA its " +
            "    INNER JOIN DOCUMENTO d ON its.SAICOD = d.DOCCOD " +
            "    WHERE its.LOJCOD = d.LOJCOD " +
            "      AND d.DOCSTA = 'E' " +
            "      AND CAST(d.ORICOD AS INT) < 100 " +
            "      AND d.DOCDATEMI >= DATEADD(MONTH, -1, DATEADD(DAY, 1-DAY(GETDATE()), GETDATE())) " +
            "      AND d.DOCDATEMI < DATEADD(DAY, 1-DAY(GETDATE()), GETDATE()) " +
            "    GROUP BY its.REFPLU " +
            "), " +
            "EstoqueTotal AS ( " +
            "    SELECT REFPLU, SUM(ESTTOT) AS Total " +
            "    FROM ESTOQUE " +
            "    WHERE LOCCOD = '001' " +
            "    GROUP BY REFPLU " +
            ") " +
            "SELECT COUNT(*) " +
            "FROM PRODUTO p " +
            "INNER JOIN GRUPO g ON p.GRPCOD = g.GRPCOD AND p.SECCOD = g.SECCOD " +
            "INNER JOIN MARCA m ON p.MARCOD = m.MARCOD " +
            "INNER JOIN REFERENCIA r ON p.PROCOD = r.PROCOD " +
            "LEFT JOIN Vendas90 v90 ON r.REFPLU = v90.REFPLU " +
            "LEFT JOIN Vendas60 v60 ON r.REFPLU = v60.REFPLU " +
            "LEFT JOIN Vendas30 v30 ON r.REFPLU = v30.REFPLU " +
            "LEFT JOIN EstoqueTotal est ON r.REFPLU = est.REFPLU " +
            "WHERE (:refplu IS NULL OR r.REFPLU LIKE :refpluFilter) " +
            "  AND (:grupo IS NULL OR g.GRPDES LIKE :grupoFilter) " +
            "  AND (:marca IS NULL OR m.MARDES LIKE :marcaFilter) " +
            "  AND (:descricao IS NULL OR :descricaoWords IS NULL OR " +
            "       (UPPER(p.PRODES) LIKE '%' + REPLACE(:descricaoWords, '|', '%') + '%' OR " +
            "        UPPER(g.GRPDES) LIKE '%' + REPLACE(:descricaoWords, '|', '%') + '%' OR " +
            "        UPPER(m.MARDES) LIKE '%' + REPLACE(:descricaoWords, '|', '%') + '%')) " +
            "  AND (ISNULL(v90.Total, 0) > 0 OR ISNULL(v60.Total, 0) > 0 OR ISNULL(v30.Total, 0) > 0) " +
            "  AND ISNULL(est.Total, 0) < ((ISNULL(v90.Total, 0) + ISNULL(v60.Total, 0) + ISNULL(v30.Total, 0)) / 3.0)",
           nativeQuery = true)
    Long countCriticalStockWithFilters(
        @Param("refplu") String refplu,
        @Param("refpluFilter") String refpluFilter,
        @Param("descricao") String descricao,
        @Param("descricaoWords") String descricaoWords,
        @Param("grupo") String grupo,
        @Param("grupoFilter") String grupoFilter,
        @Param("marca") String marca,
        @Param("marcaFilter") String marcaFilter
    );
}

