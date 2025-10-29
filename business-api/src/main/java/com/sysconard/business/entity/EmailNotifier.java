package com.sysconard.business.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidade EmailNotifier que representa as configurações de notificação por email.
 * Utiliza Lombok para reduzir boilerplate e melhorar manutenibilidade.
 * Usa UUID como chave primária para melhor distribuição e segurança.
 * Inclui campos de auditoria para rastreamento de criação e atualização.
 * Permite configurar diferentes tipos de notificações de vendas por email.
 */
@Entity
@Table(name = "email_notifiers")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailNotifier {
    
    /**
     * Identificador único da configuração de notificação.
     * Gerado automaticamente como UUID para melhor distribuição e segurança.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    /**
     * Endereço de email para notificações.
     * Utilizado para envio de notificações de vendas.
     */
    @Column(nullable = false, length = 255)
    private String email;
    
    /**
     * Flag para notificações de vendas diárias.
     * Quando true, o sistema enviará notificações diárias de vendas.
     */
    @Column(name = "daily_sell_notifier", nullable = false)
    @Builder.Default
    private boolean dailySellNotifier = false;
    
    /**
     * Flag para notificações de vendas mensais.
     * Quando true, o sistema enviará notificações mensais de vendas.
     */
    @Column(name = "daily_month_notifier", nullable = false)
    @Builder.Default
    private boolean dailyMonthNotifier = false;
    
    /**
     * Flag para notificações de vendas anuais.
     * Quando true, o sistema enviará notificações anuais de vendas.
     */
    @Column(name = "month_year_notifier", nullable = false)
    @Builder.Default
    private boolean monthYearNotifier = false;
    
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
