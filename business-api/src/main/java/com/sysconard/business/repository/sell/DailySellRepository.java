package com.sysconard.business.repository.sell;

import com.sysconard.business.dto.sell.DailySalesReportResponse;
import com.sysconard.business.dto.sell.StoreReportResponse;
import com.sysconard.business.entity.sell.DailySell;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Repository para operações de acesso a dados da entidade DailySell.
 * Estende JpaRepository para operações CRUD básicas e define métodos customizados
 * para otimização de consultas em lote durante a sincronização.
 * 
 * @author Business API
 * @version 1.0
 */
@Repository
public interface DailySellRepository extends JpaRepository<DailySell, UUID> {
    
    /**
     * Busca registros de vendas diárias existentes para um conjunto de lojas
     * em um período específico. Utilizado para identificar registros que 
     * precisam ser atualizados durante a sincronização.
     * 
     * @param storeIds Lista de IDs das lojas
     * @param startDate Data de início do período
     * @param endDate Data de fim do período
     * @return Lista de registros existentes no período
     */
    @Query("SELECT d FROM DailySell d WHERE d.storeId IN :storeIds AND d.date BETWEEN :startDate AND :endDate")
    List<DailySell> findExistingRecords(
        @Param("storeIds") List<UUID> storeIds,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
    
    /**
     * Busca registros de vendas diárias por código da loja e período.
     * Útil para consultas específicas por loja.
     * 
     * @param storeCode Código da loja
     * @param startDate Data de início do período
     * @param endDate Data de fim do período
     * @return Lista de registros da loja no período
     */
    @Query("SELECT d FROM DailySell d WHERE d.storeCode = :storeCode AND d.date BETWEEN :startDate AND :endDate ORDER BY d.date")
    List<DailySell> findByStoreCodeAndDateBetween(
        @Param("storeCode") String storeCode,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
    
    /**
     * Busca um registro específico por loja e data.
     * Utilizado para verificar existência de registro específico.
     * 
     * @param storeId ID da loja
     * @param date Data específica
     * @return Lista com o registro se existir (máximo 1 elemento)
     */
    @Query("SELECT d FROM DailySell d WHERE d.storeId = :storeId AND d.date = :date")
    List<DailySell> findByStoreIdAndDate(
        @Param("storeId") UUID storeId,
        @Param("date") LocalDate date
    );
    
    /**
     * Conta o total de registros por período.
     * Útil para estatísticas e monitoramento.
     * 
     * @param startDate Data de início do período
     * @param endDate Data de fim do período
     * @return Número total de registros no período
     */
    @Query("SELECT COUNT(d) FROM DailySell d WHERE d.date BETWEEN :startDate AND :endDate")
    long countByDateBetween(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
    
    /**
     * Busca registros agrupados por loja em um período.
     * Útil para relatórios consolidados.
     * 
     * @param startDate Data de início do período
     * @param endDate Data de fim do período
     * @return Lista de registros ordenados por loja e data
     */
    @Query("SELECT d FROM DailySell d WHERE d.date BETWEEN :startDate AND :endDate ORDER BY d.storeCode, d.date")
    List<DailySell> findByDateBetweenOrderByStoreCodeAndDate(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
    
    /**
     * Remove registros antigos baseados na data.
     * Útil para limpeza de dados históricos.
     * 
     * @param cutoffDate Data limite (registros anteriores serão removidos)
     * @return Número de registros removidos
     */
    @Query("DELETE FROM DailySell d WHERE d.date < :cutoffDate")
    int deleteByDateBefore(@Param("cutoffDate") LocalDate cutoffDate);
    
    /**
     * Busca vendas agregadas por loja em um período específico.
     * Utiliza agregação SQL para otimizar performance e retorna dados consolidados.
     * 
     * @param startDate Data de início do período
     * @param endDate Data de fim do período
     * @return Lista de vendas agregadas por loja
     */
    @Query("""
        SELECT new com.sysconard.business.dto.sell.DailySalesReportResponse(
            d.storeName,
            SUM(d.pdv),
            SUM(d.danfe),
            SUM(d.exchange),
            SUM(d.total)
        )
        FROM DailySell d 
        WHERE d.date BETWEEN :startDate AND :endDate
        GROUP BY d.storeName
        ORDER BY d.storeName
        """)
    List<DailySalesReportResponse> findAggregatedSalesByDateRange(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
    
    /**
     * Busca vendas agregadas por loja em um período específico para relatório de lojas.
     * Utiliza agregação SQL otimizada com filtro por códigos de loja.
     * Retorna dados no formato StoreReportResponse (com danfe, pdv e troca/exchange).
     * 
     * @param startDate Data de início do período
     * @param endDate Data de fim do período
     * @param storeCodes Lista de códigos das lojas para filtrar
     * @return Lista de vendas agregadas por loja no formato StoreReportResponse
     */
    @Query("""
        SELECT new com.sysconard.business.dto.sell.StoreReportResponse(
            d.storeName,
            d.storeCode,
            SUM(d.danfe),
            SUM(d.pdv),
            SUM(d.exchange)
        )
        FROM DailySell d 
        WHERE d.date BETWEEN :startDate AND :endDate
            AND d.storeCode IN :storeCodes
        GROUP BY d.storeName, d.storeCode
        ORDER BY d.storeName
        """)
    List<StoreReportResponse> findStoreReportByDateRange(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        @Param("storeCodes") List<String> storeCodes
    );
    
    /**
     * Busca vendas agregadas por loja e mês em um período específico.
     * Utiliza agregação SQL para otimizar performance e retorna dados consolidados por mês.
     * Utilizado para sincronização de vendas mensais.
     * 
     * @param startDate Data de início do período
     * @param endDate Data de fim do período
     * @return Lista de vendas agregadas por loja e mês
     */
    @Query(value = """
        SELECT 
            d.store_id,
            d.store_code,
            d.store_name,
            TO_CHAR(d.date, 'YYYY-MM') as yearMonth,
            SUM(d.total) as totalSum
        FROM daily_sells d 
        WHERE d.date BETWEEN :startDate AND :endDate
        GROUP BY d.store_id, d.store_code, d.store_name, TO_CHAR(d.date, 'YYYY-MM')
        ORDER BY d.store_code, TO_CHAR(d.date, 'YYYY-MM')
        """, nativeQuery = true)
    List<Object[]> findMonthlyAggregatedSalesByDateRange(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
}
