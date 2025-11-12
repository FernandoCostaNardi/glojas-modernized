package com.sysconard.legacy.service;

import com.sysconard.legacy.dto.StockItemDTO;
import com.sysconard.legacy.entity.store.Store;
import com.sysconard.legacy.repository.StockRepository;
import com.sysconard.legacy.repository.StoreRepository;
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
    private final StoreRepository storeRepository;

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

        // Buscar lojas ativas para conversão
        List<Store> stores = getActiveStores();
        
        // Buscar dados
        List<Object[]> rows = fetchStockData(filters, hasStock, pageable);
        Long totalElements = countStockData(filters, hasStock);

        log.info("Linhas retornadas do repositório: {} (página: {}, tamanho solicitado: {})", 
                rows.size(), pageable.getPageNumber(), pageable.getPageSize());

        // Converter para DTOs
        // A query SQL já garante que não há duplicatas (GROUP BY r.refplu)
        List<StockItemDTO> dtos = convertToStockDTOs(rows, stores);

        log.info("Itens de estoque convertidos: {} de {} linhas (totalElements: {}, página: {}, tamanho: {})", 
                dtos.size(), rows.size(), totalElements, pageable.getPageNumber(), pageable.getPageSize());

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
        // Campos básicos sempre válidos
        List<String> basicSortFields = Arrays.asList("refplu", "marca", "descricao", "total");
        
        // Verificar se é um campo básico
        if (basicSortFields.contains(sortBy.toLowerCase())) {
            // Campo básico válido
        } else if (sortBy.toLowerCase().matches("loj\\d+")) {
            // Campo de loja dinâmico (loj1, loj2, etc.) - aceitar qualquer número
            // A validação real será feita na query baseada nas lojas disponíveis
        } else {
            throw new IllegalArgumentException(
                "Campo de ordenação inválido: " + sortBy + ". Deve ser refplu, marca, descricao, total ou lojN (onde N é o número da loja)"
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
        
        // Buscar lojas ativas
        List<com.sysconard.legacy.entity.store.Store> stores = getActiveStores();
        List<Long> storeIds = stores.stream()
                .map(com.sysconard.legacy.entity.store.Store::getId)
                .collect(Collectors.toList());
        
        // Log para debug - verificar mapeamento de lojas
        if (log.isDebugEnabled()) {
            log.debug("Lojas ativas ordenadas por ID (para fetchStockData):");
            for (int i = 0; i < stores.size(); i++) {
                log.debug("  loj{}: ID={}, Nome={}", i+1, stores.get(i).getId(), stores.get(i).getName());
            }
        }
        
        return stockRepository.findStocksWithFiltersDynamic(
                filters.getRefplu(), filters.getMarca(), filters.getDescricao(),
                filters.getRefpluFilter(), filters.getMarcaFilter(),
                filters.getDescricaoWords(), filters.getGrupoWords(),
                storeIds,
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
        // Buscar lojas ativas
        List<com.sysconard.legacy.entity.store.Store> stores = getActiveStores();
        List<Long> storeIds = stores.stream()
                .map(com.sysconard.legacy.entity.store.Store::getId)
                .collect(Collectors.toList());
        
        return stockRepository.countStocksWithFiltersDynamic(
                filters.getRefplu(), filters.getMarca(), filters.getDescricao(),
                filters.getRefpluFilter(), filters.getMarcaFilter(),
                filters.getDescricaoWords(), filters.getGrupoWords(),
                storeIds,
                hasStock
        );
    }

    /**
     * Converte lista de objetos para DTOs de estoque
     * 
     * @param rows Lista de objetos do repositório
     * @param stores Lista de lojas para mapeamento dinâmico
     * @return Lista de StockItemDTO
     */
    private List<StockItemDTO> convertToStockDTOs(List<Object[]> rows, List<Store> stores) {
        return rows.stream()
                .map(row -> convertRowToStockDTO(row, stores))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Converte uma linha de dados para StockItemDTO
     * 
     * Mapeia dinamicamente as colunas retornadas pela query baseado no número de lojas.
     * A query retorna: refplu, marca, descricao, loj1, loj2, ..., lojN, total, rn
     * 
     * @param row Linha de dados do repositório
     * @param stores Lista de lojas para mapeamento dinâmico (ordenadas por ID)
     * @return StockItemDTO ou null se conversão falhar
     */
    private StockItemDTO convertRowToStockDTO(Object[] row, List<Store> stores) {
        try {
            // Estrutura do array retornado pela query dinâmica:
            // row[0] = refplu
            // row[1] = marca
            // row[2] = descricao
            // row[3] até row[2+stores.size()] = quantidades por loja (loj1, loj2, ..., lojN)
            // row[3+stores.size()] = total
            // row[3+stores.size()+1] = rn (ROW_NUMBER) - se presente, ignorar
            
            if (row.length < 3) {
                log.warn("Linha com menos de 3 colunas: {}", row.length);
                return null;
            }
            
            Long[] lojas = new Long[14]; // Inicializar com null (compatibilidade com DTO)
            Long total = 0L;
            
            int numStores = Math.min(stores.size(), 14); // Limitar a 14 para compatibilidade com DTO
            
            // Mapear quantidades das lojas dinamicamente
            for (int i = 0; i < numStores; i++) {
                int rowIndex = 3 + i; // Coluna da loja na query (após refplu, marca, descricao)
                if (rowIndex < row.length) {
                    Long quantidade = convertToLong(row[rowIndex]);
                    lojas[i] = quantidade != null ? quantidade : 0L;
                } else {
                    lojas[i] = 0L; // Se não houver coluna, assumir 0
                }
            }
            
            // Buscar o total da query (coluna após todas as lojas)
            int totalIndex = 3 + stores.size();
            if (totalIndex < row.length) {
                Long totalFromQuery = convertToLong(row[totalIndex]);
                if (totalFromQuery != null) {
                    total = totalFromQuery;
                } else {
                    // Se total for null, calcular somando as lojas
                    for (int i = 0; i < numStores; i++) {
                        if (lojas[i] != null) {
                            total += lojas[i];
                        }
                    }
                }
            } else {
                // Se não houver coluna de total, calcular somando as lojas
                for (int i = 0; i < numStores; i++) {
                    if (lojas[i] != null) {
                        total += lojas[i];
                    }
                }
            }
            
            // Log detalhado para debug
            if (log.isDebugEnabled()) {
                StringBuilder lojasStr = new StringBuilder();
                for (int i = 0; i < Math.min(numStores, 5); i++) {
                    if (lojas[i] != null) {
                        lojasStr.append(String.format("loj%d=%d, ", i+1, lojas[i]));
                    }
                }
                log.debug("Convertendo linha - refplu: {}, numStores: {}, totalIndex: {}, row.length: {}, total: {}, lojas: [{}]", 
                        row[0], numStores, totalIndex, row.length, total, lojasStr.toString());
            }
            
            return new StockItemDTO(
                    (String) row[0],    // refplu
                    (String) row[1],    // marca
                    (String) row[2],    // descricao
                    lojas[0], lojas[1], lojas[2], lojas[3], lojas[4], lojas[5], lojas[6],
                    lojas[7], lojas[8], lojas[9], lojas[10], lojas[11], lojas[12], lojas[13],
                    total               // total
            );
        } catch (Exception e) {
            log.warn("Erro ao converter linha para StockItemDTO: {}", e.getMessage(), e);
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
    
    /**
     * Busca todas as lojas ativas ordenadas por LOJCOD
     * 
     * @return Lista de lojas ordenadas por código
     */
    public List<Store> getActiveStores() {
        log.debug("Buscando lojas ativas");
        List<Store> stores = storeRepository.findAll();
        // Ordenar por LOJCOD (id)
        stores.sort((s1, s2) -> Long.compare(s1.getId(), s2.getId()));
        log.debug("Lojas encontradas: {}", stores.size());
        return stores;
    }
}
