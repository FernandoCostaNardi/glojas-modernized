# 🚀 API DE PRODUTOS CADASTRADOS

## 📋 **DESCRIÇÃO**
API para busca de produtos cadastrados com filtros, paginação e ordenação usando query nativa SQL.

## 🌐 **ENDPOINT PRINCIPAL**

### **GET** `/api/legacy/products/registered`

Busca produtos cadastrados com informações completas (seção, grupo, subgrupo, marca, part number, etc.)

## 🔧 **PARÂMETROS**

### **Filtros (Opcionais):**
- `secao` - Filtra por seção (ex: "ELETRONICOS")
- `grupo` - Filtra por grupo (ex: "COMPUTADORES")
- `marca` - Filtra por marca (ex: "SAMSUNG")
- `descricao` - Filtra por descrição do produto

### **Paginação:**
- `page` - Número da página (0-based, padrão: 0)
- `size` - Tamanho da página (padrão: 20, máximo: 100)

### **Ordenação:**
- `sortBy` - Campo para ordenação (padrão: "codigo")
  - Valores válidos: `codigo`, `secao`, `grupo`, `marca`, `descricao`
- `sortDir` - Direção da ordenação (padrão: "asc")
  - Valores válidos: `asc`, `desc`

## 📊 **EXEMPLOS DE USO**

### **1. Busca Simples**
```bash
GET /api/legacy/products/registered
```

### **2. Com Filtros**
```bash
GET /api/legacy/products/registered?secao=ELETRONICOS&grupo=COMPUTADORES
```

### **3. Com Paginação**
```bash
GET /api/legacy/products/registered?page=0&size=10
```

### **4. Com Ordenação**
```bash
GET /api/legacy/products/registered?sortBy=descricao&sortDir=desc
```

### **5. Completo**
```bash
GET /api/legacy/products/registered?secao=ELETRONICOS&page=0&size=20&sortBy=marca&sortDir=asc
```

## 📋 **RESPOSTA**

### **Estrutura da Resposta:**
```json
{
  "content": [
    {
      "codigo": 12345,
      "secao": "ELETRONICOS",
      "grupo": "COMPUTADORES",
      "subgrupo": "NOTEBOOKS",
      "marca": "SAMSUNG",
      "partNumberCodigo": "NP900X3T",
      "refplu": "123456",
      "descricao": "Notebook Samsung NP900X3T",
      "ncm": "84713000"
    }
  ],
  "totalElements": 150,
  "totalPages": 8,
  "currentPage": 0,
  "pageSize": 20,
  "hasNext": true,
  "hasPrevious": false,
  "filters": {
    "secao": "ELETRONICOS",
    "grupo": null,
    "marca": null,
    "descricao": null
  },
  "sorting": {
    "sortBy": "codigo",
    "sortDir": "asc"
  }
}
```

### **Campos do Produto:**
- `codigo` - Código único do produto
- `secao` - Descrição da seção
- `grupo` - Descrição do grupo
- `subgrupo` - Descrição do subgrupo
- `marca` - Descrição da marca
- `partNumberCodigo` - Código do part number
- `refplu` - Código PLU da referência
- `descricao` - Descrição do produto
- `ncm` - Código NCM do produto

## 🏗️ **ARQUITETURA**

### **Estrutura de Arquivos:**
```
src/main/resources/
└── sql/
    ├── products-registered.sql      # Query principal
    └── products-registered-count.sql # Query de contagem

src/main/java/com/sysconard/legacy/
├── dto/
│   └── ProductRegisteredDTO.java    # DTO de resposta
├── repository/
│   └── ProductRepository.java       # Repository com query nativa
├── service/
│   └── ProductService.java          # Service com lógica de negócio
└── controller/
    └── ProductController.java       # Controller REST
```

### **Fluxo de Execução:**
1. **Controller** recebe requisição HTTP
2. **Service** processa parâmetros e chama repository
3. **Repository** executa query nativa SQL
4. **Service** converte resultado para DTO
5. **Controller** retorna resposta JSON

## ⚡ **PERFORMANCE**

### **Otimizações Implementadas:**
- ✅ Query nativa otimizada
- ✅ Paginação automática
- ✅ Filtros opcionais (não aplica se null)
- ✅ Count query separada
- ✅ Índices recomendados no banco

### **Índices Recomendados:**
```sql
CREATE INDEX idx_produto_seccod ON PRODUTO(SECCOD);
CREATE INDEX idx_produto_grpcod ON PRODUTO(GRPCOD);
CREATE INDEX idx_produto_sbgcod ON PRODUTO(SBGCOD);
CREATE INDEX idx_produto_marcod ON PRODUTO(MARCOD);
CREATE INDEX idx_produto_prodes ON PRODUTO(PRODES);
CREATE INDEX idx_referencia_procod ON REFERENCIA(PROCOD);
```

## 🔍 **QUERY SQL**

### **Query Principal:**
```sql
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
```

## 🚀 **COMO TESTAR**

### **1. Via cURL:**
```bash
# Busca simples
curl "http://localhost:8082/api/legacy/products/registered"

# Com filtros
curl "http://localhost:8082/api/legacy/products/registered?secao=ELETRONICOS&size=5"
```

### **2. Via Postman:**
- **Method:** GET
- **URL:** `http://localhost:8082/api/legacy/products/registered`
- **Params:** Adicionar conforme necessário

### **3. Via Browser:**
```
http://localhost:8082/api/legacy/products/registered?page=0&size=10&sortBy=descricao
```

## ✅ **VANTAGENS**

1. **Query nativa** - Fácil de entender e manter
2. **Paginação automática** - Spring Data cuida de tudo
3. **Filtros flexíveis** - Qualquer combinação
4. **Ordenação dinâmica** - Qualquer campo
5. **Performance otimizada** - Count query separada
6. **Resposta padronizada** - Sempre retorna Page
7. **Arquivo SQL separado** - Fácil manutenção

---

**📝 Nota:** Esta API está em modo read-only e não permite operações de escrita no banco de dados.
