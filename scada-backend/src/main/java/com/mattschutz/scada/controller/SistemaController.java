package com.mattschutz.scada.controller;

import com.mattschutz.scada.dto.SystemStatusDTO;
import com.mattschutz.scada.entity.Equipment;
import com.mattschutz.scada.entity.EquipmentStatus;
import com.mattschutz.scada.service.EquipmentService;
import com.mattschutz.scada.service.InterlockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller para status do sistema, métricas e emergência
 */
@RestController
@RequestMapping("/api/sistema")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "${cors.allowed-origins}")
public class SistemaController {
    
    private final EquipmentService equipmentService;
    private final InterlockService interlockService;
    
    private boolean emergencyActive = false;
    
    /**
     * GET /api/sistema/status - Status geral do sistema
     */
    @GetMapping("/status")
    public ResponseEntity<SystemStatusDTO> getSystemStatus() {
        List<Equipment> allEquipment = equipmentService.findAll();
        List<Equipment> runningEquipment = equipmentService
                .findByStatus(EquipmentStatus.RUNNING);
        
        // Calcular corrente total
        double totalCurrent = runningEquipment.stream()
                .mapToDouble(e -> e.getCurrent() != null ? e.getCurrent() : 0.0)
                .sum();
        
        // Calcular tensão média
        double avgVoltage = runningEquipment.isEmpty() ? 380.0 :
                runningEquipment.stream()
                        .mapToDouble(e -> e.getVoltage() != null ? e.getVoltage() : 380.0)
                        .average()
                        .orElse(380.0);
        
        // Calcular fator de potência
        double powerFactor = calculatePowerFactor(allEquipment);
        
        // Status de interlock
        boolean interlockActive = interlockService.getRemainingInterlockTime() > 0;
        
        SystemStatusDTO status = SystemStatusDTO.builder()
                .powerFactor(powerFactor)
                .totalCurrent(totalCurrent)
                .voltage(avgVoltage)
                .isEmergencyActive(emergencyActive)
                .interlockActive(interlockActive)
                .build();
        
        return ResponseEntity.ok(status);
    }
    
    /**
     * GET /api/sistema/metricas - Métricas de potência
     */
    @GetMapping("/metricas")
    public ResponseEntity<Map<String, Object>> getPowerMetrics() {
        List<Equipment> allEquipment = equipmentService.findAll();
        List<Equipment> runningEquipment = equipmentService
                .findByStatus(EquipmentStatus.RUNNING);
        
        double totalCurrent = runningEquipment.stream()
                .mapToDouble(e -> e.getCurrent() != null ? e.getCurrent() : 0.0)
                .sum();
        
        double avgVoltage = runningEquipment.isEmpty() ? 380.0 :
                runningEquipment.stream()
                        .mapToDouble(e -> e.getVoltage() != null ? e.getVoltage() : 380.0)
                        .average()
                        .orElse(380.0);
        
        double powerFactor = calculatePowerFactor(allEquipment);
        
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("powerFactor", powerFactor);
        metrics.put("current", totalCurrent);
        metrics.put("voltage", avgVoltage);
        
        return ResponseEntity.ok(metrics);
    }
    
    /**
     * POST /api/sistema/emergencia - Ativa/desativa emergência
     * Body: { "active": true | false }
     */
    @PostMapping("/emergencia")
    @PreAuthorize("hasAnyRole('OPERATOR', 'SUPERVISOR', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> setEmergency(
            @RequestBody Map<String, Boolean> request) {
        
        Boolean active = request.get("active");
        if (active == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "error", "Parâmetro 'active' não informado"));
        }
        
        if (active) {
            log.warn("PARADA DE EMERGÊNCIA ACIONADA");
            equipmentService.emergencyStopAll();
            emergencyActive = true;
        } else {
            log.info("Parada de emergência desativada");
            emergencyActive = false;
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("emergencyActive", emergencyActive);
        response.put("message", active ? "Emergência acionada" : "Emergência desativada");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Calcula o fator de potência baseado nos equipamentos
     */
    private double calculatePowerFactor(List<Equipment> allEquipment) {
        long runningMotors = allEquipment.stream()
                .filter(e -> "MOTOR".equals(e.getType().toString()) && e.isRunning())
                .count();
        
        long runningInverters = allEquipment.stream()
                .filter(e -> "INVERTER".equals(e.getType().toString()) && e.isRunning())
                .count();
        
        boolean capacitorBankRunning = allEquipment.stream()
                .anyMatch(e -> "CAPACITOR".equals(e.getType().toString()) && e.isRunning());
        
        double powerFactor = 0.95; // Base
        
        if (runningMotors > 0) {
            powerFactor -= runningMotors * 0.05; // Motores diminuem o FP
        }
        
        if (runningInverters > 0) {
            powerFactor -= runningInverters * 0.03; // Inversores também afetam
        }
        
        if (capacitorBankRunning) {
            powerFactor += 0.15; // Banco de capacitores melhora o FP
        }
        
        // Limitar entre 0.7 e 0.99
        return Math.max(0.7, Math.min(0.99, powerFactor));
    }
}
