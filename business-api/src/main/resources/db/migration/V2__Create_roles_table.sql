-- V2__Create_roles_table.sql
-- Criação da tabela roles com UUID como chave primária

CREATE TABLE roles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(50) UNIQUE NOT NULL,
    description TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Índices para melhor performance
CREATE INDEX idx_roles_name ON roles(name);
CREATE INDEX idx_roles_active ON roles(is_active);

-- Comentários para documentação
COMMENT ON TABLE roles IS 'Tabela de roles/perfis do sistema';
COMMENT ON COLUMN roles.id IS 'Identificador único da role (UUID)';
COMMENT ON COLUMN roles.name IS 'Nome único da role';
COMMENT ON COLUMN roles.description IS 'Descrição da role';
COMMENT ON COLUMN roles.is_active IS 'Indica se a role está ativa';
COMMENT ON COLUMN roles.created_at IS 'Data e hora de criação da role';
COMMENT ON COLUMN roles.updated_at IS 'Data e hora da última atualização da role';