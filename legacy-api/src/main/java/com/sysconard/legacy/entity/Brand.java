package com.sysconard.legacy.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * Entidade que representa uma marca no sistema.
 * Mapeia a tabela 'MARCA' no banco de dados SQL Server.
 * 
 * @author Sysconard Legacy API
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "MARCA")
public class Brand implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Código único da marca - chave primária
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MARCOD", columnDefinition = "char(4)")
    private Long id;

    /**
     * Descrição/nome da marca - campo obrigatório
     */
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "MARDES", nullable = false)
    private String name;
}
