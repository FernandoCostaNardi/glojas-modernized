# Business API - Produtos Registrados

Esta API foi desenvolvida para consumir a Legacy API de produtos e aplicar regras de negócio da camada Business.

## 🎯 Objetivo

Criar uma API no serviço `business-api` que:
- Consome a API `/products/registered` do serviço `legacy-api`
- Aplica regras de negócio da camada Business
- Retorna dados estruturados com metadados adicionais
- Mantém compatibilidade com filtros e paginação

## 🏗️ Arquitetura

### Componentes Implementados

1. **DTOs (Data Transfer Objects)**
   - `ProductRegisteredResponseDTO`: Representa um produto individual
   - `LegacyApiResponseDTO`: Captura a resposta completa da Legacy API
   - `ProductsBusinessResponseDTO`: Resposta da Business API com metadados

2. **Cliente HTTP**
   - `LegacyApiClient`: Encapsula chamadas para a Legacy API
   - `WebClientConfig`: Configuração do WebClient para comunicação HTTP

3. **Serviço de Negócio**
   - `ProductService`: Orquestra chamadas e aplica regras de negócio

4. **Controller REST**
   - `ProductController`: Expõe endpoints REST para clientes

## 📋 Endpoints Disponíveis

### GET /api/business/products/registered

Busca produtos registrados com filtros, paginação e ordenação.

**Parâmetros de Query:**
- `secao` (opcional): Filtro por seção
- `grupo` (opcional): Filtro por grupo  
- `marca` (opcional): Filtro por marca
- `descricao` (opcional): Filtro por descrição
- `page` (padrão: 0): Número da página (0-based)
- `size` (padrão: 20, máx: 100): Tamanho da página
- `sortBy` (padrão: "codigo"): Campo para ordenação
- `sortDir` (padrão: "asc"): Direção da ordenação (asc/desc)

**Exemplo de Resposta:**
```json
{
  "products": [
    {
      "codigo": 123,
      "secao": "INFORMATICA",
      "grupo": "COMPUTADORES",
      "subgrupo": "DESKTOP",
      "marca": "DELL",
      "partNumberCodigo": "DT001",
      "refplu": "PLU123",
      "descricao": "Desktop Dell Inspiron",
      "ncm": "84713000"
    }
  ],
  "pagination": {
    "totalElements": 150,
    "totalPages": 8,
    "currentPage": 0,
    "pageSize": 20,
    "hasNext": true,
    "hasPrevious": false
  },
  "dataSource": {
    "source": "legacy-api",
    "version": "1.0",
    "endpoint": "/products/registered"
  },
  "timestamp": "2024-01-15T10:30:00",
  "status": "SUCCESS",
  "message": "Produtos encontrados com sucesso"
}
```

### GET /api/business/products/test-legacy-connection

Testa a conectividade com a Legacy API.

### GET /api/business/products/health

Health check específico do serviço de produtos.

## 🔧 Configuração

### application.yml

```yaml
# Configurações para integração com Legacy API
legacy-api:
  base-url: http://localhost:8082
  context-path: /api/legacy
  timeout: 30
```

### Dependências Maven

As seguintes dependências foram adicionadas ao `pom.xml`:

```xml
<!-- WebFlux para WebClient -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webflux</artifactId>
</dependency>

<!-- Lombok para reduzir boilerplate -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>

<!-- Jackson para deserialização JSON -->
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
</dependency>
```

## 🧪 Testes

Execute o script de teste PowerShell:

```powershell
.\test-business-products-api.ps1
```

Este script testa:
- Health checks das APIs
- Conectividade entre Business e Legacy APIs
- Busca de produtos sem filtros
- Busca com paginação
- Busca com filtros
- Busca com ordenação
- Comparação com Legacy API direta

## 🚀 Como Executar

1. **Certifique-se que a Legacy API está rodando** na porta 8082
2. **Inicie a Business API** na porta 8081
3. **Execute os testes** para validar a integração

```bash
# Compilar e executar
mvn clean compile
mvn spring-boot:run
```

## 📊 Fluxo de Dados

1. Cliente faz requisição para Business API
2. ProductController recebe e valida parâmetros
3. ProductService orquestra a lógica de negócio
4. LegacyApiClient faz chamada HTTP para Legacy API
5. Legacy API consulta SQL Server e retorna dados
6. Business API processa resposta e adiciona metadados
7. Resposta estruturada é retornada ao cliente

## 🔍 Monitoramento

- **Health Check**: `/api/business/products/health`
- **Teste de Conectividade**: `/api/business/products/test-legacy-connection`
- **Logs**: Configurados para nível INFO com detalhes das operações

## 🎯 Próximos Passos

- [ ] Implementar cache de respostas
- [ ] Adicionar métricas de performance
- [ ] Implementar circuit breaker para resiliência
- [ ] Adicionar autenticação/autorização
- [ ] Implementar transformações de dados específicas
- [ ] Adicionar validações de negócio customizadas

## 📝 Notas Técnicas

- Utiliza WebClient reativo para melhor performance
- Timeout configurável para chamadas HTTP
- Tratamento de erros com fallback
- Validação de parâmetros de entrada
- Logs estruturados para troubleshooting
- DTOs específicos para separação de responsabilidades
