-- Script para adicionar permissão STOCK no sistema
-- Executar no banco de dados da Business API

-- Inserir permissão STOCK se não existir
INSERT INTO permissions (name, description, created_at, updated_at)
SELECT 'STOCK', 'Permissão para acessar tela de estoque', NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM permissions WHERE name = 'STOCK'
);

-- Verificar se a permissão foi inserida
SELECT id, name, description, created_at 
FROM permissions 
WHERE name = 'STOCK';
