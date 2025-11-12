package com.sysconard.business.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Record para requisição de busca de estoque crítico.
 * Usado no Business API (Java 17) - DTOs simples usam Records.
 * 
 * @param refplu Filtro opcional por REFPLU
 * @param descricao Filtro opcional por descrição (busca também em grupo e marca)
 * @param grupo Filtro opcional por grupo
 * @param marca Filtro opcional por marca
 * @param page Número da página (base 0)
 * @param size Tamanho da página
 * @param sortBy Campo para ordenação (padrão: diferenca)
 * @param sortDir Direção da ordenação (padrão: desc)
 * 
 * @author Sysconard Business API
 * @version 1.0
 */
public record CriticalStockSearchRequest(
    
    @Size(max = 6, message = "REFPLU deve ter no máximo 6 caracteres")
    String refplu,
    
    @Size(max = 100, message = "Descrição deve ter no máximo 100 caracteres")
    String descricao,
    
    @Size(max = 100, message = "Grupo deve ter no máximo 100 caracteres")
    String grupo,
    
    @Size(max = 100, message = "Marca deve ter no máximo 100 caracteres")
    String marca,
    
    @Min(value = 0, message = "Número da página deve ser >= 0")
    Integer page,
    
    @Min(value = 1, message = "Tamanho da página deve ser >= 1")
    @Max(value = 1000, message = "Tamanho da página deve ser <= 1000")
    Integer size,
    
    @Pattern(regexp = "descricaoGrupo|codigoPartNumber|descricaoMarca|refplu|descricaoProduto|custoReposicao|precoVenda|vendas90Dias|vendas60Dias|vendas30Dias|vendasMesAtual|estoque|mediaMensal|diferenca",
             message = "Campo de ordenação inválido")
    String sortBy,
    
    @Pattern(regexp = "asc|desc", flags = Pattern.Flag.CASE_INSENSITIVE,
             message = "Direção de ordenação deve ser 'asc' ou 'desc'")
    String sortDir
) {
    /**
     * Construtor compacto com validações adicionais e defaults
     */
    public CriticalStockSearchRequest {
        page = (page != null) ? page : 0;
        size = (size != null) ? size : 20;
        sortBy = (sortBy != null && !sortBy.isEmpty()) ? sortBy : "diferenca";
        sortDir = (sortDir != null && !sortDir.isEmpty()) ? sortDir : "desc";
    }
}

