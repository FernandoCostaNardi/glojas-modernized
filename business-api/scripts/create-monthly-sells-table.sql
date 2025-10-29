-- =====================================================
-- SCRIPT PARA CRIAR TABELA MONTHLY_SELLS - BUSINESS API
-- =====================================================
-- Este script cria a tabela monthly_sells para armazenar vendas mensais
-- Execute: psql -U glojas_user -d glojas_business -f create-monthly-sells-table.sql
-- =====================================================

-- Verificar se estamos no banco correto
SELECT current_database() as database_atual;

-- =====================================================
-- CRIAÇÃO DA TABELA MONTHLY_SELLS
-- =====================================================

-- Criar tabela se não existir
CREATE TABLE IF NOT EXISTS monthly_sells (
    -- Identificador único (UUID)
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    
    -- Código da loja (VARCHAR com até 10 caracteres)
    store_code VARCHAR(10) NOT NULL,
    
    -- ID da loja (UUID - referência)
    store_id UUID NOT NULL,
    
    -- Nome da loja (VARCHAR com até 255 caracteres)
    store_name VARCHAR(255) NOT NULL,
    
    -- Valor total das vendas mensais (NUMERIC com 15 dígitos, 2 decimais)
    total NUMERIC(15, 2) NOT NULL,
    
    -- Ano e mês no formato YYYY-MM (VARCHAR com 7 caracteres)
    year_month VARCHAR(7) NOT NULL,
    
    -- Data e hora de criação do registro
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- Data e hora da última atualização do registro
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- CRIAÇÃO DE ÍNDICES PARA OTIMIZAÇÃO
-- =====================================================

-- Índice para busca por código da loja
CREATE INDEX IF NOT EXISTS idx_monthly_sells_store_code 
ON monthly_sells(store_code);

-- Índice para busca por ID da loja
CREATE INDEX IF NOT EXISTS idx_monthly_sells_store_id 
ON monthly_sells(store_id);

-- Índice para busca por data de criação
CREATE INDEX IF NOT EXISTS idx_monthly_sells_created_at 
ON monthly_sells(created_at);

-- Índice composto único para garantir unicidade por loja e mês
CREATE UNIQUE INDEX IF NOT EXISTS idx_monthly_sells_store_year_month 
ON monthly_sells(store_id, year_month);

-- =====================================================
-- COMENTÁRIOS NA TABELA E COLUNAS
-- =====================================================

COMMENT ON TABLE monthly_sells IS 'Tabela para armazenar vendas mensais consolidadas por loja';

COMMENT ON COLUMN monthly_sells.id IS 'Identificador único da venda mensal (UUID)';
COMMENT ON COLUMN monthly_sells.store_code IS 'Código da loja para identificação rápida';
COMMENT ON COLUMN monthly_sells.store_id IS 'ID único da loja (referência)';
COMMENT ON COLUMN monthly_sells.store_name IS 'Nome comercial da loja';
COMMENT ON COLUMN monthly_sells.total IS 'Valor total consolidado das vendas mensais';
COMMENT ON COLUMN monthly_sells.year_month IS 'Ano e mês no formato YYYY-MM para agrupamento';
COMMENT ON COLUMN monthly_sells.created_at IS 'Data e hora de criação do registro';
COMMENT ON COLUMN monthly_sells.updated_at IS 'Data e hora da última atualização do registro';

-- =====================================================
-- VERIFICAÇÃO DA CRIAÇÃO
-- =====================================================

-- Verificar se a tabela foi criada
SELECT 'Tabela monthly_sells criada com sucesso!' as status;

-- Listar estrutura da tabela
SELECT 
    column_name,
    data_type,
    character_maximum_length,
    is_nullable,
    column_default
FROM information_schema.columns
WHERE table_name = 'monthly_sells'
ORDER BY ordinal_position;

-- Verificar se o campo year_month foi criado
SELECT 'Campo year_month criado com sucesso!' as status
WHERE EXISTS (
    SELECT 1 FROM information_schema.columns 
    WHERE table_name = 'monthly_sells' AND column_name = 'year_month'
);

-- Listar índices criados
SELECT 
    indexname,
    indexdef
FROM pg_indexes
WHERE tablename = 'monthly_sells';

-- =====================================================
-- CONCESSÃO DE PRIVILÉGIOS
-- =====================================================

-- Conceder privilégios ao usuário glojas_user
GRANT ALL PRIVILEGES ON TABLE monthly_sells TO glojas_user;

SELECT 'Privilégios concedidos ao usuário glojas_user!' as status;

-- =====================================================
-- SCRIPT CONCLUÍDO COM SUCESSO
-- =====================================================

SELECT '=================================================' as info;
SELECT 'TABELA MONTHLY_SELLS CRIADA COM SUCESSO!' as info;
SELECT '=================================================' as info;
SELECT 'A tabela está pronta para ser utilizada pela aplicação.' as info;

