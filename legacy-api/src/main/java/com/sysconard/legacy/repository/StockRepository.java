package com.sysconard.legacy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sysconard.legacy.entity.Stock;
import com.sysconard.legacy.entity.StockId;
import java.util.List;

/**
 * Repository para operações de estoque
 * Contém queries nativas SQL para buscar estoque com PIVOT de lojas
 * 
 * @author Sysconard Legacy API
 * @version 1.0
 */
@Repository
public interface StockRepository extends JpaRepository<Stock, StockId> {
    
    /**
     * Busca estoque com filtros e paginação usando PIVOT para mostrar quantidades por loja
     * 
     * @param refplu Filtro por refplu (opcional)
     * @param marca Filtro por marca (opcional)
     * @param descricao Filtro por descrição (opcional)
     * @param refpluFilter Filtro LIKE para refplu
     * @param marcaFilter Filtro LIKE para marca
     * @param descricaoFilter Filtro LIKE para descrição
     * @param hasStock Filtrar apenas produtos com estoque total > 0 (padrão: true)
     * @param offset Offset para paginação
     * @param size Tamanho da página
     * @return Lista de arrays de objetos com dados do estoque
     */
    @Query(value = "SELECT * FROM (" +
            "SELECT " +
            "r.refplu, " +
            "m.mardes AS marca, " +
            "p.prodes AS descricao, " +
            "MAX(CASE WHEN e.lojcod = 1 THEN e.esttot ELSE 0 END) AS loj1, " +
            "MAX(CASE WHEN e.lojcod = 2 THEN e.esttot ELSE 0 END) AS loj2, " +
            "MAX(CASE WHEN e.lojcod = 3 THEN e.esttot ELSE 0 END) AS loj3, " +
            "MAX(CASE WHEN e.lojcod = 4 THEN e.esttot ELSE 0 END) AS loj4, " +
            "MAX(CASE WHEN e.lojcod = 5 THEN e.esttot ELSE 0 END) AS loj5, " +
            "MAX(CASE WHEN e.lojcod = 6 THEN e.esttot ELSE 0 END) AS loj6, " +
            "MAX(CASE WHEN e.lojcod = 7 THEN e.esttot ELSE 0 END) AS loj7, " +
            "MAX(CASE WHEN e.lojcod = 8 THEN e.esttot ELSE 0 END) AS loj8, " +
            "MAX(CASE WHEN e.lojcod = 9 THEN e.esttot ELSE 0 END) AS loj9, " +
            "MAX(CASE WHEN e.lojcod = 10 THEN e.esttot ELSE 0 END) AS loj10, " +
            "MAX(CASE WHEN e.lojcod = 11 THEN e.esttot ELSE 0 END) AS loj11, " +
            "MAX(CASE WHEN e.lojcod = 12 THEN e.esttot ELSE 0 END) AS loj12, " +
            "MAX(CASE WHEN e.lojcod = 13 THEN e.esttot ELSE 0 END) AS loj13, " +
            "MAX(CASE WHEN e.lojcod = 14 THEN e.esttot ELSE 0 END) AS loj14, " +
            "COALESCE(MAX(CASE WHEN e.lojcod = 1 THEN e.esttot ELSE 0 END), 0) + " +
            "COALESCE(MAX(CASE WHEN e.lojcod = 2 THEN e.esttot ELSE 0 END), 0) + " +
            "COALESCE(MAX(CASE WHEN e.lojcod = 3 THEN e.esttot ELSE 0 END), 0) + " +
            "COALESCE(MAX(CASE WHEN e.lojcod = 4 THEN e.esttot ELSE 0 END), 0) + " +
            "COALESCE(MAX(CASE WHEN e.lojcod = 5 THEN e.esttot ELSE 0 END), 0) + " +
            "COALESCE(MAX(CASE WHEN e.lojcod = 6 THEN e.esttot ELSE 0 END), 0) + " +
            "COALESCE(MAX(CASE WHEN e.lojcod = 7 THEN e.esttot ELSE 0 END), 0) + " +
            "COALESCE(MAX(CASE WHEN e.lojcod = 8 THEN e.esttot ELSE 0 END), 0) + " +
            "COALESCE(MAX(CASE WHEN e.lojcod = 9 THEN e.esttot ELSE 0 END), 0) + " +
            "COALESCE(MAX(CASE WHEN e.lojcod = 10 THEN e.esttot ELSE 0 END), 0) + " +
            "COALESCE(MAX(CASE WHEN e.lojcod = 11 THEN e.esttot ELSE 0 END), 0) + " +
            "COALESCE(MAX(CASE WHEN e.lojcod = 12 THEN e.esttot ELSE 0 END), 0) + " +
            "COALESCE(MAX(CASE WHEN e.lojcod = 13 THEN e.esttot ELSE 0 END), 0) + " +
            "COALESCE(MAX(CASE WHEN e.lojcod = 14 THEN e.esttot ELSE 0 END), 0) AS total, " +
            "ROW_NUMBER() OVER (" +
            "ORDER BY " +
            "CASE WHEN :sortDir = 'asc' THEN " +
            "CASE WHEN :sortBy = 'refplu' THEN r.refplu " +
            "WHEN :sortBy = 'marca' THEN m.mardes " +
            "WHEN :sortBy = 'descricao' THEN p.prodes END END ASC, " +
            "CASE WHEN :sortDir = 'desc' THEN " +
            "CASE WHEN :sortBy = 'refplu' THEN r.refplu " +
            "WHEN :sortBy = 'marca' THEN m.mardes " +
            "WHEN :sortBy = 'descricao' THEN p.prodes END END DESC, " +
            "CASE WHEN :sortDir = 'asc' THEN " +
            "CASE WHEN :sortBy = 'loj1' THEN MAX(CASE WHEN e.lojcod = 1 THEN e.esttot ELSE 0 END) " +
            "WHEN :sortBy = 'loj2' THEN MAX(CASE WHEN e.lojcod = 2 THEN e.esttot ELSE 0 END) " +
            "WHEN :sortBy = 'loj3' THEN MAX(CASE WHEN e.lojcod = 3 THEN e.esttot ELSE 0 END) " +
            "WHEN :sortBy = 'loj4' THEN MAX(CASE WHEN e.lojcod = 4 THEN e.esttot ELSE 0 END) " +
            "WHEN :sortBy = 'loj5' THEN MAX(CASE WHEN e.lojcod = 5 THEN e.esttot ELSE 0 END) " +
            "WHEN :sortBy = 'loj6' THEN MAX(CASE WHEN e.lojcod = 6 THEN e.esttot ELSE 0 END) " +
            "WHEN :sortBy = 'loj7' THEN MAX(CASE WHEN e.lojcod = 7 THEN e.esttot ELSE 0 END) " +
            "WHEN :sortBy = 'loj8' THEN MAX(CASE WHEN e.lojcod = 8 THEN e.esttot ELSE 0 END) " +
            "WHEN :sortBy = 'loj9' THEN MAX(CASE WHEN e.lojcod = 9 THEN e.esttot ELSE 0 END) " +
            "WHEN :sortBy = 'loj10' THEN MAX(CASE WHEN e.lojcod = 10 THEN e.esttot ELSE 0 END) " +
            "WHEN :sortBy = 'loj11' THEN MAX(CASE WHEN e.lojcod = 11 THEN e.esttot ELSE 0 END) " +
            "WHEN :sortBy = 'loj12' THEN MAX(CASE WHEN e.lojcod = 12 THEN e.esttot ELSE 0 END) " +
            "WHEN :sortBy = 'loj13' THEN MAX(CASE WHEN e.lojcod = 13 THEN e.esttot ELSE 0 END) " +
            "WHEN :sortBy = 'loj14' THEN MAX(CASE WHEN e.lojcod = 14 THEN e.esttot ELSE 0 END) " +
            "WHEN :sortBy = 'total' THEN " +
            "COALESCE(MAX(CASE WHEN e.lojcod = 1 THEN e.esttot ELSE 0 END), 0) + " +
            "COALESCE(MAX(CASE WHEN e.lojcod = 2 THEN e.esttot ELSE 0 END), 0) + " +
            "COALESCE(MAX(CASE WHEN e.lojcod = 3 THEN e.esttot ELSE 0 END), 0) + " +
            "COALESCE(MAX(CASE WHEN e.lojcod = 4 THEN e.esttot ELSE 0 END), 0) + " +
            "COALESCE(MAX(CASE WHEN e.lojcod = 5 THEN e.esttot ELSE 0 END), 0) + " +
            "COALESCE(MAX(CASE WHEN e.lojcod = 6 THEN e.esttot ELSE 0 END), 0) + " +
            "COALESCE(MAX(CASE WHEN e.lojcod = 7 THEN e.esttot ELSE 0 END), 0) + " +
            "COALESCE(MAX(CASE WHEN e.lojcod = 8 THEN e.esttot ELSE 0 END), 0) + " +
            "COALESCE(MAX(CASE WHEN e.lojcod = 9 THEN e.esttot ELSE 0 END), 0) + " +
            "COALESCE(MAX(CASE WHEN e.lojcod = 10 THEN e.esttot ELSE 0 END), 0) + " +
            "COALESCE(MAX(CASE WHEN e.lojcod = 11 THEN e.esttot ELSE 0 END), 0) + " +
            "COALESCE(MAX(CASE WHEN e.lojcod = 12 THEN e.esttot ELSE 0 END), 0) + " +
            "COALESCE(MAX(CASE WHEN e.lojcod = 13 THEN e.esttot ELSE 0 END), 0) + " +
            "COALESCE(MAX(CASE WHEN e.lojcod = 14 THEN e.esttot ELSE 0 END), 0) END END ASC, " +
            "CASE WHEN :sortDir = 'desc' THEN " +
            "CASE WHEN :sortBy = 'loj1' THEN MAX(CASE WHEN e.lojcod = 1 THEN e.esttot ELSE 0 END) " +
            "WHEN :sortBy = 'loj2' THEN MAX(CASE WHEN e.lojcod = 2 THEN e.esttot ELSE 0 END) " +
            "WHEN :sortBy = 'loj3' THEN MAX(CASE WHEN e.lojcod = 3 THEN e.esttot ELSE 0 END) " +
            "WHEN :sortBy = 'loj4' THEN MAX(CASE WHEN e.lojcod = 4 THEN e.esttot ELSE 0 END) " +
            "WHEN :sortBy = 'loj5' THEN MAX(CASE WHEN e.lojcod = 5 THEN e.esttot ELSE 0 END) " +
            "WHEN :sortBy = 'loj6' THEN MAX(CASE WHEN e.lojcod = 6 THEN e.esttot ELSE 0 END) " +
            "WHEN :sortBy = 'loj7' THEN MAX(CASE WHEN e.lojcod = 7 THEN e.esttot ELSE 0 END) " +
            "WHEN :sortBy = 'loj8' THEN MAX(CASE WHEN e.lojcod = 8 THEN e.esttot ELSE 0 END) " +
            "WHEN :sortBy = 'loj9' THEN MAX(CASE WHEN e.lojcod = 9 THEN e.esttot ELSE 0 END) " +
            "WHEN :sortBy = 'loj10' THEN MAX(CASE WHEN e.lojcod = 10 THEN e.esttot ELSE 0 END) " +
            "WHEN :sortBy = 'loj11' THEN MAX(CASE WHEN e.lojcod = 11 THEN e.esttot ELSE 0 END) " +
            "WHEN :sortBy = 'loj12' THEN MAX(CASE WHEN e.lojcod = 12 THEN e.esttot ELSE 0 END) " +
            "WHEN :sortBy = 'loj13' THEN MAX(CASE WHEN e.lojcod = 13 THEN e.esttot ELSE 0 END) " +
            "WHEN :sortBy = 'loj14' THEN MAX(CASE WHEN e.lojcod = 14 THEN e.esttot ELSE 0 END) " +
            "WHEN :sortBy = 'total' THEN " +
            "COALESCE(MAX(CASE WHEN e.lojcod = 1 THEN e.esttot ELSE 0 END), 0) + " +
            "COALESCE(MAX(CASE WHEN e.lojcod = 2 THEN e.esttot ELSE 0 END), 0) + " +
            "COALESCE(MAX(CASE WHEN e.lojcod = 3 THEN e.esttot ELSE 0 END), 0) + " +
            "COALESCE(MAX(CASE WHEN e.lojcod = 4 THEN e.esttot ELSE 0 END), 0) + " +
            "COALESCE(MAX(CASE WHEN e.lojcod = 5 THEN e.esttot ELSE 0 END), 0) + " +
            "COALESCE(MAX(CASE WHEN e.lojcod = 6 THEN e.esttot ELSE 0 END), 0) + " +
            "COALESCE(MAX(CASE WHEN e.lojcod = 7 THEN e.esttot ELSE 0 END), 0) + " +
            "COALESCE(MAX(CASE WHEN e.lojcod = 8 THEN e.esttot ELSE 0 END), 0) + " +
            "COALESCE(MAX(CASE WHEN e.lojcod = 9 THEN e.esttot ELSE 0 END), 0) + " +
            "COALESCE(MAX(CASE WHEN e.lojcod = 10 THEN e.esttot ELSE 0 END), 0) + " +
            "COALESCE(MAX(CASE WHEN e.lojcod = 11 THEN e.esttot ELSE 0 END), 0) + " +
            "COALESCE(MAX(CASE WHEN e.lojcod = 12 THEN e.esttot ELSE 0 END), 0) + " +
            "COALESCE(MAX(CASE WHEN e.lojcod = 13 THEN e.esttot ELSE 0 END), 0) + " +
            "COALESCE(MAX(CASE WHEN e.lojcod = 14 THEN e.esttot ELSE 0 END), 0) END END DESC" +
            ") as rn " +
            "FROM estoque e " +
            "INNER JOIN referencia r ON e.refplu = r.refplu " +
            "INNER JOIN produto p ON r.procod = p.procod " +
            "INNER JOIN marca m ON p.marcod = m.marcod " +
            "WHERE (:refplu IS NULL OR r.refplu LIKE :refpluFilter) " +
            "AND (:marca IS NULL OR m.mardes LIKE :marcaFilter) " +
            "AND (:descricao IS NULL OR p.prodes LIKE :descricaoFilter) " +
            "GROUP BY r.refplu, m.mardes, p.prodes " +
            "HAVING (:hasStock = 0 OR " +
            "COALESCE(MAX(CASE WHEN e.lojcod = 1 THEN e.esttot ELSE 0 END), 0) + " +
            "COALESCE(MAX(CASE WHEN e.lojcod = 2 THEN e.esttot ELSE 0 END), 0) + " +
            "COALESCE(MAX(CASE WHEN e.lojcod = 3 THEN e.esttot ELSE 0 END), 0) + " +
            "COALESCE(MAX(CASE WHEN e.lojcod = 4 THEN e.esttot ELSE 0 END), 0) + " +
            "COALESCE(MAX(CASE WHEN e.lojcod = 5 THEN e.esttot ELSE 0 END), 0) + " +
            "COALESCE(MAX(CASE WHEN e.lojcod = 6 THEN e.esttot ELSE 0 END), 0) + " +
            "COALESCE(MAX(CASE WHEN e.lojcod = 7 THEN e.esttot ELSE 0 END), 0) + " +
            "COALESCE(MAX(CASE WHEN e.lojcod = 8 THEN e.esttot ELSE 0 END), 0) + " +
            "COALESCE(MAX(CASE WHEN e.lojcod = 9 THEN e.esttot ELSE 0 END), 0) + " +
            "COALESCE(MAX(CASE WHEN e.lojcod = 10 THEN e.esttot ELSE 0 END), 0) + " +
            "COALESCE(MAX(CASE WHEN e.lojcod = 11 THEN e.esttot ELSE 0 END), 0) + " +
            "COALESCE(MAX(CASE WHEN e.lojcod = 12 THEN e.esttot ELSE 0 END), 0) + " +
            "COALESCE(MAX(CASE WHEN e.lojcod = 13 THEN e.esttot ELSE 0 END), 0) + " +
            "COALESCE(MAX(CASE WHEN e.lojcod = 14 THEN e.esttot ELSE 0 END), 0) > 0) " +
            ") AS paginated " +
            "WHERE rn > :offset AND rn <= :offset + :size",
            nativeQuery = true)
    List<Object[]> findStocksWithFilters(
            @Param("refplu") String refplu,
            @Param("marca") String marca,
            @Param("descricao") String descricao,
            @Param("refpluFilter") String refpluFilter,
            @Param("marcaFilter") String marcaFilter,
            @Param("descricaoFilter") String descricaoFilter,
            @Param("hasStock") Boolean hasStock,
            @Param("sortBy") String sortBy,
            @Param("sortDir") String sortDir,
            @Param("offset") int offset,
            @Param("size") int size
    );
    
