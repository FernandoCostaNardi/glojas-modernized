package com.sysconard.legacy.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Entidade que representa o estoque de produtos por loja.
 * Mapeia a tabela 'estoque' no banco de dados SQL Server.
 * 
 * @author Sysconard Legacy API
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "estoque")
@IdClass(StockId.class)
public class Stock implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Código PLU da referência - chave primária composta
     */
    @Id
    @Column(name = "refplu", columnDefinition = "char(20)")
    private String refplu;

    /**
     * Código da loja - chave primária composta
     */
    @Id
    @Column(name = "lojcod", columnDefinition = "char(6)")
    private Long lojcod;

    /**
     * Código do local - chave primária composta
     */
    @Id
    @Column(name = "loccod", columnDefinition = "char(6)")
    private Long loccod;

    /**
     * Quantidade total em estoque
     */
    @NotNull
    @Column(name = "esttot", nullable = false)
    private Long esttot;
}
