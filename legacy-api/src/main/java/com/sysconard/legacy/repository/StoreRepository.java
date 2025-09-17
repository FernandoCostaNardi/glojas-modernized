package com.sysconard.legacy.repository;

import com.sysconard.legacy.entity.store.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository para operações com a entidade Store
 * 
 * Estende JpaRepository fornecendo métodos básicos de CRUD:
 * - findAll() - busca todas as lojas
 * - findById(Long id) - busca loja por ID
 * - save(Store) - salva loja
 * - deleteById(Long id) - remove loja por ID
 * 
 * @author Sysconard Legacy API
 * @version 1.0
 */
@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
    
    // Métodos padrão do JpaRepository já disponíveis:
    // - List<Store> findAll()
    // - Optional<Store> findById(Long id)
    // - Store save(Store store)
    // - void deleteById(Long id)
    // - long count()
    // - boolean existsById(Long id)
}
