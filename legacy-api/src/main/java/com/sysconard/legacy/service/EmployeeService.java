package com.sysconard.legacy.service;

import com.sysconard.legacy.dto.EmployeeDTO;
import com.sysconard.legacy.entity.Employee;
import com.sysconard.legacy.repository.EmployeeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Serviço responsável pela lógica de negócio dos funcionários
 * 
 * Segue os princípios de Clean Code:
 * - Responsabilidade única: lógica de negócio dos funcionários
 * - Injeção de dependência via construtor
 * - Conversão de entidades para DTOs
 * - Tratamento de erros adequado
 * - Logging para monitoramento
 * 
 * @author Sysconard Legacy API
 * @version 1.0
 */
@Slf4j
@Service
public class EmployeeService {
    
    private final EmployeeRepository employeeRepository;
    
    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }
    
    /**
     * Busca todos os funcionários ativos cadastrados no sistema
     * 
     * @return Lista de funcionários ativos convertidas para DTO
     */
    public List<EmployeeDTO> findAllActiveEmployees() {
        log.debug("Buscando todos os funcionários ativos cadastrados");
        
        // Buscar funcionários com status "S" (ativo)
        List<Employee> employees = employeeRepository.findByActive("S");
        
        List<EmployeeDTO> employeeDTOs = employees.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        log.debug("Funcionários ativos encontrados: {}", employeeDTOs.size());
        
        return employeeDTOs;
    }
    
    /**
     * Converte uma entidade Employee para EmployeeDTO
     * 
     * @param employee Entidade a ser convertida
     * @return DTO convertido
     */
    private EmployeeDTO convertToDTO(Employee employee) {
        return EmployeeDTO.builder()
                .id(formatEmployeeId(employee.getId()))
                .jobPositionCode(employee.getJobPosition() != null ? 
                    formatJobPositionId(employee.getJobPosition().getId()) : null)
                .storeCode(employee.getStore() != null ? 
                    formatStoreId(employee.getStore().getId()) : null)
                .name(employee.getName())
                .birthDate(employee.getBirthDate())
                .commissionPercentage(employee.getCommissionPercentage())
                .email(employee.getEmail())
                .active(employee.getActive())
                .gender(employee.getGender())
                .build();
    }
    
    /**
     * Formata o ID do funcionário com 6 dígitos, preenchendo com zeros à esquerda
     * 
     * @param id ID do funcionário
     * @return ID formatado com 6 dígitos (ex: 000001)
     */
    private String formatEmployeeId(Long id) {
        if (id == null) {
            return null;
        }
        return String.format("%06d", id);
    }
    
    /**
     * Formata o ID do cargo com 6 dígitos, preenchendo com zeros à esquerda
     * 
     * @param id ID do cargo
     * @return ID formatado com 6 dígitos (ex: 000001)
     */
    private String formatJobPositionId(Long id) {
        if (id == null) {
            return null;
        }
        return String.format("%06d", id);
    }
    
    /**
     * Formata o ID da loja com 6 dígitos, preenchendo com zeros à esquerda
     * 
     * @param id ID da loja
     * @return ID formatado com 6 dígitos (ex: 000001)
     */
    private String formatStoreId(Long id) {
        if (id == null) {
            return null;
        }
        return String.format("%06d", id);
    }
}

