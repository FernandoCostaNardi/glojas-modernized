SELECT
    p.PROCOD as codigo,
    s.SECDES as secao,
    g.GRPDES as grupo,
    sb.SBGDES as subgrupo,
    m.MARDES as marca,
    rf.REFCOD as part_number_codigo,
    r.REFPLU as refplu,
    p.PRODES as descricao,
    p.PRONCM as ncm
FROM PRODUTO p
JOIN SECAO s ON p.SECCOD = s.SECCOD
JOIN GRUPO g ON p.GRPCOD = g.GRPCOD
JOIN SUBGRUPO sb ON p.SBGCOD = sb.SBGCOD
JOIN MARCA m ON p.MARCOD = m.MARCOD
JOIN REFERENCIA r ON p.PROCOD = r.PROCOD
LEFT JOIN REFERENCIA_FABRICANTE rf ON r.REFPLU = rf.REFPLU
WHERE s.SECCOD = g.SECCOD
    AND sb.GRPCOD = g.GRPCOD
    AND sb.SECCOD = s.SECCOD
    AND (:secao IS NULL OR s.SECDES LIKE %:secao%)
    AND (:grupo IS NULL OR g.GRPDES LIKE %:grupo%)
    AND (:marca IS NULL OR m.MARDES LIKE %:marca%)
    AND (:descricao IS NULL OR p.PRODES LIKE %:descricao%)
ORDER BY 
    CASE WHEN :sortBy = 'codigo' THEN p.PROCOD END,
    CASE WHEN :sortBy = 'secao' THEN s.SECDES END,
    CASE WHEN :sortBy = 'grupo' THEN g.GRPDES END,
    CASE WHEN :sortBy = 'marca' THEN m.MARDES END,
    CASE WHEN :sortBy = 'descricao' THEN p.PRODES END,
    p.PROCOD
