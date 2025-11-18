-- =====================================================
-- SCRIPT PARA CRIAR TABELA EXCHANGES - BUSINESS API
-- =====================================================
-- Este script cria a tabela exchanges para armazenar trocas realizadas
-- Execute: psql -U glojas_user -d glojas_business -f create-exchange-table.sql
-- =====================================================

-- Verificar se estamos no banco correto
SELECT current_database() as database_atual;

-- =====================================================
-- CRIAÇÃO DA TABELA EXCHANGES
-- =====================================================

-- Criar tabela se não existir
CREATE TABLE IF NOT EXISTS exchanges (
    -- Identificador único (UUID)
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    
    -- Código do documento (VARCHAR com 6 caracteres)
    document_code VARCHAR(6) NOT NULL,
    
    -- Código da loja (VARCHAR com 6 caracteres) - FK para stores.code
    store_code VARCHAR(6) NOT NULL,
    
    -- Código da operação (VARCHAR com 10 caracteres)
    operation_code VARCHAR(10) NOT NULL,
    
    -- Código de origem (VARCHAR com 10 caracteres)
    origin_code VARCHAR(10) NOT NULL,
    
    -- Código do colaborador que realizou a troca (VARCHAR com 6 caracteres) - FK para collaborators.employee_code
    employee_code VARCHAR(6) NOT NULL,
    
    -- Número do documento/nota (VARCHAR com 20 caracteres)
    document_number VARCHAR(20),
    
    -- Chave NFE do documento (VARCHAR com 44 caracteres)
    nfe_key VARCHAR(44),
    
    -- Data de emissão do documento (TIMESTAMP)
    issue_date TIMESTAMP NOT NULL,
    
    -- Observação do documento (TEXT)
    observation TEXT,
    
    -- Número da nova venda extraído da observação (VARCHAR com 20 caracteres)
    new_sale_number VARCHAR(20),
    
    -- Chave NFE da venda extraída da observação (VARCHAR com 44 caracteres)
    new_sale_nfe_key VARCHAR(44),
    
    -- Data e hora de criação do registro
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- Data e hora da última atualização do registro
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- Constraint única para document_code + store_code
    CONSTRAINT uk_exchanges_document_store UNIQUE (document_code, store_code)
);

-- =====================================================
-- CRIAÇÃO DE FOREIGN KEYS
-- =====================================================

-- Foreign key para stores.code
ALTER TABLE exchanges
ADD CONSTRAINT fk_exchanges_store 
FOREIGN KEY (store_code) 
REFERENCES stores(code)
ON DELETE RESTRICT
ON UPDATE CASCADE;

-- Foreign key para collaborators.employee_code
ALTER TABLE exchanges
ADD CONSTRAINT fk_exchanges_employee 
FOREIGN KEY (employee_code) 
REFERENCES collaborators(employee_code)
ON DELETE RESTRICT
ON UPDATE CASCADE;

-- =====================================================
-- CRIAÇÃO DE ÍNDICES PARA OTIMIZAÇÃO
-- =====================================================

-- Índice para busca por código do documento
CREATE INDEX IF NOT EXISTS idx_exchanges_document_code 
ON exchanges(document_code);

-- Índice para busca por código da loja
CREATE INDEX IF NOT EXISTS idx_exchanges_store_code 
ON exchanges(store_code);

-- Índice para busca por data de emissão
CREATE INDEX IF NOT EXISTS idx_exchanges_issue_date 
ON exchanges(issue_date);

-- Índice composto para busca por documento e loja (já coberto pela constraint única, mas útil para queries)
CREATE INDEX IF NOT EXISTS idx_exchanges_document_store 
ON exchanges(document_code, store_code);

-- Índice para busca por código de origem
CREATE INDEX IF NOT EXISTS idx_exchanges_origin_code 
ON exchanges(origin_code);

-- Índice para busca por código de operação
CREATE INDEX IF NOT EXISTS idx_exchanges_operation_code 
ON exchanges(operation_code);

-- Índice para busca por data de criação (para ordenação)
CREATE INDEX IF NOT EXISTS idx_exchanges_created_at 
ON exchanges(created_at);

-- =====================================================
-- COMENTÁRIOS NA TABELA E COLUNAS
-- =====================================================

COMMENT ON TABLE exchanges IS 'Tabela para armazenar trocas realizadas recebidas da Legacy API';

