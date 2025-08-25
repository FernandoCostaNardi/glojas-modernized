package com.sysconard.legacy.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * Entidade que representa um produto no sistema.
 * Mapeia a tabela 'PRODUTO' no banco de dados SQL Server.
 * 
 * @author Sysconard Legacy API
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "PRODUTO")
public class Product implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Código único do produto - chave primária
     */
    @Id
    @Column(name = "PROCOD", columnDefinition = "char(8)")
    private Long id;

    /**
     * Funcionário responsável pelo produto
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FUNCOD")
    private Employee employee;

    /**
     * Marca do produto
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MARCOD")
    private Brand brand;

    /**
     * Subgrupo do produto
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SBGCOD")
    private Subgroup subgroup;

    /**
     * Grupo do produto
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GRPCOD")
    private Group group;

    /**
     * Seção do produto
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SECCOD")
    private Section section;

    /**
     * Referência associada ao produto
     */
    @OneToOne(mappedBy = "product")
    @JoinColumn(name = "PROCOD")
    private Reference reference;

    /**
     * Descrição do produto - campo obrigatório
     */
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "PRODES", nullable = false)
    private String descricao;

    /**
     * Código NCM do produto
     */
    @Size(max = 8)
    @Column(name = "PRONCM", columnDefinition = "char(8)")
    private String ncm;
}
