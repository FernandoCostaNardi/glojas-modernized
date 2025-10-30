-- Script para adicionar a permissão de análise de compras
-- Executar no banco de dados da Business API

-- Inserir nova permissão buy:read
INSERT INTO permissions (name, description, created_at, updated_at)
SELECT 'buy:read', 'Visualizar análise de compras', NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM permissions WHERE name = 'buy:read'
);

-- Verificar se a permissão foi criada
SELECT id, name, description, created_at 
FROM permissions 
WHERE name = 'buy:read';

-- Associar a permissão ao role ADMIN (se existir)
-- Ajuste o role_id conforme necessário no seu banco de dados
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'ADMIN' 
  AND p.name = 'buy:read'
  AND NOT EXISTS (
    SELECT 1 FROM role_permissions rp
    WHERE rp.role_id = r.id AND rp.permission_id = p.id
  );

-- Verificar associações criadas
SELECT r.name AS role_name, p.name AS permission_name, p.description AS permission_description
FROM roles r
INNER JOIN role_permissions rp ON r.id = rp.role_id
INNER JOIN permissions p ON rp.permission_id = p.id
WHERE p.name = 'buy:read';

