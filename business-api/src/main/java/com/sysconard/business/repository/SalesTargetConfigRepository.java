package com.sysconard.business.repository;

import com.sysconard.business.entity.SalesTargetConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository para operações de acesso a dados da entidade SalesTargetConfig.
 * Estende JpaRepository para operações CRUD básicas e define métodos customizados.
 * Permite múltiplas configurações para a mesma loja e competência.
 */
@Repository
public interface SalesTargetConfigRepository extends JpaRepository<SalesTargetConfig, UUID> {
    
    /**
     * Busca todas as configurações de uma loja específica.
     * 
     * @param storeCode Código da loja
     * @return Lista de configurações da loja
     */
    List<SalesTargetConfig> findByStoreCode(String storeCode);
    
    /**
     * Busca todas as configurações de uma competência específica.
     * 
     * @param competenceDate Data de competência no formato MM/YYYY
     * @return Lista de configurações da competência
     */
    List<SalesTargetConfig> findByCompetenceDate(String competenceDate);
    
    /**
     * Busca todas as configurações de uma loja e competência específicas.
     * Retorna uma lista pois permite múltiplas metas escalonadas.
     * 
     * @param storeCode Código da loja
     * @param competenceDate Data de competência no formato MM/YYYY
     * @return Lista de configurações da loja e competência
     */
    List<SalesTargetConfig> findByStoreCodeAndCompetenceDate(String storeCode, String competenceDate);
    
    /**
     * Busca configurações com filtros opcionais por loja e/ou competência.
     * Permite buscar por um ou ambos os critérios, ou retornar todas se nenhum filtro for fornecido.
     * 
     * @param storeCode Código da loja (opcional, pode ser null)
     * @param competenceDate Data de competência no formato MM/YYYY (opcional, pode ser null)
     * @return Lista de configurações que atendem aos critérios de filtro
     */
    @Query("SELECT stc FROM SalesTargetConfig stc WHERE " +
           "(:storeCode IS NULL OR stc.storeCode = :storeCode) AND " +
           "(:competenceDate IS NULL OR stc.competenceDate = :competenceDate)")
    List<SalesTargetConfig> findByFilters(
            @Param("storeCode") String storeCode,
            @Param("competenceDate") String competenceDate
    );
}

