package com.mattschutz.scada.service;

import com.mattschutz.scada.entity.*;
import com.mattschutz.scada.repository.AlarmEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AlarmService {
    
    private final AlarmEventRepository alarmEventRepository;
    
    public List<AlarmEvent> findAll() {
        return alarmEventRepository.findAll();
    }
    
    public Optional<AlarmEvent> findById(String id) {
        return alarmEventRepository.findById(id);
    }
    
    public List<AlarmEvent> findActiveAlarms() {
        return alarmEventRepository.findActiveAlarms();
    }
    
    public List<AlarmEvent> findUnacknowledgedActiveAlarms() {
        return alarmEventRepository.findUnacknowledgedActiveAlarms();
    }
    
    public List<AlarmEvent> findByEquipmentId(String equipmentId) {
        return alarmEventRepository.findByEquipmentId(equipmentId);
    }
    
    public List<AlarmEvent> findByTimestampBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return alarmEventRepository.findByTimestampBetween(startDate, endDate);
    }
    
    public long countActiveAlarms() {
        return alarmEventRepository.countActiveAlarms();
    }
    
    public long countActiveAlarmsBySeverity(AlarmSeverity severity) {
        return alarmEventRepository.countActiveAlarmsBySeverity(severity);
    }
    
    /**
     * Cria um novo alarme
     */
    public AlarmEvent createAlarm(
            Equipment equipment,
            AlarmSeverity severity,
            AlarmType type,
            String message) {
        return createAlarm(equipment, severity, type, message, null, null, null);
    }
    
    /**
     * Cria um novo alarme com informações detalhadas
     */
    public AlarmEvent createAlarm(
            Equipment equipment,
            AlarmSeverity severity,
            AlarmType type,
            String message,
            String description,
            Double value,
            Double threshold) {
        
        AlarmEvent alarm = new AlarmEvent();
        alarm.setEquipment(equipment);
        alarm.setTimestamp(LocalDateTime.now());
        alarm.setSeverity(severity);
        alarm.setType(type);
        alarm.setMessage(message);
        alarm.setDescription(description);
        alarm.setValue(value);
        alarm.setThreshold(threshold);
        alarm.setAcknowledged(false);
        
        alarm = alarmEventRepository.save(alarm);
        
        log.info("Alarme criado: {} - {} - {} ({})", 
            severity, type, message, equipment != null ? equipment.getName() : "Sistema");
        
        return alarm;
    }
    
    /**
     * Reconhece um alarme
     */
    public AlarmEvent acknowledgeAlarm(String alarmId, String username) {
        AlarmEvent alarm = alarmEventRepository.findById(alarmId)
            .orElseThrow(() -> new IllegalArgumentException("Alarme não encontrado: " + alarmId));
        
        if (alarm.getAcknowledged()) {
            throw new IllegalStateException("Alarme já foi reconhecido");
        }
        
        alarm.acknowledge(username);
        alarm = alarmEventRepository.save(alarm);
        
        log.info("Alarme reconhecido por {}: {} - {}", username, alarm.getType(), alarm.getMessage());
        
        return alarm;
    }
    
    /**
     * Resolve um alarme
     */
    public AlarmEvent resolveAlarm(String alarmId) {
        AlarmEvent alarm = alarmEventRepository.findById(alarmId)
            .orElseThrow(() -> new IllegalArgumentException("Alarme não encontrado: " + alarmId));
        
        if (alarm.getResolvedAt() != null) {
            throw new IllegalStateException("Alarme já foi resolvido");
        }
        
        alarm.resolve();
        alarm = alarmEventRepository.save(alarm);
        
        log.info("Alarme resolvido: {} - {} (duração: {} minutos)", 
            alarm.getType(), alarm.getMessage(), alarm.getDurationMinutes());
        
        return alarm;
    }
    
    /**
     * Resolve alarmes automaticamente baseado em condições do equipamento
     */
    public void autoResolveAlarms(Equipment equipment) {
        List<AlarmEvent> activeAlarms = alarmEventRepository
            .findActiveAlarmsByEquipmentId(equipment.getId());
        
        for (AlarmEvent alarm : activeAlarms) {
            boolean shouldResolve = false;
            
            switch (alarm.getType()) {
                case OVERCURRENT:
                    if (equipment.getCurrent() <= equipment.getNominalCurrent() * 1.05) {
                        shouldResolve = true;
                    }
                    break;
                    
                case OVERTEMPERATURE:
                    if (equipment.getTemperature() <= 75.0) {
                        shouldResolve = true;
                    }
                    break;
                    
                case OVERLOAD:
                    double loadPercent = (equipment.getCurrent() / equipment.getNominalCurrent()) * 100;
                    if (loadPercent <= 100.0) {
                        shouldResolve = true;
                    }
                    break;
                    
                default:
                    // Outros tipos de alarme não são auto-resolvidos
                    break;
            }
            
            if (shouldResolve) {
                resolveAlarm(alarm.getId());
            }
        }
    }
    
    /**
     * Cria alarme de sistema (sem equipamento associado)
     */
    public AlarmEvent createSystemAlarm(
            AlarmSeverity severity,
            AlarmType type,
            String message) {
        return createAlarm(null, severity, type, message, null, null, null);
    }
}
