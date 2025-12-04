package com.mattschutz.scada.repository;

import com.mattschutz.scada.entity.Equipment;
import com.mattschutz.scada.entity.EquipmentStatus;
import com.mattschutz.scada.entity.EquipmentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, String> {
    
    List<Equipment> findByType(EquipmentType type);
    
    List<Equipment> findByStatus(EquipmentStatus status);
    
    List<Equipment> findByLocation(String location);
    
    Optional<Equipment> findByName(String name);
    
    @Query("SELECT e FROM Equipment e WHERE e.status = :status AND e.type = :type")
    List<Equipment> findByStatusAndType(
        @Param("status") EquipmentStatus status, 
        @Param("type") EquipmentType type
    );
    
    @Query("SELECT e FROM Equipment e WHERE e.temperature > :threshold")
    List<Equipment> findByTemperatureGreaterThan(@Param("threshold") Double threshold);
    
    @Query("SELECT e FROM Equipment e WHERE e.current > e.nominalCurrent * :factor")
    List<Equipment> findByCurrentOverload(@Param("factor") Double factor);
    
    @Query("SELECT COUNT(e) FROM Equipment e WHERE e.status = :status")
    long countByStatus(@Param("status") EquipmentStatus status);
    
    @Query("SELECT e FROM Equipment e WHERE e.status IN :statuses")
    List<Equipment> findByStatusIn(@Param("statuses") List<EquipmentStatus> statuses);
    
    @Query("SELECT e FROM Equipment e WHERE LOWER(e.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(e.location) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<Equipment> searchByNameOrLocation(@Param("search") String search);
}
