-- V8__Insert_default_permissions.sql
-- Inserção de permissões padrão do sistema

INSERT INTO permissions (id, name, resource, action, description, created_at, updated_at) VALUES
    -- Permissões de usuário
    (gen_random_uuid(), 'user:create', 'user', 'create', 'Criar usuários', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'user:read', 'user', 'read', 'Ler dados de usuários', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'user:update', 'user', 'update', 'Atualizar usuários', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'user:delete', 'user', 'delete', 'Excluir usuários', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'user:change-password', 'user', 'change-password', 'Alterar senha de usuários', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    
    -- Permissões de role
    (gen_random_uuid(), 'role:create', 'role', 'create', 'Criar roles', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'role:read', 'role', 'read', 'Ler dados de roles', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'role:update', 'role', 'update', 'Atualizar roles', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'role:delete', 'role', 'delete', 'Excluir roles', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    
    -- Permissões de permissão
    (gen_random_uuid(), 'permission:create', 'permission', 'create', 'Criar permissões', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'permission:read', 'permission', 'read', 'Ler dados de permissões', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'permission:update', 'permission', 'update', 'Atualizar permissões', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'permission:delete', 'permission', 'delete', 'Excluir permissões', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Comentário
COMMENT ON TABLE permissions IS 'Permissões padrão inseridas para gerenciamento de usuários, roles e permissões';
