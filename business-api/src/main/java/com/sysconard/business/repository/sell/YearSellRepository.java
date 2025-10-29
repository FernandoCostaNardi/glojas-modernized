package com.sysconard.business.repository.sell;

import com.sysconard.business.entity.sell.YearSell;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository para operações de acesso a dados da entidade YearSell.
 * Fornece métodos para busca de vendas anuais por diferentes critérios.
 * Seguindo padrões de Clean Code com responsabilidade única.
 */
@Repository
public interface YearSellRepository extends JpaRepository<YearSell, UUID> {
    
    /**
     * Busca venda anual por ID da loja e ano.
     * 
     * @param storeId ID da loja
     * @param year Ano das vendas
     * @return Optional contendo a venda anual se encontrada
     */
    Optional<YearSell> findByStoreIdAndYear(UUID storeId, Integer year);
    
    /**
     * Busca todas as vendas anuais de um ano específico.
     * 
     * @param year Ano das vendas
     * @return Lista de vendas anuais do ano especificado
     */
    List<YearSell> findByYear(Integer year);
    
    /**
     * Busca vendas anuais de uma loja específica em um range de anos.
     * 
     * @param storeCode Código da loja
     * @param startYear Ano de início
     * @param endYear Ano de fim
     * @return Lista de vendas anuais da loja no período especificado
     */
    List<YearSell> findByStoreCodeAndYearBetween(String storeCode, Integer startYear, Integer endYear);
    
    /**
     * Busca vendas anuais por ID da loja em um range de anos.
     * 
     * @param storeId ID da loja
     * @param startYear Ano de início
     * @param endYear Ano de fim
     * @return Lista de vendas anuais da loja no período especificado
     */
    List<YearSell> findByStoreIdAndYearBetween(UUID storeId, Integer startYear, Integer endYear);
    
    /**
     * Busca vendas anuais em um range de anos.
     * 
     * @param startYear Ano de início
     * @param endYear Ano de fim
     * @return Lista de vendas anuais no período especificado
     */
    List<YearSell> findByYearBetween(Integer startYear, Integer endYear);
    
    /**
     * Verifica se existe venda anual para uma loja e ano específicos.
     * 
     * @param storeId ID da loja
     * @param year Ano das vendas
     * @return true se existe venda anual para a loja e ano
     */
    boolean existsByStoreIdAndYear(UUID storeId, Integer year);
    
    /**
     * Conta o número de vendas anuais de um ano específico.
     * 
     * @param year Ano das vendas
     * @return Número de vendas anuais do ano
     */
    long countByYear(Integer year);
    
    /**
     * Busca vendas anuais por lista de IDs de loja e lista de anos.
     * Utilizado para identificar registros existentes durante sincronização.
     * 
     * @param storeIds Lista de IDs das lojas
     * @param years Lista de anos
     * @return Lista de vendas anuais encontradas
     */
    @Query("SELECT y FROM YearSell y WHERE y.storeId IN :storeIds AND y.year IN :years")
    List<YearSell> findByStoreIdInAndYearIn(
            @Param("storeIds") List<UUID> storeIds, 
            @Param("years") List<Integer> years
    );
    
    /**
     * Busca vendas agregadas por loja em um período de anos.
     * Utilizado para relatórios de vendas anuais.
     * 
     * @param startYear Ano de início
     * @param endYear Ano de fim
     * @return Lista de vendas agregadas por loja
     */
    @Query("SELECT y.storeName, SUM(y.total) FROM YearSell y WHERE y.year BETWEEN :startYear AND :endYear GROUP BY y.storeName ORDER BY SUM(y.total) DESC")
    List<Object[]> findAggregatedByStoreAndPeriod(
        @Param("startYear") Integer startYear,
        @Param("endYear") Integer endYear
    );
    
    /**
     * Busca dados para gráfico agregados por ano.
     * Utilizado para alimentar gráficos de vendas anuais.
     * 
     * @param startYear Ano de início
     * @param endYear Ano de fim
     * @return Lista de vendas agregadas por ano
     */
    @Query("SELECT y.year, SUM(y.total) FROM YearSell y WHERE y.year BETWEEN :startYear AND :endYear GROUP BY y.year ORDER BY y.year")
    List<Object[]> findChartData(
        @Param("startYear") Integer startYear,
        @Param("endYear") Integer endYear
    );
    
    /**
     * Busca dados para gráfico filtrado por loja específica.
     * Utilizado para gráficos de vendas de uma loja específica.
     * 
     * @param storeCode Código da loja
     * @param startYear Ano de início
     * @param endYear Ano de fim
     * @return Lista de vendas da loja por ano
     */
    @Query("SELECT y.year, y.total FROM YearSell y WHERE y.storeCode = :storeCode AND y.year BETWEEN :startYear AND :endYear ORDER BY y.year")
    List<Object[]> findChartDataByStore(
        @Param("storeCode") String storeCode,
        @Param("startYear") Integer startYear,
        @Param("endYear") Integer endYear
    );
}
