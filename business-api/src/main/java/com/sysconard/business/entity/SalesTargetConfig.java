package com.sysconard.business.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidade SalesTargetConfig que representa configurações de metas de vendas
 * e percentuais de comissão por loja e competência.
 * Utiliza Lombok para reduzir boilerplate e melhorar manutenibilidade.
 * Usa UUID como chave primária para melhor distribuição e segurança.
 * Inclui campos de auditoria para rastreamento de criação e atualização.
 * Permite múltiplas configurações para a mesma loja e competência, 
 * possibilitando metas escalonadas (ex: primeira meta 100 com 0,8%, segunda meta 200 com 1,2%).
 */
@Entity
@Table(name = "sales_targets_config")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalesTargetConfig {
    
    /**
     * Identificador único da configuração.
     * Gerado automaticamente como UUID para melhor distribuição e segurança.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    /**
     * Código da loja.
     * Utilizado para identificação da loja.
     * Formato: 6 dígitos (ex: "000001")
     */
    @Column(name = "store_code", nullable = false, length = 6)
    @NotBlank(message = "Código da loja é obrigatório")
    @Size(min = 6, max = 6, message = "Código da loja deve ter exatamente 6 caracteres")
    private String storeCode;
    
    /**
     * Data de competência no formato MM/YYYY.
     * Utilizado para identificar o período da configuração.
     * Formato: MM/YYYY (ex: "01/2024")
     */
    @Column(name = "competence_date", nullable = false, length = 7)
    @NotBlank(message = "Data de competência é obrigatória")
    @Pattern(regexp = "^(0[1-9]|1[0-2])/\\d{4}$", message = "Data de competência deve estar no formato MM/YYYY (ex: 01/2024)")
    private String competenceDate;
    
    /**
     * Meta de venda da loja.
     * Representa o valor monetário da meta de vendas para a loja.
     */
    @Column(name = "store_sales_target", nullable = false, precision = 15, scale = 2)
    @NotNull(message = "Meta de venda da loja é obrigatória")
    @DecimalMin(value = "0.0", inclusive = true, message = "Meta de venda da loja deve ser maior ou igual a zero")
    private BigDecimal storeSalesTarget;
    
    /**
     * Percentual de comissão coletiva.
     * Representa o percentual de comissão que o gerente irá ganhar.
     * Permite valores acima de 100 para incentivos especiais.
     */
    @Column(name = "collective_commission_percentage", nullable = false, precision = 5, scale = 2)
    @NotNull(message = "Percentual de comissão coletiva é obrigatório")
    @DecimalMin(value = "0.0", inclusive = true, message = "Percentual de comissão coletiva deve ser maior ou igual a zero")
    private BigDecimal collectiveCommissionPercentage;
    
    /**
     * Meta de venda individual.
     * Representa o valor monetário da meta de vendas individual para vendedores.
     */
    @Column(name = "individual_sales_target", nullable = false, precision = 15, scale = 2)
    @NotNull(message = "Meta de venda individual é obrigatória")
    @DecimalMin(value = "0.0", inclusive = true, message = "Meta de venda individual deve ser maior ou igual a zero")
    private BigDecimal individualSalesTarget;
    
    /**
     * Percentual de comissão individual.
     * Representa o percentual de comissão que o vendedor irá ganhar.
     * Permite valores acima de 100 para incentivos especiais.
     */
    @Column(name = "individual_commission_percentage", nullable = false, precision = 5, scale = 2)
    @NotNull(message = "Percentual de comissão individual é obrigatório")
    @DecimalMin(value = "0.0", inclusive = true, message = "Percentual de comissão individual deve ser maior ou igual a zero")
    private BigDecimal individualCommissionPercentage;
    
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

