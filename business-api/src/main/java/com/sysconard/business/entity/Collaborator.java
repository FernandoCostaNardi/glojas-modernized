package com.sysconard.business.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidade Collaborator que representa um colaborador no sistema.
 * Utiliza Lombok para reduzir boilerplate e melhorar manutenibilidade.
 * Usa UUID como chave primária para melhor distribuição e segurança.
 * Inclui campos de auditoria para rastreamento de criação e atualização.
 */
@Entity
@Table(name = "collaborators")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Collaborator {
    
    /**
     * Identificador único do colaborador.
     * Gerado automaticamente como UUID para melhor distribuição e segurança.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    /**
     * Código único do colaborador da Legacy API.
     * Utilizado para identificação rápida e referência externa.
     * Formato: 6 dígitos (ex: "000001")
     */
    @Column(name = "employee_code", unique = true, nullable = false, length = 6)
    private String employeeCode;
    
    /**
     * Código do cargo do colaborador.
     * Referência ao código do cargo na Legacy API.
     */
    @Column(name = "job_position_code", nullable = false, length = 6)
    private String jobPositionCode;
    
    /**
     * Código da loja onde o colaborador trabalha.
     * Referência ao código da loja na Legacy API.
     */
    @Column(name = "store_code", nullable = false, length = 6)
    private String storeCode;
    
    /**
     * Nome completo do colaborador.
     */
    @Column(nullable = false, length = 255)
    private String name;
    
    /**
     * Data de nascimento do colaborador.
     */
    @Column(name = "birth_date")
    private LocalDate birthDate;
    
    /**
     * Percentual de comissão do colaborador.
     */
    @Column(name = "commission_percentage", precision = 5, scale = 2)
    private BigDecimal commissionPercentage;
    
    /**
     * Email do colaborador.
     */
    @Column(length = 100)
    private String email;
    
    /**
     * Status ativo do colaborador (S para ativo, N para inativo).
     */
    @Column(length = 1)
    private String active;
    
    /**
     * Sexo do colaborador.
     */
    @Column(length = 1)
    private String gender;
    
    /**
     * Data e hora de criação do registro.
     * Preenchida automaticamente na primeira persistência.
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    /**
     * Data e hora da última atualização do registro.
     * Atualizada automaticamente a cada modificação.
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    /**
     * Método executado antes de uma persistência da entidade.
     * Define automaticamente os campos de auditoria com a data/hora atual.
     */
    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }
    
    /**
     * Método executado antes de uma atualização da entidade.
     * Atualiza automaticamente o campo updatedAt com a data/hora atual.
     */
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

