package com.sysconard.legacy.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.sysconard.legacy.entity.store.Store;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Entidade que representa um preço no sistema.
 * Mapeia a tabela 'PRECO' no banco de dados SQL Server.
 * 
 * @author Sysconard Legacy API
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "PRECO")
public class Price implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Código PLU da referência - chave primária
     */
    @Id
    @NotNull
    @Size(min = 1, max = 6)
    @Column(name = "REFPLU", columnDefinition = "char(6)", nullable = false)
    private String refplu;

    /**
     * Loja associada ao preço - relacionamento obrigatório
     */
    @NotNull
    @ManyToOne
    @JoinColumn(name = "LOJCOD", nullable = false)
    private Store store;

    /**
     * Preço de venda do produto
     */
    @Column(name = "PRCVDA1")
    private BigDecimal precoVenda;
}

