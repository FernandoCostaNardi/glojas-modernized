package com.sysconard.business.controller;

import com.sysconard.business.entity.DatabaseTest;
import com.sysconard.business.repository.DatabaseTestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/database-test")
public class DatabaseTestController {
    
    @Autowired
    private DatabaseTestRepository databaseTestRepository;
    
    @PostMapping("/connection-test")
    public ResponseEntity<Map<String, Object>> testDatabaseConnection() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Teste 1: Inserir um registro
            DatabaseTest testEntity = new DatabaseTest("Teste de conexão PostgreSQL - " + System.currentTimeMillis());
            DatabaseTest savedEntity = databaseTestRepository.save(testEntity);
            response.put("insertSuccess", true);
            response.put("insertedRecord", Map.of(
                "id", savedEntity.getId(),
                "message", savedEntity.getTestMessage(),
                "createdAt", savedEntity.getCreatedAt()
            ));
            
            // Teste 2: Verificar se o registro foi inserido
            boolean exists = databaseTestRepository.existsById(savedEntity.getId());
            response.put("recordExists", exists);
            
            // Teste 3: Remover o registro
            databaseTestRepository.deleteById(savedEntity.getId());
            response.put("deleteSuccess", true);
            
            // Teste 4: Verificar se o registro foi removido
            boolean stillExists = databaseTestRepository.existsById(savedEntity.getId());
            response.put("recordDeletedSuccessfully", !stillExists);
            
            // Resultado final
            response.put("overallTestResult", "SUCCESS");
            response.put("message", "Todos os testes de conexão PostgreSQL passaram com sucesso!");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("overallTestResult", "FAILURE");
            response.put("error", e.getMessage());
            response.put("message", "Falha nos testes de conexão PostgreSQL");
            
            return ResponseEntity.status(500).body(response);
        }
    }
}
