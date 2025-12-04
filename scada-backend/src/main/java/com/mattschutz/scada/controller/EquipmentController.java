package com.mattschutz.scada.controller;

import com.mattschutz.scada.entity.*;
import com.mattschutz.scada.service.EquipmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/equipment")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "${cors.allowed-origins}")
public class EquipmentController {
    
    private final EquipmentService equipmentService;
    
    @GetMapping
    public ResponseEntity<List<Equipment>> getAllEquipment() {
        return ResponseEntity.ok(equipmentService.findAll());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Equipment> getEquipmentById(@PathVariable String id) {
        return equipmentService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Equipment>> getEquipmentByStatus(@PathVariable EquipmentStatus status) {
        return ResponseEntity.ok(equipmentService.findByStatus(status));
    }
    
    @GetMapping("/type/{type}")
    public ResponseEntity<List<Equipment>> getEquipmentByType(@PathVariable EquipmentType type) {
        return ResponseEntity.ok(equipmentService.findByType(type));
    }
    
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<Equipment> createEquipment(@RequestBody Equipment equipment) {
        Equipment saved = equipmentService.save(equipment);
        return ResponseEntity.ok(saved);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<Equipment> updateEquipment(
            @PathVariable String id,
            @RequestBody Equipment equipment) {
        return equipmentService.findById(id)
                .map(existing -> {
                    equipment.setId(id);
                    return ResponseEntity.ok(equipmentService.save(equipment));
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteEquipment(@PathVariable String id) {
        equipmentService.delete(id);
        return ResponseEntity.ok().build();
    }
    
    /**
     * Inicia um equipamento
     */
    @PostMapping("/{id}/start")
    @PreAuthorize("hasAnyRole('OPERATOR', 'SUPERVISOR', 'ADMIN')")
    public ResponseEntity<?> startEquipment(@PathVariable String id) {
        try {
            Equipment equipment = equipmentService.startEquipment(id);
            return ResponseEntity.ok(equipment);
        } catch (Exception e) {
            log.error("Erro ao iniciar equipamento {}: {}", id, e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Para um equipamento
     */
    @PostMapping("/{id}/stop")
    @PreAuthorize("hasAnyRole('OPERATOR', 'SUPERVISOR', 'ADMIN')")
    public ResponseEntity<?> stopEquipment(@PathVariable String id) {
        try {
            Equipment equipment = equipmentService.stopEquipment(id);
            return ResponseEntity.ok(equipment);
        } catch (Exception e) {
            log.error("Erro ao parar equipamento {}: {}", id, e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Parada de emergência de todos os equipamentos
     */
    @PostMapping("/emergency-stop")
    @PreAuthorize("hasAnyRole('OPERATOR', 'SUPERVISOR', 'ADMIN')")
    public ResponseEntity<Map<String, String>> emergencyStopAll() {
        equipmentService.emergencyStopAll();
        Map<String, String> response = new HashMap<>();
        response.put("message", "Parada de emergência acionada com sucesso");
        return ResponseEntity.ok(response);
    }
    
    /**
     * Ajusta frequência de um inversor
     */
    @PostMapping("/{id}/frequency")
    @PreAuthorize("hasAnyRole('OPERATOR', 'SUPERVISOR', 'ADMIN')")
    public ResponseEntity<?> adjustFrequency(
            @PathVariable String id,
            @RequestBody Map<String, Double> request) {
        try {
            Double frequency = request.get("frequency");
            if (frequency == null) {
                throw new IllegalArgumentException("Frequência não informada");
            }
            
            Equipment equipment = equipmentService.updateInverterFrequency(id, frequency);
            return ResponseEntity.ok(equipment);
        } catch (Exception e) {
            log.error("Erro ao ajustar frequência do equipamento {}: {}", id, e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Calcula corrente de inrush de um equipamento
     */
    @GetMapping("/{id}/inrush")
    public ResponseEntity<Map<String, Object>> getInrushCurrent(@PathVariable String id) {
        return equipmentService.findById(id)
                .map(equipment -> {
                    double inrush = equipment.calculateInrushCurrent();
                    double factor = equipment.getNominalCurrent() != null && equipment.getNominalCurrent() > 0
                            ? inrush / equipment.getNominalCurrent()
                            : 0;
                    
                    Map<String, Object> response = new HashMap<>();
                    response.put("equipmentId", id);
                    response.put("equipmentName", equipment.getName());
                    response.put("nominalCurrent", equipment.getNominalCurrent());
                    response.put("inrushCurrent", inrush);
                    response.put("inrushFactor", factor);
                    response.put("type", equipment.getType());
                    
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
