package com.sysconard.legacy.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import com.sysconard.legacy.entity.store.Store;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Entidade que representa um item de entrada no sistema.
 * Mapeia a tabela 'ITEM_ENTRADA' no banco de dados SQL Server.
 * 
 * @author Sysconard Legacy API
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ITEM_ENTRADA")
public class IncomingItems implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Código único do item de entrada - chave primária
     */
    @Id
    @Column(name = "ENTCOD", columnDefinition = "char(6)")
    private Long id;

    /**
     * Loja associada ao item de entrada
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LOJCOD")
    private Store store;

    /**
     * Referência do produto de entrada
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REFPLU")
    private Reference reference;

    /**
     * Quantidade total de itens de entrada
     */
    @Column(name = "ITEQTDEMB")
    private Integer totalItems;

    /**
     * Valor total dos itens de entrada
     */
    @Column(name = "ITEVLREMB")
    private BigDecimal itemsTotalValue;

    /**
     * Documento associado ao item de entrada
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DOCCOD")
    private Documento documento;
}
