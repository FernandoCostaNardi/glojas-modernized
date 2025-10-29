# Guia de Configuração do Postman - StoreLegacyController

## 📋 Visão Geral

Este guia fornece instruções detalhadas para configurar e testar a API de Lojas Legacy (`StoreLegacyController`) da Business API usando o Postman. O controller oferece 2 endpoints para buscar lojas da Legacy API que não estão cadastradas no sistema e health check. Endpoints públicos, não requerem autenticação JWT.

## 🚀 Configuração Inicial

### 1. Importar Collection e Environment

#### Importar Collection
1. Abra o Postman
2. Clique em **Import**
3. Selecione o arquivo `StoreLegacyController-Postman-Collection.json`
4. Clique em **Import**

#### Importar Environment
1. Clique em **Import** novamente
2. Selecione o arquivo `StoreLegacyController-Postman-Environment.json`
3. Clique em **Import**

#### Ativar Environment
1. No canto superior direito, clique no dropdown de environments
2. Selecione **"StoreLegacyController - Business API Environment"**

### 2. Configurar Variáveis de Ambiente

#### Variáveis Principais
- `base_url`: `http://localhost:8089/api/business`
- `legacy_api_url`: `http://localhost:8087/api/legacy`

## 🧪 Cenários de Teste

### 1. Fluxo Completo de Integração

#### Passo 1: Health Check
```http
GET {{base_url}}/legacy/stores/health
```
Expected: Status 200, Mensagem de saúde

#### Passo 2: Buscar Lojas Não Cadastradas
```http
GET {{base_url}}/legacy/stores
```
Expected: Status 200, Lista de lojas não cadastradas

#### Passo 3: Verificar Formatação de IDs
- Validar que IDs têm 6 dígitos
- Validar que zeros à esquerda são preservados (ex: 000001)

### 2. Testes de Integração

#### Teste 1: Legacy API Disponível
```http
GET {{base_url}}/legacy/stores
```
Expected: Status 200, Lista de lojas

#### Teste 2: Legacy API Indisponível
```http
GET {{base_url}}/legacy/stores
```
Expected: Status 500, Erro de integração

### 3. Testes de Dados

#### Teste 1: Lojas Não Cadastradas Existem
```http
GET {{base_url}}/legacy/stores
```
Expected: Status 200, Lista não vazia

#### Teste 2: Todas as Lojas Já Cadastradas
```http
GET {{base_url}}/legacy/stores
```
Expected: Status 200, Lista vazia

## 📊 Códigos de Resposta HTTP

| Código | Descrição | Cenário |
|--------|-----------|---------|
| `200` | OK | Lojas não cadastradas encontradas, health check |
| `500` | Internal Server Error | Erro interno ou Legacy API indisponível |

## 🔍 Troubleshooting

### 1. Problemas Comuns

#### Erro 500 - Internal Server Error
- **Causa**: Legacy API indisponível ou erro de conexão
- **Solução**: Verificar se a Legacy API está rodando (porta 8087)

#### Lista Vazia
- **Causa**: Todas as lojas da Legacy API já estão cadastradas
- **Solução**: Normal, não é erro

### 2. Validação de Dados

#### ID da Loja
- Deve ter exatamente 6 dígitos
- Zeros à esquerda são preservados
- Exemplo válido: `000001`, `000002`
- Exemplo inválido: `1`, `01`

## 📝 Exemplos Práticos

### Exemplo 1: Buscar Lojas Não Cadastradas com cURL

```bash
curl -X GET "http://localhost:8089/api/business/legacy/stores" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json"
```

### Exemplo 2: Health Check com cURL

```bash
curl -X GET "http://localhost:8089/api/business/legacy/stores/health" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json"
```

## ⚠️ Considerações Importantes

### Limitações da API

1. **Sem autenticação**: Endpoints públicos (não requerem JWT)
2. **Sem autorização**: Não requer permissões específicas
3. **CORS**: Configurado para `http://localhost:3000`
4. **Integração**: Depende da disponibilidade da Legacy API (porta 8087)
5. **Apenas leitura**: Não permite criar, atualizar ou remover lojas
6. **Formatação de ID**: Preserva zeros à esquerda (000001, 000002)

### Dependências

- **Legacy API**: Fonte de dados de lojas (localhost:8087)
- **Database**: PostgreSQL (localhost:5432) - para comparação de lojas cadastradas
- **Integração**: WebClient para consumir Legacy API

### Diferenças dos Controllers Anteriores

1. **Sem autenticação**: Endpoints públicos (vs JWT obrigatório)
2. **Sem autorização**: Não requer permissões (vs PreAuthorize)
3. **Integração externa**: Consome Legacy API (vs dados internos)
4. **Apenas leitura**: Somente GET (vs CRUD completo)
5. **Comparação de dados**: Filtra lojas não cadastradas
6. **ID como String**: Preserva formatação de 6 dígitos
7. **Controller simples**: Apenas 2 endpoints

---

**Última Atualização**: 28/08/2025  
**Versão**: 1.0  
**Responsável**: Equipe de Desenvolvimento Business API


