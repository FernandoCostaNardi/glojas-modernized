package com.sysconard.business.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Configuração do Jackson para serialização de datas.
 * Garante que LocalDateTime seja serializado em formato ISO-8601 compatível com JavaScript.
 * Seguindo princípios de Clean Code com responsabilidade única.
 */
@Configuration
public class JacksonConfig {

    /**
     * Formato padrão para serialização de LocalDateTime.
     * Usa formato ISO-8601 que é amplamente suportado por JavaScript.
     */
    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

    /**
     * Configura o ObjectMapper para serialização correta de datas.
     * 
     * @return ObjectMapper configurado para datas
     */
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        
        // Registra o módulo JavaTime para suporte a LocalDateTime
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        
        // Configura o serializador para LocalDateTime
        javaTimeModule.addSerializer(LocalDateTime.class, 
            new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)));
        
        mapper.registerModule(javaTimeModule);
        
        // Desabilita a serialização de datas como timestamps
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        return mapper;
    }
}
