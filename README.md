# 🏭 MattSchutz SCADA System

<div align="center">

![SCADA System](https://img.shields.io/badge/SCADA-Industrial%20System-blue?style=for-the-badge)
![Java](https://img.shields.io/badge/Java-11+-orange?style=for-the-badge&logo=java)
![React](https://img.shields.io/badge/React-18-61DAFB?style=for-the-badge&logo=react)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.7.18-6DB33F?style=for-the-badge&logo=spring)
![License](https://img.shields.io/badge/License-MIT-green?style=for-the-badge)

**Sistema completo de Supervisão e Controle Industrial desenvolvido com React + Java Spring Boot**

[🚀 Demo](#-demo) • [📦 Instalação](#-instalação) • [📚 Documentação](#-documentação) • [🤝 Contribuição](#-contribuição)

</div>

---

## 📋 Sobre o Projeto

O **MattSchutz SCADA System** é um sistema de supervisão e controle industrial moderno que simula o monitoramento de equipamentos industriais em tempo real. Desenvolvido com arquitetura **React** (frontend) + **Java Spring Boot** (backend), oferece uma interface profissional para controle de motores, monitoramento de alarmes e visualização de dados industriais.

### ✨ Características Principais

- 🎛️ **Interface Industrial Moderna** - Dashboard responsivo com tema escuro profissional
- ⚡ **Dados em Tempo Real** - Simulação de equipamentos com atualização a cada 2 segundos
- 🔧 **Controle de Motores** - Sistema completo de partida/parada com simulação de corrente de inrush
- 🚨 **Sistema de Alarmes** - Geração automática de eventos com níveis de criticidade
- 📊 **Gráficos Dinâmicos** - Monitoramento visual de tensão, corrente e fator de potência
- 🛑 **Parada de Emergência** - Sistema de segurança para parada imediata de todos os equipamentos
- 🌐 **APIs REST** - Backend robusto com endpoints completos para integração

## 🎯 Funcionalidades

### 🖥️ Frontend (React)
- **Dashboard Principal** com status geral da planta
- **Controle Individual** de 3 motores industriais
- **Tela de Alarmes** com log completo de eventos
- **Gráficos em Tempo Real** usando Recharts
- **Interface Responsiva** com Tailwind CSS
- **Navegação Intuitiva** entre diferentes módulos

### ⚙️ Backend (Java Spring Boot)
- **Simulador de Equipamentos** com dados realistas
- **Sistema de Persistência** com H2 Database
- **APIs REST** completas para CRUD de motores e alarmes
- **WebSocket** para comunicação em tempo real
- **Sistema de Logs** com diferentes níveis de criticidade
- **Configuração CORS** para integração frontend/backend

## 🛠️ Tecnologias Utilizadas

### Backend
- **Java 11+** - Linguagem principal
- **Spring Boot 2.7.18** - Framework principal
- **Spring Data JPA** - Persistência de dados
- **H2 Database** - Banco de dados em memória
- **Maven** - Gerenciamento de dependências

### Frontend
- **React 18** - Biblioteca de interface
- **Vite** - Build tool e dev server
- **Tailwind CSS** - Framework de estilização
- **Recharts** - Biblioteca de gráficos
- **Lucide React** - Ícones modernos
- **pnpm** - Gerenciador de pacotes

## 🚀 Demo

### 📱 Interface Principal
```
┌─────────────────────────────────────────────────────────┐
│ 🏭 MattSchutz SCADA System v2.0 + Java Backend         │
│ ⏰ 10/10/2025, 13:10:40  👤 Operador  🚨 EMERGÊNCIA    │
├─────────────────────────────────────────────────────────┤
│ 📊 Status Geral                                        │
│ • Fator de Potência: 0.84                              │
│ • Potência Total: 127.0 kW                             │
│ • Motores Ativos: 3 / 3                                │
│ • Banco Capacitores: 2 estágios ativos                 │
├─────────────────────────────────────────────────────────┤
│ ⚡ Motores                                              │
│ • Motor 1: ON  [1] • Motor 2: ON  [2] • Motor 3: ON [3]│
├─────────────────────────────────────────────────────────┤
│ 🚨 Alarmes Recentes                                     │
│ • Motor 3 iniciado - Corrente de inrush detectada      │
│ • 10/10/2025, 09:10:03                                 │
└─────────────────────────────────────────────────────────┘
```

### 🔧 Tela de Motores Detalhada
```
Motor 1 - LIGADO
├── Corrente: 110.1A
├── RPM: 1777
├── Temperatura: 57.3°C
├── Vibração: 1.4 mm/s
└── [Desligar Motor]

Motor 2 - LIGADO  
├── Corrente: 105.7A
├── RPM: 1800
├── Temperatura: 54.0°C
├── Vibração: 1.4 mm/s
└── [Desligar Motor]

Motor 3 - LIGADO
├── Corrente: 107.2A
├── RPM: 1799
├── Temperatura: 53.1°C
├── Vibração: 1.3 mm/s
└── [Desligar Motor]
```

## 📦 Instalação

### 🔧 Pré-requisitos

- **Java 11+** - [Download](https://adoptium.net/)
- **Maven 3.6+** - [Download](https://maven.apache.org/)
- **Node.js 16+** - [Download](https://nodejs.org/)
- **pnpm** - `npm install -g pnpm`

### ⚡ Instalação Rápida

#### 1️⃣ Clone o repositório
```bash
git clone https://github.com/seu-usuario/mattschutz-scada-system.git
cd mattschutz-scada-system
```

#### 2️⃣ Backend (Terminal 1)
```bash
cd backend
mvn spring-boot:run
```
✅ **Backend rodando em:** http://localhost:8080

#### 3️⃣ Frontend (Terminal 2)
```bash
cd frontend
pnpm install
pnpm run dev
```
✅ **Frontend rodando em:** http://localhost:5173

#### 4️⃣ Acessar Sistema
1. Abrir: http://localhost:5173
2. Clicar: **"Entrar no Sistema"**
3. Testar: Botões dos motores (1, 2, 3)

### 🪟 Scripts Automáticos

#### Windows
```cmd
# Execute o arquivo
INSTALAR_WINDOWS.bat
```

#### Linux/Mac
```bash
chmod +x instalar_linux_mac.sh
./instalar_linux_mac.sh
```

## 🌐 APIs Disponíveis

### 🔧 Motores
```http
GET    /api/motors              # Lista todos os motores
GET    /api/motors/{id}         # Busca motor específico
POST   /api/motors/{id}/start   # Liga motor
POST   /api/motors/{id}/stop    # Desliga motor
GET    /api/motors/statistics   # Estatísticas dos motores
```

### 🚨 Alarmes
```http
GET    /api/alarms              # Lista alarmes (paginado)
GET    /api/alarms/recent       # Últimos 10 alarmes
GET    /api/alarms/unacknowledged # Alarmes não reconhecidos
POST   /api/alarms              # Cria novo alarme
PUT    /api/alarms/{id}/acknowledge # Reconhece alarme
```

### 📊 Dashboard
```http
GET    /api/dashboard/data      # Dados completos do dashboard
GET    /api/dashboard/status    # Status resumido do sistema
```

### 🛑 Emergência
```http
POST   /api/emergency/activate  # Ativa parada de emergência
POST   /api/emergency/reset     # Reseta emergência
GET    /api/emergency/status    # Status da emergência
```

## 🧪 Exemplos de Uso

### Testar APIs via cURL
```bash
# Listar motores
curl http://localhost:8080/api/motors

# Ligar Motor 1
curl -X POST http://localhost:8080/api/motors/1/start

# Ver alarmes recentes
curl http://localhost:8080/api/alarms/recent

# Ativar emergência
curl -X POST http://localhost:8080/api/emergency/activate
```

### Integração com JavaScript
```javascript
// Buscar dados do dashboard
const response = await fetch('http://localhost:8080/api/dashboard/data');
const data = await response.json();

// Ligar motor
await fetch('http://localhost:8080/api/motors/1/start', {
  method: 'POST'
});

// Monitorar alarmes
const alarms = await fetch('http://localhost:8080/api/alarms/recent')
  .then(res => res.json());
```

## 📁 Estrutura do Projeto

```
mattschutz-scada-system/
├── backend/                    # Backend Java Spring Boot
│   ├── src/main/java/com/mattschutz/scada/
│   │   ├── ScadaApplication.java      # Classe principal
│   │   ├── model/                     # Entidades JPA
│   │   │   ├── Motor.java
│   │   │   ├── Alarm.java
│   │   │   ├── MotorStatus.java
│   │   │   └── AlarmLevel.java
│   │   ├── repository/                # Repositórios de dados
│   │   │   ├── MotorRepository.java
│   │   │   └── AlarmRepository.java
│   │   ├── service/                   # Lógica de negócio
│   │   │   ├── MotorService.java
│   │   │   └── AlarmService.java
│   │   ├── controller/                # APIs REST
│   │   │   ├── MotorController.java
│   │   │   ├── AlarmController.java
│   │   │   ├── DashboardController.java
│   │   │   └── EmergencyController.java
│   │   ├── simulator/                 # Simulação em tempo real
│   │   │   └── ScadaSimulator.java
│   │   └── config/                    # Configurações
│   │       └── CorsConfig.java
│   ├── pom.xml                        # Dependências Maven
│   └── src/main/resources/
│       └── application.yml            # Configurações da aplicação
├── frontend/                   # Frontend React
│   ├── src/
│   │   ├── App.jsx            # Componente principal
│   │   └── main.jsx           # Ponto de entrada
│   ├── package.json           # Dependências npm
│   ├── tailwind.config.js     # Configuração Tailwind
│   └── vite.config.js         # Configuração Vite
├── docs/                      # Documentação
├── scripts/                   # Scripts de instalação
└── README.md                  # Este arquivo
```

## 🔧 Configuração

### Backend (application.yml)
```yaml
server:
  port: 8080
  servlet:
    context-path: /api

spring:
  datasource:
    url: jdbc:h2:mem:scada_db
    driver-class-name: org.h2.Driver
  
  h2:
    console:
      enabled: true
      path: /h2-console
  
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: false

logging:
  level:
    com.mattschutz.scada: INFO
```

### Frontend (vite.config.js)
```javascript
import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173
  }
})
```

## 🐛 Solução de Problemas

### ❌ Backend não inicia
```bash
# Verificar Java
java -version

# Verificar porta 8080
netstat -an | grep 8080

# Recompilar
mvn clean compile
```

### ❌ Frontend com erro de dependências
```bash
# Instalar com flag especial
pnpm install --legacy-peer-deps

# Ou usar npm
npm install --legacy-peer-deps
```

### ❌ Dados não carregam
- ✅ Verificar se backend está rodando (http://localhost:8080/api/motors)
- ✅ Aguardar 10-15 segundos para inicialização completa
- ✅ Verificar console do navegador (F12)

## 📊 Monitoramento

### Console H2 Database
- **URL:** http://localhost:8080/api/h2-console
- **JDBC URL:** `jdbc:h2:mem:scada_db`
- **User:** `sa`
- **Password:** (vazio)

### Logs do Sistema
```bash
# Backend - logs detalhados
mvn spring-boot:run -Dlogging.level.com.mattschutz.scada=DEBUG

# Frontend - console do navegador
# Pressione F12 para abrir DevTools
```

## 🚀 Deploy

### 🐳 Docker (Recomendado)
```dockerfile
# Dockerfile para backend
FROM openjdk:11-jre-slim
COPY target/scada-system-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### ☁️ Cloud Deploy
```bash
# Build para produção
cd frontend
pnpm run build

cd ../backend
mvn clean package

# Deploy no Heroku, AWS, etc.
```

## 🤝 Contribuição

Contribuições são bem-vindas! Para contribuir:

1. **Fork** o projeto
2. **Crie** uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. **Commit** suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. **Push** para a branch (`git push origin feature/AmazingFeature`)
5. **Abra** um Pull Request

### 📝 Padrões de Código

- **Java:** Seguir convenções Spring Boot
- **React:** Usar hooks e componentes funcionais
- **Commits:** Usar [Conventional Commits](https://conventionalcommits.org/)

## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

## 👥 Autores

- **Desenvolvedor Principal** - [Seu Nome](https://github.com/seu-usuario)

## 🙏 Agradecimentos

- Comunidade Spring Boot
- Equipe React
- Contribuidores do projeto
- Inspiração em sistemas SCADA industriais reais

---

<div align="center">

**⭐ Se este projeto foi útil, considere dar uma estrela!**

[🐛 Reportar Bug](https://github.com/seu-usuario/mattschutz-scada-system/issues) • [💡 Solicitar Feature](https://github.com/seu-usuario/mattschutz-scada-system/issues) • [💬 Discussões](https://github.com/seu-usuario/mattschutz-scada-system/discussions)

</div>
