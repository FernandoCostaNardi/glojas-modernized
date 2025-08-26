package com.sysconard.business.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "database_test")
public class DatabaseTest {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "test_message")
    private String testMessage;
    
    @Column(name = "created_at")
    private java.time.LocalDateTime createdAt;
    
    // Construtor padrão
    public DatabaseTest() {
        this.createdAt = java.time.LocalDateTime.now();
    }
    
    // Construtor com parâmetros
    public DatabaseTest(String testMessage) {
        this.testMessage = testMessage;
        this.createdAt = java.time.LocalDateTime.now();
    }
    
    // Getters e Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTestMessage() {
        return testMessage;
    }
    
    public void setTestMessage(String testMessage) {
        this.testMessage = testMessage;
    }
    
    public java.time.LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(java.time.LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    @Override
    public String toString() {
        return "DatabaseTest{" +
                "id=" + id +
                ", testMessage='" + testMessage + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
