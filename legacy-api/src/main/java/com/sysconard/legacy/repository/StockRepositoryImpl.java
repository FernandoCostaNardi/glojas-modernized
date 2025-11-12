package com.sysconard.legacy.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * Implementação customizada do StockRepository para queries dinâmicas
 * 
 * Baseado na query original performática, adaptada para buscar lojas dinamicamente
 * da tabela LOJA ao invés de ter lojas hardcoded.
 * 
 * @author Sysconard Legacy API
 * @version 2.0
 */
@Slf4j
@Repository
public class StockRepositoryImpl implements StockRepositoryCustom {
    
    @PersistenceContext
    private EntityManager entityManager;
    
    @Override
    public List<Object[]> findStocksWithFiltersDynamic(
            String refplu, String marca, String descricao,
            String refpluFilter, String marcaFilter,
            String descricaoWords, String grupoWords,
            List<Long> storeIds,
            Boolean hasStock, String sortBy, String sortDir,
            int offset, int size) {
        
        String query = buildDynamicStockQuery(storeIds, false);
        Query nativeQuery = entityManager.createNativeQuery(query);
        
        // Set parameters
        setQueryParameters(nativeQuery, refplu, marca, descricao, refpluFilter, marcaFilter,
                descricaoWords, grupoWords, storeIds, hasStock, sortBy, sortDir, offset, size);
        
        @SuppressWarnings("unchecked")
        List<Object[]> results = nativeQuery.getResultList();
        log.debug("Resultados retornados da query: {} (offset: {}, size: {})", results.size(), offset, size);
        
        return results;
    }
    
    @Override
    public Long countStocksWithFiltersDynamic(
            String refplu, String marca, String descricao,
            String refpluFilter, String marcaFilter,
            String descricaoWords, String grupoWords,
            List<Long> storeIds,
            Boolean hasStock) {
        
        String query = buildDynamicStockQuery(storeIds, true);
        Query nativeQuery = entityManager.createNativeQuery(query);
        
        // Set parameters
        setCountQueryParameters(nativeQuery, refplu, marca, descricao, refpluFilter, marcaFilter,
                descricaoWords, grupoWords, storeIds, hasStock);
        
        Object result = nativeQuery.getSingleResult();
        if (result instanceof Number) {
            return ((Number) result).longValue();
        }
        return 0L;
    }
    
    /**
     * Constrói query SQL dinamicamente baseada nas lojas
     * Baseado na query original fornecida, adaptada para ser dinâmica
     */
    private String buildDynamicStockQuery(List<Long> storeIds, boolean isCount) {
        if (storeIds == null || storeIds.isEmpty()) {
            throw new IllegalArgumentException("Lista de lojas não pode ser vazia");
        }
        
        StringBuilder query = new StringBuilder();
        
        if (isCount) {
            query.append("SELECT COUNT(*) FROM (");
            query.append("SELECT r.refplu ");
        } else {
            // Query externa com ROW_NUMBER para paginação
            query.append("SELECT * FROM (");
            query.append("SELECT *, ROW_NUMBER() OVER (");
            query.append("ORDER BY ");
            query.append(buildExternalSortOrderBy(storeIds));
            query.append(") as rn FROM (");
            // Query interna com agregações
            query.append("SELECT ");
            query.append("r.refplu, ");
            query.append("m.mardes AS marca, ");
            query.append("p.prodes AS descricao, ");
            
            // Construir colunas dinâmicas para cada loja usando ISNULL(MAX(CASE WHEN...))
            // Baseado na query original fornecida
            for (int i = 0; i < storeIds.size(); i++) {
                Long storeId = storeIds.get(i);
                if (i > 0) {
                    query.append(", ");
                }
                query.append("ISNULL(MAX(CASE WHEN e.lojcod = ").append(storeId)
                     .append(" AND e.loccod = 1 THEN e.esttot END), 0) AS loj").append(i + 1);
            }
            
            // Adicionar total (soma de todas as lojas)
            if (storeIds.size() > 0) {
                query.append(", ");
            }
            query.append(buildTotalExpression(storeIds));
            query.append(" AS total ");
        }
        
        // FROM baseado na query original - começando de referencia
        query.append("FROM referencia r ");
        query.append("INNER JOIN produto p ON r.procod = p.procod ");
        query.append("INNER JOIN marca m ON p.marcod = m.marcod ");
        query.append("LEFT JOIN estoque e ON r.refplu = e.refplu AND e.loccod = 1 ");
        
        // WHERE com filtros - seguindo estrutura da query original
        query.append("WHERE 1=1 ");
        query.append("AND (:refplu IS NULL OR r.refplu LIKE :refpluFilter) ");
        query.append("AND (:marca IS NULL OR m.mardes LIKE :marcaFilter) ");
        query.append("AND (:descricao IS NULL OR :descricaoWords IS NULL OR ");
        query.append("(UPPER(p.prodes) LIKE '%' + REPLACE(:descricaoWords, '|', '%') + '%' OR ");
        query.append("UPPER(m.mardes) LIKE '%' + REPLACE(:descricaoWords, '|', '%') + '%')) ");
        
        // GROUP BY baseado na query original
        query.append("GROUP BY r.refplu, m.mardes, p.prodes ");
        
        // HAVING com filtro de estoque total > 0
        query.append("HAVING (:hasStock = 0 OR ");
        query.append(buildTotalExpression(storeIds));
        query.append(" > 0) ");
        
        if (!isCount) {
            query.append(") AS paginated ");
            query.append(") AS numbered ");
            query.append("WHERE rn > :offset AND rn <= :offset + :size");
        } else {
            query.append(") AS filtered");
        }
        
        String finalQuery = query.toString();
        
        // Log da query para debug - sempre logar para identificar problemas
        log.info("Query SQL gerada (isCount={}, numLojas={}): {}", isCount, storeIds.size(), finalQuery);
        
        return finalQuery;
    }
    
