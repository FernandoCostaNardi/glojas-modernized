-- V4__Create_user_roles_table.sql
-- Criação da tabela de relacionamento user_roles com UUID como chaves estrangeiras

CREATE TABLE user_roles (
    user_id UUID NOT NULL,
    role_id UUID NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- Índices para melhor performance
CREATE INDEX idx_user_roles_user_id ON user_roles(user_id);
CREATE INDEX idx_user_roles_role_id ON user_roles(role_id);

-- Comentários para documentação
COMMENT ON TABLE user_roles IS 'Tabela de relacionamento entre usuários e roles';
COMMENT ON COLUMN user_roles.user_id IS 'ID do usuário (UUID)';
COMMENT ON COLUMN user_roles.role_id IS 'ID da role (UUID)';
COMMENT ON COLUMN user_roles.created_at IS 'Data e hora da atribuição da role ao usuário';