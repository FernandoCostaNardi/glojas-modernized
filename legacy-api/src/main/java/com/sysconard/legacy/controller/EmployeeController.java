package com.sysconard.legacy.controller;

import com.sysconard.legacy.dto.EmployeeDTO;
import com.sysconard.legacy.service.EmployeeControllerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller para operações com funcionários do sistema
 * 
 * Segue os princípios de Clean Code:
 * - Responsabilidade única: apenas roteamento HTTP
 * - Injeção de dependência via construtor
 * - Delegação de lógica para o serviço
 * - Documentação clara
 * - Tratamento adequado de códigos HTTP
 * 
 * @author Sysconard Legacy API
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeControllerService employeeControllerService;
    
    @Autowired
    public EmployeeController(EmployeeControllerService employeeControllerService) {
        this.employeeControllerService = employeeControllerService;
    }

    /**
     * Busca todos os funcionários ativos cadastrados no sistema
     * 
     * @return Lista de funcionários ativos com status 200 OK
     */
    @GetMapping("/active")
    public ResponseEntity<List<EmployeeDTO>> getAllActiveEmployees() {
        log.debug("Recebida requisição para buscar todos os funcionários ativos");
        
        try {
            List<EmployeeDTO> employees = employeeControllerService.getAllActiveEmployees();
            log.info("Requisição de busca de funcionários ativos processada com sucesso. Total: {}", employees.size());
            return ResponseEntity.ok(employees);
        } catch (Exception e) {
            log.error("Erro ao processar requisição de busca de funcionários ativos: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

