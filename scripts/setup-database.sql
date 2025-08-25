-- ============================================
-- SCRIPT DE CONFIGURAÇÃO INICIAL - GLOJAS
-- Executar após criar database glojas_business
-- ============================================

-- Enum types (vantagem do PostgreSQL)
CREATE TYPE status_usuario AS ENUM ('ATIVO', 'INATIVO', 'BLOQUEADO');
CREATE TYPE tipo_permissao AS ENUM ('READ', 'WRITE', 'DELETE', 'ADMIN');

-- ============================================
-- TABELA DE USUÁRIOS
-- ============================================
CREATE TABLE usuarios (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    senha_hash VARCHAR(255) NOT NULL,
    status status_usuario DEFAULT 'ATIVO',
    data_nascimento DATE,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ultimo_login TIMESTAMP,
    configuracoes JSONB DEFAULT '{}', -- Configurações personalizadas
    metadata JSONB DEFAULT '{}'       -- Dados extras
);

-- ============================================
-- TABELA DE PERMISSÕES
-- ============================================
CREATE TABLE permissoes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome VARCHAR(100) UNIQUE NOT NULL,
    descricao TEXT,
    recursos JSONB NOT NULL, -- {"vendas": ["read", "write"], "produtos": ["read"]}
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================
-- RELACIONAMENTO USUÁRIO-PERMISSÕES
-- ============================================
CREATE TABLE usuario_permissoes (
    usuario_id UUID REFERENCES usuarios(id) ON DELETE CASCADE,
    permissao_id UUID REFERENCES permissoes(id) ON DELETE CASCADE,
    data_atribuicao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    atribuido_por UUID REFERENCES usuarios(id),
    PRIMARY KEY (usuario_id, permissao_id)
);

-- ============================================
-- TABELA DE CACHE PARA PRODUTOS (SQL SERVER)
-- ============================================
CREATE TABLE cache_produtos (
    codigo BIGINT PRIMARY KEY,
    descricao TEXT,
    preco DECIMAL(10,2),
    estoque INTEGER,
    dados_completos JSONB, -- Cache do objeto completo
    ultima_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================
-- ÍNDICES OTIMIZADOS
-- ============================================
CREATE INDEX idx_usuarios_email ON usuarios(email);
CREATE INDEX idx_usuarios_status ON usuarios(status);
CREATE INDEX idx_usuarios_ultimo_login ON usuarios(ultimo_login);
CREATE INDEX idx_cache_produtos_descricao ON cache_produtos USING gin(to_tsvector('portuguese', descricao));
CREATE INDEX idx_permissoes_recursos ON permissoes USING gin(recursos);
CREATE INDEX idx_usuario_permissoes_usuario ON usuario_permissoes(usuario_id);
CREATE INDEX idx_cache_produtos_atualizacao ON cache_produtos(ultima_atualizacao);

-- ============================================
-- INSERIR PERMISSÕES PADRÃO
-- ============================================
INSERT INTO permissoes (nome, descricao, recursos) VALUES 
('ADMIN', 'Administrador completo', '{"*": ["read", "write", "delete", "admin"]}'),
('VENDEDOR', 'Vendedor padrão', '{"vendas": ["read", "write"], "produtos": ["read"], "dashboard": ["read"]}'),
('GERENTE', 'Gerente de loja', '{"vendas": ["read", "write"], "produtos": ["read", "write"], "dashboard": ["read"], "relatorios": ["read"]}'),
('OPERADOR', 'Operador básico', '{"produtos": ["read"], "dashboard": ["read"]}');

-- ============================================
-- INSERIR USUÁRIO ADMIN PADRÃO
-- Senha: admin123 (hash bcrypt)
-- ============================================
INSERT INTO usuarios (nome, email, senha_hash, status, configuracoes) VALUES 
('Administrador', 'admin@glojas.com', '$2a$10$N4QKB.BdYXwNmgLH2TXc8O.xt1jNLY9J8sOa6X8LzKOZ5mSTY1K8m', 'ATIVO', '{"tema": "light", "idioma": "pt-BR"}');

-- ============================================
-- ATRIBUIR PERMISSÃO ADMIN AO USUÁRIO ADMIN
-- ============================================
INSERT INTO usuario_permissoes (usuario_id, permissao_id)
SELECT u.id, p.id 
FROM usuarios u, permissoes p 
WHERE u.email = 'admin@glojas.com' AND p.nome = 'ADMIN';

-- ============================================
-- FUNÇÃO PARA ATUALIZAR TIMESTAMP AUTOMATICAMENTE
-- ============================================
CREATE OR REPLACE FUNCTION atualizar_timestamp()
RETURNS TRIGGER AS $$
BEGIN
    NEW.data_atualizacao = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- ============================================
-- TRIGGERS PARA ATUALIZAÇÃO AUTOMÁTICA
-- ============================================
CREATE TRIGGER trigger_usuarios_atualizacao
    BEFORE UPDATE ON usuarios
    FOR EACH ROW
    EXECUTE FUNCTION atualizar_timestamp();

CREATE TRIGGER trigger_cache_produtos_atualizacao
    BEFORE UPDATE ON cache_produtos
    FOR EACH ROW
    EXECUTE FUNCTION atualizar_timestamp();

-- ============================================
-- INSERIR ALGUNS DADOS DE TESTE (OPCIONAL)
-- ============================================
-- Usuário teste para desenvolvimento
INSERT INTO usuarios (nome, email, senha_hash, status) VALUES 
('Desenvolvedor Teste', 'dev@glojas.com', '$2a$10$N4QKB.BdYXwNmgLH2TXc8O.xt1jNLY9J8sOa6X8LzKOZ5mSTY1K8m', 'ATIVO');

-- Atribuir permissão GERENTE ao dev
INSERT INTO usuario_permissoes (usuario_id, permissao_id)
SELECT u.id, p.id 
FROM usuarios u, permissoes p 
WHERE u.email = 'dev@glojas.com' AND p.nome = 'GERENTE';

-- ============================================
-- VIEWS ÚTEIS PARA CONSULTAS
-- ============================================
CREATE VIEW vw_usuarios_completo AS
SELECT 
    u.id,
    u.nome,
    u.email,
    u.status,
    u.data_criacao,
    u.ultimo_login,
    array_agg(p.nome) as permissoes
FROM usuarios u
LEFT JOIN usuario_permissoes up ON u.id = up.usuario_id
LEFT JOIN permissoes p ON p.id = up.permissao_id
GROUP BY u.id, u.nome, u.email, u.status, u.data_criacao, u.ultimo_login;

-- ============================================
-- VERIFICAÇÕES FINAIS
-- ============================================
-- Mostrar estatísticas das tabelas criadas
DO $$
BEGIN
    RAISE NOTICE '=== SETUP CONCLUÍDO ===';
    RAISE NOTICE 'Tabelas: %', (SELECT count(*) FROM information_schema.tables WHERE table_schema = 'public');
    RAISE NOTICE 'Usuários: %', (SELECT count(*) FROM usuarios);
    RAISE NOTICE 'Permissões: %', (SELECT count(*) FROM permissoes);
    RAISE NOTICE 'Relacionamentos: %', (SELECT count(*) FROM usuario_permissoes);
END $$;