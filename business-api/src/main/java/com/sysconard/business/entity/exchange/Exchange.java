package com.sysconard.business.entity.exchange;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidade Exchange que representa uma troca realizada no sistema.
 * Utiliza Lombok para reduzir boilerplate e melhorar manutenibilidade.
 * Usa UUID como chave primária para melhor distribuição e segurança.
 * Inclui campos de auditoria para rastreamento de criação e atualização.
 */
@Entity
@Table(name = "exchanges")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Exchange {
    
    /**
     * Identificador único da troca.
     * Gerado automaticamente como UUID para melhor distribuição e segurança.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    /**
     * Código do documento da troca.
     * Utilizado para identificar o documento da troca.
     * Formato: 6 dígitos (ex: "123456")
     */
    @Column(name = "document_code", nullable = false, length = 6)
    private String documentCode;
    
    /**
     * Código da loja onde a troca foi realizada.
     * Referência ao código da loja (FK para stores.code).
     * Formato: 6 dígitos (ex: "000011")
     */
    @Column(name = "store_code", nullable = false, length = 6)
    private String storeCode;
    
    /**
     * Código da operação da troca.
     * Identifica o tipo de operação realizada.
     * Formato: até 10 caracteres (ex: "000015")
     */
    @Column(name = "operation_code", nullable = false, length = 10)
    private String operationCode;
    
    /**
     * Código de origem da troca.
     * Identifica a origem da troca.
     * Formato: até 10 caracteres (ex: "051")
     */
    @Column(name = "origin_code", nullable = false, length = 10)
    private String originCode;
    
    /**
     * Código do colaborador que realizou a troca.
     * Referência ao código do colaborador (FK para collaborators.employee_code).
     * Formato: 6 dígitos (ex: "000138")
     */
    @Column(name = "employee_code", nullable = false, length = 6)
    private String employeeCode;
    
    /**
     * Número do documento/nota da troca.
     * Número da nota fiscal relacionada à troca.
     * Formato: até 20 caracteres
     */
    @Column(name = "document_number", length = 20)
    private String documentNumber;
    
    /**
     * Chave NFE do documento da troca.
     * Chave de acesso da nota fiscal eletrônica.
     * Formato: 44 caracteres
     */
    @Column(name = "nfe_key", length = 44)
    private String nfeKey;
    
    /**
     * Data de emissão do documento da troca.
     * Representa quando a troca foi realizada.
     */
    @Column(name = "issue_date", nullable = false)
    private LocalDateTime issueDate;
    
    /**
     * Observação do documento.
     * Pode conter informações sobre nova venda ou chave NFE que serão processadas.
     */
    @Column(columnDefinition = "TEXT")
    private String observation;
    
    /**
     * Número da nova venda extraído da observação.
     * Extraído através do processamento da observação.
     * Formato: até 20 caracteres
     */
    @Column(name = "new_sale_number", length = 20)
    private String newSaleNumber;
    
    /**
     * Chave NFE da venda extraída da observação.
     * Extraída através do processamento da observação.
     * Formato: 44 caracteres
     */
    @Column(name = "new_sale_nfe_key", length = 44)
    private String newSaleNfeKey;
    
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

