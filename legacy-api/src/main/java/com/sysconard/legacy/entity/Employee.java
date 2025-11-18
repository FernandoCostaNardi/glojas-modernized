package com.sysconard.legacy.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.sysconard.legacy.entity.store.Store;

import javax.validation.constraints.Email;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Entidade que representa um funcionário no sistema.
 * Mapeia a tabela 'FUNCIONARIO' no banco de dados SQL Server.
 * 
 * @author Sysconard Legacy API
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "FUNCIONARIO")
public class Employee implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Código único do funcionário - chave primária
     */
    @Id
    @Column(name = "FUNCOD", columnDefinition = "char(6)")
    private Long id;

    /**
     * Cargo do funcionário
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CARCOD")
    private JobPosition jobPosition;

    /**
     * Loja onde o funcionário trabalha
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LOJCOD")
    private Store store;

    /**
     * Dados pessoais do funcionário
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AGECOD")
    private Person person;

    /**
     * Nome do funcionário - campo obrigatório
     */
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "FUNDES", nullable = false)
    private String name;

    /**
     * Apelido do funcionário
     */
    @Size(max = 50)
    @Column(name = "FUNAPE")
    private String nickname;

    /**
     * Percentual de comissão do funcionário
     */
    @Column(name = "FUNPERCOM1")
    private BigDecimal commissionPercentage;

    /**
     * Email do funcionário
     */
    @Email
    @Size(max = 100)
    @Column(name = "FUNMAIL")
    private String email;

    /**
     * Data de nascimento do funcionário
     */
    @Column(name = "FUNNAS")
    @Temporal(TemporalType.DATE)
    private Date birthDate;

    /**
     * Status ativo do funcionário (S/N)
     */
    @Size(max = 1)
    @Column(name = "FUNATV", columnDefinition = "char(1)")
    private String active;

    /**
     * Sexo do funcionário
     */
    @Size(max = 1)
    @Column(name = "FUNSEX", columnDefinition = "char(1)")
    private String gender;
}
