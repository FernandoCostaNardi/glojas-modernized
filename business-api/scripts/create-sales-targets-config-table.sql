-- =====================================================
-- SCRIPT PARA CRIAR TABELA SALES_TARGETS_CONFIG - BUSINESS API
-- =====================================================
-- Este script cria a tabela sales_targets_config para armazenar
-- configurações de metas de vendas e percentuais de comissão por loja e competência
-- Execute: psql -U glojas_user -d glojas_business -f create-sales-targets-config-table.sql
-- =====================================================

-- Verificar se estamos no banco correto
SELECT current_database() as database_atual;

-- =====================================================
-- CRIAÇÃO DA TABELA SALES_TARGETS_CONFIG
-- =====================================================

-- Criar tabela se não existir
CREATE TABLE IF NOT EXISTS sales_targets_config (
    -- Identificador único (UUID)
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    
    -- Código da loja (VARCHAR com 6 caracteres)
    store_code VARCHAR(6) NOT NULL,
    
    -- Data de competência no formato MM/YYYY (VARCHAR com 7 caracteres)
    competence_date VARCHAR(7) NOT NULL,
    
    -- Meta de venda da loja (NUMERIC com 15 dígitos, 2 decimais)
    store_sales_target NUMERIC(15,2) NOT NULL,
    
    -- Percentual de comissão coletiva para o gerente (NUMERIC com 5 dígitos, 2 decimais)
    collective_commission_percentage NUMERIC(5,2) NOT NULL,
    
    -- Meta de venda individual para vendedores (NUMERIC com 15 dígitos, 2 decimais)
    individual_sales_target NUMERIC(15,2) NOT NULL,
    
    -- Percentual de comissão individual para vendedores (NUMERIC com 5 dígitos, 2 decimais)
    individual_commission_percentage NUMERIC(5,2) NOT NULL,
    
    -- Data e hora de criação do registro
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- Data e hora da última atualização do registro
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- CRIAÇÃO DE ÍNDICES PARA OTIMIZAÇÃO
-- =====================================================

-- Índice para busca por código da loja
CREATE INDEX IF NOT EXISTS idx_sales_targets_config_store_code 
ON sales_targets_config(store_code);

-- Índice para busca por data de competência
CREATE INDEX IF NOT EXISTS idx_sales_targets_config_competence_date 
ON sales_targets_config(competence_date);

-- Índice composto para busca por loja e competência (otimização de buscas combinadas)
CREATE INDEX IF NOT EXISTS idx_sales_targets_config_store_competence 
ON sales_targets_config(store_code, competence_date);

-- Índice para busca por data de criação (para ordenação)
CREATE INDEX IF NOT EXISTS idx_sales_targets_config_created_at 
ON sales_targets_config(created_at);

-- =====================================================
-- COMENTÁRIOS NA TABELA E COLUNAS
-- =====================================================

COMMENT ON TABLE sales_targets_config IS 'Tabela para armazenar configurações de metas de vendas e percentuais de comissão por loja e competência. Permite múltiplas metas escalonadas para a mesma loja e competência.';

COMMENT ON COLUMN sales_targets_config.id IS 'Identificador único da configuração (UUID)';
COMMENT ON COLUMN sales_targets_config.store_code IS 'Código da loja (ex: 000001)';
COMMENT ON COLUMN sales_targets_config.competence_date IS 'Data de competência no formato MM/YYYY (ex: 01/2024)';
COMMENT ON COLUMN sales_targets_config.store_sales_target IS 'Meta de venda da loja em valor monetário';
COMMENT ON COLUMN sales_targets_config.collective_commission_percentage IS 'Percentual de comissão coletiva que o gerente irá ganhar (permite valores acima de 100 para incentivos)';
COMMENT ON COLUMN sales_targets_config.individual_sales_target IS 'Meta de venda individual em valor monetário feita pelos vendedores da loja';
COMMENT ON COLUMN sales_targets_config.individual_commission_percentage IS 'Percentual de comissão individual que o vendedor irá ganhar (permite valores acima de 100 para incentivos)';
COMMENT ON COLUMN sales_targets_config.created_at IS 'Data e hora de criação do registro';
COMMENT ON COLUMN sales_targets_config.updated_at IS 'Data e hora da última atualização do registro';

-- =====================================================
-- VERIFICAÇÃO DA CRIAÇÃO
-- =====================================================

-- Verificar se a tabela foi criada
SELECT 'Tabela sales_targets_config criada com sucesso!' as status;

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
WHERE table_name = 'sales_targets_config'
ORDER BY ordinal_position;

-- Verificar se os campos principais foram criados
SELECT 'Campo store_code criado com sucesso!' as status
WHERE EXISTS (
    SELECT 1 FROM information_schema.columns 
    WHERE table_name = 'sales_targets_config' AND column_name = 'store_code'
);

SELECT 'Campo competence_date criado com sucesso!' as status
WHERE EXISTS (
    SELECT 1 FROM information_schema.columns 
    WHERE table_name = 'sales_targets_config' AND column_name = 'competence_date'
);

SELECT 'Campo store_sales_target criado com sucesso!' as status
WHERE EXISTS (
    SELECT 1 FROM information_schema.columns 
    WHERE table_name = 'sales_targets_config' AND column_name = 'store_sales_target'
);

SELECT 'Campo collective_commission_percentage criado com sucesso!' as status
WHERE EXISTS (
    SELECT 1 FROM information_schema.columns 
    WHERE table_name = 'sales_targets_config' AND column_name = 'collective_commission_percentage'
);

SELECT 'Campo individual_sales_target criado com sucesso!' as status
WHERE EXISTS (
    SELECT 1 FROM information_schema.columns 
    WHERE table_name = 'sales_targets_config' AND column_name = 'individual_sales_target'
);

SELECT 'Campo individual_commission_percentage criado com sucesso!' as status
WHERE EXISTS (
    SELECT 1 FROM information_schema.columns 
    WHERE table_name = 'sales_targets_config' AND column_name = 'individual_commission_percentage'
);

-- Listar índices criados
SELECT 
    indexname,
    indexdef
FROM pg_indexes
WHERE tablename = 'sales_targets_config';

-- =====================================================
-- CONCESSÃO DE PRIVILÉGIOS
-- =====================================================

-- Conceder privilégios ao usuário glojas_user
GRANT ALL PRIVILEGES ON TABLE sales_targets_config TO glojas_user;

SELECT 'Privilégios concedidos ao usuário glojas_user!' as status;

-- =====================================================
-- SCRIPT CONCLUÍDO COM SUCESSO
-- =====================================================

SELECT '=================================================' as info;
SELECT 'TABELA SALES_TARGETS_CONFIG CRIADA COM SUCESSO!' as info;
SELECT '=================================================' as info;
SELECT 'A tabela está pronta para ser utilizada pela aplicação.' as info;
SELECT 'NOTA: Esta tabela permite múltiplas configurações para a mesma loja e competência, permitindo metas escalonadas.' as info;

