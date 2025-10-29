package com.sysconard.business.repository.sell;

import com.sysconard.business.entity.sell.MonthlySell;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository para operações de acesso a dados da entidade MonthlySell.
 * Estende JpaRepository para operações CRUD básicas e define métodos customizados
 * para otimização de consultas durante a sincronização de vendas mensais.
 * 
 * @author Business API
 * @version 1.0
 */
@Repository
public interface MonthlySellRepository extends JpaRepository<MonthlySell, UUID> {
    
    /**
     * Busca um registro de venda mensal por loja e ano/mês.
     * Utilizado para verificar se já existe registro para uma loja em um mês específico.
     * 
     * @param storeId ID da loja
     * @param yearMonth Ano e mês no formato YYYY-MM
     * @return Optional com o registro se existir
     */
    @Query("SELECT m FROM MonthlySell m WHERE m.storeId = :storeId AND m.yearMonth = :yearMonth")
    Optional<MonthlySell> findByStoreIdAndYearMonth(
        @Param("storeId") UUID storeId,
        @Param("yearMonth") String yearMonth
    );
    
    /**
     * Busca registros existentes para um conjunto de lojas e meses.
     * Utilizado para identificar registros que precisam ser atualizados
     * durante a sincronização em lote.
     * 
     * @param storeIds Lista de IDs das lojas
     * @param yearMonths Lista de anos/meses no formato YYYY-MM
     * @return Lista de registros existentes
     */
    @Query("SELECT m FROM MonthlySell m WHERE m.storeId IN :storeIds AND m.yearMonth IN :yearMonths")
    List<MonthlySell> findExistingRecords(
        @Param("storeIds") List<UUID> storeIds,
        @Param("yearMonths") List<String> yearMonths
    );
    
    /**
     * Busca registros de vendas mensais por código da loja e período.
     * Útil para consultas específicas por loja.
     * 
     * @param storeCode Código da loja
     * @param startYearMonth Ano/mês de início (formato YYYY-MM)
     * @param endYearMonth Ano/mês de fim (formato YYYY-MM)
     * @return Lista de registros da loja no período
     */
    @Query("SELECT m FROM MonthlySell m WHERE m.storeCode = :storeCode AND m.yearMonth BETWEEN :startYearMonth AND :endYearMonth ORDER BY m.yearMonth")
    List<MonthlySell> findByStoreCodeAndYearMonthBetween(
        @Param("storeCode") String storeCode,
        @Param("startYearMonth") String startYearMonth,
        @Param("endYearMonth") String endYearMonth
    );
    
    /**
     * Conta o total de registros por período de ano/mês.
     * Útil para estatísticas e monitoramento.
     * 
     * @param startYearMonth Ano/mês de início (formato YYYY-MM)
     * @param endYearMonth Ano/mês de fim (formato YYYY-MM)
     * @return Número total de registros no período
     */
    @Query("SELECT COUNT(m) FROM MonthlySell m WHERE m.yearMonth BETWEEN :startYearMonth AND :endYearMonth")
    long countByYearMonthBetween(
        @Param("startYearMonth") String startYearMonth,
        @Param("endYearMonth") String endYearMonth
    );
    
    /**
     * Busca registros agrupados por loja em um período de ano/mês.
     * Útil para relatórios consolidados.
     * 
     * @param startYearMonth Ano/mês de início (formato YYYY-MM)
     * @param endYearMonth Ano/mês de fim (formato YYYY-MM)
     * @return Lista de registros ordenados por loja e ano/mês
     */
    @Query("SELECT m FROM MonthlySell m WHERE m.yearMonth BETWEEN :startYearMonth AND :endYearMonth ORDER BY m.storeCode, m.yearMonth")
    List<MonthlySell> findByYearMonthBetweenOrderByStoreCodeAndYearMonth(
        @Param("startYearMonth") String startYearMonth,
        @Param("endYearMonth") String endYearMonth
    );
    
    /**
     * Remove registros antigos baseados no ano/mês.
     * Útil para limpeza de dados históricos.
     * 
     * @param cutoffYearMonth Ano/mês limite (registros anteriores serão removidos)
     * @return Número de registros removidos
     */
    @Query("DELETE FROM MonthlySell m WHERE m.yearMonth < :cutoffYearMonth")
    int deleteByYearMonthBefore(@Param("cutoffYearMonth") String cutoffYearMonth);
    
    /**
     * Busca vendas agregadas por loja em um período de ano/mês.
     * Utilizado para relatórios de vendas mensais.
     * 
     * @param startYearMonth Ano/mês de início (formato YYYY-MM)
     * @param endYearMonth Ano/mês de fim (formato YYYY-MM)
     * @return Lista de vendas agregadas por loja
     */
    @Query("SELECT m.storeName, SUM(m.total) FROM MonthlySell m WHERE m.yearMonth BETWEEN :startYearMonth AND :endYearMonth GROUP BY m.storeName ORDER BY SUM(m.total) DESC")
    List<Object[]> findAggregatedByStoreAndPeriod(
        @Param("startYearMonth") String startYearMonth,
        @Param("endYearMonth") String endYearMonth
    );
    
    /**
     * Busca dados para gráfico agregados por mês.
     * Utilizado para alimentar gráficos de vendas mensais.
     * 
     * @param startYearMonth Ano/mês de início (formato YYYY-MM)
     * @param endYearMonth Ano/mês de fim (formato YYYY-MM)
     * @return Lista de vendas agregadas por mês
     */
    @Query("SELECT m.yearMonth, SUM(m.total) FROM MonthlySell m WHERE m.yearMonth BETWEEN :startYearMonth AND :endYearMonth GROUP BY m.yearMonth ORDER BY m.yearMonth")
    List<Object[]> findChartData(
        @Param("startYearMonth") String startYearMonth,
        @Param("endYearMonth") String endYearMonth
    );
    
    /**
     * Busca dados para gráfico filtrado por loja específica.
     * Utilizado para gráficos de vendas de uma loja específica.
     * 
     * @param storeCode Código da loja
     * @param startYearMonth Ano/mês de início (formato YYYY-MM)
     * @param endYearMonth Ano/mês de fim (formato YYYY-MM)
     * @return Lista de vendas da loja por mês
     */
    @Query("SELECT m.yearMonth, m.total FROM MonthlySell m WHERE m.storeCode = :storeCode AND m.yearMonth BETWEEN :startYearMonth AND :endYearMonth ORDER BY m.yearMonth")
    List<Object[]> findChartDataByStore(
        @Param("storeCode") String storeCode,
        @Param("startYearMonth") String startYearMonth,
        @Param("endYearMonth") String endYearMonth
    );
    
    /**
     * Busca vendas agregadas por ano para sincronização anual.
     * Agrega dados de monthly_sells por loja e ano para popular year_sells.
     * 
     * @param year Ano para agregação
     * @return Lista de vendas agregadas por loja e ano
     */
    @Query(value = "SELECT " +
           "m.store_id, " +
           "m.store_code, " +
           "m.store_name, " +
           "CAST(SUBSTRING(m.year_month, 1, 4) AS INTEGER) as year, " +
           "SUM(m.total) as total " +
           "FROM monthly_sells m " +
           "WHERE CAST(SUBSTRING(m.year_month, 1, 4) AS INTEGER) = :year " +
           "GROUP BY m.store_id, m.store_code, m.store_name, CAST(SUBSTRING(m.year_month, 1, 4) AS INTEGER) " +
           "ORDER BY m.store_code",
           nativeQuery = true)
    List<Object[]> findYearlyAggregatedSalesByYear(@Param("year") Integer year);
}
