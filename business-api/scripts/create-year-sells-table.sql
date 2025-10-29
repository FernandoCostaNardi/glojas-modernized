-- =====================================================
-- SCRIPT PARA CRIAR TABELA YEAR_SELLS - BUSINESS API
-- =====================================================
-- Este script cria a tabela year_sells para armazenar vendas anuais
-- Execute: psql -U glojas_user -d glojas_business -f create-year-sells-table.sql
-- =====================================================

-- Verificar se estamos no banco correto
SELECT current_database() as database_atual;

-- =====================================================
-- CRIAÇÃO DA TABELA YEAR_SELLS
-- =====================================================

-- Criar tabela se não existir
CREATE TABLE IF NOT EXISTS year_sells (
    -- Identificador único (UUID)
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    
    -- Código da loja (VARCHAR com até 10 caracteres)
    store_code VARCHAR(10) NOT NULL,
    
    -- ID da loja (UUID - referência)
    store_id UUID NOT NULL,
    
    -- Nome da loja (VARCHAR com até 255 caracteres)
    store_name VARCHAR(255) NOT NULL,
    
    -- Valor total das vendas anuais (NUMERIC com 15 dígitos, 2 decimais)
    total NUMERIC(15, 2) NOT NULL,
    
    -- Ano das vendas (INTEGER)
    year INTEGER NOT NULL,
    
    -- Data e hora de criação do registro
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- Data e hora da última atualização do registro
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- CRIAÇÃO DE ÍNDICES PARA OTIMIZAÇÃO
-- =====================================================

-- Índice para busca por código da loja
CREATE INDEX IF NOT EXISTS idx_year_sells_store_code 
ON year_sells(store_code);

-- Índice para busca por ID da loja
CREATE INDEX IF NOT EXISTS idx_year_sells_store_id 
ON year_sells(store_id);

-- Índice para busca por ano
CREATE INDEX IF NOT EXISTS idx_year_sells_year 
ON year_sells(year);

-- Índice para busca por data de criação
CREATE INDEX IF NOT EXISTS idx_year_sells_created_at 
ON year_sells(created_at);

-- Índice composto único para garantir unicidade por loja e ano
CREATE UNIQUE INDEX IF NOT EXISTS idx_year_sells_store_year 
ON year_sells(store_id, year);

-- =====================================================
-- COMENTÁRIOS NA TABELA E COLUNAS
-- =====================================================

COMMENT ON TABLE year_sells IS 'Tabela para armazenar vendas anuais consolidadas por loja';

COMMENT ON COLUMN year_sells.id IS 'Identificador único da venda anual (UUID)';
COMMENT ON COLUMN year_sells.store_code IS 'Código da loja para identificação rápida';
COMMENT ON COLUMN year_sells.store_id IS 'ID único da loja (referência)';
COMMENT ON COLUMN year_sells.store_name IS 'Nome comercial da loja';
COMMENT ON COLUMN year_sells.total IS 'Valor total consolidado das vendas anuais';
COMMENT ON COLUMN year_sells.year IS 'Ano das vendas para agrupamento';
COMMENT ON COLUMN year_sells.created_at IS 'Data e hora de criação do registro';
COMMENT ON COLUMN year_sells.updated_at IS 'Data e hora da última atualização do registro';

-- =====================================================
-- VERIFICAÇÃO DA CRIAÇÃO
-- =====================================================

-- Verificar se a tabela foi criada
SELECT 'Tabela year_sells criada com sucesso!' as status;

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
WHERE table_name = 'year_sells'
ORDER BY ordinal_position;

-- Verificar se o campo year foi criado
SELECT 'Campo year criado com sucesso!' as status
WHERE EXISTS (
    SELECT 1 FROM information_schema.columns 
    WHERE table_name = 'year_sells' AND column_name = 'year'
);

-- Listar índices criados
SELECT 
    indexname,
    indexdef
FROM pg_indexes
WHERE tablename = 'year_sells';

-- =====================================================
-- CONCESSÃO DE PRIVILÉGIOS
-- =====================================================

-- Conceder privilégios ao usuário glojas_user
GRANT ALL PRIVILEGES ON TABLE year_sells TO glojas_user;

SELECT 'Privilégios concedidos ao usuário glojas_user!' as status;

-- =====================================================
-- SCRIPT CONCLUÍDO COM SUCESSO
-- =====================================================

SELECT '=================================================' as info;
SELECT 'TABELA YEAR_SELLS CRIADA COM SUCESSO!' as info;
SELECT '=================================================' as info;
SELECT 'A tabela está pronta para ser utilizada pela aplicação.' as info;
