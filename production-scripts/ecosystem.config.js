// ecosystem.config.js - Configuração PM2 com múltiplas versões do Java
module.exports = {
  apps: [
    {
      name: 'business-api',
      script: '/usr/lib/jvm/java-17-openjdk-amd64/bin/java',  // Java 17 específico
      args: [
        '-jar',
        '-Xmx2g',
        '-Xms1g', 
        '-Dspring.profiles.active=prod',
        'target/business-api-1.0.0.jar'
      ],
      cwd: '/opt/glojas-modernized/business-api',
      instances: 1,
      exec_mode: 'fork',
      autorestart: true,
      watch: false,
      max_memory_restart: '2G',
      env: {
        JAVA_HOME: '/usr/lib/jvm/java-17-openjdk-amd64',
        PATH: '/usr/lib/jvm/java-17-openjdk-amd64/bin:' + process.env.PATH,
        NODE_ENV: 'production',
        DB_PASSWORD: 'MinhaSenh@123!',
        JWT_SECRET: 'Z2xvamFzLXByb2Qtc2VjcmV0LWtleS0yMDI0LXZlcnktc2VjdXJlLXN0cmluZy1jaGFuZ2U='
      },
      log_file: '/opt/glojas-modernized/logs/business-api-combined.log',
      out_file: '/opt/glojas-modernized/logs/business-api-out.log',
      error_file: '/opt/glojas-modernized/logs/business-api-error.log',
      time: true
    },
    {
      name: 'legacy-api',
      script: '/usr/lib/jvm/java-8-openjdk-amd64/bin/java',   // Java 8 específico
      args: [
        '-jar',
        '-Xmx1g',
        '-Xms512m',
        '-Dspring.profiles.active=prod',
        'target/legacy-api-1.0.0.jar'
      ],
      cwd: '/opt/glojas-modernized/legacy-api',
      instances: 1,
      exec_mode: 'fork',
      autorestart: true,
      watch: false,
      max_memory_restart: '1G',
      env: {
        JAVA_HOME: '/usr/lib/jvm/java-8-openjdk-amd64',
        PATH: '/usr/lib/jvm/java-8-openjdk-amd64/bin:' + process.env.PATH,
        NODE_ENV: 'production'
      },
      log_file: '/opt/glojas-modernized/logs/legacy-api-combined.log',
      out_file: '/opt/glojas-modernized/logs/legacy-api-out.log',
      error_file: '/opt/glojas-modernized/logs/legacy-api-error.log',
      time: true
    }
  ]
};
