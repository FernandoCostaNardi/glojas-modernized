-- V5__Create_role_permissions_table.sql
-- Criação da tabela de relacionamento role_permissions com UUID como chaves estrangeiras

CREATE TABLE role_permissions (
    role_id UUID NOT NULL,
    permission_id UUID NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE
);

-- Índices para melhor performance
CREATE INDEX idx_role_permissions_role_id ON role_permissions(role_id);
CREATE INDEX idx_role_permissions_permission_id ON role_permissions(permission_id);

-- Comentários para documentação
COMMENT ON TABLE role_permissions IS 'Tabela de relacionamento entre roles e permissões';
COMMENT ON COLUMN role_permissions.role_id IS 'ID da role (UUID)';
COMMENT ON COLUMN role_permissions.permission_id IS 'ID da permissão (UUID)';
COMMENT ON COLUMN role_permissions.created_at IS 'Data e hora da atribuição da permissão à role';