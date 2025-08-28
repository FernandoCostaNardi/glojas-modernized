-- Adicionar coluna last_login_date_display na tabela users
ALTER TABLE users ADD COLUMN last_login_date_display TIMESTAMP;

-- Criar trigger para atualizar updated_at automaticamente na tabela users
CREATE TRIGGER update_users_updated_at 
    BEFORE UPDATE ON users 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();
