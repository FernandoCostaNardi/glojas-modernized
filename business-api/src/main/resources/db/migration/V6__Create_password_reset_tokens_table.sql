-- V6__Create_password_reset_tokens_table.sql
-- Criação da tabela password_reset_tokens com UUID como chave estrangeira

CREATE TABLE password_reset_tokens (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    token VARCHAR(255) UNIQUE NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    used_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Índices para melhor performance
CREATE INDEX idx_password_reset_tokens_user_id ON password_reset_tokens(user_id);
CREATE INDEX idx_password_reset_tokens_token ON password_reset_tokens(token);
CREATE INDEX idx_password_reset_tokens_expires_at ON password_reset_tokens(expires_at);

-- Comentários para documentação
COMMENT ON TABLE password_reset_tokens IS 'Tabela de tokens para reset de senha';
COMMENT ON COLUMN password_reset_tokens.id IS 'Identificador único do token (UUID)';
COMMENT ON COLUMN password_reset_tokens.user_id IS 'ID do usuário (UUID)';
COMMENT ON COLUMN password_reset_tokens.token IS 'Token único para reset de senha';
COMMENT ON COLUMN password_reset_tokens.expires_at IS 'Data e hora de expiração do token';
COMMENT ON COLUMN password_reset_tokens.used_at IS 'Data e hora de uso do token';
COMMENT ON COLUMN password_reset_tokens.created_at IS 'Data e hora de criação do token';