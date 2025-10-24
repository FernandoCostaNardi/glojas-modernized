#!/bin/bash

# Script para validar as correções da Legacy API
# Execute na VPS após aplicar as correções

echo "=== VALIDANDO CORREÇÕES DA LEGACY API ==="

echo "1. Verificando se as APIs estão rodando..."
netstat -tlnp | grep -E '8089|8087'

echo ""
echo "2. Testando Business API localmente..."
curl -v http://localhost:8089/api/business/legacy/stores

echo ""
echo "3. Testando via Nginx..."
curl -v https://gestaosmarteletron.com.br/api/legacy/stores

echo ""
echo "4. Verificando logs da Business API..."
pm2 logs business-api --lines 20

echo ""
echo "5. Verificando logs do Nginx..."
tail -n 10 /var/log/nginx/glojas-access.log | grep -E "legacy|stores"

echo ""
echo "=== VALIDAÇÃO CONCLUÍDA ==="
echo "Se todos os testes retornaram dados JSON válidos, as correções funcionaram!"