COMMENT ON COLUMN exchanges.id IS 'Identificador único da troca (UUID)';
COMMENT ON COLUMN exchanges.document_code IS 'Código do documento da troca';
COMMENT ON COLUMN exchanges.store_code IS 'Código da loja onde a troca foi realizada (FK para stores.code)';
COMMENT ON COLUMN exchanges.operation_code IS 'Código da operação da troca';
COMMENT ON COLUMN exchanges.origin_code IS 'Código de origem da troca';
COMMENT ON COLUMN exchanges.employee_code IS 'Código do colaborador que realizou a troca (FK para collaborators.employee_code)';
COMMENT ON COLUMN exchanges.document_number IS 'Número do documento/nota da troca';
COMMENT ON COLUMN exchanges.nfe_key IS 'Chave NFE do documento da troca';
COMMENT ON COLUMN exchanges.issue_date IS 'Data de emissão do documento da troca';
COMMENT ON COLUMN exchanges.observation IS 'Observação do documento, pode conter informações sobre nova venda ou chave NFE';
COMMENT ON COLUMN exchanges.new_sale_number IS 'Número da nova venda extraído da observação';
COMMENT ON COLUMN exchanges.new_sale_nfe_key IS 'Chave NFE da venda extraída da observação';
COMMENT ON COLUMN exchanges.created_at IS 'Data e hora de criação do registro';
COMMENT ON COLUMN exchanges.updated_at IS 'Data e hora da última atualização do registro';

-- =====================================================
-- VERIFICAÇÃO DA CRIAÇÃO
-- =====================================================

-- Verificar se a tabela foi criada
SELECT 'Tabela exchanges criada com sucesso!' as status;

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
WHERE table_name = 'exchanges'
ORDER BY ordinal_position;

-- Verificar se os campos principais foram criados
SELECT 'Campo document_code criado com sucesso!' as status
WHERE EXISTS (
    SELECT 1 FROM information_schema.columns 
    WHERE table_name = 'exchanges' AND column_name = 'document_code'
);

SELECT 'Campo store_code criado com sucesso!' as status
WHERE EXISTS (
    SELECT 1 FROM information_schema.columns 
    WHERE table_name = 'exchanges' AND column_name = 'store_code'
);

SELECT 'Campo issue_date criado com sucesso!' as status
WHERE EXISTS (
    SELECT 1 FROM information_schema.columns 
    WHERE table_name = 'exchanges' AND column_name = 'issue_date'
);

-- Verificar se as foreign keys foram criadas
SELECT 'Foreign key para stores criada com sucesso!' as status
WHERE EXISTS (
    SELECT 1 FROM information_schema.table_constraints 
    WHERE table_name = 'exchanges' 
    AND constraint_name = 'fk_exchanges_store'
    AND constraint_type = 'FOREIGN KEY'
);

SELECT 'Foreign key para collaborators criada com sucesso!' as status
WHERE EXISTS (
    SELECT 1 FROM information_schema.table_constraints 
    WHERE table_name = 'exchanges' 
    AND constraint_name = 'fk_exchanges_employee'
    AND constraint_type = 'FOREIGN KEY'
);

-- Verificar constraint única
SELECT 'Constraint única document_code + store_code criada com sucesso!' as status
WHERE EXISTS (
    SELECT 1 FROM information_schema.table_constraints 
    WHERE table_name = 'exchanges' 
    AND constraint_name = 'uk_exchanges_document_store'
    AND constraint_type = 'UNIQUE'
);

-- Listar índices criados
SELECT 
    indexname,
    indexdef
FROM pg_indexes
WHERE tablename = 'exchanges';

-- Listar foreign keys criadas
SELECT
    tc.constraint_name,
    tc.table_name,
    kcu.column_name,
    ccu.table_name AS foreign_table_name,
    ccu.column_name AS foreign_column_name
FROM information_schema.table_constraints AS tc
JOIN information_schema.key_column_usage AS kcu
    ON tc.constraint_name = kcu.constraint_name
    AND tc.table_schema = kcu.table_schema
JOIN information_schema.constraint_column_usage AS ccu
    ON ccu.constraint_name = tc.constraint_name
    AND ccu.table_schema = tc.table_schema
WHERE tc.constraint_type = 'FOREIGN KEY'
    AND tc.table_name = 'exchanges';

-- =====================================================
-- CONCESSÃO DE PRIVILÉGIOS
-- =====================================================

-- Conceder privilégios ao usuário glojas_user
GRANT ALL PRIVILEGES ON TABLE exchanges TO glojas_user;

SELECT 'Privilégios concedidos ao usuário glojas_user!' as status;

-- =====================================================
-- SCRIPT CONCLUÍDO COM SUCESSO
-- =====================================================

SELECT '=================================================' as info;
SELECT 'TABELA EXCHANGES CRIADA COM SUCESSO!' as info;
SELECT '=================================================' as info;
SELECT 'A tabela está pronta para ser utilizada pela aplicação.' as info;

