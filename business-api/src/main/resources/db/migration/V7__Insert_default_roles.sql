-- V7__Insert_default_roles.sql
-- Inserção de roles padrão do sistema

INSERT INTO roles (id, name, description, is_active, created_at, updated_at) VALUES
    (gen_random_uuid(), 'ADMIN', 'Administrador do sistema com acesso total', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'USER', 'Usuário padrão do sistema', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'MANAGER', 'Gerente com acesso intermediário', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Comentário
COMMENT ON TABLE roles IS 'Roles padrão inseridas: ADMIN, USER, MANAGER';
