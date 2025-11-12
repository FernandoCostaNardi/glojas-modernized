package com.sysconard.legacy.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.sysconard.legacy.dto.CriticalStockItemDTO;
import com.sysconard.legacy.repository.CriticalStockRepository;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service para análise de estoque crítico.
 * Responsável por validações e conversão de dados.
 * 
 * @author Sysconard Legacy API
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CriticalStockService {
    
    private final CriticalStockRepository criticalStockRepository;
    
    // Campos válidos para ordenação
    private static final List<String> VALID_SORT_FIELDS = Arrays.asList(
        "descricaoGrupo", "codigoPartNumber", "descricaoMarca", "refplu", 
        "descricaoProduto", "custoReposicao", "precoVenda", 
        "vendas90Dias", "vendas60Dias", "vendas30Dias", "vendasMesAtual", 
        "estoque", "mediaMensal", "diferenca"
    );
    
    /**
     * Busca produtos com estoque crítico.
     * 
     * @param refplu Filtro opcional por REFPLU
     * @param descricao Filtro opcional por descrição (busca também em grupo e marca)
     * @param grupo Filtro opcional por grupo
     * @param marca Filtro opcional por marca
     * @param page Número da página (base 0)
     * @param size Tamanho da página
     * @param sortBy Campo para ordenação
     * @param sortDir Direção da ordenação
     * @return Lista de itens de estoque crítico
     */
    public List<CriticalStockItemDTO> findCriticalStockWithFilters(
            String refplu, String descricao, String grupo, String marca, int page, int size, String sortBy, String sortDir) {
        
        log.debug("Buscando estoque crítico: refplu={}, descricao={}, grupo={}, marca={}, page={}, size={}, sortBy={}, sortDir={}",
                refplu, descricao, grupo, marca, page, size, sortBy, sortDir);
        
        // Validações
        validatePaginationParameters(page, size);
        validateSortParameters(sortBy, sortDir);
        
        // Preparar filtros
        String refpluFilter = createLikeFilter(refplu);
        String grupoFilter = createLikeFilter(grupo);
        String marcaFilter = createLikeFilter(marca);
        
        // Processar múltiplas palavras na descrição para busca ampla
        List<String> descricaoWords = splitIntoWords(descricao);
        String descricaoWordsParam = descricaoWords.isEmpty() ? null : String.join("|", descricaoWords);
        
        // Calcular offset
        int offset = page * size;
        
        // Executar query
        List<Object[]> rows = criticalStockRepository.findCriticalStockWithFilters(
            refplu,
            refpluFilter,
            descricao,
            descricaoWordsParam,
            grupo,
            grupoFilter,
            marca,
            marcaFilter,
            sortBy,
            sortDir.toUpperCase(),
            offset,
            size
        );
        
        // Converter para DTOs
        List<CriticalStockItemDTO> dtos = convertToDTO(rows);
        
        log.debug("Estoque crítico encontrado: {} itens", dtos.size());
        
        return dtos;
    }
    
    /**
     * Conta o total de produtos com estoque crítico.
     * 
     * @param refplu Filtro opcional por REFPLU
     * @param descricao Filtro opcional por descrição (busca também em grupo e marca)
     * @param grupo Filtro opcional por grupo
     * @param marca Filtro opcional por marca
     * @return Total de registros
     */
    public long countWithFilters(String refplu, String descricao, String grupo, String marca) {
        log.debug("Contando produtos com estoque crítico: refplu={}, descricao={}, grupo={}, marca={}", 
                refplu, descricao, grupo, marca);
        
        String refpluFilter = createLikeFilter(refplu);
        String grupoFilter = createLikeFilter(grupo);
        String marcaFilter = createLikeFilter(marca);
        
        // Processar múltiplas palavras na descrição para busca ampla
        List<String> descricaoWords = splitIntoWords(descricao);
        String descricaoWordsParam = descricaoWords.isEmpty() ? null : String.join("|", descricaoWords);
        
        Long count = criticalStockRepository.countCriticalStockWithFilters(
            refplu, refpluFilter, descricao, descricaoWordsParam, grupo, grupoFilter, marca, marcaFilter);
        
        log.debug("Total de produtos com estoque crítico: {}", count);
        
        return count != null ? count : 0L;
    }
    
    /**
     * Divide uma string em palavras, removendo espaços extras
     * 
     * @param value Valor para processar
     * @return Lista de palavras ou lista vazia se valor for nulo/vazio
     */
    private List<String> splitIntoWords(String value) {
        if (!StringUtils.hasText(value)) {
            return Collections.emptyList();
        }
        String[] words = value.trim().toUpperCase().split("\\s+");
        return Arrays.asList(words);
    }

    /**
     * Cria filtro LIKE para busca no banco
     * 
     * @param value Valor para filtrar
     * @return Filtro LIKE ou null se valor for nulo/vazio
     */
    private String createLikeFilter(String value) {
        return StringUtils.hasText(value) ? "%" + value.trim() + "%" : null;
    }
    
    /**
     * Converte array de objetos para DTO.
     * 
     * @param rows Lista de arrays de objetos
     * @return Lista de DTOs
     */
    private List<CriticalStockItemDTO> convertToDTO(List<Object[]> rows) {
        return rows.stream()
                .map(this::mapRowToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Mapeia uma linha (Object[]) para DTO.
     * 
     * @param row Array de objetos (14 campos)
     * @return DTO mapeado
     */
    private CriticalStockItemDTO mapRowToDTO(Object[] row) {
        return CriticalStockItemDTO.builder()
                .descricaoGrupo(row[0] != null ? row[0].toString() : null)
                .codigoPartNumber(row[1] != null ? row[1].toString() : null)
                .descricaoMarca(row[2] != null ? row[2].toString() : null)
                .refplu(row[3] != null ? row[3].toString() : null)
                .descricaoProduto(row[4] != null ? row[4].toString() : null)
                .custoReposicao(row[5] != null ? new BigDecimal(row[5].toString()) : BigDecimal.ZERO)
                .precoVenda(row[6] != null ? new BigDecimal(row[6].toString()) : BigDecimal.ZERO)
                .vendas90Dias(row[7] != null ? new BigDecimal(row[7].toString()) : BigDecimal.ZERO)
                .vendas60Dias(row[8] != null ? new BigDecimal(row[8].toString()) : BigDecimal.ZERO)
                .vendas30Dias(row[9] != null ? new BigDecimal(row[9].toString()) : BigDecimal.ZERO)
                .vendasMesAtual(row[10] != null ? new BigDecimal(row[10].toString()) : BigDecimal.ZERO)
                .estoque(row[11] != null ? new BigDecimal(row[11].toString()) : BigDecimal.ZERO)
                .mediaMensal(row[12] != null ? new BigDecimal(row[12].toString()) : BigDecimal.ZERO)
                .diferenca(row[13] != null ? new BigDecimal(row[13].toString()) : BigDecimal.ZERO)
                .build();
    }
    
    /**
     * Valida parâmetros de paginação.
     * 
     * @param page Número da página
     * @param size Tamanho da página
     */
    private void validatePaginationParameters(int page, int size) {
        if (page < 0) {
            throw new IllegalArgumentException("Número da página deve ser >= 0");
        }
        if (size <= 0 || size > 1000) {
            throw new IllegalArgumentException("Tamanho da página deve ser entre 1 e 1000");
        }
    }
    
    /**
     * Valida parâmetros de ordenação.
     * 
     * @param sortBy Campo para ordenação
     * @param sortDir Direção da ordenação
     */
    private void validateSortParameters(String sortBy, String sortDir) {
        if (!VALID_SORT_FIELDS.contains(sortBy)) {
            throw new IllegalArgumentException(
                "Campo de ordenação inválido: " + sortBy + ". Campos válidos: " + VALID_SORT_FIELDS
            );
        }
        if (!sortDir.equalsIgnoreCase("asc") && !sortDir.equalsIgnoreCase("desc")) {
            throw new IllegalArgumentException("Direção de ordenação deve ser 'asc' ou 'desc'");
        }
    }
}

