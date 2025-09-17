package com.sysconard.business.entity.store;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidade Store que representa uma loja no sistema.
 * Utiliza Lombok para reduzir boilerplate e melhorar manutenibilidade.
 * Usa UUID como chave primária para melhor distribuição e segurança.
 * Inclui campos de auditoria para rastreamento de criação e atualização.
 */
@Entity
@Table(name = "stores")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Store {
    
    /**
     * Identificador único da loja.
     * Gerado automaticamente como UUID para melhor distribuição e segurança.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    /**
     * Código único da loja.
     * Utilizado para identificação rápida e referência externa.
     */
    @Column(unique = true, nullable = false, length = 6)
    private String code;
    
    /**
     * Nome da loja.
     * Representa o nome comercial ou fantasia da loja.
     */
    @Column(nullable = false, length = 255)
    private String name;
    
    /**
     * Cidade onde a loja está localizada.
     * Utilizado para organização geográfica e relatórios.
     */
    @Column(nullable = false, length = 100)
    private String city;

    /**
     * Status da loja.
     * Utilizado para indicar se a loja está ativa ou inativa.
     */
    @Column(nullable = false)
    private boolean status;
    
    
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
