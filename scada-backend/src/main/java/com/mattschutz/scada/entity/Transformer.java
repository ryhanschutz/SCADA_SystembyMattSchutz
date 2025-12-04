package com.mattschutz.scada.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "transformer")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@PrimaryKeyJoinColumn(name = "equipment_id")
public class Transformer extends Equipment {
    
    @Column(name = "primary_voltage")
    private Double primaryVoltage; // V
    
    @Column(name = "secondary_voltage")
    private Double secondaryVoltage; // V
    
    @Column(name = "rated_power_kva")
    private Double ratedPowerKVA; // kVA
    
    @Column(name = "connection_type")
    private String connectionType = "Delta-Estrela"; // Delta-Wye, Delta-Delta, Wye-Wye
    
    @Column(name = "cooling_type")
    private String coolingType = "ONAN"; // ONAN, ONAF, OFAF
    
    @Column(name = "impedance_percent")
    private Double impedancePercent = 5.0; // %
    
    @Column(name = "oil_level")
    private Double oilLevel = 0.85; // 0-1 (85%)
    
    @Column(name = "oil_temperature")
    private Double oilTemperature = 45.0; // °C
    
    @Column(name = "winding_temperature_primary")
    private Double windingTemperaturePrimary = 50.0; // °C
    
    @Column(name = "winding_temperature_secondary")
    private Double windingTemperatureSecondary = 50.0; // °C
    
    @Column(name = "tap_position")
    private Integer tapPosition = 0; // -5 to +5 typically
    
    @Column(name = "insulation_resistance")
    private Double insulationResistance; // MΩ
    
    @Column(name = "power_factor_loss")
    private Double powerFactorLoss = 0.98;
    
    public Transformer(String name, String location, String manufacturer, String model) {
        super();
        setName(name);
        setLocation(location);
        setManufacturer(manufacturer);
        setModel(model);
        setType(EquipmentType.TRANSFORMER);
    }
    
    public double getTransformationRatio() {
        if (primaryVoltage != null && secondaryVoltage != null && secondaryVoltage != 0) {
            return primaryVoltage / secondaryVoltage;
        }
        return 1.0;
    }
    
    public double getLoadPercentage() {
        if (ratedPowerKVA != null && ratedPowerKVA > 0) {
            double apparentPower = getVoltage() * getCurrent() * Math.sqrt(3) / 1000; // kVA
            return (apparentPower / ratedPowerKVA) * 100;
        }
        return 0.0;
    }
    
    public boolean isOverloaded() {
        return getLoadPercentage() > 110.0;
    }
    
    public boolean isOilLevelLow() {
        return oilLevel != null && oilLevel < 0.7; // Below 70%
    }
    
    public boolean isOilTemperatureHigh() {
        return oilTemperature != null && oilTemperature > 95.0; // > 95°C
    }
    
    public boolean isWindingTemperatureHigh() {
        if (windingTemperaturePrimary != null && windingTemperaturePrimary > 105.0) {
            return true;
        }
        if (windingTemperatureSecondary != null && windingTemperatureSecondary > 105.0) {
            return true;
        }
        return false;
    }
    
    public double calculateEfficiency() {
        if (getPower() != null && getPower() > 0) {
            double input = getPower() / powerFactorLoss;
            return (getPower() / input) * 100;
        }
        return 100.0;
    }
}
