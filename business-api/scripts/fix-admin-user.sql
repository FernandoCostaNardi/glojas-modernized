-- =====================================================
-- SCRIPT PARA CORRIGIR USUÁRIO ADMIN - BUSINESS API
-- =====================================================
-- Este script corrige o usuário admin para usar email correto
-- Execute: psql -U glojas_user -d glojas_business -f fix-admin-user.sql
-- =====================================================

-- Verificar se estamos no banco correto
SELECT current_database() as database_atual;

-- =====================================================
-- VERIFICAR USUÁRIOS EXISTENTES
-- =====================================================
SELECT '=== USUÁRIOS EXISTENTES ===' as info;
SELECT id, name, username, email, is_active, is_not_locked, created_at 
FROM users 
ORDER BY created_at;

-- =====================================================
-- VERIFICAR ROLES EXISTENTES
-- =====================================================
SELECT '=== ROLES EXISTENTES ===' as info;
SELECT id, name, description, is_active, created_at 
FROM roles 
ORDER BY name;

-- =====================================================
-- VERIFICAR PERMISSÕES EXISTENTES
-- =====================================================
SELECT '=== PERMISSÕES EXISTENTES ===' as info;
SELECT id, name, resource, action, description, created_at 
FROM permissions 
ORDER BY name;

-- =====================================================
-- CORRIGIR USUÁRIO ADMIN
-- =====================================================

-- 1. Remover usuário admin existente (se houver)
DELETE FROM user_roles WHERE user_id IN (SELECT id FROM users WHERE username = 'admin');
DELETE FROM users WHERE username = 'admin';

-- 2. Remover role ADMIN existente (se houver)
DELETE FROM role_permissions WHERE role_id IN (SELECT id FROM roles WHERE name = 'ADMIN');
DELETE FROM roles WHERE name = 'ADMIN';

-- 3. Remover permissão SYSTEM_ADMIN existente (se houver)
DELETE FROM permissions WHERE name = 'SYSTEM_ADMIN';

-- 4. Criar usuário admin com email correto
INSERT INTO users (id, name, username, email, password, is_active, is_not_locked, join_date, created_at, updated_at) 
VALUES (
    gen_random_uuid(), 
    'Administrador', 
    'admin', 
    'admin', -- Email será 'admin' conforme alteração do sistema
    'admin123', -- Senha em texto plano para teste
    true, 
    true, 
    CURRENT_TIMESTAMP, 
    CURRENT_TIMESTAMP, 
    CURRENT_TIMESTAMP
);

-- 5. Criar role ADMIN
INSERT INTO roles (id, name, description, is_active, created_at, updated_at) 
VALUES (
    gen_random_uuid(), 
    'ADMIN', 
    'Administrador do sistema com acesso total', 
    true, 
    CURRENT_TIMESTAMP, 
    CURRENT_TIMESTAMP
);

-- 6. Criar permissão SYSTEM_ADMIN
INSERT INTO permissions (id, name, resource, action, description, created_at, updated_at) 
VALUES (
    gen_random_uuid(), 
    'SYSTEM_ADMIN', 
    'SYSTEM', 
    'ADMIN', 
    'Administração completa do sistema', 
    CURRENT_TIMESTAMP, 
    CURRENT_TIMESTAMP
);

-- 7. Associar admin à role ADMIN
INSERT INTO user_roles (user_id, role_id, created_at)
SELECT u.id, r.id, CURRENT_TIMESTAMP
FROM users u, roles r 
WHERE u.username = 'admin' AND r.name = 'ADMIN';

-- 8. Associar permission SYSTEM_ADMIN à role ADMIN
INSERT INTO role_permissions (role_id, permission_id, created_at)
SELECT r.id, p.id, CURRENT_TIMESTAMP
FROM roles r, permissions p
WHERE r.name = 'ADMIN' AND p.name = 'SYSTEM_ADMIN';

-- =====================================================
-- VERIFICAR RESULTADO
-- =====================================================
SELECT '=== VERIFICAÇÃO FINAL ===' as info;

SELECT 'Usuário Admin:' as tipo, u.username, u.email, u.is_active
FROM users u 
WHERE u.username = 'admin';

SELECT 'Role Admin:' as tipo, r.name, r.is_active
FROM roles r 
WHERE r.name = 'ADMIN';

SELECT 'Permissão System Admin:' as tipo, p.name, p.resource, p.action
FROM permissions p 
WHERE p.name = 'SYSTEM_ADMIN';

SELECT 'Associação User-Role:' as tipo, u.username, r.name
FROM users u 
JOIN user_roles ur ON u.id = ur.user_id
JOIN roles r ON ur.role_id = r.id
WHERE u.username = 'admin';

SELECT 'Associação Role-Permission:' as tipo, r.name, p.name
FROM roles r 
JOIN role_permissions rp ON r.id = rp.role_id
JOIN permissions p ON rp.permission_id = p.id
WHERE r.name = 'ADMIN';

-- =====================================================
-- CREDENCIAIS PARA TESTE
-- =====================================================
SELECT '=== CREDENCIAIS PARA TESTE ===' as info;
SELECT 'Username: admin' as credencial;
SELECT 'Email: admin' as credencial;
SELECT 'Senha: admin123' as credencial;
SELECT 'Role: ADMIN' as credencial;
