package com.sysconard.legacy.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuração web para registrar interceptors
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private ReadOnlyInterceptor readOnlyInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Registra o interceptor de somente leitura para todas as rotas
        registry.addInterceptor(readOnlyInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/actuator/**"); // Permite endpoints de monitoramento
    }
}
