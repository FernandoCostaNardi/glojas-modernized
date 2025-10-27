package com.sysconard.legacy.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuração web para registrar interceptors e CORS
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private ReadOnlyInterceptor readOnlyInterceptor;

    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        // Registra o interceptor de somente leitura para todas as rotas
        // EXCETO para endpoints de sales (relatórios de consulta)
        registry.addInterceptor(readOnlyInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/actuator/**",  // Permite endpoints de monitoramento
                        "/sales/**"      // Permite endpoints de relatórios (POST de consulta)
                );
    }

    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .maxAge(3600);
    }
}
