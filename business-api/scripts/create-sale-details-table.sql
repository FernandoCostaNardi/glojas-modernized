-- =====================================================
-- SCRIPT PARA CRIAR TABELA SALE_DETAILS - BUSINESS API
-- =====================================================
-- Este script cria a tabela sale_details para armazenar vendas detalhadas
-- Execute: psql -U glojas_user -d glojas_business -f create-sale-details-table.sql
-- =====================================================

-- Verificar se estamos no banco correto
SELECT current_database() as database_atual;

-- =====================================================
-- CRIAÇÃO DA TABELA SALE_DETAILS
-- =====================================================

-- Criar tabela se não existir
CREATE TABLE IF NOT EXISTS sale_details (
    -- Identificador único (UUID)
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    
    -- Data da venda (TIMESTAMP)
    sale_date TIMESTAMP NOT NULL,
    
    -- Código da venda (VARCHAR com até 10 caracteres)
    sale_code VARCHAR(10) NOT NULL,
    
    -- Sequência do item na venda (INTEGER)
    item_sequence INTEGER NOT NULL,
    
    -- Código do colaborador (VARCHAR com 6 caracteres) - FK para collaborators.employee_code
    collaborator_code VARCHAR(6) NOT NULL,
    
    -- Código da loja (VARCHAR com 6 caracteres) - FK para stores.code
    store_code VARCHAR(6) NOT NULL,
    
    -- Código de referência do produto (VARCHAR com 6 caracteres) - FK para products.product_ref_code
    product_ref_code VARCHAR(6) NOT NULL,
    
    -- Código NCM do produto (VARCHAR com 8 caracteres)
    ncm VARCHAR(8),
    
    -- Quantidade vendida (INTEGER)
    quantity INTEGER NOT NULL,
    
    -- Preço unitário (NUMERIC com 15 dígitos, 2 decimais)
    unit_price NUMERIC(15, 2) NOT NULL,
    
    -- Preço total (NUMERIC com 15 dígitos, 2 decimais)
    total_price NUMERIC(15, 2) NOT NULL,
    
    -- Data e hora de criação do registro
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- Data e hora da última atualização do registro
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- CRIAÇÃO DE FOREIGN KEYS
-- =====================================================

-- Foreign key para collaborators.employee_code
ALTER TABLE sale_details
ADD CONSTRAINT fk_sale_details_collaborator 
FOREIGN KEY (collaborator_code) 
REFERENCES collaborators(employee_code)
ON DELETE RESTRICT
ON UPDATE CASCADE;

-- Foreign key para stores.code
ALTER TABLE sale_details
ADD CONSTRAINT fk_sale_details_store 
FOREIGN KEY (store_code) 
REFERENCES stores(code)
ON DELETE RESTRICT
ON UPDATE CASCADE;

-- Foreign key para products.product_ref_code
ALTER TABLE sale_details
ADD CONSTRAINT fk_sale_details_product 
FOREIGN KEY (product_ref_code) 
REFERENCES products(product_ref_code)
ON DELETE RESTRICT
ON UPDATE CASCADE;

-- =====================================================
-- CRIAÇÃO DE ÍNDICES PARA OTIMIZAÇÃO
-- =====================================================

-- Índice para busca por data da venda
CREATE INDEX IF NOT EXISTS idx_sale_details_sale_date 
ON sale_details(sale_date);

-- Índice para busca por código da venda
CREATE INDEX IF NOT EXISTS idx_sale_details_sale_code 
ON sale_details(sale_code);

-- Índice para busca por código do colaborador
CREATE INDEX IF NOT EXISTS idx_sale_details_collaborator_code 
ON sale_details(collaborator_code);

-- Índice para busca por código da loja
CREATE INDEX IF NOT EXISTS idx_sale_details_store_code 
ON sale_details(store_code);

-- Índice para busca por código de referência do produto
CREATE INDEX IF NOT EXISTS idx_sale_details_product_ref_code 
ON sale_details(product_ref_code);

-- Índice composto para busca por venda e sequência do item
CREATE INDEX IF NOT EXISTS idx_sale_details_sale_code_item_sequence 
ON sale_details(sale_code, item_sequence);

-- Índice para busca por data de criação (para ordenação)
CREATE INDEX IF NOT EXISTS idx_sale_details_created_at 
ON sale_details(created_at);

-- =====================================================
-- COMENTÁRIOS NA TABELA E COLUNAS
-- =====================================================

COMMENT ON TABLE sale_details IS 'Tabela para armazenar vendas detalhadas recebidas da Legacy API';

COMMENT ON COLUMN sale_details.id IS 'Identificador único da venda detalhada (UUID)';
COMMENT ON COLUMN sale_details.sale_date IS 'Data e hora da venda';
COMMENT ON COLUMN sale_details.sale_code IS 'Código único da venda (ex: 037955)';
COMMENT ON COLUMN sale_details.item_sequence IS 'Sequência do item na venda';
COMMENT ON COLUMN sale_details.collaborator_code IS 'Código do colaborador que realizou a venda (FK para collaborators.employee_code)';
COMMENT ON COLUMN sale_details.store_code IS 'Código da loja onde a venda foi realizada (FK para stores.code)';
COMMENT ON COLUMN sale_details.product_ref_code IS 'Código de referência do produto vendido (FK para products.product_ref_code)';
COMMENT ON COLUMN sale_details.ncm IS 'Código NCM do produto (Nomenclatura Comum do Mercosul)';
COMMENT ON COLUMN sale_details.quantity IS 'Quantidade de produtos vendidos';
COMMENT ON COLUMN sale_details.unit_price IS 'Preço unitário do produto';
COMMENT ON COLUMN sale_details.total_price IS 'Preço total do item (quantity * unit_price)';
COMMENT ON COLUMN sale_details.created_at IS 'Data e hora de criação do registro';
COMMENT ON COLUMN sale_details.updated_at IS 'Data e hora da última atualização do registro';

-- =====================================================
-- VERIFICAÇÃO DA CRIAÇÃO
-- =====================================================

-- Verificar se a tabela foi criada
SELECT 'Tabela sale_details criada com sucesso!' as status;

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
WHERE table_name = 'sale_details'
ORDER BY ordinal_position;

-- Verificar se os campos principais foram criados
SELECT 'Campo sale_date criado com sucesso!' as status
WHERE EXISTS (
    SELECT 1 FROM information_schema.columns 
    WHERE table_name = 'sale_details' AND column_name = 'sale_date'
);

SELECT 'Campo sale_code criado com sucesso!' as status
WHERE EXISTS (
    SELECT 1 FROM information_schema.columns 
    WHERE table_name = 'sale_details' AND column_name = 'sale_code'
);

SELECT 'Campo collaborator_code criado com sucesso!' as status
WHERE EXISTS (
    SELECT 1 FROM information_schema.columns 
    WHERE table_name = 'sale_details' AND column_name = 'collaborator_code'
);

SELECT 'Campo store_code criado com sucesso!' as status
WHERE EXISTS (
    SELECT 1 FROM information_schema.columns 
    WHERE table_name = 'sale_details' AND column_name = 'store_code'
);

SELECT 'Campo product_ref_code criado com sucesso!' as status
WHERE EXISTS (
    SELECT 1 FROM information_schema.columns 
    WHERE table_name = 'sale_details' AND column_name = 'product_ref_code'
);

-- Verificar se as foreign keys foram criadas
SELECT 'Foreign key para collaborators criada com sucesso!' as status
WHERE EXISTS (
    SELECT 1 FROM information_schema.table_constraints 
    WHERE table_name = 'sale_details' 
    AND constraint_name = 'fk_sale_details_collaborator'
    AND constraint_type = 'FOREIGN KEY'
);

SELECT 'Foreign key para stores criada com sucesso!' as status
WHERE EXISTS (
    SELECT 1 FROM information_schema.table_constraints 
    WHERE table_name = 'sale_details' 
    AND constraint_name = 'fk_sale_details_store'
    AND constraint_type = 'FOREIGN KEY'
);

SELECT 'Foreign key para products criada com sucesso!' as status
WHERE EXISTS (
    SELECT 1 FROM information_schema.table_constraints 
    WHERE table_name = 'sale_details' 
    AND constraint_name = 'fk_sale_details_product'
    AND constraint_type = 'FOREIGN KEY'
);

-- Listar índices criados
SELECT 
    indexname,
    indexdef
FROM pg_indexes
WHERE tablename = 'sale_details';

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
    AND tc.table_name = 'sale_details';

-- =====================================================
-- CONCESSÃO DE PRIVILÉGIOS
-- =====================================================

-- Conceder privilégios ao usuário glojas_user
GRANT ALL PRIVILEGES ON TABLE sale_details TO glojas_user;

SELECT 'Privilégios concedidos ao usuário glojas_user!' as status;

-- =====================================================
-- SCRIPT CONCLUÍDO COM SUCESSO
-- =====================================================

SELECT '=================================================' as info;
SELECT 'TABELA SALE_DETAILS CRIADA COM SUCESSO!' as info;
SELECT '=================================================' as info;
SELECT 'A tabela está pronta para ser utilizada pela aplicação.' as info;

