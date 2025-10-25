-- =====================================================
-- SCRIPT PARA POPULAR DADOS DE TESTE - BUSINESS API
-- =====================================================
-- Este script deve ser executado após a criação das tabelas pelo Hibernate
-- Execute: psql -U glojas_user -d glojas_business -f populate-test-data.sql
-- =====================================================

-- Verificar se estamos no banco correto
SELECT current_database() as database_atual;

-- =====================================================
-- LIMPEZA DE DADOS EXISTENTES (OPCIONAL)
-- =====================================================
-- Descomente as linhas abaixo se quiser limpar dados existentes
-- DELETE FROM password_reset_tokens;
-- DELETE FROM user_roles;
-- DELETE FROM role_permissions;
-- DELETE FROM users WHERE username != 'admin';
-- DELETE FROM permissions WHERE name NOT IN ('user:create', 'user:read', 'user:update', 'user:delete', 'user:change-password', 'role:create', 'role:read', 'role:update', 'role:delete', 'permission:create', 'permission:read', 'permission:update', 'permission:delete');
-- DELETE FROM roles WHERE name NOT IN ('ADMIN', 'USER', 'MANAGER');

-- =====================================================
-- INSERÇÃO DE ROLES ADICIONAIS PARA TESTES
-- =====================================================
INSERT INTO roles (id, name, description, is_active, created_at, updated_at) VALUES
    (gen_random_uuid(), 'TESTER', 'Usuário para testes do sistema', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'DEVELOPER', 'Desenvolvedor do sistema', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'ANALYST', 'Analista de negócio', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (name) DO NOTHING;

-- =====================================================
-- INSERÇÃO DE PERMISSÕES ADICIONAIS PARA TESTES
-- =====================================================
INSERT INTO permissions (id, name, resource, action, description, created_at, updated_at) VALUES
    -- Permissões de teste
    (gen_random_uuid(), 'test:execute', 'test', 'execute', 'Executar testes', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'test:create', 'test', 'create', 'Criar testes', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'test:read', 'test', 'read', 'Ler testes', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    
    -- Permissões de desenvolvimento
    (gen_random_uuid(), 'dev:code', 'development', 'code', 'Escrever código', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'dev:deploy', 'development', 'deploy', 'Fazer deploy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'dev:review', 'development', 'review', 'Revisar código', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    
    -- Permissões de análise
    (gen_random_uuid(), 'analysis:create', 'analysis', 'create', 'Criar análises', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'analysis:read', 'analysis', 'read', 'Ler análises', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'analysis:update', 'analysis', 'update', 'Atualizar análises', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (name) DO NOTHING;

-- =====================================================
-- ATRIBUIÇÃO DE PERMISSÕES ADICIONAIS ÀS ROLES
-- =====================================================

-- TESTER: Permissões de teste
INSERT INTO role_permissions (role_id, permission_id, created_at)
SELECT r.id, p.id, CURRENT_TIMESTAMP
FROM roles r, permissions p
WHERE r.name = 'TESTER' 
  AND p.name IN ('test:execute', 'test:create', 'test:read', 'user:read')
ON CONFLICT DO NOTHING;

-- DEVELOPER: Permissões de desenvolvimento
INSERT INTO role_permissions (role_id, permission_id, created_at)
SELECT r.id, p.id, CURRENT_TIMESTAMP
FROM roles r, permissions p
WHERE r.name = 'DEVELOPER' 
  AND p.name IN ('dev:code', 'dev:deploy', 'dev:review', 'user:read', 'user:update', 'test:execute')
ON CONFLICT DO NOTHING;

-- ANALYST: Permissões de análise
INSERT INTO role_permissions (role_id, permission_id, created_at)
SELECT r.id, p.id, CURRENT_TIMESTAMP
FROM roles r, permissions p
WHERE r.name = 'ANALYST' 
  AND p.name IN ('analysis:create', 'analysis:read', 'analysis:update', 'user:read', 'role:read')
ON CONFLICT DO NOTHING;

-- =====================================================
-- INSERÇÃO DE USUÁRIOS DE TESTE
-- =====================================================

-- Usuário Teste 1: João Silva (TESTER)
INSERT INTO users (id, name, username, email, password, is_active, is_not_locked, join_date, created_at, updated_at) VALUES
    (gen_random_uuid(), 'João Silva', 'joao.silva', 'joao.silva@exemplo.com', 
     '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', -- senha: admin123
     TRUE, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (username) DO NOTHING;

-- Usuário Teste 2: Maria Santos (DEVELOPER)
INSERT INTO users (id, name, username, email, password, is_active, is_not_locked, join_date, created_at, updated_at) VALUES
    (gen_random_uuid(), 'Maria Santos', 'maria.santos', 'maria.santos@exemplo.com', 
     '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', -- senha: admin123
     TRUE, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (username) DO NOTHING;

-- Usuário Teste 3: Pedro Oliveira (ANALYST)
INSERT INTO users (id, name, username, email, password, is_active, is_not_locked, join_date, created_at, updated_at) VALUES
    (gen_random_uuid(), 'Pedro Oliveira', 'pedro.oliveira', 'pedro.oliveira@exemplo.com', 
     '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', -- senha: admin123
     TRUE, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (username) DO NOTHING;

-- Usuário Teste 4: Ana Costa (MANAGER)
INSERT INTO users (id, name, username, email, password, is_active, is_not_locked, join_date, created_at, updated_at) VALUES
    (gen_random_uuid(), 'Ana Costa', 'ana.costa', 'ana.costa@exemplo.com', 
     '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', -- senha: admin123
     TRUE, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (username) DO NOTHING;

-- Usuário Teste 5: Carlos Ferreira (USER)
INSERT INTO users (id, name, username, email, password, is_active, is_not_locked, join_date, created_at, updated_at) VALUES
    (gen_random_uuid(), 'Carlos Ferreira', 'carlos.ferreira', 'carlos.ferreira@exemplo.com', 
     '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', -- senha: admin123
     TRUE, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (username) DO NOTHING;

-- Usuário Teste 6: Lucía Rodriguez (DEVELOPER + TESTER)
INSERT INTO users (id, name, username, email, password, is_active, is_not_locked, join_date, created_at, updated_at) VALUES
    (gen_random_uuid(), 'Lucía Rodriguez', 'lucia.rodriguez', 'lucia.rodriguez@exemplo.com', 
     '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', -- senha: admin123
     TRUE, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (username) DO NOTHING;

-- =====================================================
-- ATRIBUIÇÃO DE ROLES AOS USUÁRIOS DE TESTE
-- =====================================================

-- João Silva -> TESTER
INSERT INTO user_roles (user_id, role_id, created_at)
SELECT u.id, r.id, CURRENT_TIMESTAMP
FROM users u, roles r
WHERE u.username = 'joao.silva' AND r.name = 'TESTER'
ON CONFLICT DO NOTHING;

-- Maria Santos -> DEVELOPER
INSERT INTO user_roles (user_id, role_id, created_at)
SELECT u.id, r.id, CURRENT_TIMESTAMP
FROM users u, roles r
WHERE u.username = 'maria.santos' AND r.name = 'DEVELOPER'
ON CONFLICT DO NOTHING;

-- Pedro Oliveira -> ANALYST
INSERT INTO user_roles (user_id, role_id, created_at)
SELECT u.id, r.id, CURRENT_TIMESTAMP
FROM users u, roles r
WHERE u.username = 'pedro.oliveira' AND r.name = 'ANALYST'
ON CONFLICT DO NOTHING;

-- Ana Costa -> MANAGER
INSERT INTO user_roles (user_id, role_id, created_at)
SELECT u.id, r.id, CURRENT_TIMESTAMP
FROM users u, roles r
WHERE u.username = 'ana.costa' AND r.name = 'MANAGER'
ON CONFLICT DO NOTHING;

-- Carlos Ferreira -> USER
INSERT INTO user_roles (user_id, role_id, created_at)
SELECT u.id, r.id, CURRENT_TIMESTAMP
FROM users u, roles r
WHERE u.username = 'carlos.ferreira' AND r.name = 'USER'
ON CONFLICT DO NOTHING;

-- Lucía Rodriguez -> DEVELOPER + TESTER (múltiplas roles)
INSERT INTO user_roles (user_id, role_id, created_at)
SELECT u.id, r.id, CURRENT_TIMESTAMP
FROM users u, roles r
WHERE u.username = 'lucia.rodriguez' AND r.name = 'DEVELOPER'
ON CONFLICT DO NOTHING;

INSERT INTO user_roles (user_id, role_id, created_at)
SELECT u.id, r.id, CURRENT_TIMESTAMP
FROM users u, roles r
WHERE u.username = 'lucia.rodriguez' AND r.name = 'TESTER'
ON CONFLICT DO NOTHING;

-- =====================================================
-- INSERÇÃO DE TOKENS DE RESET DE SENHA PARA TESTES
-- =====================================================

-- Token de reset para João Silva (expira em 1 hora)
INSERT INTO password_reset_tokens (id, user_id, token, expires_at, created_at) VALUES
    (gen_random_uuid(), 
     (SELECT id FROM users WHERE username = 'joao.silva'), 
     'test-token-joao-silva-12345', 
     CURRENT_TIMESTAMP + INTERVAL '1 hour', 
     CURRENT_TIMESTAMP)
ON CONFLICT DO NOTHING;

-- Token de reset para Maria Santos (expira em 30 minutos)
INSERT INTO password_reset_tokens (id, user_id, token, expires_at, created_at) VALUES
    (gen_random_uuid(), 
     (SELECT id FROM users WHERE username = 'maria.santos'), 
     'test-token-maria-santos-67890', 
     CURRENT_TIMESTAMP + INTERVAL '30 minutes', 
     CURRENT_TIMESTAMP)
ON CONFLICT DO NOTHING;

-- =====================================================
-- INSERÇÃO DE LOJAS DE TESTE
-- =====================================================

-- Loja 1: JAB MATRIZ (código real da Legacy API)
INSERT INTO stores (id, code, name, city, status, created_at, updated_at) VALUES
    (gen_random_uuid(), '000001', 'JAB MATRIZ', 'Fortaleza', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (code) DO NOTHING;

-- Loja 2: CD JANGURUSSU (código real da Legacy API)
INSERT INTO stores (id, code, name, city, status, created_at, updated_at) VALUES
    (gen_random_uuid(), '000002', 'CD JANGURUSSU', 'Fortaleza', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (code) DO NOTHING;

-- Loja 3: SMART ANT. SALES (código real da Legacy API)
INSERT INTO stores (id, code, name, city, status, created_at, updated_at) VALUES
    (gen_random_uuid(), '000003', 'SMART ANT. SALES', 'Fortaleza', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (code) DO NOTHING;

-- Loja 4: SMART MARACANAU (código real da Legacy API)
INSERT INTO stores (id, code, name, city, status, created_at, updated_at) VALUES
    (gen_random_uuid(), '000004', 'SMART MARACANAU', 'Fortaleza', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (code) DO NOTHING;

-- Loja 5: SMART IGUATEMI (código real da Legacy API)
INSERT INTO stores (id, code, name, city, status, created_at, updated_at) VALUES
    (gen_random_uuid(), '000005', 'SMART IGUATEMI', 'Fortaleza', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (code) DO NOTHING;

-- Loja 6: SMART MESSEJANA (código real da Legacy API)
INSERT INTO stores (id, code, name, city, status, created_at, updated_at) VALUES
    (gen_random_uuid(), '000006', 'SMART MESSEJANA', 'Fortaleza', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (code) DO NOTHING;

-- Loja 7: SMART IANDE (código real da Legacy API)
INSERT INTO stores (id, code, name, city, status, created_at, updated_at) VALUES
    (gen_random_uuid(), '000007', 'SMART IANDE', 'Fortaleza', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (code) DO NOTHING;

-- Loja 8: SMART VIA SUL (código real da Legacy API)
INSERT INTO stores (id, code, name, city, status, created_at, updated_at) VALUES
    (gen_random_uuid(), '000008', 'SMART VIA SUL', 'Fortaleza', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (code) DO NOTHING;

-- Loja 9: JAB CD 2 (código real da Legacy API)
INSERT INTO stores (id, code, name, city, status, created_at, updated_at) VALUES
    (gen_random_uuid(), '000009', 'JAB CD 2', 'Fortaleza', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (code) DO NOTHING;

-- Loja 10: SMART NORTH (código real da Legacy API)
INSERT INTO stores (id, code, name, city, status, created_at, updated_at) VALUES
    (gen_random_uuid(), '000010', 'SMART NORTH', 'Fortaleza', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (code) DO NOTHING;

-- Loja 11: SMART PARANGABA (código real da Legacy API)
INSERT INTO stores (id, code, name, city, status, created_at, updated_at) VALUES
    (gen_random_uuid(), '000011', 'SMART PARANGABA', 'Fortaleza', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (code) DO NOTHING;

-- Loja 12: SMART RIOMAR KENNEDY (código real da Legacy API)
INSERT INTO stores (id, code, name, city, status, created_at, updated_at) VALUES
    (gen_random_uuid(), '000012', 'SMART RIOMAR KENNEDY', 'Fortaleza', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (code) DO NOTHING;

-- Loja 13: SMART RIOMAR FORTALE (código real da Legacy API)
INSERT INTO stores (id, code, name, city, status, created_at, updated_at) VALUES
    (gen_random_uuid(), '000013', 'SMART RIOMAR FORTALE', 'Fortaleza', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (code) DO NOTHING;

-- Loja 14: SMART JOQUEI (código real da Legacy API)
INSERT INTO stores (id, code, name, city, status, created_at, updated_at) VALUES
    (gen_random_uuid(), '000014', 'SMART JOQUEI', 'Fortaleza', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (code) DO NOTHING;

-- =====================================================
-- VERIFICAÇÃO DOS DADOS INSERIDOS
-- =====================================================

-- Contar usuários
SELECT 'Usuários criados:' as info, COUNT(*) as total FROM users;

-- Contar roles
SELECT 'Roles criadas:' as info, COUNT(*) as total FROM roles;

-- Contar permissões
SELECT 'Permissões criadas:' as info, COUNT(*) as total FROM permissions;

-- Contar lojas
SELECT 'Lojas criadas:' as info, COUNT(*) as total FROM stores;

-- Contar lojas ativas
SELECT 'Lojas ativas:' as info, COUNT(*) as total FROM stores WHERE status = TRUE;

-- Listar usuários com suas roles
SELECT 
    u.username,
    u.name,
    u.email,
    u.is_active,
    u.is_not_locked,
    STRING_AGG(r.name, ', ') as roles
FROM users u
LEFT JOIN user_roles ur ON u.id = ur.user_id
LEFT JOIN roles r ON ur.role_id = r.id
GROUP BY u.id, u.username, u.name, u.email, u.is_active, u.is_not_locked
ORDER BY u.username;

-- =====================================================
-- RESUMO DOS DADOS DE TESTE
-- =====================================================

SELECT '=== RESUMO DOS DADOS DE TESTE ===' as info;
SELECT 'Senha padrão para todos os usuários: admin123' as credenciais;
SELECT 'Usuário admin: admin/admin123' as admin_user;
SELECT 'Lojas criadas: 14 lojas (todas ativas) com códigos reais da Legacy API' as lojas_info;
SELECT 'Usuários e lojas de teste criados com sucesso!' as status;
