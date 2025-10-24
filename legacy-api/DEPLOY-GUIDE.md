# Guia de Deploy - Legacy API em Produção

## 📋 Pré-requisitos

- Maven instalado no Windows
- Acesso SSH à VPS como root
- Docker rodando na VPS

## 🚀 Deploy Automatizado (Recomendado)

### Windows

```powershell
cd G:\olisystem\glojas-modernized\legacy-api
.\deploy-production.ps1
```

O script automatiza:
1. Build do JAR
2. Upload para VPS
3. Build da imagem Docker
4. Deploy do container
5. Validação automática

## 📝 Deploy Manual (Passo a Passo)

### Passo 1: Build Local (Windows)

```powershell
cd G:\olisystem\glojas-modernized\legacy-api
mvn clean package -DskipTests
```

Verifique se o JAR foi criado:
```powershell
ls target\legacy-api-1.0.0.jar
```

### Passo 2: Upload para VPS

```powershell
scp target\legacy-api-1.0.0.jar root@212.85.12.228:/opt/glojas-modernized/legacy-api/target/
```

### Passo 3: Deploy na VPS

Conecte na VPS:
```bash
ssh root@212.85.12.228
```

Execute o deploy:
```bash
cd /opt/glojas-modernized/legacy-api
bash deploy-vps.sh
```

### Passo 4: Validação

```bash
# Ver logs
docker logs -f legacy-api

# Testar health
curl http://localhost:8087/api/legacy/actuator/health

# Testar endpoint
curl http://localhost:8087/api/legacy/stores
```

## 🔄 Rollback

Se algo der errado:

```bash
ssh root@212.85.12.228
cd /opt/glojas-modernized/legacy-api
bash rollback-vps.sh
```

## 🛠️ Comandos Úteis

### Monitoramento

```bash
# Ver logs em tempo real
docker logs -f legacy-api

# Ver últimas 100 linhas
docker logs legacy-api --tail 100

# Ver uso de recursos
docker stats legacy-api --no-stream

# Ver status do container
docker ps | grep legacy-api
```

### Manutenção

```bash
# Reiniciar container
docker restart legacy-api

# Parar container
docker stop legacy-api

# Ver variáveis de ambiente
docker exec legacy-api env | grep SPRING

# Entrar no container
docker exec -it legacy-api sh
```

### Debug

```bash
# Verificar conectividade SQL Server
docker exec legacy-api timeout 5 sh -c 'cat < /dev/null > /dev/tcp/45.174.189.210/1433' && echo "OK" || echo "FALHOU"

# Ver configuração de rede
docker inspect legacy-api | grep -A 20 NetworkSettings

# Ver healthcheck
docker inspect legacy-api | grep -A 10 Health
```

## ⚠️ Troubleshooting

### Container não inicia

```bash
# Ver logs de erro
docker logs legacy-api

# Ver eventos do Docker
docker events --since 5m

# Verificar porta em uso
netstat -tulpn | grep 8087
```

### Erro de conexão SQL Server

```bash
# Testar de dentro do container
docker exec legacy-api ping -c 3 45.174.189.210

# Ver logs de SQL Server
docker logs legacy-api | grep -i "sqlserver\|prelogin\|timeout"
```

### Rollback não funciona

```bash
# Parar container
docker stop legacy-api
docker rm legacy-api

# Listar imagens disponíveis
docker images | grep legacy-api

# Iniciar manualmente com imagem de backup
docker run -d --name legacy-api --restart unless-stopped -p 8087:8087 -e SPRING_PROFILES_ACTIVE=prod legacy-api:backup
```

## 📊 Checklist de Deploy

- [ ] Build do JAR sem erros
- [ ] Upload do JAR para VPS
- [ ] Build da imagem Docker
- [ ] Container iniciado
- [ ] Health check retorna UP
- [ ] Endpoints respondem corretamente
- [ ] Logs sem erros de SQL Server
- [ ] Backup da versão anterior criado

## 🎯 URLs de Produção

- **API Base**: http://glojas.com.br/api/legacy
- **Health Check**: http://glojas.com.br/api/legacy/actuator/health
- **Stores**: http://glojas.com.br/api/legacy/stores
- **Products**: http://glojas.com.br/api/legacy/products/registered

## 📞 Suporte

Em caso de problemas:
1. Verificar logs: `docker logs legacy-api`
2. Executar rollback: `bash rollback-vps.sh`
3. Verificar conectividade SQL Server
4. Contatar equipe de desenvolvimento
