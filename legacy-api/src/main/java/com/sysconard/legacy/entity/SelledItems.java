package com.sysconard.legacy.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import com.sysconard.legacy.entity.store.Store;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Entidade que representa um item vendido no sistema.
 * Mapeia a tabela 'ITEM_SAIDA' no banco de dados SQL Server.
 * 
 * @author Sysconard Legacy API
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ITEM_SAIDA")
public class SelledItems implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Código único do item vendido - chave primária
     */
    @Id
    @Column(name = "SAICOD", columnDefinition = "char(6)")
    private Long id;

    /**
     * Loja associada ao item vendido
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LOJCOD")
    private Store store;

    /**
     * Referência do produto vendido
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REFPLU")
    private Reference reference;

    /**
     * Funcionário responsável pela venda
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FUNCOD")
    private Employee employee;

    /**
     * Preço de venda do item
     */
    @Column(name = "ITSVLREMBREA")
    private BigDecimal salePrice;

    /**
     * Valor do desconto aplicado
     */
    @Column(name = "ITSVLRDCN")
    private BigDecimal discountValue;

    /**
     * Valor líquido do item (preço - desconto)
     */
    @Column(name = "ITSVLRLIQ")
    private BigDecimal netValue;

    /**
     * Sequência do item na venda
     */
    @Column(name = "ITSSEQ")
    private Integer sequency;

    /**
     * Valor total dos itens
     */
    @Column(name = "ITSQTDTOT")
    private BigDecimal totalItemsValue;

    /**
     * Documento associado ao item vendido
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DOCCOD")
    private Documento documento;
}
