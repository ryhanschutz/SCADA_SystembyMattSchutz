package com.mattschutz.scada.repository;

import com.mattschutz.scada.entity.Equipment;
import com.mattschutz.scada.entity.HistoricalData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HistoricalDataRepository extends JpaRepository<HistoricalData, String> {
    
    List<HistoricalData> findByEquipment(Equipment equipment);
    
    @Query("SELECT h FROM HistoricalData h WHERE h.equipment.id = :equipmentId ORDER BY h.timestamp DESC")
    List<HistoricalData> findByEquipmentId(@Param("equipmentId") String equipmentId);
    
    List<HistoricalData> findByEquipmentAndTimestampBetween(
            Equipment equipment, LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT h FROM HistoricalData h WHERE h.equipment.id = :equipmentId AND h.timestamp BETWEEN :startDate AND :endDate ORDER BY h.timestamp DESC")
    List<HistoricalData> findByEquipmentIdAndTimestampBetween(
            @Param("equipmentId") String equipmentId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
    
    @Modifying
    @Query("DELETE FROM HistoricalData h WHERE h.timestamp < :beforeDate")
    long deleteByTimestampBefore(@Param("beforeDate") LocalDateTime beforeDate);
    
    void deleteByEquipment(Equipment equipment);
    
    long countByEquipment(Equipment equipment);
    
    // Métodos para dados agregados por hora (usando MySQL)
    @Query(value = "SELECT DATE_FORMAT(h.timestamp, '%Y-%m-%d %H:00:00') as hour, " +
                  "AVG(h.current_value) as avg_current, AVG(h.voltage) as avg_voltage, " +
                  "AVG(h.power) as avg_power, AVG(h.temperature) as avg_temperature " +
                  "FROM historical_data h WHERE h.equipment_id = :equipmentId " +
                  "AND h.timestamp BETWEEN :startDate AND :endDate " +
                  "GROUP BY DATE_FORMAT(h.timestamp, '%Y-%m-%d %H:00:00') " +
                  "ORDER BY hour", nativeQuery = true)
    List<Object[]> getHourlyAggregatedData(@Param("equipmentId") String equipmentId,
                                          @Param("startDate") LocalDateTime startDate,
                                          @Param("endDate") LocalDateTime endDate);
    
    // Métodos para dados agregados por dia (usando MySQL)
    @Query(value = "SELECT DATE_FORMAT(h.timestamp, '%Y-%m-%d') as day, " +
                  "AVG(h.current_value) as avg_current, AVG(h.voltage) as avg_voltage, " +
                  "AVG(h.power) as avg_power, AVG(h.temperature) as avg_temperature " +
                  "FROM historical_data h WHERE h.equipment_id = :equipmentId " +
                  "AND h.timestamp BETWEEN :startDate AND :endDate " +
                  "GROUP BY DATE_FORMAT(h.timestamp, '%Y-%m-%d') " +
                  "ORDER BY day", nativeQuery = true)
    List<Object[]> getDailyAggregatedData(@Param("equipmentId") String equipmentId,
                                         @Param("startDate") LocalDateTime startDate,
                                         @Param("endDate") LocalDateTime endDate);
}
