-- =====================================================
-- SCRIPT PARA CRIAR TABELA JOB_POSITIONS - BUSINESS API
-- =====================================================
-- Este script cria a tabela job_positions para armazenar cargos
-- Execute: psql -U glojas_user -d glojas_business -f create-job-positions-table.sql
-- =====================================================

-- Verificar se estamos no banco correto
SELECT current_database() as database_atual;

-- =====================================================
-- CRIAÇÃO DA TABELA JOB_POSITIONS
-- =====================================================

-- Criar tabela se não existir
CREATE TABLE IF NOT EXISTS job_positions (
    -- Identificador único (UUID)
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    
    -- Código do cargo da legacy API (VARCHAR com 6 caracteres, único)
    job_position_code VARCHAR(6) NOT NULL UNIQUE,
    
    -- Descrição do cargo (VARCHAR com até 255 caracteres)
    job_position_description VARCHAR(255) NOT NULL,
    
    -- Data e hora de criação do registro
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- Data e hora da última atualização do registro
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- CRIAÇÃO DE ÍNDICES PARA OTIMIZAÇÃO
-- =====================================================

-- Índice único para busca por código do cargo (já criado via UNIQUE constraint, mas criamos índice explícito)
CREATE UNIQUE INDEX IF NOT EXISTS idx_job_positions_code 
ON job_positions(job_position_code);

-- Índice para busca por data de criação (para ordenação)
CREATE INDEX IF NOT EXISTS idx_job_positions_created_at 
ON job_positions(created_at);

-- =====================================================
-- COMENTÁRIOS NA TABELA E COLUNAS
-- =====================================================

COMMENT ON TABLE job_positions IS 'Tabela para armazenar cargos sincronizados da Legacy API';

COMMENT ON COLUMN job_positions.id IS 'Identificador único do cargo (UUID)';
COMMENT ON COLUMN job_positions.job_position_code IS 'Código único do cargo da Legacy API (ex: 000001)';
COMMENT ON COLUMN job_positions.job_position_description IS 'Descrição do cargo';
COMMENT ON COLUMN job_positions.created_at IS 'Data e hora de criação do registro';
COMMENT ON COLUMN job_positions.updated_at IS 'Data e hora da última atualização do registro';

-- =====================================================
-- VERIFICAÇÃO DA CRIAÇÃO
-- =====================================================

-- Verificar se a tabela foi criada
SELECT 'Tabela job_positions criada com sucesso!' as status;

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
WHERE table_name = 'job_positions'
ORDER BY ordinal_position;

-- Verificar se os campos foram criados
SELECT 'Campo job_position_code criado com sucesso!' as status
WHERE EXISTS (
    SELECT 1 FROM information_schema.columns 
    WHERE table_name = 'job_positions' AND column_name = 'job_position_code'
);

SELECT 'Campo job_position_description criado com sucesso!' as status
WHERE EXISTS (
    SELECT 1 FROM information_schema.columns 
    WHERE table_name = 'job_positions' AND column_name = 'job_position_description'
);

-- Listar índices criados
SELECT 
    indexname,
    indexdef
FROM pg_indexes
WHERE tablename = 'job_positions';

-- =====================================================
-- CONCESSÃO DE PRIVILÉGIOS
-- =====================================================

-- Conceder privilégios ao usuário glojas_user
GRANT ALL PRIVILEGES ON TABLE job_positions TO glojas_user;

SELECT 'Privilégios concedidos ao usuário glojas_user!' as status;

-- =====================================================
-- SCRIPT CONCLUÍDO COM SUCESSO
-- =====================================================

SELECT '=================================================' as info;
SELECT 'TABELA JOB_POSITIONS CRIADA COM SUCESSO!' as info;
SELECT '=================================================' as info;
SELECT 'A tabela está pronta para ser utilizada pela aplicação.' as info;

