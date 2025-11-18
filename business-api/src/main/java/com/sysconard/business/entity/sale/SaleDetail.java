package com.sysconard.business.entity.sale;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidade SaleDetail que representa um item detalhado de venda no sistema.
 * Utiliza Lombok para reduzir boilerplate e melhorar manutenibilidade.
 * Usa UUID como chave primária para melhor distribuição e segurança.
 * Inclui campos de auditoria para rastreamento de criação e atualização.
 */
@Entity
@Table(name = "sale_details")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaleDetail {
    
    /**
     * Identificador único do item de venda.
     * Gerado automaticamente como UUID para melhor distribuição e segurança.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    /**
     * Data e hora da venda.
     * Representa quando a venda foi realizada.
     */
    @Column(name = "sale_date", nullable = false)
    private LocalDateTime saleDate;
    
    /**
     * Código único da venda.
     * Utilizado para identificar a venda completa.
     * Formato: até 10 caracteres (ex: "037955")
     */
    @Column(name = "sale_code", nullable = false, length = 10)
    private String saleCode;
    
    /**
     * Sequência do item na venda.
     * Representa a ordem do item dentro da venda.
     */
    @Column(name = "item_sequence", nullable = false)
    private Integer itemSequence;
    
    /**
     * Código do colaborador que realizou a venda.
     * Referência ao código do colaborador (FK para collaborators.employee_code).
     * Formato: 6 dígitos (ex: "000138")
     */
    @Column(name = "collaborator_code", nullable = false, length = 6)
    private String collaboratorCode;
    
    /**
     * Código da loja onde a venda foi realizada.
     * Referência ao código da loja (FK para stores.code).
     * Formato: 6 dígitos (ex: "000011")
     */
    @Column(name = "store_code", nullable = false, length = 6)
    private String storeCode;
    
    /**
     * Código de referência do produto vendido.
     * Referência ao código de referência do produto (FK para products.product_ref_code).
     * Formato: 6 dígitos (ex: "010984")
     */
    @Column(name = "product_ref_code", nullable = false, length = 6)
    private String productRefCode;
    
    /**
     * Código NCM do produto.
     * Nomenclatura Comum do Mercosul.
     * Formato: 8 caracteres (ex: "84716053")
     */
    @Column(length = 8)
    private String ncm;
    
    /**
     * Quantidade de produtos vendidos.
     * Representa a quantidade do item na venda.
     */
    @Column(nullable = false)
    private Integer quantity;
    
    /**
     * Preço unitário do produto.
     * Representa o preço de uma unidade do produto.
     * Formato: NUMERIC(15,2)
     */
    @Column(name = "unit_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal unitPrice;
    
    /**
     * Preço total do item.
     * Representa o valor total do item (quantity * unitPrice).
     * Formato: NUMERIC(15,2)
     */
    @Column(name = "total_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalPrice;
    
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

