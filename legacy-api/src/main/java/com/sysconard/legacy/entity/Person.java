package com.sysconard.legacy.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.validation.constraints.Email;
import java.io.Serializable;

/**
 * Entidade que representa uma pessoa no sistema.
 * Mapeia a tabela 'AGENTE' no banco de dados SQL Server.
 * 
 * @author Sysconard Legacy API
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "AGENTE")
public class Person implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Código único da pessoa - chave primária
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AGECOD", columnDefinition = "char(6)")
    private Long id;

    /**
     * Nome da pessoa - campo obrigatório
     */
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "AGEDES", nullable = false)
    private String name;

    /**
     * Nome fantasia da pessoa
     */
    @Size(max = 100)
    @Column(name = "AGEFAN")
    private String nickname;

    /**
     * Email da pessoa
     */
    @Email
    @Size(max = 100)
    @Column(name = "AGECORELE")
    private String email;

    /**
     * CPF ou CNPJ da pessoa
     */
    @Size(max = 18)
    @Column(name = "AGECGCCPF")
    private String cpfOuCnpj;
}
