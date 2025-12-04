package com.mattschutz.scada.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;

@Entity
@Table(name = "alarm_event", indexes = {
    @Index(name = "idx_equipment_timestamp", columnList = "equipment_id,timestamp"),
    @Index(name = "idx_severity", columnList = "severity"),
    @Index(name = "idx_acknowledged", columnList = "acknowledged")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlarmEvent {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipment_id")
    @JsonIgnoreProperties({"historicalData", "alarms"})
    private Equipment equipment;
    
    @Column(nullable = false)
    private LocalDateTime timestamp;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AlarmSeverity severity;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AlarmType type;
    
    @Column(nullable = false, length = 500)
    private String message;
    
    @Column(length = 1000)
    private String description;
    
    private Double value; // The value that triggered the alarm
    private Double threshold; // The threshold that was exceeded
    
    private Boolean acknowledged = false;
    
    @Column(name = "acknowledged_by")
    private String acknowledgedBy;
    
    @Column(name = "acknowledged_at")
    private LocalDateTime acknowledgedAt;
    
    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;
    
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
    
    public boolean isActive() {
        return resolvedAt == null;
    }
    
    public void acknowledge(String username) {
        this.acknowledged = true;
        this.acknowledgedBy = username;
        this.acknowledgedAt = LocalDateTime.now();
    }
    
    public void resolve() {
        this.resolvedAt = LocalDateTime.now();
    }
    
    public long getDurationMinutes() {
        LocalDateTime end = resolvedAt != null ? resolvedAt : LocalDateTime.now();
        return java.time.Duration.between(timestamp, end).toMinutes();
    }
}
