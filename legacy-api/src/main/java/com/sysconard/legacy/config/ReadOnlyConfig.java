package com.sysconard.legacy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.persistence.EntityManagerFactory;

/**
 * Configuração para garantir que a aplicação seja somente leitura.
 * Bloqueia todas as operações de escrita no banco de dados.
 */
@Configuration
@EnableTransactionManagement
public class ReadOnlyConfig {

    /**
     * Configura o TransactionManager para ser somente leitura
     */
    @Bean
    @Primary
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager(entityManagerFactory);
        
        // Configuração para transações somente leitura
        transactionManager.setDefaultTimeout(30); // 30 segundos timeout
        transactionManager.setRollbackOnCommitFailure(true);
        
        return transactionManager;
    }
}
