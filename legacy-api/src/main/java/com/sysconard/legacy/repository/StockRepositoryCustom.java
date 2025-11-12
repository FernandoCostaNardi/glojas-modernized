package com.sysconard.legacy.repository;

import java.util.List;

/**
 * Interface customizada para métodos de estoque que requerem queries dinâmicas
 * 
 * @author Sysconard Legacy API
 * @version 1.0
 */
public interface StockRepositoryCustom {
    
    /**
     * Busca estoque com filtros e paginação usando query dinâmica baseada nas lojas
     * 
     * @param refplu Filtro por refplu (opcional)
     * @param marca Filtro por marca (opcional)
     * @param descricao Filtro por descrição (opcional)
     * @param refpluFilter Filtro LIKE para refplu
     * @param marcaFilter Filtro LIKE para marca
     * @param descricaoWords Palavras de descrição separadas por |
     * @param grupoWords Palavras de grupo separadas por |
     * @param storeIds Lista de IDs de lojas para incluir na query
     * @param hasStock Filtrar apenas produtos com estoque total > 0
     * @param sortBy Campo para ordenação
     * @param sortDir Direção da ordenação (asc/desc)
     * @param offset Offset para paginação
     * @param size Tamanho da página
     * @return Lista de arrays de objetos com dados do estoque
     */
    List<Object[]> findStocksWithFiltersDynamic(
            String refplu, String marca, String descricao,
            String refpluFilter, String marcaFilter,
            String descricaoWords, String grupoWords,
            List<Long> storeIds,
            Boolean hasStock, String sortBy, String sortDir,
            int offset, int size
    );
    
    /**
     * Conta total de registros de estoque com filtros aplicados usando query dinâmica
     * 
     * @param refplu Filtro por refplu (opcional)
     * @param marca Filtro por marca (opcional)
     * @param descricao Filtro por descrição (opcional)
     * @param refpluFilter Filtro LIKE para refplu
     * @param marcaFilter Filtro LIKE para marca
     * @param descricaoWords Palavras de descrição separadas por |
     * @param grupoWords Palavras de grupo separadas por |
     * @param storeIds Lista de IDs de lojas para incluir na query
     * @param hasStock Filtrar apenas produtos com estoque total > 0
     * @return Total de registros
     */
    Long countStocksWithFiltersDynamic(
            String refplu, String marca, String descricao,
            String refpluFilter, String marcaFilter,
            String descricaoWords, String grupoWords,
            List<Long> storeIds,
            Boolean hasStock
    );
}

