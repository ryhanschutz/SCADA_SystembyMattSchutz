package com.mattschutz.scada.repository;

import com.mattschutz.scada.entity.AlarmEvent;
import com.mattschutz.scada.entity.AlarmSeverity;
import com.mattschutz.scada.entity.AlarmType;
import com.mattschutz.scada.entity.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AlarmEventRepository extends JpaRepository<AlarmEvent, String> {
    
    List<AlarmEvent> findByEquipment(Equipment equipment);
    
    @Query("SELECT a FROM AlarmEvent a WHERE a.equipment.id = :equipmentId ORDER BY a.timestamp DESC")
    List<AlarmEvent> findByEquipmentId(@Param("equipmentId") String equipmentId);
    
    @Query("SELECT a FROM AlarmEvent a WHERE a.resolvedAt IS NULL ORDER BY a.severity DESC, a.timestamp DESC")
    List<AlarmEvent> findActiveAlarms();
    
    @Query("SELECT a FROM AlarmEvent a WHERE a.acknowledged = false AND a.resolvedAt IS NULL " +
           "ORDER BY a.severity DESC, a.timestamp DESC")
    List<AlarmEvent> findUnacknowledgedActiveAlarms();
    
    List<AlarmEvent> findBySeverity(AlarmSeverity severity);
    
    List<AlarmEvent> findByType(AlarmType type);
    
    @Query("SELECT a FROM AlarmEvent a WHERE a.timestamp BETWEEN :startDate AND :endDate " +
           "ORDER BY a.timestamp DESC")
    List<AlarmEvent> findByTimestampBetween(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
    
    @Query("SELECT a FROM AlarmEvent a WHERE a.equipment.id = :equipmentId " +
           "AND a.timestamp BETWEEN :startDate AND :endDate ORDER BY a.timestamp DESC")
    List<AlarmEvent> findByEquipmentIdAndTimestampBetween(
        @Param("equipmentId") String equipmentId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
    
    @Query("SELECT COUNT(a) FROM AlarmEvent a WHERE a.resolvedAt IS NULL")
    long countActiveAlarms();
    
    @Query("SELECT COUNT(a) FROM AlarmEvent a WHERE a.resolvedAt IS NULL AND a.severity = :severity")
    long countActiveAlarmsBySeverity(@Param("severity") AlarmSeverity severity);
    
    @Query("SELECT a FROM AlarmEvent a WHERE a.equipment.id = :equipmentId " +
           "AND a.resolvedAt IS NULL ORDER BY a.timestamp DESC")
    List<AlarmEvent> findActiveAlarmsByEquipmentId(@Param("equipmentId") String equipmentId);
}
