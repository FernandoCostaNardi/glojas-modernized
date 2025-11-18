package com.sysconard.legacy.service;

import com.sysconard.legacy.dto.EmployeeDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Serviço responsável pela lógica de negócio do EmployeeController
 * 
 * Segue os princípios de Clean Code:
 * - Responsabilidade única: coordenação entre controller e service
 * - Injeção de dependência via construtor
 * - Delegação de lógica para o service
 * - Tratamento de erros adequado
 * - Logging para monitoramento
 * 
 * @author Sysconard Legacy API
 * @version 1.0
 */
@Slf4j
@Service
public class EmployeeControllerService {
    
    private final EmployeeService employeeService;
    
    @Autowired
    public EmployeeControllerService(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }
    
    /**
     * Busca todos os funcionários ativos cadastrados no sistema
     * 
     * @return Lista de funcionários ativos
     */
    public List<EmployeeDTO> getAllActiveEmployees() {
        log.debug("Iniciando busca de todos os funcionários ativos");
        
        try {
            List<EmployeeDTO> employees = employeeService.findAllActiveEmployees();
            log.info("Busca de funcionários ativos concluída com sucesso. Total: {}", employees.size());
            return employees;
        } catch (Exception e) {
            log.error("Erro ao buscar funcionários ativos: {}", e.getMessage(), e);
            throw e;
        }
    }
}

