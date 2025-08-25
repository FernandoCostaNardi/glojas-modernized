package com.sysconard.legacy.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * Entidade que representa uma seção no sistema.
 * Mapeia a tabela 'SECAO' no banco de dados SQL Server.
 * 
 * @author Sysconard Legacy API
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "SECAO")
public class Section implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Código único da seção - chave primária
     */
    @Id
    @Column(name = "SECCOD", columnDefinition = "char(4)")
    private Long id;

    /**
     * Descrição/nome da seção - campo obrigatório
     */
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "SECDES", nullable = false)
    private String name;
}
