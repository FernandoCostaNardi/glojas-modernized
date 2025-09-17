package com.sysconard.legacy.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * Entidade que representa uma operação no sistema.
 * Mapeia a tabela 'OPERACAO' no banco de dados SQL Server.
 * 
 * @author Sysconard Legacy API
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "OPERACAO")
public class Operation implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Código único da operação - chave primária
     */
    @Id
    @Column(name = "OPECOD")
    private Long id;

    /**
     * Descrição da operação - campo obrigatório
     */
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "OPEDES", nullable = false)
    private String description;
}
