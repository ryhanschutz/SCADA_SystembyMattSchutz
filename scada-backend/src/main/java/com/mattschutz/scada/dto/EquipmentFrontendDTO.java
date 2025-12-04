package com.mattschutz.scada.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO de equipamento compatível com frontend React
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EquipmentFrontendDTO {
    private String id;
    private String name;
    private String type; // lowercase: 'motor', 'transformer', etc
    private String status; // simplified: 'running' ou 'stopped'
    private Double current;
    private Double voltage;
    private Double power;
    private Double temperature;
    private List<HistoricalDataSimpleDTO> history; // últimos 50 registros
    private Double nominalCurrent;
    private Double capacitance; // Para capacitores
}
