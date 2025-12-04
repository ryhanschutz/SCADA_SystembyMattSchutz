package com.mattschutz.scada.controller;

import com.mattschutz.scada.dto.InrushLogDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Controller para logs de inrush
 */
@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "${cors.allowed-origins}")
public class LogsController {
    
    // Armazenamento em memória dos logs de inrush (últimos 100)
    private static final ConcurrentLinkedQueue<InrushLogDTO> inrushLogs = new ConcurrentLinkedQueue<>();
    private static final int MAX_LOGS = 100;
    
    /**
     * GET /api/logs/inrush - Retorna todos os logs de inrush
     */
    @GetMapping("/inrush")
    public ResponseEntity<List<InrushLogDTO>> getInrushLogs() {
        return ResponseEntity.ok(new ArrayList<>(inrushLogs));
    }
    
    /**
     * Método estático para registrar log de inrush (chamado de outros serviços)
     */
    public static void addInrushLog(InrushLogDTO log) {
        inrushLogs.add(log);
        
        // Manter apenas os últimos 100 logs
        while (inrushLogs.size() > MAX_LOGS) {
            inrushLogs.poll();
        }
        
        log.info("Log de inrush registrado: {} - {}A ({}x)", 
                log.getEquipmentName(), 
                String.format("%.1f", log.getInrushCurrent()),
                String.format("%.1f", log.getInrushFactor()));
    }
}
