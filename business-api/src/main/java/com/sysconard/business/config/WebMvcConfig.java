package com.sysconard.business.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuração do Spring Web MVC para resolver problemas de roteamento.
 * Garante que os controllers sejam mapeados corretamente e que recursos estáticos
 * não interfiram com as rotas da API.
 */
@Configuration
@EnableWebMvc
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * Configuração de CORS para permitir requisições do frontend.
     * Mantém consistência com a configuração de segurança.
     */
    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("http://localhost:3000")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    /**
     * Configuração de recursos estáticos para evitar conflitos com rotas da API.
     * Define explicitamente quais recursos são estáticos para evitar que o Spring
     * tente tratar rotas da API como recursos estáticos.
     */
    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        // Configura recursos estáticos específicos
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
        
        registry.addResourceHandler("/public/**")
                .addResourceLocations("classpath:/public/");
        
        // Desabilita o handler padrão para evitar conflitos
        registry.setOrder(Integer.MAX_VALUE);
    }
}
