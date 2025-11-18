-- =====================================================
-- SCRIPT PARA CRIAR TABELA COLLABORATORS - BUSINESS API
-- =====================================================
-- Este script cria a tabela collaborators para armazenar colaboradores
-- Execute: psql -U glojas_user -d glojas_business -f create-collaborators-table.sql
-- =====================================================

-- Verificar se estamos no banco correto
SELECT current_database() as database_atual;

-- =====================================================
-- CRIAÇÃO DA TABELA COLLABORATORS
-- =====================================================

-- Criar tabela se não existir
CREATE TABLE IF NOT EXISTS collaborators (
    -- Identificador único (UUID)
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    
    -- Código do colaborador da legacy API (VARCHAR com 6 caracteres, único)
    employee_code VARCHAR(6) NOT NULL UNIQUE,
    
    -- Código do cargo do colaborador (VARCHAR com 6 caracteres)
    job_position_code VARCHAR(6) NOT NULL,
    
    -- Código da loja onde o colaborador trabalha (VARCHAR com 6 caracteres)
    store_code VARCHAR(6) NOT NULL,
    
    -- Nome do colaborador (VARCHAR com até 255 caracteres)
    name VARCHAR(255) NOT NULL,
    
    -- Data de nascimento do colaborador
    birth_date DATE,
    
    -- Percentual de comissão do colaborador (NUMERIC com 5 dígitos, 2 decimais)
    commission_percentage NUMERIC(5,2),
    
    -- Email do colaborador (VARCHAR com até 100 caracteres)
    email VARCHAR(100),
    
    -- Status ativo do colaborador (S/N)
    active VARCHAR(1),
    
    -- Sexo do colaborador
    gender VARCHAR(1),
    
    -- Data e hora de criação do registro
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- Data e hora da última atualização do registro
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- CRIAÇÃO DE ÍNDICES PARA OTIMIZAÇÃO
-- =====================================================

-- Índice único para busca por código do colaborador (já criado via UNIQUE constraint, mas criamos índice explícito)
CREATE UNIQUE INDEX IF NOT EXISTS idx_collaborators_employee_code 
ON collaborators(employee_code);

-- Índice para busca por código do cargo
CREATE INDEX IF NOT EXISTS idx_collaborators_job_position_code 
ON collaborators(job_position_code);

-- Índice para busca por código da loja
CREATE INDEX IF NOT EXISTS idx_collaborators_store_code 
ON collaborators(store_code);

-- Índice para busca por data de criação (para ordenação)
CREATE INDEX IF NOT EXISTS idx_collaborators_created_at 
ON collaborators(created_at);

-- =====================================================
-- COMENTÁRIOS NA TABELA E COLUNAS
-- =====================================================

COMMENT ON TABLE collaborators IS 'Tabela para armazenar colaboradores sincronizados da Legacy API';

COMMENT ON COLUMN collaborators.id IS 'Identificador único do colaborador (UUID)';
COMMENT ON COLUMN collaborators.employee_code IS 'Código único do colaborador da Legacy API (ex: 000001)';
COMMENT ON COLUMN collaborators.job_position_code IS 'Código do cargo do colaborador';
COMMENT ON COLUMN collaborators.store_code IS 'Código da loja onde o colaborador trabalha';
COMMENT ON COLUMN collaborators.name IS 'Nome completo do colaborador';
COMMENT ON COLUMN collaborators.birth_date IS 'Data de nascimento do colaborador';
COMMENT ON COLUMN collaborators.commission_percentage IS 'Percentual de comissão do colaborador';
COMMENT ON COLUMN collaborators.email IS 'Email do colaborador';
COMMENT ON COLUMN collaborators.active IS 'Status ativo do colaborador (S para ativo, N para inativo)';
COMMENT ON COLUMN collaborators.gender IS 'Sexo do colaborador';
COMMENT ON COLUMN collaborators.created_at IS 'Data e hora de criação do registro';
COMMENT ON COLUMN collaborators.updated_at IS 'Data e hora da última atualização do registro';

-- =====================================================
-- VERIFICAÇÃO DA CRIAÇÃO
-- =====================================================

-- Verificar se a tabela foi criada
SELECT 'Tabela collaborators criada com sucesso!' as status;

-- Listar estrutura da tabela
SELECT 
    column_name,
    data_type,
    character_maximum_length,
    numeric_precision,
    numeric_scale,
    is_nullable,
    column_default
FROM information_schema.columns
WHERE table_name = 'collaborators'
ORDER BY ordinal_position;

-- Verificar se os campos principais foram criados
SELECT 'Campo employee_code criado com sucesso!' as status
WHERE EXISTS (
    SELECT 1 FROM information_schema.columns 
    WHERE table_name = 'collaborators' AND column_name = 'employee_code'
);

SELECT 'Campo job_position_code criado com sucesso!' as status
WHERE EXISTS (
    SELECT 1 FROM information_schema.columns 
    WHERE table_name = 'collaborators' AND column_name = 'job_position_code'
);

SELECT 'Campo store_code criado com sucesso!' as status
WHERE EXISTS (
    SELECT 1 FROM information_schema.columns 
    WHERE table_name = 'collaborators' AND column_name = 'store_code'
);

-- Listar índices criados
SELECT 
    indexname,
    indexdef
FROM pg_indexes
WHERE tablename = 'collaborators';

-- =====================================================
-- CONCESSÃO DE PRIVILÉGIOS
-- =====================================================

-- Conceder privilégios ao usuário glojas_user
GRANT ALL PRIVILEGES ON TABLE collaborators TO glojas_user;

SELECT 'Privilégios concedidos ao usuário glojas_user!' as status;

-- =====================================================
-- SCRIPT CONCLUÍDO COM SUCESSO
-- =====================================================

SELECT '=================================================' as info;
SELECT 'TABELA COLLABORATORS CRIADA COM SUCESSO!' as info;
SELECT '=================================================' as info;
SELECT 'A tabela está pronta para ser utilizada pela aplicação.' as info;

