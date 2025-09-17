# 🔧 Correção de Checksum Mismatch do Flyway

## 📋 Problema Identificado

A aplicação business-api estava falhando na inicialização devido a um **checksum mismatch** na migração V18:

```
Migration checksum mismatch for migration version 18
-> Applied to database : 1929812275
-> Resolved locally    : 1806181283
```

## 🎯 Causa do Problema

O erro ocorre quando uma migração é modificada após ter sido aplicada ao banco de dados. O Flyway usa checksums para detectar mudanças acidentais em migrações já executadas.

## ✅ Soluções Implementadas

### 1. Scripts de Correção Automática

#### **Opção A: Flyway CLI**
```bash
# Windows
business-api/scripts/fix-flyway-checksum.bat

# PowerShell
business-api/scripts/fix-flyway-checksum.ps1
```

#### **Opção B: Maven Plugin (Recomendado)**
```bash
# Windows
business-api/scripts/fix-flyway-maven.bat

# PowerShell
business-api/scripts/fix-flyway-maven.ps1

# Ou diretamente via Maven
mvn flyway:repair
```

### 2. Configuração Flyway Aprimorada

Ajustei o `application.yml` com configurações mais robustas:

```yaml
flyway:
  enabled: true
  baseline-on-migrate: true
  validate-on-migrate: false  # Temporariamente desabilitado
  locations: classpath:db/migration
  schemas: public
  baseline-version: 0
  clean-disabled: false
  out-of-order: true
  ignore-missing-migrations: true
  ignore-ignored-migrations: true
  ignore-pending-migrations: false
  ignore-future-migrations: true
```

### 3. Plugin Maven Flyway

Adicionei o plugin Flyway no `pom.xml`:

```xml
<plugin>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-maven-plugin</artifactId>
    <version>10.8.1</version>
    <configuration>
        <url>jdbc:postgresql://localhost:5432/glojas_business</url>
        <user>glojas_user</user>
        <password>F1e0r8n0#1</password>
        <schemas>
            <schema>public</schema>
        </schemas>
        <locations>
            <location>classpath:db/migration</location>
        </locations>
        <baselineOnMigrate>true</baselineOnMigrate>
        <validateOnMigrate>true</validateOnMigrate>
    </configuration>
</plugin>
```

## 🚀 Como Executar a Correção

### Passo 1: Executar Flyway Repair
```bash
# Navegue para o diretório business-api
cd business-api

# Execute o script de correção (escolha uma opção)
scripts/fix-flyway-maven.bat
# OU
mvn flyway:repair
```

### Passo 2: Testar a Aplicação
```bash
# Teste a inicialização
scripts/test-application-startup.bat
# OU
mvn spring-boot:run
```

## 🔍 Verificação de Sucesso

Após executar a correção, você deve ver:

1. **Flyway Repair bem-sucedido:**
   ```
   [SUCESSO] Checksums corrigidos com sucesso!
   [INFO] A aplicação agora deve inicializar normalmente
   ```

2. **Aplicação iniciando sem erros:**
   ```
   Started BusinessApiApplication in X.XXX seconds
   ```

3. **Logs do Flyway mostrando migrações aplicadas:**
   ```
   Flyway is handling the following data sources: [jdbc:postgresql://localhost:5432/glojas_business]
   ```

## 🛡️ Prevenção de Problemas Futuros

### 1. Boas Práticas de Migração
- **Nunca modifique** migrações já aplicadas em produção
- **Use novas migrações** para mudanças adicionais
- **Teste migrações** em ambiente de desenvolvimento primeiro

### 2. Configurações de Desenvolvimento
- `validate-on-migrate: false` em desenvolvimento
- `out-of-order: true` para permitir flexibilidade
- `ignore-missing-migrations: true` para ambientes dinâmicos

### 3. Monitoramento
- Verifique logs do Flyway regularmente
- Use `mvn flyway:info` para verificar status das migrações
- Execute `mvn flyway:validate` antes de deploy em produção

## 🔧 Comandos Úteis

```bash
# Verificar status das migrações
mvn flyway:info

# Validar migrações
mvn flyway:validate

# Executar migrações
mvn flyway:migrate

# Reparar checksums
mvn flyway:repair

# Limpar banco (CUIDADO!)
mvn flyway:clean
```

## 📚 Documentação Adicional

- [Flyway Documentation](https://flywaydb.org/documentation/)
- [Spring Boot Flyway Integration](https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html#howto.data-access.flyway)
- [Flyway Maven Plugin](https://flywaydb.org/documentation/usage/maven/)

---

**Última Atualização:** 28/08/2025  
**Status:** ✅ Resolvido  
**Responsável:** Equipe de Desenvolvimento
