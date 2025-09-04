-- V3__Create_permissions_table.sql
-- Criação da tabela permissions com UUID como chave primária

CREATE TABLE permissions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) UNIQUE NOT NULL,
    resource VARCHAR(100) NOT NULL,
    action VARCHAR(50) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Índices para melhor performance
CREATE INDEX idx_permissions_name ON permissions(name);
CREATE INDEX idx_permissions_resource ON permissions(resource);
CREATE INDEX idx_permissions_action ON permissions(action);
CREATE INDEX idx_permissions_resource_action ON permissions(resource, action);

-- Comentários para documentação
COMMENT ON TABLE permissions IS 'Tabela de permissões do sistema';
COMMENT ON COLUMN permissions.id IS 'Identificador único da permissão (UUID)';
COMMENT ON COLUMN permissions.name IS 'Nome único da permissão';
COMMENT ON COLUMN permissions.resource IS 'Recurso ao qual a permissão se aplica';
COMMENT ON COLUMN permissions.action IS 'Ação permitida no recurso';
COMMENT ON COLUMN permissions.description IS 'Descrição da permissão';
COMMENT ON COLUMN permissions.created_at IS 'Data e hora de criação da permissão';
COMMENT ON COLUMN permissions.updated_at IS 'Data e hora da última atualização da permissão';