package com.sysconard.business.entity.product;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidade Product que representa um produto no sistema.
 * Utiliza Lombok para reduzir boilerplate e melhorar manutenibilidade.
 * Usa UUID como chave primária para melhor distribuição e segurança.
 * Inclui campos de auditoria para rastreamento de criação e atualização.
 */
@Entity
@Table(name = "products")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    
    /**
     * Identificador único do produto.
     * Gerado automaticamente como UUID para melhor distribuição e segurança.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    /**
     * Código único de referência do produto.
     * Utilizado para identificação rápida e referência externa.
     * Formato: 6 dígitos (ex: "010984")
     */
    @Column(name = "product_ref_code", unique = true, nullable = false, length = 6)
    private String productRefCode;
    
    /**
     * Código do produto.
     * Utilizado para identificação do produto.
     * Formato: 6 dígitos (ex: "011930")
     */
    @Column(name = "product_code", nullable = false, length = 6)
    private String productCode;
    
    /**
     * Seção do produto.
     * Representa a seção/categoria do produto (ex: "INFORMATICA").
     */
    @Column(length = 50)
    private String section;
    
    /**
     * Grupo do produto.
     * Representa o grupo/categoria do produto (ex: "MOUSE").
     */
    @Column(name = "\"group\"", length = 50, columnDefinition = "VARCHAR(50)")
    private String group;
    
    /**
     * Subgrupo do produto.
     * Representa o subgrupo/categoria do produto (ex: "MOUSE USB C/FIO").
     */
    @Column(length = 50)
    private String subgroup;
    
    /**
     * Marca do produto.
     * Representa a marca do produto (ex: "MAXPRINT").
     */
    @Column(length = 50)
    private String brand;
    
    /**
     * Descrição completa do produto.
     * Representa a descrição detalhada do produto.
     */
    @Column(name = "product_description", length = 250)
    private String productDescription;
    
    /**
     * Data e hora de criação do registro.
     * Preenchida automaticamente na primeira persistência.
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    /**
     * Data e hora da última atualização do registro.
     * Atualizada automaticamente a cada modificação.
     */
    @Column(name = "updated_at", nullable = false)
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

