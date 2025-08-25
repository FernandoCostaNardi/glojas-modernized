package com.sysconard.legacy.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * Entidade que representa um subgrupo no sistema.
 * Mapeia a tabela 'SUBGRUPO' no banco de dados SQL Server.
 * 
 * @author Sysconard Legacy API
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "SUBGRUPO")
public class Subgroup implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Código único do subgrupo - chave primária
     */
    @Id
    @Column(name = "SBGCOD", columnDefinition = "char(4)")
    private Long id;

    /**
     * Grupo ao qual o subgrupo pertence - relacionamento obrigatório
     */
    @NotNull
    @ManyToOne
    @JoinColumn(name = "GRPCOD", nullable = false)
    private Group group;

    /**
     * Descrição/nome do subgrupo - campo obrigatório
     */
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "SBGDES", nullable = false)
    private String name;
}
