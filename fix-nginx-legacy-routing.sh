#!/bin/bash

# Script para corrigir configuração do Nginx para roteamento Legacy API
# Execute como root na VPS

echo "=== CORRIGINDO CONFIGURAÇÃO DO NGINX PARA LEGACY API ==="

# Backup da configuração atual
echo "1. Fazendo backup da configuração atual..."
cp /etc/nginx/sites-available/glojas /etc/nginx/sites-available/glojas.backup.$(date +%Y%m%d_%H%M%S)

# Criar nova configuração corrigida
echo "2. Aplicando correções na configuração do Nginx..."

cat > /etc/nginx/sites-available/glojas << 'EOF'
server {
    listen 80;
    server_name gestaosmarteletron.com.br www.gestaosmarteletron.com.br;
    return 301 https://$host$request_uri;
}

server {
    listen 443 ssl http2;
    server_name gestaosmarteletron.com.br www.gestaosmarteletron.com.br;

    # SSL Certificates (managed by Certbot)
    ssl_certificate /etc/letsencrypt/live/gestaosmarteletron.com.br/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/gestaosmarteletron.com.br/privkey.pem;
    include /etc/letsencrypt/options-ssl-nginx.conf;
    ssl_dhparam /etc/letsencrypt/ssl-dhparams.pem;

    # Logs
    access_log /var/log/nginx/glojas-access.log;
    error_log /var/log/nginx/glojas-error.log;

    # ==================================================
    # ROTAS DA API - REWRITE PARA /api/business
    # ==================================================
    location ~ ^/api/(auth|users|stores|stores-legacy|products|sales|operations|roles|permissions|sync|dashboard|legacy)(/.*)?$ {
        # Handle OPTIONS primeiro
        if ($request_method = 'OPTIONS') {
            add_header 'Access-Control-Allow-Origin' '*' always;
            add_header 'Access-Control-Allow-Methods' 'GET, POST, PUT, DELETE, PATCH, OPTIONS' always;
            add_header 'Access-Control-Allow-Headers' 'Content-Type, Authorization, X-Requested-With, Accept, Origin' always;
            add_header 'Access-Control-Max-Age' 86400 always;
            add_header 'Content-Length' 0;
            add_header 'Content-Type' 'text/plain charset=UTF-8';
            return 204;
        }

        rewrite ^/api/(.*)$ /api/business/$1 break;

        proxy_pass http://localhost:8089;
        proxy_http_version 1.1;

        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";

        # REMOVER Origin (workaround)
        proxy_set_header Origin "";

        proxy_connect_timeout 90;
        proxy_send_timeout 90;
        proxy_read_timeout 90;
        proxy_redirect off;

        # CORS Headers
        add_header 'Access-Control-Allow-Origin' '*' always;
        add_header 'Access-Control-Allow-Methods' 'GET, POST, PUT, DELETE, PATCH, OPTIONS' always;
        add_header 'Access-Control-Allow-Headers' 'Content-Type, Authorization, X-Requested-With, Accept, Origin' always;
        add_header 'Access-Control-Expose-Headers' 'Content-Length, Content-Range, Authorization' always;
    }

    # Rota direta para /api/business (caso alguém chame direto)
    location /api/business {
        proxy_pass http://localhost:8089;
        proxy_http_version 1.1;

        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;

        proxy_connect_timeout 90;
        proxy_send_timeout 90;
        proxy_read_timeout 90;
    }

    # Frontend (React)
    location / {
        root /opt/glojas-modernized/frontend/dist;
        index index.html index.htm;
        try_files $uri $uri/ /index.html;

        # Cache para assets
        location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2|ttf|eot)$ {
            expires 1y;
            add_header Cache-Control "public, immutable";
        }

        # Sem cache para HTML
        location ~* \.html$ {
            add_header Cache-Control "no-store, no-cache, must-revalidate";
            expires 0;
        }
    }
}
EOF

echo "3. Validando sintaxe do Nginx..."
nginx -t

if [ $? -eq 0 ]; then
    echo "4. Sintaxe OK! Recarregando Nginx..."
    systemctl reload nginx
    
    echo "5. Testando configuração..."
    echo "Teste local:"
    curl -v http://localhost:8089/api/business/legacy/stores
    
    echo ""
    echo "Teste via Nginx:"
    curl -v https://gestaosmarteletron.com.br/api/legacy/stores
    
    echo ""
    echo "=== CORREÇÕES APLICADAS COM SUCESSO ==="
    echo "✅ Nginx configurado para rotear /api/legacy/* para Business API"
    echo "✅ Rota direta para Legacy API removida"
    echo "✅ stores-legacy incluído no regex de rotas"
    echo ""
    echo "Próximos passos:"
    echo "1. Rebuild e redeploy da Business API"
    echo "2. Atualizar frontend para usar /api/legacy/stores"
    echo "3. Testar em produção"
    
else
    echo "❌ ERRO: Sintaxe do Nginx inválida!"
    echo "Restaurando backup..."
    cp /etc/nginx/sites-available/glojas.backup.* /etc/nginx/sites-available/glojas
    exit 1
fi
