-- Teste da query de estoque
-- Verificar se há dados na tabela estoque

-- 1. Verificar se há dados na tabela estoque
SELECT COUNT(*) as total_estoque FROM estoque;

-- 2. Verificar alguns registros da tabela estoque
SELECT TOP 10 * FROM estoque;

-- 3. Verificar se há dados com loccod = 1
SELECT COUNT(*) as estoque_loccod_1 FROM estoque WHERE loccod = 1;

-- 4. Verificar se há dados nas tabelas relacionadas
SELECT COUNT(*) as total_referencias FROM referencia;
SELECT COUNT(*) as total_produtos FROM produto;
SELECT COUNT(*) as total_marcas FROM marca;

-- 5. Testar a query completa com LIMIT
SELECT TOP 5
    r.refplu,
    m.mardes AS marca,
    p.prodes AS descricao,
    MAX(CASE WHEN e.lojcod = 1 THEN e.esttot ELSE 0 END) AS loj1,
    MAX(CASE WHEN e.lojcod = 2 THEN e.esttot ELSE 0 END) AS loj2,
    MAX(CASE WHEN e.lojcod = 3 THEN e.esttot ELSE 0 END) AS loj3,
    MAX(CASE WHEN e.lojcod = 4 THEN e.esttot ELSE 0 END) AS loj4,
    MAX(CASE WHEN e.lojcod = 5 THEN e.esttot ELSE 0 END) AS loj5,
    MAX(CASE WHEN e.lojcod = 6 THEN e.esttot ELSE 0 END) AS loj6,
    MAX(CASE WHEN e.lojcod = 7 THEN e.esttot ELSE 0 END) AS loj7,
    MAX(CASE WHEN e.lojcod = 8 THEN e.esttot ELSE 0 END) AS loj8,
    MAX(CASE WHEN e.lojcod = 9 THEN e.esttot ELSE 0 END) AS loj9,
    MAX(CASE WHEN e.lojcod = 10 THEN e.esttot ELSE 0 END) AS loj10,
    MAX(CASE WHEN e.lojcod = 11 THEN e.esttot ELSE 0 END) AS loj11,
    MAX(CASE WHEN e.lojcod = 12 THEN e.esttot ELSE 0 END) AS loj12,
    MAX(CASE WHEN e.lojcod = 13 THEN e.esttot ELSE 0 END) AS loj13,
    MAX(CASE WHEN e.lojcod = 14 THEN e.esttot ELSE 0 END) AS loj14
FROM estoque e
INNER JOIN referencia r ON e.refplu = r.refplu
INNER JOIN produto p ON r.procod = p.procod
INNER JOIN marca m ON p.marcod = m.marcod
WHERE e.loccod = 1
GROUP BY r.refplu, m.mardes, p.prodes
ORDER BY r.refplu;

-- 6. Verificar se há dados sem o filtro loccod = 1
SELECT TOP 5
    r.refplu,
    m.mardes AS marca,
    p.prodes AS descricao,
    e.loccod,
    e.lojcod,
    e.esttot
FROM estoque e
INNER JOIN referencia r ON e.refplu = r.refplu
INNER JOIN produto p ON r.procod = p.procod
INNER JOIN marca m ON p.marcod = m.marcod
ORDER BY r.refplu;
