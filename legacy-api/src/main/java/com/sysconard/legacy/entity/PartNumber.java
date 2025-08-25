package com.sysconard.legacy.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * Entidade que representa um part number no sistema.
 * Mapeia a tabela 'REFERENCIA_FABRICANTE' no banco de dados SQL Server.
 * 
 * @author Sysconard Legacy API
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "REFERENCIA_FABRICANTE")
public class PartNumber implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Código do part number - chave primária
     */
    @Id
    @NotNull
    @Size(min = 1, max = 30)
    @Column(name = "REFCOD", columnDefinition = "char(30)", nullable = false)
    private String id;

    /**
     * Referência associada ao part number - relacionamento obrigatório
     */
    @NotNull
    @OneToOne
    @JoinColumn(name = "REFPLU", nullable = false)
    private Reference reference;
}
