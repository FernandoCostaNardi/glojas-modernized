-- V20__Insert_store_permissions.sql
-- Inserção de permissões para gerenciamento de lojas (stores)
-- Este script adiciona as permissões necessárias para o StoreController

-- =====================================================
-- INSERÇÃO DE PERMISSÕES DE STORE
-- =====================================================
INSERT INTO permissions (name, resource, action, description, created_at, updated_at) VALUES
    -- Permissões de loja (store)
    ('store:create', 'store', 'create', 'Criar lojas no sistema', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('store:read', 'store', 'read', 'Ler dados de lojas', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('store:update', 'store', 'update', 'Atualizar dados de lojas', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('store:delete', 'store', 'delete', 'Excluir lojas do sistema', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- =====================================================
-- ASSOCIAÇÃO DAS PERMISSÕES DE STORE AO ROLE ADMIN
-- =====================================================
INSERT INTO role_permissions (role_id, permission_id, created_at)
SELECT r.id, p.id, CURRENT_TIMESTAMP
FROM roles r, permissions p
WHERE r.name = 'ADMIN' 
  AND p.name IN ('store:create', 'store:read', 'store:update', 'store:delete');

-- =====================================================
-- COMENTÁRIOS PARA DOCUMENTAÇÃO
-- =====================================================
COMMENT ON TABLE permissions IS 'Permissões de store adicionadas: store:create, store:read, store:update, store:delete';
COMMENT ON TABLE role_permissions IS 'Permissões de store associadas ao role ADMIN para gerenciamento completo de lojas';
