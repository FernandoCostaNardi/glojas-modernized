package com.sysconard.business.repository;

import com.sysconard.business.entity.DatabaseTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DatabaseTestRepository extends JpaRepository<DatabaseTest, Long> {
}
