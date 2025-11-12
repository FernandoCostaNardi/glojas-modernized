package com.sysconard.legacy.service;

import com.sysconard.legacy.dto.StockItemDTO;
import com.sysconard.legacy.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Serviço responsável pelas operações de acesso a dados de estoque
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
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;

    /**
     * Busca estoque com filtros, paginação e ordenação
     * 
     * @param refplu Filtro por refplu (opcional)
     * @param marca Filtro por marca (opcional)
     * @param descricao Filtro por descrição (opcional)
     * @param hasStock Filtrar apenas produtos com estoque total > 0 (padrão: true)
     * @param page Número da página (0-based)
     * @param size Tamanho da página
     * @param sortBy Campo para ordenação
     * @param sortDir Direção da ordenação (asc/desc)
     * @return Página com itens de estoque
     * @throws IllegalArgumentException se parâmetros inválidos
     */
    public Page<StockItemDTO> findStocksWithFilters(
            String refplu, String marca, String descricao, Boolean hasStock,
            int page, int size, String sortBy, String sortDir) {

        log.debug("Buscando estoque com filtros: refplu={}, marca={}, descricao={}, hasStock={}, page={}, size={}, sortBy={}, sortDir={}",
                refplu, marca, descricao, hasStock, page, size, sortBy, sortDir);

        // Validar parâmetros
        validatePaginationParameters(page, size);
        validateSortParameters(sortBy, sortDir);

        // Criar configurações de paginação e ordenação
        Pageable pageable = createPageable(page, size, sortBy, sortDir);
        
        // Preparar filtros
        StockFilters filters = createStockFilters(refplu, marca, descricao);

        // Buscar dados
        List<Object[]> rows = fetchStockData(filters, hasStock, pageable);
        Long totalElements = countStockData(filters, hasStock);

        // Converter para DTOs
        List<StockItemDTO> dtos = convertToStockDTOs(rows);

        log.debug("Itens de estoque encontrados: {} de {}", dtos.size(), totalElements);

        return new PageImpl<>(dtos, pageable, totalElements);
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
        
        // Validar valores permitidos para sortBy
        List<String> validSortFields = Arrays.asList(
            "refplu", "marca", "descricao", 
            "loj1", "loj2", "loj3", "loj4", "loj5", "loj6", "loj7", 
            "loj8", "loj9", "loj10", "loj11", "loj12", "loj13", "loj14",
            "total"
        );
        
        if (!validSortFields.contains(sortBy.toLowerCase())) {
            throw new IllegalArgumentException(
                "Campo de ordenação inválido: " + sortBy + ". Valores válidos: " + validSortFields
            );
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
     * Cria filtros de estoque a partir dos parâmetros
     * 
     * @param refplu Filtro por refplu
     * @param marca Filtro por marca
     * @param descricao Filtro por descrição
     * @return StockFilters configurado
     */
    private StockFilters createStockFilters(String refplu, String marca, String descricao) {
        // Processa múltiplas palavras na descrição
        List<String> descricaoWords = splitIntoWords(descricao);
        
        return StockFilters.builder()
                .refplu(refplu)
                .marca(marca)
                .descricao(descricao)
                .refpluFilter(createLikeFilter(refplu))
                .marcaFilter(createLikeFilter(marca))
                .descricaoFilter(createContainedLettersFilter(descricao))
                .grupoFilter(createContainedLettersFilter(descricao))
                .descricaoWords(descricaoWords.isEmpty() ? null : String.join("|", descricaoWords))
                .grupoWords(descricaoWords.isEmpty() ? null : String.join("|", descricaoWords))
                .build();
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
        return StringUtils.hasText(value) ? "%" + value + "%" : null;
    }

    /**
     * Cria filtro de busca por letras contidas
     * Transforma "NTB" em "%N%T%B%" para buscar palavras que contenham essas letras na ordem
     * Exemplo: "NTB" encontra "notebook", "NTB123", etc.
     * 
     * @param value Valor para filtrar
     * @return Filtro LIKE com letras contidas ou null se valor for nulo/vazio
     */
    private String createContainedLettersFilter(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        
        // Remove espaços e converte para maiúsculo para busca case-insensitive
        String cleaned = value.trim().toUpperCase();
        
        // Transforma cada letra em "%LETRA%"
        // Exemplo: "NTB" -> "%N%T%B%"
        StringBuilder filter = new StringBuilder();
        for (char c : cleaned.toCharArray()) {
            if (Character.isLetterOrDigit(c)) {
                filter.append("%").append(c);
            }
        }
        filter.append("%");
        
        return filter.toString();
    }

    /**
     * Busca dados de estoque no repositório
     * 
     * @param filters Filtros aplicados
     * @param hasStock Filtrar apenas produtos com estoque total > 0
     * @param pageable Configuração de paginação
     * @return Lista de objetos retornados pelo repositório
     */
    private List<Object[]> fetchStockData(StockFilters filters, Boolean hasStock, Pageable pageable) {
        int offset = pageable.getPageNumber() * pageable.getPageSize();
        String sortBy = pageable.getSort().iterator().hasNext() ? 
                pageable.getSort().iterator().next().getProperty() : "refplu";
        String sortDir = pageable.getSort().iterator().hasNext() && 
                pageable.getSort().iterator().next().isDescending() ? "desc" : "asc";
        
        return stockRepository.findStocksWithFilters(
                filters.getRefplu(), filters.getMarca(), filters.getDescricao(),
                filters.getRefpluFilter(), filters.getMarcaFilter(),
                filters.getDescricaoWords(), filters.getGrupoWords(),
                hasStock, sortBy, sortDir, offset, pageable.getPageSize()
        );
    }

    /**
     * Conta total de registros com filtros aplicados
     * 
     * @param filters Filtros aplicados
     * @param hasStock Filtrar apenas produtos com estoque total > 0
     * @return Total de registros
     */
    private Long countStockData(StockFilters filters, Boolean hasStock) {
        return stockRepository.countStocksWithFilters(
                filters.getRefplu(), filters.getMarca(), filters.getDescricao(),
                filters.getRefpluFilter(), filters.getMarcaFilter(),
                filters.getDescricaoWords(), filters.getGrupoWords(),
                hasStock
        );
    }

    /**
     * Converte lista de objetos para DTOs de estoque
     * 
     * @param rows Lista de objetos do repositório
     * @return Lista de StockItemDTO
     */
    private List<StockItemDTO> convertToStockDTOs(List<Object[]> rows) {
        return rows.stream()
                .map(this::convertRowToStockDTO)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Converte uma linha de dados para StockItemDTO
     * 
     * @param row Linha de dados do repositório
     * @return StockItemDTO ou null se conversão falhar
     */
    private StockItemDTO convertRowToStockDTO(Object[] row) {
        try {
            Long loj1 = convertToLong(row[3]);
            Long loj2 = convertToLong(row[4]);
            Long loj3 = convertToLong(row[5]);
            Long loj4 = convertToLong(row[6]);
            Long loj5 = convertToLong(row[7]);
            Long loj6 = convertToLong(row[8]);
            Long loj7 = convertToLong(row[9]);
            Long loj8 = convertToLong(row[10]);
            Long loj9 = convertToLong(row[11]);
            Long loj10 = convertToLong(row[12]);
            Long loj11 = convertToLong(row[13]);
            Long loj12 = convertToLong(row[14]);
            Long loj13 = convertToLong(row[15]);
            Long loj14 = convertToLong(row[16]);
            
            // Calcular total somando todas as quantidades das lojas
            Long total = calculateTotal(loj1, loj2, loj3, loj4, loj5, loj6, loj7, 
                                       loj8, loj9, loj10, loj11, loj12, loj13, loj14);
            
            return new StockItemDTO(
                    (String) row[0],    // refplu
                    (String) row[1],    // marca
                    (String) row[2],    // descricao
                    loj1, loj2, loj3, loj4, loj5, loj6, loj7,
                    loj8, loj9, loj10, loj11, loj12, loj13, loj14,
                    total               // total
            );
        } catch (Exception e) {
            log.warn("Erro ao converter linha para StockItemDTO: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * Calcula o total somando todas as quantidades das lojas
     * Trata valores nulos como zero
     * 
     * @param quantities Quantidades das lojas
     * @return Total calculado
     */
    private Long calculateTotal(Long... quantities) {
        long sum = 0;
        for (Long quantity : quantities) {
            if (quantity != null) {
                sum += quantity;
            }
        }
        return sum;
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
     * Classe interna para encapsular filtros de estoque
     */
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    private static class StockFilters {
        private String refplu;
        private String marca;
        private String descricao;
        private String refpluFilter;
        private String marcaFilter;
        private String descricaoFilter;
        private String grupoFilter;
        private String descricaoWords;
        private String grupoWords;
    }
    
    /**
     * Testa dados sem filtro de loccod
     * 
     * @return Lista de dados de teste
     */
    public List<Object[]> testStocks() {
        return stockRepository.findTestStocks();
    }
    
    /**
     * Testa dados com loccod = 1
     * 
     * @return Lista de dados de teste
     */
    public List<Object[]> testStocksWithLoccod1() {
        return stockRepository.findTestStocksWithLoccod1();
    }
    
    /**
     * Testa dados com PIVOT simplificado
     * 
     * @return Lista de dados de teste
     */
    public List<Object[]> testStocksWithPivot() {
        return stockRepository.findTestStocksWithPivot();
    }
}
