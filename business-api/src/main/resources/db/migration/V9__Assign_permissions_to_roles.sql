-- V9__Assign_permissions_to_roles.sql
-- Atribuição de permissões às roles padrão

-- ADMIN: Todas as permissões
INSERT INTO role_permissions (role_id, permission_id, created_at)
SELECT r.id, p.id, CURRENT_TIMESTAMP
FROM roles r, permissions p
WHERE r.name = 'ADMIN';

-- USER: Apenas permissões básicas
INSERT INTO role_permissions (role_id, permission_id, created_at)
SELECT r.id, p.id, CURRENT_TIMESTAMP
FROM roles r, permissions p
WHERE r.name = 'USER' 
  AND p.name IN ('user:read', 'user:change-password');

-- MANAGER: Permissões intermediárias
INSERT INTO role_permissions (role_id, permission_id, created_at)
SELECT r.id, p.id, CURRENT_TIMESTAMP
FROM roles r, permissions p
WHERE r.name = 'MANAGER' 
  AND p.name IN ('user:create', 'user:read', 'user:update', 'user:change-password', 'role:read');

-- Comentário
COMMENT ON TABLE role_permissions IS 'Permissões atribuídas às roles: ADMIN (todas), USER (básicas), MANAGER (intermediárias)';
