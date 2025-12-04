# MattSchütz SCADA Backend

Sistema SCADA (Supervisory Control And Data Acquisition) para Monitoramento e Controle de Equipamentos Industriais - Backend Java Spring Boot

## 📋 Visão Geral

Backend completo em Java Spring Boot para o sistema SCADA desenvolvido como projeto acadêmico no Senai-CentroWEG em Jaraguá do Sul, SC. O sistema implementa funcionalidades avançadas de monitoramento industrial, incluindo:

- Cálculo de corrente de inrush diferenciado por tipo de carga
- Sistema de interlock de partida para motores
- Monitoramento de fator de potência dinâmico
- Proteções elétricas e alarmes hierárquicos
- Coleta automática de dados históricos
- Autenticação JWT com controle de acesso baseado em roles

## 🚀 Tecnologias Utilizadas

- **Java 17**
- **Spring Boot 3.2.0**
  - Spring Data JPA
  - Spring Security
  - Spring Web
  - Spring WebSocket
- **H2 Database** (desenvolvimento)
- **MySQL** (produção - opcional)
- **JWT** (JSON Web Tokens)
- **Maven**
- **Lombok**

## 📁 Estrutura do Projeto

```
scada-backend/
├── src/
│   └── main/
│       ├── java/com/mattschutz/scada/
│       │   ├── config/          # Configurações
│       │   │   ├── DataInitializer.java
│       │   │   └── SecurityConfig.java
│       │   ├── controller/      # REST Controllers
│       │   │   ├── AlarmController.java
│       │   │   ├── AuthenticationController.java
│       │   │   ├── EquipmentController.java
│       │   │   └── HistoricalDataController.java
│       │   ├── dto/             # Data Transfer Objects
│       │   │   ├── AuthenticationRequest.java
│       │   │   ├── AuthenticationResponse.java
│       │   │   └── RegisterRequest.java
│       │   ├── entity/          # Entidades JPA
│       │   │   ├── AlarmEvent.java
│       │   │   ├── AlarmSeverity.java
│       │   │   ├── AlarmType.java
│       │   │   ├── Equipment.java
│       │   │   ├── EquipmentStatus.java
│       │   │   ├── EquipmentType.java
│       │   │   ├── HistoricalData.java
│       │   │   ├── Inverter.java
│       │   │   ├── Motor.java
│       │   │   ├── Transformer.java
│       │   │   ├── User.java
│       │   │   └── UserRole.java
│       │   ├── repository/      # Repositórios JPA
│       │   │   ├── AlarmEventRepository.java
│       │   │   ├── EquipmentRepository.java
│       │   │   ├── HistoricalDataRepository.java
│       │   │   └── UserRepository.java
│       │   ├── security/        # Segurança
│       │   │   ├── JwtAuthenticationFilter.java
│       │   │   └── UserDetailsServiceImpl.java
│       │   ├── service/         # Serviços
│       │   │   ├── AlarmService.java
│       │   │   ├── AuthenticationService.java
│       │   │   ├── EquipmentService.java
│       │   │   ├── HistoricalDataService.java
│       │   │   ├── InterlockService.java
│       │   │   └── JwtService.java
│       │   └── ScadaBackendApplication.java
│       └── resources/
│           └── application.properties
└── pom.xml
```

## ⚙️ Configuração

### Pré-requisitos

- JDK 17 ou superior
- Maven 3.6+
- MySQL 8.0+ (opcional, para produção)

### Instalação

1. Clone o repositório:
```bash
git clone [URL_DO_REPOSITORIO]
cd scada-backend
```

2. Configure o banco de dados no `application.properties`:

**Desenvolvimento (H2):**
```properties
spring.datasource.url=jdbc:h2:mem:scadadb
spring.datasource.username=sa
spring.datasource.password=
spring.h2.console.enabled=true
```

**Produção (MySQL):**
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/scadadb
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
```

3. Compile o projeto:
```bash
mvn clean install
```

4. Execute a aplicação:
```bash
mvn spring-boot:run
```

A aplicação estará disponível em: `http://localhost:8080`

## 🔐 Usuários Padrão

O sistema cria automaticamente os seguintes usuários na inicialização:

| Usuário | Senha | Role | Permissões |
|---------|-------|------|------------|
| admin | admin123 | ADMIN | Acesso completo |
| supervisor | sup123 | SUPERVISOR | Controle avançado e configuração |
| operador | op123 | OPERATOR | Controle básico e visualização |
| visitante | visit123 | VISITOR | Apenas visualização |

