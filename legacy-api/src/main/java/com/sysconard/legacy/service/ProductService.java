package com.sysconard.legacy.service;

import com.sysconard.legacy.dto.ProductRegisteredDTO;
import com.sysconard.legacy.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Serviço responsável pelas operações de acesso a dados de produtos
 * 
 * Segue os princípios de Clean Code:
 * - Responsabilidade única: apenas acesso a dados
 * - Injeção de dependência via construtor
 * - Métodos pequenos e coesos
 * - Validação de parâmetros
 * - Tratamento de exceções
 * - Logging estruturado
 * 
 * @author Sysconard Legacy API
 * @version 2.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

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
     * @throws IllegalArgumentException se parâmetros inválidos
     */
    public Page<ProductRegisteredDTO> findProductsWithFilters(
            String secao, String grupo, String marca, String descricao,
            int page, int size, String sortBy, String sortDir) {

        log.debug("Buscando produtos com filtros: secao={}, grupo={}, marca={}, descricao={}, page={}, size={}, sortBy={}, sortDir={}",
                secao, grupo, marca, descricao, page, size, sortBy, sortDir);

        // Validar parâmetros
        validatePaginationParameters(page, size);
        validateSortParameters(sortBy, sortDir);

        // Criar configurações de paginação e ordenação
        Pageable pageable = createPageable(page, size, sortBy, sortDir);
        
        // Preparar filtros
        ProductFilters filters = createProductFilters(secao, grupo, marca, descricao);

        // Buscar dados
        List<Object[]> rows = fetchProductData(filters, pageable);
        Long totalElements = countProductData(filters);

        // Converter para DTOs
        List<ProductRegisteredDTO> dtos = convertToProductDTOs(rows);

        log.debug("Produtos encontrados: {} de {}", dtos.size(), totalElements);

        return new PageImpl<>(dtos, pageable, totalElements);
    }

    /**
     * Conta o total de produtos cadastrados (para teste de conexão)
     * 
     * @return Número total de produtos
     */
    public long countTotalProducts() {
        log.debug("Contando total de produtos cadastrados");
        long count = productRepository.countTotalProducts();
        log.debug("Total de produtos: {}", count);
        return count;
    }

    /**
     * Valida parâmetros de paginação
     * 
     * @param page Número da página
     * @param size Tamanho da página
     * @throws IllegalArgumentException se parâmetros inválidos
     */
    private void validatePaginationParameters(int page, int size) {
        if (page < 0) {
            throw new IllegalArgumentException("Número da página deve ser >= 0");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("Tamanho da página deve ser > 0");
        }
        if (size > 1000) {
            throw new IllegalArgumentException("Tamanho da página não pode ser > 1000");
        }
    }

    /**
     * Valida parâmetros de ordenação
     * 
     * @param sortBy Campo para ordenação
     * @param sortDir Direção da ordenação
     * @throws IllegalArgumentException se parâmetros inválidos
     */
    private void validateSortParameters(String sortBy, String sortDir) {
        if (!StringUtils.hasText(sortBy)) {
            throw new IllegalArgumentException("Campo de ordenação não pode ser vazio");
        }
        if (!StringUtils.hasText(sortDir)) {
            throw new IllegalArgumentException("Direção de ordenação não pode ser vazia");
        }
        if (!sortDir.equalsIgnoreCase("asc") && !sortDir.equalsIgnoreCase("desc")) {
            throw new IllegalArgumentException("Direção de ordenação deve ser 'asc' ou 'desc'");
        }
    }

    /**
     * Cria configuração de paginação e ordenação
     * 
     * @param page Número da página
     * @param size Tamanho da página
     * @param sortBy Campo para ordenação
     * @param sortDir Direção da ordenação
     * @return Pageable configurado
     */
    private Pageable createPageable(int page, int size, String sortBy, String sortDir) {
        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? 
                Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortBy);
        return PageRequest.of(page, size, sort);
    }

    /**
     * Cria filtros de produtos a partir dos parâmetros
     * 
     * @param secao Filtro por seção
     * @param grupo Filtro por grupo
     * @param marca Filtro por marca
     * @param descricao Filtro por descrição
     * @return ProductFilters configurado
     */
    private ProductFilters createProductFilters(String secao, String grupo, String marca, String descricao) {
        return ProductFilters.builder()
                .secao(secao)
                .grupo(grupo)
                .marca(marca)
                .descricao(descricao)
                .secaoFilter(createLikeFilter(secao))
                .grupoFilter(createLikeFilter(grupo))
                .marcaFilter(createLikeFilter(marca))
                .descricaoFilter(createLikeFilter(descricao))
                .build();
    }

    /**
     * Cria filtro LIKE para busca no banco
     * 
     * @param value Valor para filtrar
     * @return Filtro LIKE ou null se valor for nulo/vazio
     */
    private String createLikeFilter(String value) {
        return StringUtils.hasText(value) ? "%" + value + "%" : null;
    }

    /**
     * Busca dados de produtos no repositório
     * 
     * @param filters Filtros aplicados
     * @param pageable Configuração de paginação
     * @return Lista de objetos retornados pelo repositório
     */
    private List<Object[]> fetchProductData(ProductFilters filters, Pageable pageable) {
        int offset = pageable.getPageNumber() * pageable.getPageSize();
        
        return productRepository.findProductsWithFilters(
                filters.getSecao(), filters.getGrupo(), filters.getMarca(), filters.getDescricao(),
                filters.getSecaoFilter(), filters.getGrupoFilter(), filters.getMarcaFilter(), filters.getDescricaoFilter(),
                pageable.getSort().iterator().next().getProperty(), offset, pageable.getPageSize()
        );
    }

    /**
     * Conta total de registros com filtros aplicados
     * 
     * @param filters Filtros aplicados
     * @return Total de registros
     */
    private Long countProductData(ProductFilters filters) {
        return productRepository.countProductsWithFilters(
                filters.getSecao(), filters.getGrupo(), filters.getMarca(), filters.getDescricao(),
                filters.getSecaoFilter(), filters.getGrupoFilter(), filters.getMarcaFilter(), filters.getDescricaoFilter()
        );
    }

    /**
     * Converte lista de objetos para DTOs de produtos
     * 
     * @param rows Lista de objetos do repositório
     * @return Lista de ProductRegisteredDTO
     */
    private List<ProductRegisteredDTO> convertToProductDTOs(List<Object[]> rows) {
        return rows.stream()
                .map(this::convertRowToProductDTO)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Converte uma linha de dados para ProductRegisteredDTO
     * 
     * @param row Linha de dados do repositório
     * @return ProductRegisteredDTO ou null se conversão falhar
     */
    private ProductRegisteredDTO convertRowToProductDTO(Object[] row) {
        try {
            return new ProductRegisteredDTO(
                    convertToLong(row[0]),      // codigo
                    (String) row[1],            // secao
                    (String) row[2],            // grupo
                    (String) row[3],            // subgrupo
                    (String) row[4],            // marca
                    (String) row[5],            // part_number_codigo
                    (String) row[6],            // refplu
                    (String) row[7],            // descricao
                    (String) row[8]             // ncm
            );
        } catch (Exception e) {
            log.warn("Erro ao converter linha para ProductRegisteredDTO: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Converte Object para Long, tratando diferentes tipos de dados do SQL Server
     * 
     * @param value Valor vindo do banco de dados
     * @return Long convertido ou null se conversão falhar
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
                log.warn("Erro ao converter String para Long: {}", value);
                return null;
            }
        }
        
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        
        log.warn("Tipo não suportado para conversão para Long: {}", value.getClass().getSimpleName());
        return null;
    }

    /**
     * Classe interna para encapsular filtros de produtos
     */
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    private static class ProductFilters {
        private String secao;
        private String grupo;
        private String marca;
        private String descricao;
        private String secaoFilter;
        private String grupoFilter;
        private String marcaFilter;
        private String descricaoFilter;
    }
}
