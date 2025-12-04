package com.mattschutz.scada.controller;

import com.mattschutz.scada.entity.HistoricalData;
import com.mattschutz.scada.service.HistoricalDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/historical")
@RequiredArgsConstructor
@CrossOrigin(origins = "${cors.allowed-origins}")
public class HistoricalDataController {
    
    private final HistoricalDataService historicalDataService;
    
    @GetMapping("/equipment/{equipmentId}")
    public ResponseEntity<List<HistoricalData>> getHistoricalData(@PathVariable String equipmentId) {
        return ResponseEntity.ok(historicalDataService.findByEquipmentId(equipmentId));
    }
    
    @GetMapping("/equipment/{equipmentId}/range")
    public ResponseEntity<List<HistoricalData>> getHistoricalDataByRange(
            @PathVariable String equipmentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(historicalDataService.findByEquipmentIdAndTimestampBetween(
            equipmentId, start, end));
    }
    
    @GetMapping("/equipment/{equipmentId}/hourly")
    public ResponseEntity<List<Object[]>> getHourlyAggregatedData(
            @PathVariable String equipmentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(historicalDataService.getHourlyAggregatedData(
            equipmentId, start, end));
    }
    
    @GetMapping("/equipment/{equipmentId}/daily")
    public ResponseEntity<List<Object[]>> getDailyAggregatedData(
            @PathVariable String equipmentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(historicalDataService.getDailyAggregatedData(
            equipmentId, start, end));
    }
}
