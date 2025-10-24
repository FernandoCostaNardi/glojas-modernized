package com.sysconard.legacy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Aplicação Legacy API - Sistema Glojas
 * Esta API é responsável por expor dados do SQL Server existente
 * como endpoints REST para serem consumidos pela Business API.
 * Características:
 * - Read-only access ao SQL Server
 * - Queries otimizadas para performance
 * - Estrutura simples focada em integração
 * 
 * @author Equipe Desenvolvimento Glojas
 * @version 1.0.0
 * @since 2024
 */
@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.sysconard.legacy.repository")
@EnableTransactionManagement // Habilita gerenciamento de transações
public class LegacyApiApplication {

    public static void main(String[] args) {
        System.out.println("=== Iniciando Legacy API - Sistema Glojas ===");
        System.out.println("Porta: 8087");
        System.out.println("Context Path: /api/legacy");
        System.out.println("Conectando com SQL Server...");
        
        SpringApplication.run(LegacyApiApplication.class, args);
        
        System.out.println("✅ Legacy API iniciada com sucesso!");
        System.out.println("🌐 URL Base: http://localhost:8087/api/legacy");
        System.out.println("📊 Health Check: http://localhost:8087/api/legacy/actuator/health");
    }
}
