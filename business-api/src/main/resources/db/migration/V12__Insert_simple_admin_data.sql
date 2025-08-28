-- Migration V17: Inserir dados básicos do admin (sem criptografia)

-- Limpar dados existentes primeiro para evitar conflitos
DELETE FROM role_permissions WHERE role_id IN (SELECT id FROM roles WHERE name = 'ADMIN');
DELETE FROM user_roles WHERE user_id IN (SELECT id FROM users WHERE username = 'admin');
DELETE FROM users WHERE username = 'admin';
DELETE FROM roles WHERE name = 'ADMIN';
DELETE FROM permissions WHERE name = 'SYSTEM_ADMIN';

-- Inserir usuário admin com senha em texto plano
INSERT INTO users (name, username, email, password, profile_image_url, last_login_date, last_login_date_display, join_date, is_active, is_not_locked) 
VALUES ('Administrador', 'admin', 'admin@sysconard.com', 'admin123', NULL, NULL, NULL, CURRENT_TIMESTAMP, true, true);

-- Inserir role ADMIN
INSERT INTO roles (name, description, is_active) 
VALUES ('ADMIN', 'Administrador do sistema', true);

-- Inserir permission SYSTEM_ADMIN
INSERT INTO permissions (name, resource, action, description) 
VALUES ('SYSTEM_ADMIN', 'SYSTEM', 'ADMIN', 'Administração do sistema');

-- Associar admin à role ADMIN
INSERT INTO user_roles (user_id, role_id) 
SELECT u.id, r.id 
FROM users u, roles r 
WHERE u.username = 'admin' AND r.name = 'ADMIN';

-- Associar permission SYSTEM_ADMIN à role ADMIN
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'ADMIN' AND p.name = 'SYSTEM_ADMIN';
