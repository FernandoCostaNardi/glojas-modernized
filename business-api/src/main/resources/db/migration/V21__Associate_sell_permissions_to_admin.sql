-- V22__Associate_sell_permissions_to_admin.sql
-- Associação das permissões de vendas (sell) ao role ADMIN
-- Este script associa as permissões de vendas existentes ao role ADMIN

-- =====================================================
-- ASSOCIAÇÃO DAS PERMISSÕES DE SELL AO ROLE ADMIN
-- =====================================================
INSERT INTO role_permissions (role_id, permission_id, created_at)
SELECT r.id, p.id, CURRENT_TIMESTAMP
FROM roles r, permissions p
WHERE r.name = 'ADMIN' 
  AND p.name IN ('sell:read', 'sell:create', 'sell:update', 'sell:delete')
  AND NOT EXISTS (
    SELECT 1 FROM role_permissions rp2 
    WHERE rp2.role_id = r.id AND rp2.permission_id = p.id
  );

-- =====================================================
-- COMENTÁRIOS PARA DOCUMENTAÇÃO
-- =====================================================
COMMENT ON TABLE role_permissions IS 'Permissões de sell associadas ao role ADMIN para gerenciamento completo de vendas';
