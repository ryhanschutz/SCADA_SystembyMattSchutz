package com.mattschutz.scada.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "motor")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@PrimaryKeyJoinColumn(name = "equipment_id")
public class Motor extends Equipment {
    
    private Double rpm = 0.0;
    private Integer poles = 4;
    
    @Column(name = "rated_power")
    private Double ratedPower; // kW
    
    @Column(name = "rated_voltage")
    private Double ratedVoltage; // V
    
    @Column(name = "rated_frequency")
    private Double ratedFrequency = 60.0; // Hz
    
    @Column(name = "insulation_class")
    private String insulationClass = "F";
    
    @Column(name = "service_type")
    private String serviceType = "S1";
    
    @Column(name = "cooling_type")
    private String coolingType = "IC411";
    
    @Column(name = "efficiency_class")
    private String efficiencyClass = "IE3";
    
    @Column(name = "starting_type")
    private String startingType = "DOL"; // Direct On Line, Star-Delta, Soft-Start, VFD
    
    private Double torque = 0.0; // N.m
    
    @Column(name = "vibration_level")
    private Double vibrationLevel = 0.0; // mm/s
    
    @Column(name = "bearing_temperature")
    private Double bearingTemperature = 25.0; // °C
    
    // Calculated Properties
    @Transient
    private Double slip;
    
    @Transient
    private Double synchronousSpeed;
    
    public Motor(String name, String location, String manufacturer, String model) {
        super();
        setName(name);
        setLocation(location);
        setManufacturer(manufacturer);
        setModel(model);
        setType(EquipmentType.MOTOR);
    }
    
    public void calculateSlip() {
        synchronousSpeed = (120.0 * getRatedFrequency()) / poles;
        if (synchronousSpeed > 0 && rpm != null) {
            slip = ((synchronousSpeed - rpm) / synchronousSpeed) * 100;
        } else {
            slip = 0.0;
        }
    }
    
    public void calculateTorque() {
        // T = (P * 60) / (2 * π * n)
        if (getPower() != null && rpm != null && rpm > 0) {
            torque = (getPower() * 1000 * 60) / (2 * Math.PI * rpm);
        }
    }
    
    public boolean isOverheating() {
        Double temp = getTemperature();
        if (temp == null) return false;
        
        switch (insulationClass != null ? insulationClass : "F") {
            case "A": return temp > 105;
            case "E": return temp > 120;
            case "B": return temp > 130;
            case "F": return temp > 155;
            case "H": return temp > 180;
            default: return temp > 155;
        }
    }
    
    public boolean hasExcessiveVibration() {
        if (vibrationLevel == null) return false;
        return vibrationLevel > 7.1; // ISO 10816-3 Zone C threshold
    }
}
