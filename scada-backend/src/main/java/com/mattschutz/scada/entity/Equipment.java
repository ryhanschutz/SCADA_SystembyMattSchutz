package com.mattschutz.scada.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "equipment")
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Equipment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @Column(nullable = false)
    private String name;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EquipmentType type;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EquipmentStatus status = EquipmentStatus.STOPPED;
    
    private String location;
    private String manufacturer;
    private String model;
    private String serialNumber;
    
    // Electrical Parameters
    @Column(name = "nominal_current")
    private Double nominalCurrent;
    
    @Column(name = "current_value")
    private Double current = 0.0;
    
    private Double voltage = 0.0;
    private Double power = 0.0;
    private Double temperature = 25.0;
    
    @Column(name = "active_power")
    private Double activePower = 0.0;
    
    @Column(name = "reactive_power")
    private Double reactivePower = 0.0;
    
    @Column(name = "power_factor")
    private Double powerFactor = 1.0;
    
    // Additional Properties
    private Double capacitance; // For capacitors
    
    @Column(name = "installation_date")
    private LocalDateTime installationDate;
    
    @Column(name = "last_maintenance")
    private LocalDateTime lastMaintenance;
    
    @Column(name = "next_maintenance")
    private LocalDateTime nextMaintenance;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Relationships
    @OneToMany(mappedBy = "equipment", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("equipment")
    private List<HistoricalData> historicalData = new ArrayList<>();
    
    @OneToMany(mappedBy = "equipment", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("equipment")
    private List<AlarmEvent> alarms = new ArrayList<>();
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Business Methods
    public boolean isRunning() {
        return status == EquipmentStatus.RUNNING;
    }
    
    public boolean canStart() {
        return status == EquipmentStatus.STOPPED || status == EquipmentStatus.IDLE;
    }
    
    public boolean canStop() {
        return status == EquipmentStatus.RUNNING || status == EquipmentStatus.WARNING;
    }
    
    public double calculateInrushCurrent() {
        if (nominalCurrent == null || nominalCurrent == 0) {
            return 0.0;
        }
        
        switch (type) {
            case MOTOR:
                return nominalCurrent * 7.0; // k = 7 for motors
            case TRANSFORMER:
                return nominalCurrent * 8.0; // k = 8 for transformers
            case CAPACITOR:
                // Iinrush = Vrms × ω × C
                if (voltage != null && capacitance != null) {
                    double omega = 2 * Math.PI * 60; // 60 Hz
                    return voltage * omega * capacitance / 1_000_000; // Convert µF to F
                }
                return nominalCurrent * 4.8;
            default:
                return nominalCurrent * 6.0;
        }
    }
    
    public void updatePowerFactor() {
        if (activePower != null && activePower > 0) {
            double apparentPower = Math.sqrt(
                Math.pow(activePower, 2) + Math.pow(reactivePower != null ? reactivePower : 0, 2)
            );
            powerFactor = apparentPower > 0 ? activePower / apparentPower : 1.0;
        } else {
            powerFactor = 1.0;
        }
    }
}
