package com.sysconard.business.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;



/**
 * Configuração do WebClient para comunicação com APIs externas
 * 
 * @author Business API
 * @version 1.0
 */
@Configuration
public class WebClientConfig {
    
    @Value("${legacy-api.base-url:http://localhost:8087}")
    private String legacyApiBaseUrl;
    
    @Value("${legacy-api.context-path:/api/legacy}")
    private String legacyApiContextPath;
    
    @Value("${legacy-api.timeout:30}")
    private Integer timeoutSeconds;
    
    /**
     * Bean do WebClient configurado para comunicar com a Legacy API
     * 
     * @return WebClient configurado
     */
    @Bean("legacyApiWebClient")
    public WebClient legacyApiWebClient() {
        // URL correta para Legacy API SEM o context-path (já está incluído no endpoint)
        String correctBaseUrl = legacyApiBaseUrl;
        
        System.out.println("=== WEBCLIENT CONFIG DEBUG ===");
        System.out.println("legacyApiBaseUrl value: " + legacyApiBaseUrl);
        System.out.println("legacyApiContextPath value: " + legacyApiContextPath);
        System.out.println("Full URL would be: " + legacyApiBaseUrl + legacyApiContextPath);
        System.out.println("CORRECT URL: " + correctBaseUrl);
        System.out.println("================================");
        
        return WebClient.builder()
                .baseUrl(correctBaseUrl + legacyApiContextPath)  // URL completa COM context-path
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(16 * 1024 * 1024)) // 16MB buffer
                .build();
    }
    
    /**
     * Bean genérico do WebClient para outras integrações futuras
     * 
     * @return WebClient genérico
     */
    @Bean("genericWebClient")
    public WebClient genericWebClient() {
        return WebClient.builder()
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(16 * 1024 * 1024)) // 16MB buffer
                .build();
    }
}