    /**
     * Constrói expressão de total somando todas as lojas
     * Usa ISNULL para garantir que valores nulos sejam tratados como 0
     */
    private String buildTotalExpression(List<Long> storeIds) {
        if (storeIds.isEmpty()) {
            return "0";
        }
        StringBuilder total = new StringBuilder();
        for (int i = 0; i < storeIds.size(); i++) {
            Long storeId = storeIds.get(i);
            if (i > 0) {
                total.append(" + ");
            }
            total.append("ISNULL(MAX(CASE WHEN e.lojcod = ").append(storeId)
                 .append(" AND e.loccod = 1 THEN e.esttot END), 0)");
        }
        return total.toString();
    }
    
    /**
     * Constrói ORDER BY para ordenação no ROW_NUMBER (dentro da subquery)
     * Suporta ordenação por refplu, marca, descricao, lojas dinâmicas e total
     * Usa nomes de colunas diretamente (sem prefixo paginated) pois está dentro da subquery
     */
    private String buildExternalSortOrderBy(List<Long> storeIds) {
        StringBuilder orderBy = new StringBuilder();
        
        // Ordenação por refplu, marca, descricao
        orderBy.append("CASE WHEN :sortDir = 'asc' THEN ");
        orderBy.append("CASE WHEN :sortBy = 'refplu' THEN refplu ");
        orderBy.append("WHEN :sortBy = 'marca' THEN marca ");
        orderBy.append("WHEN :sortBy = 'descricao' THEN descricao END END ASC, ");
        orderBy.append("CASE WHEN :sortDir = 'desc' THEN ");
        orderBy.append("CASE WHEN :sortBy = 'refplu' THEN refplu ");
        orderBy.append("WHEN :sortBy = 'marca' THEN marca ");
        orderBy.append("WHEN :sortBy = 'descricao' THEN descricao END END DESC");
        
        // Adicionar ordenação por lojas dinâmicas (usando os aliases das colunas)
        for (int i = 0; i < storeIds.size(); i++) {
            String lojName = "loj" + (i + 1);
            orderBy.append(", CASE WHEN :sortDir = 'asc' THEN ");
            orderBy.append("CASE WHEN :sortBy = '").append(lojName).append("' THEN ").append(lojName).append(" ");
            orderBy.append("END END ASC, ");
            orderBy.append("CASE WHEN :sortDir = 'desc' THEN ");
            orderBy.append("CASE WHEN :sortBy = '").append(lojName).append("' THEN ").append(lojName).append(" ");
            orderBy.append("END END DESC");
        }
        
        // Adicionar ordenação por total
        orderBy.append(", CASE WHEN :sortDir = 'asc' THEN ");
        orderBy.append("CASE WHEN :sortBy = 'total' THEN total ");
        orderBy.append("END END ASC, ");
        orderBy.append("CASE WHEN :sortDir = 'desc' THEN ");
        orderBy.append("CASE WHEN :sortBy = 'total' THEN total ");
        orderBy.append("END END DESC");
        
        // Adicionar refplu como ordenação final para garantir determinismo
        orderBy.append(", refplu");
        
        return orderBy.toString();
    }
    
    /**
     * Define parâmetros da query
     */
    private void setQueryParameters(Query query, String refplu, String marca, String descricao,
                                   String refpluFilter, String marcaFilter,
                                   String descricaoWords, String grupoWords,
                                   List<Long> storeIds, Boolean hasStock,
                                   String sortBy, String sortDir, int offset, int size) {
        query.setParameter("refplu", refplu);
        query.setParameter("marca", marca);
        query.setParameter("descricao", descricao);
        query.setParameter("refpluFilter", refpluFilter);
        query.setParameter("marcaFilter", marcaFilter);
        query.setParameter("descricaoWords", descricaoWords != null ? descricaoWords : null);
        // grupoWords removido - não é mais usado na query após remover JOIN com grupo
        query.setParameter("hasStock", hasStock != null && hasStock ? 1 : 0);
        query.setParameter("sortBy", sortBy != null ? sortBy : "refplu");
        query.setParameter("sortDir", sortDir != null ? sortDir : "asc");
        query.setParameter("offset", offset);
        query.setParameter("size", size);
    }
    
    /**
     * Define parâmetros da query de contagem
     */
    private void setCountQueryParameters(Query query, String refplu, String marca, String descricao,
                                        String refpluFilter, String marcaFilter,
                                        String descricaoWords, String grupoWords,
                                        List<Long> storeIds, Boolean hasStock) {
        query.setParameter("refplu", refplu);
        query.setParameter("marca", marca);
        query.setParameter("descricao", descricao);
        query.setParameter("refpluFilter", refpluFilter);
        query.setParameter("marcaFilter", marcaFilter);
        query.setParameter("descricaoWords", descricaoWords != null ? descricaoWords : null);
        // grupoWords removido - não é mais usado na query após remover JOIN com grupo
        query.setParameter("hasStock", hasStock != null && hasStock ? 1 : 0);
    }
}
