package com.sysconard.legacy.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * Entidade que representa um cargo no sistema.
 * Mapeia a tabela 'CARGO' no banco de dados SQL Server.
 * 
 * @author Sysconard Legacy API
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "CARGO")
public class JobPosition implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Código único do cargo - chave primária
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CARCOD", columnDefinition = "char(3)")
    private Long id;

    /**
     * Nome do cargo - campo obrigatório
     */
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "CARDES", nullable = false)
    private String name;
}
