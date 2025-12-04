package com.mattschutz.scada.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de log de inrush para frontend
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InrushLogDTO {
    private Long timestamp; // Unix timestamp em milliseconds
    private String equipmentId;
    private String equipmentName;
    private Double inrushCurrent;
    private Double nominalCurrent;
    private Double inrushFactor;
    private Boolean alarm;
}
