package com.sysconard.legacy.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * Entidade que representa um grupo no sistema.
 * Mapeia a tabela 'GRUPO' no banco de dados SQL Server.
 * 
 * @author Sysconard Legacy API
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "GRUPO")
public class Group implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Código único do grupo - chave primária
     */
    @Id
    @Column(name = "GRPCOD", columnDefinition = "char(4)")
    private Long id;

    /**
     * Seção à qual o grupo pertence - relacionamento obrigatório
     */
    @NotNull
    @ManyToOne
    @JoinColumn(name = "SECCOD", nullable = false)
    private Section section;

    /**
     * Descrição/nome do grupo - campo obrigatório
     */
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "GRPDES", nullable = false)
    private String name;
}
