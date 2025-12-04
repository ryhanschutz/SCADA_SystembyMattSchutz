package com.mattschutz.scada.controller;

import com.mattschutz.scada.dto.EquipmentFrontendDTO;
import com.mattschutz.scada.dto.HistoricalDataSimpleDTO;
import com.mattschutz.scada.entity.Equipment;
import com.mattschutz.scada.mapper.FrontendMapper;
import com.mattschutz.scada.service.EquipmentService;
import com.mattschutz.scada.service.HistoricalDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller compatível com frontend React (endpoints em português)
 */
@RestController
@RequestMapping("/api/equipamentos")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "${cors.allowed-origins}")
public class EquipamentoController {
    
    private final EquipmentService equipmentService;
    private final HistoricalDataService historicalDataService;
    private final FrontendMapper frontendMapper;
    
    /**
     * GET /api/equipamentos - Lista todos os equipamentos
     */
    @GetMapping
    public ResponseEntity<List<EquipmentFrontendDTO>> getAllEquipamentos() {
        List<Equipment> equipment = equipmentService.findAll();
        List<EquipmentFrontendDTO> dtos = frontendMapper.toFrontendDTOList(equipment);
        return ResponseEntity.ok(dtos);
    }
    
    /**
     * GET /api/equipamentos/{id} - Busca equipamento por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<EquipmentFrontendDTO> getEquipamentoById(@PathVariable String id) {
        return equipmentService.findById(id)
                .map(frontendMapper::toFrontendDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * PUT /api/equipamentos/{id}/status - Atualiza status do equipamento
     * Frontend envia: { "status": "running" | "stopped" }
     */
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('OPERATOR', 'SUPERVISOR', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> updateStatus(
            @PathVariable String id,
            @RequestBody Map<String, String> request) {
        
        try {
            String status = request.get("status");
            
            if (status == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "error", "Status não informado"));
            }
            
            Equipment equipment;
            
            if ("running".equals(status)) {
                equipment = equipmentService.startEquipment(id);
            } else if ("stopped".equals(status)) {
                equipment = equipmentService.stopEquipment(id);
            } else {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "error", "Status inválido: " + status));
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("equipment", frontendMapper.toFrontendDTO(equipment));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Erro ao atualizar status do equipamento {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }
    
    /**
     * GET /api/equipamentos/{id}/historico - Busca histórico do equipamento
     */
    @GetMapping("/{id}/historico")
    public ResponseEntity<List<HistoricalDataSimpleDTO>> getHistorico(@PathVariable String id) {
        List<com.mattschutz.scada.entity.HistoricalData> historicalData = 
                historicalDataService.findByEquipmentId(id);
        
        // Retornar últimos 50 registros ordenados por timestamp desc
        List<HistoricalDataSimpleDTO> dtos = historicalData.stream()
                .sorted((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()))
                .limit(50)
                .map(frontendMapper::toHistoryDTO)
                .toList();
        
        return ResponseEntity.ok(dtos);
    }
}
