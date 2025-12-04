package com.mattschutz.scada.mapper;

import com.mattschutz.scada.dto.EquipmentFrontendDTO;
import com.mattschutz.scada.dto.HistoricalDataSimpleDTO;
import com.mattschutz.scada.entity.*;
import com.mattschutz.scada.repository.HistoricalDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper para converter entidades do backend em DTOs compatíveis com frontend
 */
@Component
@RequiredArgsConstructor
public class FrontendMapper {
    
    private final HistoricalDataRepository historicalDataRepository;
    
    /**
     * Converte Equipment para EquipmentFrontendDTO
     */
    public EquipmentFrontendDTO toFrontendDTO(Equipment equipment) {
        // Buscar últimos 50 registros históricos
        List<HistoricalData> historicalList = historicalDataRepository
            .findByEquipmentId(equipment.getId())
            .stream()
            .sorted((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()))
            .limit(50)
            .collect(Collectors.toList());
        
        return EquipmentFrontendDTO.builder()
                .id(equipment.getId())
                .name(equipment.getName())
                .type(mapEquipmentType(equipment.getType()))
                .status(mapEquipmentStatus(equipment.getStatus()))
                .current(equipment.getCurrent())
                .voltage(equipment.getVoltage())
                .power(equipment.getPower())
                .temperature(equipment.getTemperature())
                .history(toHistoryDTOList(historicalList))
                .nominalCurrent(equipment.getNominalCurrent())
                .capacitance(equipment.getCapacitance())
                .build();
    }
    
    /**
     * Converte lista de Equipment para lista de EquipmentFrontendDTO
     */
    public List<EquipmentFrontendDTO> toFrontendDTOList(List<Equipment> equipmentList) {
        return equipmentList.stream()
                .map(this::toFrontendDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Converte HistoricalData para HistoricalDataSimpleDTO
     */
    public HistoricalDataSimpleDTO toHistoryDTO(HistoricalData data) {
        return HistoricalDataSimpleDTO.builder()
                .timestamp(data.getTimestamp()
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli())
                .current(data.getCurrent())
                .voltage(data.getVoltage())
                .power(data.getPower())
                .temperature(data.getTemperature())
                .build();
    }
    
    /**
     * Converte lista de HistoricalData para lista de HistoricalDataSimpleDTO
     */
    public List<HistoricalDataSimpleDTO> toHistoryDTOList(List<HistoricalData> dataList) {
        return dataList.stream()
                .map(this::toHistoryDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Mapeia EquipmentType para string em lowercase
     */
    private String mapEquipmentType(EquipmentType type) {
        switch (type) {
            case MOTOR:
                return "motor";
            case TRANSFORMER:
                return "transformer";
            case CAPACITOR:
                return "capacitor";
            case INVERTER:
                return "inverter";
            case GENERATOR:
                return "generator";
            default:
                return "other";
        }
    }
    
    /**
     * Mapeia EquipmentStatus para 'running' ou 'stopped'
     */
    private String mapEquipmentStatus(EquipmentStatus status) {
        switch (status) {
            case RUNNING:
            case STARTING:
            case WARNING:
                return "running";
            case STOPPED:
            case IDLE:
            case STOPPING:
            case ALARM:
            case FAULT:
            case MAINTENANCE:
            case OFFLINE:
            default:
                return "stopped";
        }
    }
}
