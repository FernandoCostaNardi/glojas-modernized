package com.sysconard.legacy.service;

import com.sysconard.legacy.dto.ProductRegisteredDTO;
import com.sysconard.legacy.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service para operações com produtos
 * 
 * @author Sysconard Legacy API
 * @version 1.0
 */
@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    /**
     * Busca produtos cadastrados com filtros, paginação e ordenação
     * 
     * @param secao Filtro por seção (opcional)
     * @param grupo Filtro por grupo (opcional)
     * @param marca Filtro por marca (opcional)
     * @param descricao Filtro por descrição (opcional)
     * @param page Número da página (0-based)
     * @param size Tamanho da página
     * @param sortBy Campo para ordenação
     * @param sortDir Direção da ordenação (asc/desc)
     * @return Página com produtos cadastrados
     */
    public Page<ProductRegisteredDTO> findProductsWithFilters(
            String secao, String grupo, String marca, String descricao,
            int page, int size, String sortBy, String sortDir) {

        // Criar Sort
        Sort sort = Sort.by(
            sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC,
            sortBy
        );

        // Criar Pageable
        Pageable pageable = PageRequest.of(page, size, sort);

        // Preparar filtros com % para LIKE
        String secaoFilter = secao != null ? "%" + secao + "%" : null;
        String grupoFilter = grupo != null ? "%" + grupo + "%" : null;
        String marcaFilter = marca != null ? "%" + marca + "%" : null;
        String descricaoFilter = descricao != null ? "%" + descricao + "%" : null;

        // Calcular offset para paginação manual
        int offset = page * size;

        // Buscar dados com paginação manual
        List<Object[]> rows = productRepository.findProductsWithFilters(
            secao, grupo, marca, descricao, 
            secaoFilter, grupoFilter, marcaFilter, descricaoFilter,
            sortBy, offset, size
        );

        // Buscar total de registros
        Long totalElements = productRepository.countProductsWithFilters(
            secao, grupo, marca, descricao, 
            secaoFilter, grupoFilter, marcaFilter, descricaoFilter
        );

        // Converter para DTO
        List<ProductRegisteredDTO> dtos = rows.stream()
            .map(row -> new ProductRegisteredDTO(
                convertToLong(row[0]),      // codigo
                (String) row[1],            // secao
                (String) row[2],            // grupo
                (String) row[3],            // subgrupo
                (String) row[4],            // marca
                (String) row[5],            // part_number_codigo
                (String) row[6],            // refplu
                (String) row[7],            // descricao
                (String) row[8]             // ncm
            ))
            .collect(Collectors.toList());

        // Retornar Page com DTOs
        return new PageImpl<>(dtos, pageable, totalElements);
    }

    /**
     * Conta o total de produtos cadastrados (para teste de conexão)
     * 
     * @return Número total de produtos
     */
    public long countTotalProducts() {
        return productRepository.countTotalProducts();
    }

    /**
     * Converte Object para Long, tratando diferentes tipos de dados do SQL Server
     * 
     * @param value Valor vindo do banco de dados
     * @return Long convertido
     */
    private Long convertToLong(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Long) {
            return (Long) value;
        }
        if (value instanceof Integer) {
            return ((Integer) value).longValue();
        }
        if (value instanceof String) {
            try {
                return Long.parseLong((String) value);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return null;
    }
}
