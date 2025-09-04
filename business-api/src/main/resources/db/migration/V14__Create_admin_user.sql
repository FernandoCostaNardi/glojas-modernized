-- V10__Create_admin_user.sql
-- Criação do usuário administrador padrão

-- Inserir usuário admin
INSERT INTO users (id, name, username, email, password, is_active, is_not_locked, join_date, created_at, updated_at) VALUES
    (gen_random_uuid(), 'Administrador', 'admin', 'admin@exemplo.com', 
     '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', -- senha: admin123
     TRUE, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Atribuir role ADMIN ao usuário admin
INSERT INTO user_roles (user_id, role_id, created_at)
SELECT u.id, r.id, CURRENT_TIMESTAMP
FROM users u, roles r
WHERE u.username = 'admin' AND r.name = 'ADMIN';

-- Comentário
COMMENT ON TABLE users IS 'Usuário administrador criado: admin/admin123';
