package com.mattschutz.scada.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "inverter")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@PrimaryKeyJoinColumn(name = "equipment_id")
public class Inverter extends Equipment {
    
    @Column(name = "output_frequency")
    private Double outputFrequency = 0.0; // Hz
    
    @Column(name = "frequency_setpoint")
    private Double frequencySetpoint = 60.0; // Hz
    
    @Column(name = "min_frequency")
    private Double minFrequency = 10.0; // Hz
    
    @Column(name = "max_frequency")
    private Double maxFrequency = 60.0; // Hz
    
    @Column(name = "acceleration_ramp")
    private Double accelerationRamp = 10.0; // seconds
    
    @Column(name = "deceleration_ramp")
    private Double decelerationRamp = 10.0; // seconds
    
    @Column(name = "pwm_frequency")
    private Double pwmFrequency = 5000.0; // Hz
    
    @Column(name = "dc_bus_voltage")
    private Double dcBusVoltage = 0.0; // V
    
    @Column(name = "output_current_limit")
    private Double outputCurrentLimit = 150.0; // % of nominal
    
    @Column(name = "overload_capacity")
    private Double overloadCapacity = 150.0; // % for 60s
    
    @Column(name = "control_mode")
    private String controlMode = "V/F"; // V/F, Vector, Torque
    
    @Column(name = "drive_status")
    private String driveStatus = "READY"; // READY, RUNNING, FAULT, WARNING
    
    @Column(name = "fault_code")
    private String faultCode;
    
    @Column(name = "running_hours")
    private Double runningHours = 0.0;
    
    @Column(name = "energy_consumption")
    private Double energyConsumption = 0.0; // kWh
    
    @Column(name = "regenerative_energy")
    private Double regenerativeEnergy = 0.0; // kWh
    
    // Motor Parameters
    @Column(name = "motor_rated_power")
    private Double motorRatedPower; // kW
    
    @Column(name = "motor_rated_voltage")
    private Double motorRatedVoltage = 380.0; // V
    
    @Column(name = "motor_rated_current")
    private Double motorRatedCurrent; // A
    
    @Column(name = "motor_rated_frequency")
    private Double motorRatedFrequency = 60.0; // Hz
    
    @Column(name = "motor_rated_rpm")
    private Double motorRatedRpm = 1800.0; // rpm
    
    @Column(name = "motor_poles")
    private Integer motorPoles = 4;
    
    public Inverter(String name, String location, String manufacturer, String model) {
        super();
        setName(name);
        setLocation(location);
        setManufacturer(manufacturer);
        setModel(model);
        setType(EquipmentType.INVERTER);
    }
    
    public boolean isFrequencyValid(Double frequency) {
        return frequency != null && 
               frequency >= minFrequency && 
               frequency <= maxFrequency;
    }
    
    public void adjustFrequency(Double targetFrequency) {
        if (!isFrequencyValid(targetFrequency)) {
            throw new IllegalArgumentException(
                String.format("Frequência inválida: %.2f Hz. Deve estar entre %.2f e %.2f Hz",
                    targetFrequency, minFrequency, maxFrequency)
            );
        }
        this.frequencySetpoint = targetFrequency;
    }
    
    public double calculateMotorSpeed() {
        if (outputFrequency != null && motorPoles != null && motorPoles > 0) {
            // n = (120 * f) / p
            return (120.0 * outputFrequency) / motorPoles;
        }
        return 0.0;
    }
    
    public double getLoadPercentage() {
        if (motorRatedCurrent != null && motorRatedCurrent > 0) {
            return (getCurrent() / motorRatedCurrent) * 100;
        }
        return 0.0;
    }
    
    public boolean isOverloaded() {
        return getLoadPercentage() > outputCurrentLimit;
    }
    
    public boolean isDcBusVoltageLow() {
        return dcBusVoltage != null && dcBusVoltage < 500.0; // Typical threshold
    }
    
    public boolean isDcBusVoltageHigh() {
        return dcBusVoltage != null && dcBusVoltage > 820.0; // Typical threshold
    }
    
    public boolean hasFault() {
        return "FAULT".equals(driveStatus) || faultCode != null;
    }
    
    public void clearFault() {
        this.driveStatus = "READY";
        this.faultCode = null;
    }
    
    public void updateEnergyConsumption(double intervalHours) {
        if (getPower() != null && getPower() > 0) {
            energyConsumption += (getPower() * intervalHours);
        }
    }
}
