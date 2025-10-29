# Tabela Year Sells - Documentação

## Visão Geral

A tabela `year_sells` foi criada para armazenar vendas anuais consolidadas por loja, completando a hierarquia de agregação de vendas do sistema:

1. **`daily_sells`** - Dados diários detalhados (PDV, DANFE, Exchange)
2. **`monthly_sells`** - Agregação mensal consolidada
3. **`year_sells`** - Agregação anual consolidada (nova)

## Estrutura da Tabela

### Campos

| Campo | Tipo | Descrição | Restrições |
|-------|------|-----------|------------|
| `id` | UUID | Identificador único da venda anual | PRIMARY KEY, NOT NULL |
| `store_code` | VARCHAR(10) | Código da loja para identificação rápida | NOT NULL |
| `store_id` | UUID | ID único da loja (referência) | NOT NULL |
| `store_name` | VARCHAR(255) | Nome comercial da loja | NOT NULL |
| `total` | NUMERIC(15,2) | Valor total consolidado das vendas anuais | NOT NULL |
| `year` | INTEGER | Ano das vendas para agrupamento | NOT NULL |
| `created_at` | TIMESTAMP | Data e hora de criação do registro | NOT NULL, DEFAULT CURRENT_TIMESTAMP |
| `updated_at` | TIMESTAMP | Data e hora da última atualização | NOT NULL, DEFAULT CURRENT_TIMESTAMP |

### Índices Criados

1. **`idx_year_sells_store_code`** - Índice em `store_code`
   - **Propósito**: Otimizar buscas por código da loja
   - **Uso**: Filtros e relatórios por loja específica

2. **`idx_year_sells_store_id`** - Índice em `store_id`
   - **Propósito**: Otimizar buscas por ID da loja
   - **Uso**: Joins e operações internas do sistema

3. **`idx_year_sells_year`** - Índice em `year`
   - **Propósito**: Otimizar buscas por ano
   - **Uso**: Relatórios anuais e comparações temporais

4. **`idx_year_sells_created_at`** - Índice em `created_at`
   - **Propósito**: Otimizar buscas por data de criação
   - **Uso**: Auditoria e análise de dados históricos

5. **`idx_year_sells_store_year`** - Índice único composto em `(store_id, year)`
   - **Propósito**: Garantir unicidade por loja e ano
   - **Uso**: Prevenir duplicatas e otimizar buscas específicas

## Instruções de Execução

### Via PostgreSQL (psql)

```bash
cd business-api/scripts
psql -U glojas_user -d glojas_business -f create-year-sells-table.sql
```

### Via PowerShell

```powershell
cd business-api/scripts
.\run-create-year-sells-table.ps1
```

## Entidade JPA - YearSell

### Localização
`business-api/src/main/java/com/sysconard/business/entity/sell/YearSell.java`

### Características
- **Anotações**: `@Entity`, `@Table(name = "year_sells")`, `@Data`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`
- **Auditoria**: Campos `createdAt` e `updatedAt` preenchidos automaticamente
- **Validação**: Campos obrigatórios com `nullable = false`
- **Precisão**: Campo `total` com `BigDecimal` e `NUMERIC(15,2)`

### Exemplo de Uso

```java
// Criar nova venda anual
YearSell yearSell = YearSell.builder()
    .storeCode("001")
    .storeId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))
    .storeName("Loja Matriz")
    .total(new BigDecimal("1500000.50"))
    .year(2025)
    .build();

// Salvar no banco
yearSellRepository.save(yearSell);
```

## Repository - YearSellRepository

### Localização
`business-api/src/main/java/com/sysconard/business/repository/sell/YearSellRepository.java`

### Métodos Disponíveis

```java
// Buscar por loja e ano específicos
Optional<YearSell> findByStoreIdAndYear(UUID storeId, Integer year);

// Buscar todas as vendas de um ano
List<YearSell> findByYear(Integer year);

// Buscar vendas de uma loja em um período
List<YearSell> findByStoreCodeAndYearBetween(String storeCode, Integer startYear, Integer endYear);

// Verificar existência
boolean existsByStoreIdAndYear(UUID storeId, Integer year);

// Contar registros
long countByYear(Integer year);
```

## Relacionamento com Outras Tabelas

### Hierarquia de Agregação

```
daily_sells (dados diários)
    ↓ agregação mensal
monthly_sells (dados mensais)
    ↓ agregação anual
year_sells (dados anuais) ← NOVA
```

### Campos de Relacionamento

- **`store_id`**: Relaciona com a tabela de lojas
- **`store_code`**: Código de referência para identificação rápida
- **`store_name`**: Nome da loja (denormalizado para performance)

## Casos de Uso

### 1. Relatórios Anuais
- Comparação de performance entre anos
- Análise de crescimento/declínio anual
- Ranking de lojas por ano

### 2. Sincronização de Dados
- Agregação automática de dados mensais para anuais
- Atualização de totais anuais
- Consolidação de métricas

### 3. Análise Temporal
- Tendências de longo prazo
- Sazonalidade anual
- Projeções baseadas em dados históricos

## Considerações de Performance

### Otimizações Implementadas
- **Índices estratégicos** para campos mais consultados
- **Índice único composto** para evitar duplicatas
- **Tipos de dados otimizados** (INTEGER para ano, NUMERIC para valores)
- **Campos denormalizados** (store_name) para reduzir joins

### Recomendações
- Use `year` como filtro principal em consultas temporais
- Aproveite o índice composto `(store_id, year)` para buscas específicas
- Considere particionamento por ano para grandes volumes de dados

## Monitoramento

### Queries de Verificação

```sql
-- Verificar estrutura da tabela
SELECT column_name, data_type, is_nullable 
FROM information_schema.columns 
WHERE table_name = 'year_sells';

-- Verificar índices criados
SELECT indexname, indexdef 
FROM pg_indexes 
WHERE tablename = 'year_sells';

-- Verificar dados inseridos
SELECT COUNT(*) as total_registros FROM year_sells;
```

## Manutenção

### Limpeza de Dados Antigos
```sql
-- Remover dados de anos muito antigos (exemplo: antes de 2020)
DELETE FROM year_sells WHERE year < 2020;
```

### Backup e Restore
```bash
# Backup da tabela
pg_dump -U glojas_user -d glojas_business -t year_sells > year_sells_backup.sql

# Restore da tabela
psql -U glojas_user -d glojas_business < year_sells_backup.sql
```

---

**Última Atualização**: 28/08/2025  
**Versão**: 1.0  
**Responsável**: Equipe de Desenvolvimento
