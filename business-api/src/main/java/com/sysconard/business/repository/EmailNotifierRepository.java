package com.sysconard.business.repository;

import com.sysconard.business.entity.EmailNotifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository para operações de EmailNotifier
 * Seguindo princípios de Clean Code com responsabilidade única
 */
@Repository
public interface EmailNotifierRepository extends JpaRepository<EmailNotifier, UUID> {
    
    // Log para verificar se o repository está sendo detectado
    default void logRepositoryDetection() {
        System.out.println("=== EmailNotifierRepository detectado ===");
    }
    
    /**
     * Busca um notificador por email
     * @param email Email a ser buscado
     * @return Optional com o notificador encontrado
     */
    Optional<EmailNotifier> findByEmail(String email);
    
    /**
     * Verifica se existe um notificador com o email informado
     * @param email Email a ser verificado
     * @return true se existe, false caso contrário
     */
    boolean existsByEmail(String email);
    
    /**
     * Busca todos os notificadores com paginação
     * @param pageable Configurações de paginação
     * @return Página de notificadores
     */
    @NonNull
    Page<EmailNotifier> findAll(@NonNull Pageable pageable);
    
    /**
     * Busca todos os notificadores que têm notificação de vendas diárias ativada
     * @return Lista de notificadores com dailySellNotifier = true
     */
    List<EmailNotifier> findByDailySellNotifierTrue();
    
    /**
     * Busca todos os notificadores que têm notificação de vendas mensais ativada
     * @return Lista de notificadores com dailyMonthNotifier = true
     */
    List<EmailNotifier> findByDailyMonthNotifierTrue();
}
