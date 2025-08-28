-- Migration V13: Inserir dados de usuário comum para testes de segurança

-- Limpar dados existentes primeiro para evitar conflitos
DELETE FROM role_permissions WHERE role_id IN (SELECT id FROM roles WHERE name = 'USER');
DELETE FROM user_roles WHERE user_id IN (SELECT id FROM users WHERE username = 'user');
DELETE FROM users WHERE username = 'user';
DELETE FROM roles WHERE name = 'USER';
DELETE FROM permissions WHERE name = 'USER_READ';

-- Inserir usuário comum com senha em texto plano
INSERT INTO users (name, username, email, password, profile_image_url, last_login_date, last_login_date_display, join_date, is_active, is_not_locked) 
VALUES ('Usuário Teste', 'user', 'user@test.com', 'user123', NULL, NULL, NULL, CURRENT_TIMESTAMP, true, true);

-- Inserir role USER
INSERT INTO roles (name, description, is_active) 
VALUES ('USER', 'Usuário comum do sistema', true);

-- Inserir permission USER_READ
INSERT INTO permissions (name, resource, action, description) 
VALUES ('USER_READ', 'USER', 'READ', 'Permissão para leitura de dados de usuário');

-- Associar usuário à role USER
INSERT INTO user_roles (user_id, role_id) 
SELECT u.id, r.id 
FROM users u, roles r 
WHERE u.username = 'user' AND r.name = 'USER';

-- Associar permission USER_READ à role USER
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'USER' AND p.name = 'USER_READ';
