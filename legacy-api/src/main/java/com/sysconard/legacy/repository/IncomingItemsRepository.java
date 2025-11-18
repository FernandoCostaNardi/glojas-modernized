package com.sysconard.legacy.repository;

import com.sysconard.legacy.entity.IncomingItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository para operações com a entidade IncomingItems.
 * Contém queries nativas para buscar produtos de trocas.
 * 
 * @author Sysconard Legacy API
 * @version 1.0
 */
@Repository
public interface IncomingItemsRepository extends JpaRepository<IncomingItems, Long> {
    
    /**
     * Busca produtos de trocas baseado em números de nota e chaves NFE.
     * Retorna dados detalhados de cada produto de troca com informações do item de entrada.
     *
     * @param documentNumbers Lista de números de nota (DOCNUMDOC) convertidos para INT
     * @param nfeKeys Lista de chaves NFE (DOCCHVNFE) para filtrar
     * @return Lista de arrays Object[] com dados dos produtos: [LOJCOD, ENTCOD, REFPLU, ITEDATMOV, QUANTIDADE, ITEVLREMBAS, DOCNUMDOC, DOCCHVNFE]
     */
    @Query(value = "SELECT C.LOJCOD, C.ENTCOD, C.REFPLU, C.ITEDATMOV, " +
                   "CONVERT(INT, C.ITEQTDEMB) AS QUANTIDADE, " +
                   "CAST(C.ITEVLREMB AS DECIMAL(10,2)) AS ITEVLREMBAS, " +
                   "D.DOCNUMDOC, " +
                   "D.DOCCHVNFE " +
                   "FROM ITEM_ENTRADA C " +
                   "INNER JOIN DOCUMENTO D ON C.ENTCOD = D.DOCCOD " +
                   "WHERE (" +
                   "    (:documentNumbers IS NULL OR CAST(D.DOCNUMDOC AS INT) IN (:documentNumbers)) " +
                   "    OR " +
                   "    (:nfeKeys IS NULL OR D.DOCCHVNFE IN (:nfeKeys))" +
                   ")",
           nativeQuery = true)
    List<Object[]> findExchangeProducts(@Param("documentNumbers") List<Integer> documentNumbers,
                                        @Param("nfeKeys") List<String> nfeKeys);
}

