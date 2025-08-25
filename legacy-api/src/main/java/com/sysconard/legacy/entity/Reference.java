package com.sysconard.legacy.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * Entidade que representa uma referência no sistema.
 * Mapeia a tabela 'REFERENCIA' no banco de dados SQL Server.
 * 
 * @author Sysconard Legacy API
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "REFERENCIA")
public class Reference implements Serializable {

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
     * Produto associado à referência - relacionamento obrigatório
     */
    @NotNull
    @OneToOne
    @JoinColumn(name = "PROCOD", nullable = false)
    private Product product;

    /**
     * PartNumber associado à referência
     */
    @OneToOne(mappedBy = "reference")
    @JoinColumn(name = "REFPLU")
    private PartNumber partNumber;

    /**
     * Custo associado à referência - carregamento lazy
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REFPLU")
    private Cost cost;
}
