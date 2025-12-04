package com.mattschutz.scada.controller;

import com.mattschutz.scada.entity.AlarmEvent;
import com.mattschutz.scada.service.AlarmService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/alarms")
@RequiredArgsConstructor
@CrossOrigin(origins = "${cors.allowed-origins}")
public class AlarmController {
    
    private final AlarmService alarmService;
    
    @GetMapping
    public ResponseEntity<List<AlarmEvent>> getAllAlarms() {
        return ResponseEntity.ok(alarmService.findAll());
    }
    
    @GetMapping("/active")
    public ResponseEntity<List<AlarmEvent>> getActiveAlarms() {
        return ResponseEntity.ok(alarmService.findActiveAlarms());
    }
    
    @GetMapping("/unacknowledged")
    public ResponseEntity<List<AlarmEvent>> getUnacknowledgedAlarms() {
        return ResponseEntity.ok(alarmService.findUnacknowledgedActiveAlarms());
    }
    
    @GetMapping("/equipment/{equipmentId}")
    public ResponseEntity<List<AlarmEvent>> getAlarmsByEquipment(@PathVariable String equipmentId) {
        return ResponseEntity.ok(alarmService.findByEquipmentId(equipmentId));
    }
    
    @GetMapping("/range")
    public ResponseEntity<List<AlarmEvent>> getAlarmsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(alarmService.findByTimestampBetween(start, end));
    }
    
    @PostMapping("/{id}/acknowledge")
    @PreAuthorize("hasAnyRole('OPERATOR', 'SUPERVISOR', 'ADMIN')")
    public ResponseEntity<AlarmEvent> acknowledgeAlarm(
            @PathVariable String id,
            Authentication authentication) {
        String username = authentication.getName();
        AlarmEvent alarm = alarmService.acknowledgeAlarm(id, username);
        return ResponseEntity.ok(alarm);
    }
    
    @PostMapping("/{id}/resolve")
    @PreAuthorize("hasAnyRole('SUPERVISOR', 'ADMIN')")
    public ResponseEntity<AlarmEvent> resolveAlarm(@PathVariable String id) {
        AlarmEvent alarm = alarmService.resolveAlarm(id);
        return ResponseEntity.ok(alarm);
    }
    
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getAlarmStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalActive", alarmService.countActiveAlarms());
        stats.put("critical", alarmService.countActiveAlarmsBySeverity(
            com.mattschutz.scada.entity.AlarmSeverity.CRITICAL));
        stats.put("high", alarmService.countActiveAlarmsBySeverity(
            com.mattschutz.scada.entity.AlarmSeverity.HIGH));
        stats.put("medium", alarmService.countActiveAlarmsBySeverity(
            com.mattschutz.scada.entity.AlarmSeverity.MEDIUM));
        return ResponseEntity.ok(stats);
    }
}
