-- =====================================================
-- SCRIPT PARA CRIAR TABELA PRODUCTS - BUSINESS API
-- =====================================================
-- Este script cria a tabela products para armazenar produtos
-- Execute: psql -U glojas_user -d glojas_business -f create-products-table.sql
-- =====================================================

-- Verificar se estamos no banco correto
SELECT current_database() as database_atual;

-- =====================================================
-- CRIAÇÃO DA TABELA PRODUCTS
-- =====================================================

-- Criar tabela se não existir
CREATE TABLE IF NOT EXISTS products (
    -- Identificador único (UUID)
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    
    -- Código de referência do produto (VARCHAR com 6 caracteres, único)
    product_ref_code VARCHAR(6) NOT NULL UNIQUE,
    
    -- Código do produto (VARCHAR com 6 caracteres)
    product_code VARCHAR(6) NOT NULL,
    
    -- Seção do produto (VARCHAR com até 50 caracteres)
    section VARCHAR(50),
    
    -- Grupo do produto (VARCHAR com até 50 caracteres)
    "group" VARCHAR(50),
    
    -- Subgrupo do produto (VARCHAR com até 50 caracteres)
    subgroup VARCHAR(50),
    
    -- Marca do produto (VARCHAR com até 50 caracteres)
    brand VARCHAR(50),
    
    -- Descrição do produto (VARCHAR com até 250 caracteres)
    product_description VARCHAR(250),
    
    -- Data e hora de criação do registro
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- Data e hora da última atualização do registro
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- CRIAÇÃO DE ÍNDICES PARA OTIMIZAÇÃO
-- =====================================================

-- Índice único para busca por código de referência do produto (já criado via UNIQUE constraint, mas criamos índice explícito)
CREATE UNIQUE INDEX IF NOT EXISTS idx_products_product_ref_code 
ON products(product_ref_code);

-- Índice para busca por código do produto
CREATE INDEX IF NOT EXISTS idx_products_product_code 
ON products(product_code);

-- Índice para busca por marca
CREATE INDEX IF NOT EXISTS idx_products_brand 
ON products(brand);

-- Índice para busca por seção
CREATE INDEX IF NOT EXISTS idx_products_section 
ON products(section);

-- Índice para busca por data de criação (para ordenação)
CREATE INDEX IF NOT EXISTS idx_products_created_at 
ON products(created_at);

-- =====================================================
-- COMENTÁRIOS NA TABELA E COLUNAS
-- =====================================================

COMMENT ON TABLE products IS 'Tabela para armazenar produtos recebidos da Legacy API';

COMMENT ON COLUMN products.id IS 'Identificador único do produto (UUID)';
COMMENT ON COLUMN products.product_ref_code IS 'Código único de referência do produto (ex: 010984)';
COMMENT ON COLUMN products.product_code IS 'Código do produto (ex: 011930)';
COMMENT ON COLUMN products.section IS 'Seção do produto (ex: INFORMATICA)';
COMMENT ON COLUMN products."group" IS 'Grupo do produto (ex: MOUSE)';
COMMENT ON COLUMN products.subgroup IS 'Subgrupo do produto (ex: MOUSE USB C/FIO)';
COMMENT ON COLUMN products.brand IS 'Marca do produto (ex: MAXPRINT)';
COMMENT ON COLUMN products.product_description IS 'Descrição completa do produto';
COMMENT ON COLUMN products.created_at IS 'Data e hora de criação do registro';
COMMENT ON COLUMN products.updated_at IS 'Data e hora da última atualização do registro';

-- =====================================================
-- VERIFICAÇÃO DA CRIAÇÃO
-- =====================================================

-- Verificar se a tabela foi criada
SELECT 'Tabela products criada com sucesso!' as status;

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
WHERE table_name = 'products'
ORDER BY ordinal_position;

-- Verificar se os campos principais foram criados
SELECT 'Campo product_ref_code criado com sucesso!' as status
WHERE EXISTS (
    SELECT 1 FROM information_schema.columns 
    WHERE table_name = 'products' AND column_name = 'product_ref_code'
);

SELECT 'Campo product_code criado com sucesso!' as status
WHERE EXISTS (
    SELECT 1 FROM information_schema.columns 
    WHERE table_name = 'products' AND column_name = 'product_code'
);

SELECT 'Campo brand criado com sucesso!' as status
WHERE EXISTS (
    SELECT 1 FROM information_schema.columns 
    WHERE table_name = 'products' AND column_name = 'brand'
);

SELECT 'Campo section criado com sucesso!' as status
WHERE EXISTS (
    SELECT 1 FROM information_schema.columns 
    WHERE table_name = 'products' AND column_name = 'section'
);

-- Listar índices criados
SELECT 
    indexname,
    indexdef
FROM pg_indexes
WHERE tablename = 'products';

-- =====================================================
-- CONCESSÃO DE PRIVILÉGIOS
-- =====================================================

-- Conceder privilégios ao usuário glojas_user
GRANT ALL PRIVILEGES ON TABLE products TO glojas_user;

SELECT 'Privilégios concedidos ao usuário glojas_user!' as status;

-- =====================================================
-- SCRIPT CONCLUÍDO COM SUCESSO
-- =====================================================

SELECT '=================================================' as info;
SELECT 'TABELA PRODUCTS CRIADA COM SUCESSO!' as info;
SELECT '=================================================' as info;
SELECT 'A tabela está pronta para ser utilizada pela aplicação.' as info;

