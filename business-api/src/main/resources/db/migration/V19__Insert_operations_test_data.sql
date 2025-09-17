-- Inserção de dados de teste para a tabela operations
-- Inclui operações com diferentes fontes de operação

INSERT INTO operations (code, description, operation_source) VALUES
-- Operações SELL
('VEND001', 'Venda de produto', 'SELL'),
('CANC001', 'Cancelamento de venda', 'SELL'),
('TROC001', 'Troca de produto', 'SELL'),
('DEV001', 'Devolução de produto', 'SELL'),
('DESC001', 'Desconto aplicado', 'SELL'),
('ACRESC001', 'Acréscimo aplicado', 'SELL'),

-- Operações EXCHANGE
('SYNC001', 'Sincronização de dados', 'EXCHANGE'),
('EXP001', 'Exportação de relatórios', 'EXCHANGE'),
('IMP001', 'Importação de produtos', 'EXCHANGE'),
('BACK001', 'Backup de dados', 'EXCHANGE'),
('REST001', 'Restauração de dados', 'EXCHANGE'),
('MIGR001', 'Migração de dados', 'EXCHANGE');

-- Comentário sobre os dados inseridos
-- Estes dados servem para testes e demonstração das diferentes fontes de operação
