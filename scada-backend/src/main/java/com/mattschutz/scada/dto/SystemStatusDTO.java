package com.mattschutz.scada.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de status do sistema para frontend
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SystemStatusDTO {
    private Double powerFactor;
    private Double totalCurrent;
    private Double voltage;
    private Boolean isEmergencyActive;
    private Boolean interlockActive;
}
