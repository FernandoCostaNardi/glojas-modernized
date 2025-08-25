package com.sysconard.legacy.repository;

import com.sysconard.legacy.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository para operações com produtos
 * 
 * @author Sysconard Legacy API
 * @version 1.0
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Busca produtos cadastrados com filtros e paginação
     * 
     * @param secao Filtro por seção (opcional)
     * @param grupo Filtro por grupo (opcional)
     * @param marca Filtro por marca (opcional)
     * @param descricao Filtro por descrição (opcional)
     * @param sortBy Campo para ordenação
     * @param pageable Configuração de paginação
     * @return Página com produtos cadastrados
     */
    @Query(value = "SELECT * FROM (" +
                   "SELECT p.PROCOD as codigo, s.SECDES as secao, g.GRPDES as grupo, sb.SBGDES as subgrupo, " +
                   "m.MARDES as marca, rf.REFCOD as part_number_codigo, r.REFPLU as refplu, p.PRODES as descricao, " +
                   "p.PRONCM as ncm, " +
                   "ROW_NUMBER() OVER (ORDER BY " +
                   "CASE WHEN :sortBy = 'codigo' THEN p.PROCOD END, " +
                   "CASE WHEN :sortBy = 'secao' THEN s.SECDES END, " +
                   "CASE WHEN :sortBy = 'grupo' THEN g.GRPDES END, " +
                   "CASE WHEN :sortBy = 'marca' THEN m.MARDES END, " +
                   "CASE WHEN :sortBy = 'descricao' THEN p.PRODES END, p.PROCOD) as rn " +
                   "FROM PRODUTO p JOIN SECAO s ON p.SECCOD = s.SECCOD " +
                   "JOIN GRUPO g ON p.GRPCOD = g.GRPCOD JOIN SUBGRUPO sb ON p.SBGCOD = sb.SBGCOD " +
                   "JOIN MARCA m ON p.MARCOD = m.MARCOD JOIN REFERENCIA r ON p.PROCOD = r.PROCOD " +
                   "LEFT JOIN REFERENCIA_FABRICANTE rf ON r.REFPLU = rf.REFPLU " +
                   "WHERE s.SECCOD = g.SECCOD AND sb.GRPCOD = g.GRPCOD AND sb.SECCOD = s.SECCOD " +
                   "AND (:secao IS NULL OR s.SECDES LIKE :secaoFilter) " +
                   "AND (:grupo IS NULL OR g.GRPDES LIKE :grupoFilter) " +
                   "AND (:marca IS NULL OR m.MARDES LIKE :marcaFilter) " +
                   "AND (:descricao IS NULL OR p.PRODES LIKE :descricaoFilter)" +
                   ") AS numbered WHERE rn BETWEEN :offset + 1 AND :offset + :pageSize",
        nativeQuery = true)
    List<Object[]> findProductsWithFilters(
        @Param("secao") String secao,
        @Param("grupo") String grupo,
        @Param("marca") String marca,
        @Param("descricao") String descricao,
        @Param("secaoFilter") String secaoFilter,
        @Param("grupoFilter") String grupoFilter,
        @Param("marcaFilter") String marcaFilter,
        @Param("descricaoFilter") String descricaoFilter,
        @Param("sortBy") String sortBy,
        @Param("offset") int offset,
        @Param("pageSize") int pageSize
    );

    @Query(value = "SELECT COUNT(*) FROM PRODUTO p JOIN SECAO s ON p.SECCOD = s.SECCOD " +
                   "JOIN GRUPO g ON p.GRPCOD = g.GRPCOD JOIN SUBGRUPO sb ON p.SBGCOD = sb.SBGCOD " +
                   "JOIN MARCA m ON p.MARCOD = m.MARCOD JOIN REFERENCIA r ON p.PROCOD = r.PROCOD " +
                   "LEFT JOIN REFERENCIA_FABRICANTE rf ON r.REFPLU = rf.REFPLU " +
                   "WHERE s.SECCOD = g.SECCOD AND sb.GRPCOD = g.GRPCOD AND sb.SECCOD = s.SECCOD " +
                   "AND (:secao IS NULL OR s.SECDES LIKE :secaoFilter) " +
                   "AND (:grupo IS NULL OR g.GRPDES LIKE :grupoFilter) " +
                   "AND (:marca IS NULL OR m.MARDES LIKE :marcaFilter) " +
                   "AND (:descricao IS NULL OR p.PRODES LIKE :descricaoFilter)",
        nativeQuery = true)
    Long countProductsWithFilters(
        @Param("secao") String secao,
        @Param("grupo") String grupo,
        @Param("marca") String marca,
        @Param("descricao") String descricao,
        @Param("secaoFilter") String secaoFilter,
        @Param("grupoFilter") String grupoFilter,
        @Param("marcaFilter") String marcaFilter,
        @Param("descricaoFilter") String descricaoFilter
    );

    /**
     * Conta o total de produtos (para teste de conexão)
     * 
     * @return Número total de produtos
     */
    @Query(value = "SELECT COUNT(*) FROM PRODUTO", nativeQuery = true)
    Long countTotalProducts();
}
