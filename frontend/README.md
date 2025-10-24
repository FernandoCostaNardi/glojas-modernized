# 🏪 Glojas Frontend - Sistema Modernizado

Frontend React simples para consumir a API de produtos do sistema Glojas.

## 🚀 Funcionalidades

- ✅ Listagem de produtos cadastrados
- ✅ Tabela responsiva com paginação
- ✅ Estados de loading e erro
- ✅ Design moderno e limpo
- ✅ ESLint e Prettier configurados

## 📋 Pré-requisitos

- Node.js 18+ 
- npm ou yarn
- Business API rodando em `http://localhost:8087`

## 🛠️ Instalação

1. **Instalar dependências:**
```bash
npm install
```

2. **Iniciar o servidor de desenvolvimento:**
```bash
npm start
```

3. **Acessar no navegador:**
```
http://localhost:3000
```

## 📁 Estrutura do Projeto

```
src/
├── components/
│   ├── ProductsTable.js      # Tabela de produtos com paginação
│   └── ProductsTable.css     # Estilos da tabela
├── App.js                    # Componente principal
├── App.css                   # Estilos globais
├── index.js                  # Ponto de entrada
└── index.css                 # Reset CSS
```

## 🔧 Scripts Disponíveis

```bash
# Desenvolvimento
npm start                     # Inicia servidor dev (porta 3000)

# Build para produção
npm run build                 # Gera build otimizado

# Qualidade de código
npm run lint                  # Executa ESLint
npm run lint:fix             # Corrige problemas ESLint automaticamente
npm run format               # Formata código com Prettier

# Testes
npm test                     # Executa testes
```

## 🌐 API Integration

O frontend consome o endpoint:
```
GET http://localhost:8089/api/business/products/registered
```

### Parâmetros esperados:
- `page`: número da página (0-based)
- `size`: quantidade de items por página

### Resposta esperada:
```json
{
  "content": [
    {
      "id": 1,
      "codigo": "PROD001",
      "descricao": "Produto Exemplo",
      "preco": 29.90,
      "estoque": 100,
      "status": "ATIVO"
    }
  ],
  "totalPages": 5,
  "totalElements": 50
}
```

## 🚀 Como Testar

1. Certifique-se que a Business API está rodando na porta 8089
2. Execute `npm install && npm start`
3. Abra http://localhost:3000
4. Verifique se a tabela carrega os produtos da API

---

**Baby steps concluído! 🚀**
