package com.mattschutz.scada.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO simplificado de dados históricos para compatibilidade com frontend
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HistoricalDataSimpleDTO {
    private Long timestamp; // Unix timestamp em milliseconds
    private Double current;
    private Double voltage;
    private Double power;
    private Double temperature;
}
