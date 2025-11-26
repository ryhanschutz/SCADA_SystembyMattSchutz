# Integração com Backend Spring Boot

Este frontend está preparado para consumir dados de uma API REST Spring Boot.

## 🔧 Configuração

### 1. Modo Simulado (Padrão)
Por padrão, o frontend roda em **modo simulado** com dados mockados. Nenhuma configuração é necessária.

### 2. Modo API Real

Para conectar ao backend Spring Boot:

1. Crie um arquivo `.env` na raiz do projeto:
```bash
VITE_API_URL=http://localhost:8080/api
```

2. Reinicie o servidor de desenvolvimento:
```bash
npm run dev
```

## 📡 Endpoints Esperados no Spring Boot

O backend deve implementar os seguintes endpoints:

### Equipamentos
```
GET    /api/equipamentos              - Lista todos os equipamentos
GET    /api/equipamentos/{id}         - Busca equipamento por ID
PUT    /api/equipamentos/{id}/status  - Atualiza status (running/stopped)
GET    /api/equipamentos/{id}/historico - Histórico de dados
```

### Sistema
```
GET    /api/sistema/status     - Status geral do sistema
POST   /api/sistema/emergencia - Ativa/desativa emergência
GET    /api/sistema/metricas   - Métricas de potência
```

### Logs
```
GET    /api/logs/inrush        - Logs de corrente de inrush
```

## 📋 DTOs Esperados

### EquipmentDTO
```java
public class EquipmentDTO {
    private String id;
    private String name;
    private String type; // "motor", "transformer", "capacitor", "inverter", "generator"
    private String status; // "running", "stopped"
    private Double current;
    private Double voltage;
    private Double power;
    private Double temperature;
    private Double nominalCurrent;
    private Double capacitance; // Para capacitores
}
```

### SystemStatusDTO
```java
public class SystemStatusDTO {
    private Double powerFactor;
    private Double totalCurrent;
    private Double voltage;
    private Boolean isEmergencyActive;
    private Boolean interlockActive;
}
```

### InrushLogDTO
```java
public class InrushLogDTO {
    private Long timestamp;
    private String equipmentId;
    private String equipmentName;
    private Double inrushCurrent;
    private Double nominalCurrent;
    private Double inrushFactor;
    private Boolean alarm;
}
```

### HistoricalDataDTO
```java
public class HistoricalDataDTO {
    private Long timestamp;
    private Double current;
    private Double voltage;
    private Double power;
    private Double temperature;
}
```

## 🔐 CORS

Configure CORS no Spring Boot:

```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:5173", "https://seu-frontend.lovable.app")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("*");
    }
}
```

## 🎯 Estrutura do Projeto Frontend

```
src/
├── config/
│   └── api.ts              # Configuração de URLs e endpoints
├── services/
│   └── api.ts              # Service para chamadas HTTP
├── contexts/
│   ├── ScadaContext.tsx    # Contexto simulado (padrão)
│   └── ScadaContextApi.tsx # Contexto com API real
├── hooks/
│   └── useApiMode.ts       # Hook para detectar modo API
└── env.d.ts                # Tipos do ambiente
```

## 🚀 Alternando Entre Modos

O frontend detecta automaticamente se deve usar API real baseado na variável `VITE_API_URL`:

- **Sem `VITE_API_URL`**: Modo simulado
- **Com `VITE_API_URL`**: Tenta conectar à API real
  - Se a API estiver disponível: usa dados reais
  - Se não estiver: volta ao modo simulado

## 📊 Polling de Dados

O frontend atualiza dados automaticamente:
- **Status e equipamentos**: A cada 3 segundos
- **Histórico**: A cada 10 segundos

Configure o intervalo em `src/config/api.ts`:
```typescript
export const API_CONFIG = {
  POLLING_INTERVAL: 3000, // em milissegundos
};
```

## 🐛 Debug

Para ver logs de comunicação com a API, abra o console do navegador.

## 📝 Exemplo de Controller Spring Boot

```java
@RestController
@RequestMapping("/api/equipamentos")
public class EquipmentController {
    
    @GetMapping
    public List<EquipmentDTO> getAllEquipment() {
        // Implementar lógica
        return equipmentService.findAll();
    }
    
    @GetMapping("/{id}")
    public EquipmentDTO getEquipmentById(@PathVariable String id) {
        return equipmentService.findById(id);
    }
    
    @PutMapping("/{id}/status")
    public Map<String, Boolean> updateStatus(
            @PathVariable String id,
            @RequestBody Map<String, String> body) {
        String status = body.get("status");
        boolean success = equipmentService.updateStatus(id, status);
        return Map.of("success", success);
    }
    
    @GetMapping("/{id}/historico")
    public List<HistoricalDataDTO> getHistory(@PathVariable String id) {
        return equipmentService.getHistory(id);
    }
}
```
