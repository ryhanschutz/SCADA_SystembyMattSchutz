package com.mattschutz.scada.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;

@Entity
@Table(name = "historical_data", indexes = {
    @Index(name = "idx_equipment_timestamp", columnList = "equipment_id,timestamp"),
    @Index(name = "idx_timestamp", columnList = "timestamp")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistoricalData {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipment_id", nullable = false)
    @JsonIgnoreProperties({"historicalData", "alarms"})
    private Equipment equipment;
    
    @Column(nullable = false)
    private LocalDateTime timestamp;
    
    // Electrical Measurements
    @Column(name = "current_value")
    private Double current;
    
    private Double voltage;
    private Double power;
    private Double temperature;
    
    @Column(name = "active_power")
    private Double activePower;
    
    @Column(name = "reactive_power")
    private Double reactivePower;
    
    @Column(name = "power_factor")
    private Double powerFactor;
    
    // Motor-specific
    private Double rpm;
    private Double torque;
    private Double frequency;
    
    // Transformer-specific
    @Column(name = "oil_temperature")
    private Double oilTemperature;
    
    @Column(name = "oil_level")
    private Double oilLevel;
    
    // Quality indicators
    @Column(name = "quality_index")
    private Integer qualityIndex = 100; // 0-100
    
    private String source = "automatic"; // automatic, manual, modbus, etc.
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }
    
    public HistoricalData(Equipment equipment) {
        this.equipment = equipment;
        this.timestamp = LocalDateTime.now();
        copyFromEquipment(equipment);
    }
    
    public void copyFromEquipment(Equipment eq) {
        this.current = eq.getCurrent();
        this.voltage = eq.getVoltage();
        this.power = eq.getPower();
        this.temperature = eq.getTemperature();
        this.activePower = eq.getActivePower();
        this.reactivePower = eq.getReactivePower();
        this.powerFactor = eq.getPowerFactor();
        
        if (eq instanceof Motor) {
            Motor motor = (Motor) eq;
            this.rpm = motor.getRpm();
            this.torque = motor.getTorque();
        }
        
        if (eq instanceof Inverter) {
            Inverter inverter = (Inverter) eq;
            this.frequency = inverter.getOutputFrequency();
        }
        
        if (eq instanceof Transformer) {
            Transformer transformer = (Transformer) eq;
            this.oilTemperature = transformer.getOilTemperature();
            this.oilLevel = transformer.getOilLevel();
        }
    }
}
