package com.mattschutz.scada.service;

import com.mattschutz.scada.entity.Equipment;
import com.mattschutz.scada.entity.EquipmentType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Serviço de Interlock para prevenir partidas simultâneas de motores
 * Implementa tempo morto de 5 segundos entre partidas
 */
@Service
@Slf4j
public class InterlockService {
    
    private static final long INTERLOCK_DELAY_SECONDS = 5;
    private final Map<String, LocalDateTime> lastStartTimes = new ConcurrentHashMap<>();
    private LocalDateTime lastMotorStart = null;
    
    /**
     * Verifica se o equipamento pode ser iniciado
     */
    public boolean canStart(Equipment equipment) {
        // Apenas motores são sujeitos ao interlock
        if (equipment.getType() != EquipmentType.MOTOR) {
            return true;
        }
        
        // Verificar se há start recente de qualquer motor
        if (lastMotorStart != null) {
            Duration timeSinceLastStart = Duration.between(lastMotorStart, LocalDateTime.now());
            
            if (timeSinceLastStart.getSeconds() < INTERLOCK_DELAY_SECONDS) {
                long remainingSeconds = INTERLOCK_DELAY_SECONDS - timeSinceLastStart.getSeconds();
                log.warn("Interlock ativo: aguarde {} segundos", remainingSeconds);
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Registra o início de um equipamento
     */
    public void registerStart(Equipment equipment) {
        LocalDateTime now = LocalDateTime.now();
        lastStartTimes.put(equipment.getId(), now);
        
        if (equipment.getType() == EquipmentType.MOTOR) {
            lastMotorStart = now;
            log.info("Motor start registrado: {} às {}", equipment.getName(), now);
        }
    }
    
    /**
     * Retorna o tempo restante de interlock em segundos
     */
    public long getRemainingInterlockTime() {
        if (lastMotorStart == null) {
            return 0;
        }
        
        Duration timeSinceLastStart = Duration.between(lastMotorStart, LocalDateTime.now());
        long remainingSeconds = INTERLOCK_DELAY_SECONDS - timeSinceLastStart.getSeconds();
        
        return Math.max(0, remainingSeconds);
    }
    
    /**
     * Retorna quando foi o último start de motor
     */
    public LocalDateTime getLastMotorStartTime() {
        return lastMotorStart;
    }
    
    /**
     * Limpa o histórico de interlock (para testes)
     */
    public void clearInterlockHistory() {
        lastStartTimes.clear();
        lastMotorStart = null;
        log.info("Histórico de interlock limpo");
    }
}