    /**
     * Conta total de registros de estoque com filtros aplicados
     * 
     * @param refplu Filtro por refplu (opcional)
     * @param marca Filtro por marca (opcional)
     * @param descricao Filtro por descrição (opcional)
     * @param refpluFilter Filtro LIKE para refplu
     * @param marcaFilter Filtro LIKE para marca
     * @param descricaoFilter Filtro LIKE para descrição
     * @param hasStock Filtrar apenas produtos com estoque total > 0 (padrão: true)
     * @return Total de registros
     */
    @Query(value = "SELECT COUNT(*) FROM (" +
            "SELECT r.refplu " +
            "FROM estoque e " +
            "INNER JOIN referencia r ON e.refplu = r.refplu " +
            "INNER JOIN produto p ON r.procod = p.procod " +
            "INNER JOIN marca m ON p.marcod = m.marcod " +
            "WHERE (:refplu IS NULL OR r.refplu LIKE :refpluFilter) " +
            "AND (:marca IS NULL OR m.mardes LIKE :marcaFilter) " +
            "AND (:descricao IS NULL OR p.prodes LIKE :descricaoFilter) " +
            "GROUP BY r.refplu, m.mardes, p.prodes " +
            "HAVING (:hasStock = 0 OR " +
            "COALESCE(MAX(CASE WHEN e.lojcod = 1 THEN e.esttot ELSE 0 END), 0) + " +
            "COALESCE(MAX(CASE WHEN e.lojcod = 2 THEN e.esttot ELSE 0 END), 0) + " +
            "COALESCE(MAX(CASE WHEN e.lojcod = 3 THEN e.esttot ELSE 0 END), 0) + " +
            "COALESCE(MAX(CASE WHEN e.lojcod = 4 THEN e.esttot ELSE 0 END), 0) + " +
            "COALESCE(MAX(CASE WHEN e.lojcod = 5 THEN e.esttot ELSE 0 END), 0) + " +
            "COALESCE(MAX(CASE WHEN e.lojcod = 6 THEN e.esttot ELSE 0 END), 0) + " +
            "COALESCE(MAX(CASE WHEN e.lojcod = 7 THEN e.esttot ELSE 0 END), 0) + " +
            "COALESCE(MAX(CASE WHEN e.lojcod = 8 THEN e.esttot ELSE 0 END), 0) + " +
            "COALESCE(MAX(CASE WHEN e.lojcod = 9 THEN e.esttot ELSE 0 END), 0) + " +
            "COALESCE(MAX(CASE WHEN e.lojcod = 10 THEN e.esttot ELSE 0 END), 0) + " +
            "COALESCE(MAX(CASE WHEN e.lojcod = 11 THEN e.esttot ELSE 0 END), 0) + " +
            "COALESCE(MAX(CASE WHEN e.lojcod = 12 THEN e.esttot ELSE 0 END), 0) + " +
            "COALESCE(MAX(CASE WHEN e.lojcod = 13 THEN e.esttot ELSE 0 END), 0) + " +
            "COALESCE(MAX(CASE WHEN e.lojcod = 14 THEN e.esttot ELSE 0 END), 0) > 0) " +
            ") AS filtered",
            nativeQuery = true)
    Long countStocksWithFilters(
            @Param("refplu") String refplu,
            @Param("marca") String marca,
            @Param("descricao") String descricao,
            @Param("refpluFilter") String refpluFilter,
            @Param("marcaFilter") String marcaFilter,
            @Param("descricaoFilter") String descricaoFilter,
            @Param("hasStock") Boolean hasStock
    );
    
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
            "MAX(CASE WHEN e.lojcod = 1 THEN e.esttot ELSE 0 END) AS loj1, " +
            "MAX(CASE WHEN e.lojcod = 2 THEN e.esttot ELSE 0 END) AS loj2 " +
            "FROM estoque e " +
            "INNER JOIN referencia r ON e.refplu = r.refplu " +
            "INNER JOIN produto p ON r.procod = p.procod " +
            "INNER JOIN marca m ON p.marcod = m.marcod " +
            "GROUP BY r.refplu, m.mardes, p.prodes " +
            "ORDER BY r.refplu",
            nativeQuery = true)
    List<Object[]> findTestStocksWithPivot();
}
