package com.sysconard.business.entity.sell;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidade DailySell que representa as vendas diárias de uma loja.
 * Utiliza Lombok para reduzir boilerplate e melhorar manutenibilidade.
 * Usa UUID como chave primária para melhor distribuição e segurança.
 * Inclui campos de auditoria para rastreamento de criação e atualização.
 * Campos monetários utilizam BigDecimal para precisão em cálculos financeiros.
 */
@Entity
@Table(name = "daily_sells")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailySell {
    
    /**
     * Identificador único da venda diária.
     * Gerado automaticamente como UUID para melhor distribuição e segurança.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    /**
     * Identificador único da loja.
     * Referência para a loja que realizou as vendas.
     */
    @Column(name = "store_id", nullable = false)
    private UUID storeId;
    
    /**
     * Código da loja.
     * Utilizado para identificação rápida e referência externa.
     */
    @Column(name = "store_code", nullable = false, length = 10)
    private String storeCode;
    
    /**
     * Nome da loja.
     * Representa o nome comercial ou fantasia da loja.
     */
    @Column(name = "store_name", nullable = false, length = 255)
    private String storeName;
    
    /**
     * Data das vendas.
     * Representa o dia específico das vendas registradas.
     */
    @Column(nullable = false)
    private LocalDate date;
    
    /**
     * Valor total das vendas DANFE.
     * Representa o valor total das vendas com nota fiscal eletrônica.
     */
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal danfe;
    
    /**
     * Valor total das vendas PDV.
     * Representa o valor total das vendas no ponto de venda.
     */
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal pdv;
    
    /**
     * Valor total das trocas.
     * Representa o valor total das operações de troca realizadas.
     */
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal exchange;
    
    /**
     * Valor total geral.
     * Representa o valor total consolidado de todas as vendas do dia.
     */
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal total;
    
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