## 📡 Endpoints da API

### Autenticação

```
POST /api/auth/login
POST /api/auth/register
```

### Equipamentos

```
GET    /api/equipment                    - Lista todos equipamentos
GET    /api/equipment/{id}              - Busca equipamento por ID
GET    /api/equipment/status/{status}   - Busca por status
GET    /api/equipment/type/{type}       - Busca por tipo
POST   /api/equipment                    - Cria equipamento
PUT    /api/equipment/{id}              - Atualiza equipamento
DELETE /api/equipment/{id}              - Deleta equipamento
POST   /api/equipment/{id}/start        - Inicia equipamento
POST   /api/equipment/{id}/stop         - Para equipamento
POST   /api/equipment/emergency-stop    - Parada de emergência
POST   /api/equipment/{id}/frequency    - Ajusta frequência (inversor)
GET    /api/equipment/{id}/inrush       - Calcula corrente de inrush
```

### Alarmes

```
GET  /api/alarms                          - Lista todos alarmes
GET  /api/alarms/active                   - Alarmes ativos
GET  /api/alarms/unacknowledged          - Alarmes não reconhecidos
GET  /api/alarms/equipment/{equipmentId} - Alarmes de equipamento
POST /api/alarms/{id}/acknowledge        - Reconhece alarme
POST /api/alarms/{id}/resolve            - Resolve alarme
GET  /api/alarms/statistics              - Estatísticas de alarmes
```

### Dados Históricos

```
GET /api/historical/equipment/{equipmentId}        - Dados históricos
GET /api/historical/equipment/{equipmentId}/range - Dados por período
GET /api/historical/equipment/{equipmentId}/hourly - Agregação horária
GET /api/historical/equipment/{equipmentId}/daily  - Agregação diária
```

## 🔧 Funcionalidades Principais

### 1. Cálculo de Corrente de Inrush

O sistema calcula automaticamente a corrente de inrush diferenciada por tipo de equipamento:

- **Motores**: k = 7 (Iinrush = 7 × Inominal)
- **Transformadores**: k = 8 (Iinrush = 8 × Inominal)
- **Capacitores**: Iinrush = Vrms × ω × C

### 2. Sistema de Interlock

Implementa tempo morto de 5 segundos entre partidas de motores para prevenir sobrecarga transitória no sistema de alimentação.

### 3. Coleta Automática de Dados

O sistema coleta automaticamente dados históricos de todos os equipamentos a cada 3 segundos usando `@Scheduled`.

### 4. Alarmes Inteligentes

Sistema hierárquico de alarmes com 5 níveis de severidade:
- INFO
- LOW
- MEDIUM
- HIGH
- CRITICAL

### 5. Autenticação JWT

Sistema completo de autenticação com tokens JWT e controle de acesso baseado em roles.

## 🔍 Console H2

Durante o desenvolvimento, você pode acessar o console H2:

```
URL: http://localhost:8080/h2-console
JDBC URL: jdbc:h2:mem:scadadb
Username: sa
Password: (deixe em branco)
```

## 📊 Modelo de Dados

### Hierarquia de Equipamentos

```
Equipment (classe base)
├── Motor (especialização)
├── Transformer (especialização)
└── Inverter (especialização)
```

### Relacionamentos

- Equipment 1 → N HistoricalData
- Equipment 1 → N AlarmEvent
- User → Role (enum)

## 🧪 Testes

Para executar os testes:

```bash
mvn test
```

## 📝 Documentação Adicional

Para mais informações sobre o projeto, consulte:

- `Documento_de_Especificação_de_Requisitos.pdf` - Especificação completa do sistema
- `Artigo.pdf` - Artigo científico sobre o sistema

## 👥 Autores

- **Ryhan Schutz** - Analista Técnico Senior
- **André Matteussi** - Arquiteto de Software

**Instituição:** Senai - CentroWEG  
**Localização:** Jaraguá do Sul – SC – Brasil  
**Ano:** 2025

## 📄 Licença

Este projeto é parte de um trabalho acadêmico desenvolvido no Senai-CentroWEG.

## 🤝 Contribuindo

Este é um projeto acadêmico. Para sugestões e melhorias, entre em contato com os autores.

## 📧 Contato

**MattSchütz Industrial Systems**  
Email: contato@mattschutz.com.br  
Slogan: *"Excelência em Automação Industrial"*

---

**Versão:** 1.0.0  
**Última Atualização:** Dezembro 2025
