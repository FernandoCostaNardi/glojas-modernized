-- Criação da tabela operations
-- Inclui o campo operationSource como enum obrigatório
CREATE TABLE operations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255) NOT NULL,
    operation_source VARCHAR(20) NOT NULL CHECK (operation_source IN ('SELL', 'EXCHANGE')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Índices para melhor performance
CREATE INDEX idx_operations_code ON operations(code);
CREATE INDEX idx_operations_operation_source ON operations(operation_source);
CREATE INDEX idx_operations_created_at ON operations(created_at);

-- Comentários para documentação
COMMENT ON TABLE operations IS 'Tabela para armazenar operações do sistema com suas fontes de operação';
COMMENT ON COLUMN operations.id IS 'Identificador único da operação (UUID)';
COMMENT ON COLUMN operations.code IS 'Código único da operação';
COMMENT ON COLUMN operations.description IS 'Descrição da operação';
COMMENT ON COLUMN operations.operation_source IS 'Fonte da operação: SELL ou EXCHANGE';
COMMENT ON COLUMN operations.created_at IS 'Data e hora de criação da operação';
COMMENT ON COLUMN operations.updated_at IS 'Data e hora da última atualização da operação';
