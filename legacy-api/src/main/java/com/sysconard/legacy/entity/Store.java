package com.sysconard.legacy.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * Entidade que representa uma loja no sistema.
 * Mapeia a tabela 'LOJA' no banco de dados SQL Server.
 * 
 * @author Sysconard Legacy API
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "LOJA")
public class Store implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Código único da loja - chave primária
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LOJCOD", columnDefinition = "char(6)")
    private Long id;

    /**
     * Nome fantasia da loja - campo obrigatório
     */
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "LOJFAN", nullable = false)
    private String name;
}
