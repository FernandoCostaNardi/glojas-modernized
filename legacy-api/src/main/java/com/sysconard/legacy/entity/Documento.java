package com.sysconard.legacy.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Size;

import com.sysconard.legacy.entity.store.Store;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Entidade que representa um documento no sistema.
 * Mapeia a tabela 'DOCUMENTO' no banco de dados SQL Server.
 * 
 * @author Sysconard Legacy API
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "DOCUMENTO")
public class Documento implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Código único do documento - chave primária
     */
    @Id
    @Column(name = "DOCCOD", columnDefinition = "char(6)")
    private Long id;

    /**
     * Loja associada ao documento
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LOJCOD")
    private Store store;

    /**
     * Pessoa associada ao documento
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AGECOD")
    private Person person;

    /**
     * Funcionário associado ao documento
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FUNCOD")
    private Employee employee;

    /**
     * Código de origem do documento
     */
    @Size(max = 10)
    @Column(name = "ORICOD")
    private String origin;

    /**
     * Número do documento
     */
    @Size(max = 20)
    @Column(name = "DOCNUMDOC")
    private String documentNumber;

    /**
     * Valor total do documento
     */
    @Column(name = "DOCVLRTOT")
    private BigDecimal documentValue;

    /**
     * Status do documento
     */
    @Size(max = 1)
    @Column(name = "DOCSTA", columnDefinition = "char(1)")
    private String documentStatus;

    /**
     * Operação do documento
     */
    @Size(max = 10)
    @Column(name = "OPERATION")
    private String operation;

    /**
     * Status DANFE do documento
     */
    @Size(max = 1)
    @Column(name = "DOCSTANFE", columnDefinition = "char(1)")
    private String danfeStatus;

    /**
     * Data de emissão do documento
     */
    @Column(name = "DOCDATEMI")
    private Date issueDate;

    /**
     *  Itens vendidos
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DOCCOD")
    private SelledItems selledItems;

    /**
     *  Itens comprados
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DOCCOD")
    private IncomingItems incomingItems;    
}
