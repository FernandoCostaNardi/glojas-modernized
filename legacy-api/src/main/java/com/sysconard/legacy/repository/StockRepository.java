package com.sysconard.legacy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.sysconard.legacy.entity.Stock;
import com.sysconard.legacy.entity.StockId;
import java.util.List;

/**
 * Repository para operações de estoque
 * 
 * Utiliza StockRepositoryCustom para queries dinâmicas baseadas nas lojas cadastradas.
 * As queries principais foram movidas para StockRepositoryImpl para suportar lojas dinâmicas.
 * 
 * @author Sysconard Legacy API
 * @version 2.0
 */
@Repository
public interface StockRepository extends JpaRepository<Stock, StockId>, StockRepositoryCustom {
    
    /**
     * Query de teste para verificar se há dados sem filtro de loccod
     * 
     * @return Lista de arrays de objetos com dados do estoque
     */
    @Query(value = "SELECT TOP 5 " +
            "r.refplu, " +
            "m.mardes AS marca, " +
            "p.prodes AS descricao, " +
            "e.loccod, " +
            "e.lojcod, " +
            "e.esttot " +
            "FROM estoque e " +
            "INNER JOIN referencia r ON e.refplu = r.refplu " +
            "INNER JOIN produto p ON r.procod = p.procod " +
            "INNER JOIN marca m ON p.marcod = m.marcod " +
            "ORDER BY r.refplu",
            nativeQuery = true)
    List<Object[]> findTestStocks();
    
    /**
     * Query de teste para verificar se há dados com loccod = 1
     * 
     * @return Lista de arrays de objetos com dados do estoque
     */
    @Query(value = "SELECT TOP 5 " +
            "r.refplu, " +
            "m.mardes AS marca, " +
            "p.prodes AS descricao, " +
            "e.loccod, " +
            "e.lojcod, " +
            "e.esttot " +
            "FROM estoque e " +
            "INNER JOIN referencia r ON e.refplu = r.refplu " +
            "INNER JOIN produto p ON r.procod = p.procod " +
            "INNER JOIN marca m ON p.marcod = m.marcod " +
            "WHERE e.loccod = 1 " +
            "ORDER BY r.refplu",
            nativeQuery = true)
    List<Object[]> findTestStocksWithLoccod1();
    
    /**
     * Query de teste simplificada para verificar se há dados com PIVOT
     * 
     * @return Lista de arrays de objetos com dados do estoque
     */
    @Query(value = "SELECT TOP 3 " +
            "r.refplu, " +
            "m.mardes AS marca, " +
            "p.prodes AS descricao, " +
            "SUM(CASE WHEN e.lojcod = 1 THEN e.esttot ELSE 0 END) AS loj1, " +
            "SUM(CASE WHEN e.lojcod = 2 THEN e.esttot ELSE 0 END) AS loj2 " +
            "FROM estoque e " +
            "INNER JOIN referencia r ON e.refplu = r.refplu " +
            "INNER JOIN produto p ON r.procod = p.procod " +
            "INNER JOIN marca m ON p.marcod = m.marcod " +
            "INNER JOIN grupo g ON p.grpcod = g.grpcod " +
            "GROUP BY r.refplu, m.mardes, p.prodes, g.grpdes " +
            "ORDER BY r.refplu",
            nativeQuery = true)
    List<Object[]> findTestStocksWithPivot();
}
