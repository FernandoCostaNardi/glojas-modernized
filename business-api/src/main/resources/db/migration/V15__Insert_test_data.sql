-- V11__Insert_test_data.sql
-- Inserção de dados de teste para desenvolvimento e testes
-- Este script deve ser executado após as migrações de estrutura

-- =====================================================
-- INSERÇÃO DE ROLES ADICIONAIS PARA TESTES
-- =====================================================
INSERT INTO roles (id, name, description, is_active, created_at, updated_at) VALUES
    (gen_random_uuid(), 'TESTER', 'Usuário para testes do sistema', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'DEVELOPER', 'Desenvolvedor do sistema', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'ANALYST', 'Analista de negócio', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

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
    (gen_random_uuid(), 'analysis:update', 'analysis', 'update', 'Atualizar análises', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- =====================================================
-- ATRIBUIÇÃO DE PERMISSÕES ADICIONAIS ÀS ROLES
-- =====================================================

-- TESTER: Permissões de teste
INSERT INTO role_permissions (role_id, permission_id, created_at)
SELECT r.id, p.id, CURRENT_TIMESTAMP
FROM roles r, permissions p
WHERE r.name = 'TESTER' 
  AND p.name IN ('test:execute', 'test:create', 'test:read', 'user:read');

-- DEVELOPER: Permissões de desenvolvimento
INSERT INTO role_permissions (role_id, permission_id, created_at)
SELECT r.id, p.id, CURRENT_TIMESTAMP
FROM roles r, permissions p
WHERE r.name = 'DEVELOPER' 
  AND p.name IN ('dev:code', 'dev:deploy', 'dev:review', 'user:read', 'user:update', 'test:execute');

-- ANALYST: Permissões de análise
INSERT INTO role_permissions (role_id, permission_id, created_at)
SELECT r.id, p.id, CURRENT_TIMESTAMP
FROM roles r, permissions p
WHERE r.name = 'ANALYST' 
  AND p.name IN ('analysis:create', 'analysis:read', 'analysis:update', 'user:read', 'role:read');

-- =====================================================
-- INSERÇÃO DE USUÁRIOS DE TESTE
-- =====================================================

-- Usuário Teste 1: João Silva (TESTER)
INSERT INTO users (id, name, username, email, password, is_active, is_not_locked, join_date, created_at, updated_at) VALUES
    (gen_random_uuid(), 'João Silva', 'joao.silva', 'joao.silva@exemplo.com', 
     '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', -- senha: admin123
     TRUE, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Usuário Teste 2: Maria Santos (DEVELOPER)
INSERT INTO users (id, name, username, email, password, is_active, is_not_locked, join_date, created_at, updated_at) VALUES
    (gen_random_uuid(), 'Maria Santos', 'maria.santos', 'maria.santos@exemplo.com', 
     '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', -- senha: admin123
     TRUE, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Usuário Teste 3: Pedro Oliveira (ANALYST)
INSERT INTO users (id, name, username, email, password, is_active, is_not_locked, join_date, created_at, updated_at) VALUES
    (gen_random_uuid(), 'Pedro Oliveira', 'pedro.oliveira', 'pedro.oliveira@exemplo.com', 
     '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', -- senha: admin123
     TRUE, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Usuário Teste 4: Ana Costa (MANAGER)
INSERT INTO users (id, name, username, email, password, is_active, is_not_locked, join_date, created_at, updated_at) VALUES
    (gen_random_uuid(), 'Ana Costa', 'ana.costa', 'ana.costa@exemplo.com', 
     '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', -- senha: admin123
     TRUE, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Usuário Teste 5: Carlos Ferreira (USER)
INSERT INTO users (id, name, username, email, password, is_active, is_not_locked, join_date, created_at, updated_at) VALUES
    (gen_random_uuid(), 'Carlos Ferreira', 'carlos.ferreira', 'carlos.ferreira@exemplo.com', 
     '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', -- senha: admin123
     TRUE, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Usuário Teste 6: Lucía Rodriguez (DEVELOPER + TESTER)
INSERT INTO users (id, name, username, email, password, is_active, is_not_locked, join_date, created_at, updated_at) VALUES
    (gen_random_uuid(), 'Lucía Rodriguez', 'lucia.rodriguez', 'lucia.rodriguez@exemplo.com', 
     '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', -- senha: admin123
     TRUE, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- =====================================================
-- ATRIBUIÇÃO DE ROLES AOS USUÁRIOS DE TESTE
-- =====================================================

-- João Silva -> TESTER
INSERT INTO user_roles (user_id, role_id, created_at)
SELECT u.id, r.id, CURRENT_TIMESTAMP
FROM users u, roles r
WHERE u.username = 'joao.silva' AND r.name = 'TESTER';

-- Maria Santos -> DEVELOPER
INSERT INTO user_roles (user_id, role_id, created_at)
SELECT u.id, r.id, CURRENT_TIMESTAMP
FROM users u, roles r
WHERE u.username = 'maria.santos' AND r.name = 'DEVELOPER';

-- Pedro Oliveira -> ANALYST
INSERT INTO user_roles (user_id, role_id, created_at)
SELECT u.id, r.id, CURRENT_TIMESTAMP
FROM users u, roles r
WHERE u.username = 'pedro.oliveira' AND r.name = 'ANALYST';

-- Ana Costa -> MANAGER
INSERT INTO user_roles (user_id, role_id, created_at)
SELECT u.id, r.id, CURRENT_TIMESTAMP
FROM users u, roles r
WHERE u.username = 'ana.costa' AND r.name = 'MANAGER';

-- Carlos Ferreira -> USER
INSERT INTO user_roles (user_id, role_id, created_at)
SELECT u.id, r.id, CURRENT_TIMESTAMP
FROM users u, roles r
WHERE u.username = 'carlos.ferreira' AND r.name = 'USER';

-- Lucía Rodriguez -> DEVELOPER + TESTER (múltiplas roles)
INSERT INTO user_roles (user_id, role_id, created_at)
SELECT u.id, r.id, CURRENT_TIMESTAMP
FROM users u, roles r
WHERE u.username = 'lucia.rodriguez' AND r.name = 'DEVELOPER';

INSERT INTO user_roles (user_id, role_id, created_at)
SELECT u.id, r.id, CURRENT_TIMESTAMP
FROM users u, roles r
WHERE u.username = 'lucia.rodriguez' AND r.name = 'TESTER';

-- =====================================================
-- INSERÇÃO DE TOKENS DE RESET DE SENHA PARA TESTES
-- =====================================================

-- Token de reset para João Silva (expira em 1 hora)
INSERT INTO password_reset_tokens (id, user_id, token, expires_at, created_at) VALUES
    (gen_random_uuid(), 
     (SELECT id FROM users WHERE username = 'joao.silva'), 
     'test-token-joao-silva-12345', 
     CURRENT_TIMESTAMP + INTERVAL '1 hour', 
     CURRENT_TIMESTAMP);

-- Token de reset para Maria Santos (expira em 30 minutos)
INSERT INTO password_reset_tokens (id, user_id, token, expires_at, created_at) VALUES
    (gen_random_uuid(), 
     (SELECT id FROM users WHERE username = 'maria.santos'), 
     'test-token-maria-santos-67890', 
     CURRENT_TIMESTAMP + INTERVAL '30 minutes', 
     CURRENT_TIMESTAMP);

-- =====================================================
-- COMENTÁRIOS FINAIS
-- =====================================================

COMMENT ON TABLE users IS 'Dados de teste inseridos: admin/admin123, joao.silva/admin123, maria.santos/admin123, pedro.oliveira/admin123, ana.costa/admin123, carlos.ferreira/admin123, lucia.rodriguez/admin123';
COMMENT ON TABLE roles IS 'Roles de teste adicionadas: TESTER, DEVELOPER, ANALYST';
COMMENT ON TABLE permissions IS 'Permissões de teste adicionadas para desenvolvimento e testes';
COMMENT ON TABLE password_reset_tokens IS 'Tokens de reset de senha para testes (expiração configurada)';
