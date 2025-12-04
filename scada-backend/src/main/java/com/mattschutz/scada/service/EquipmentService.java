package com.mattschutz.scada.service;

import com.mattschutz.scada.entity.*;
import com.mattschutz.scada.repository.EquipmentRepository;
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
public class EquipmentService {
    
    private final EquipmentRepository equipmentRepository;
    private final AlarmService alarmService;
    private final InterlockService interlockService;
    
    public List<Equipment> findAll() {
        return equipmentRepository.findAll();
    }
    
    public Optional<Equipment> findById(String id) {
        return equipmentRepository.findById(id);
    }
    
    public List<Equipment> findByStatus(EquipmentStatus status) {
        return equipmentRepository.findByStatus(status);
    }
    
    public List<Equipment> findByType(EquipmentType type) {
        return equipmentRepository.findByType(type);
    }
    
    public Equipment save(Equipment equipment) {
        log.info("Salvando equipamento: {}", equipment.getName());
        return equipmentRepository.save(equipment);
    }
    
    public void delete(String id) {
        log.info("Deletando equipamento com ID: {}", id);
        equipmentRepository.deleteById(id);
    }
    
    /**
     * Inicia um equipamento com verificações de segurança
     */
    public Equipment startEquipment(String id) throws Exception {
        Equipment equipment = equipmentRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Equipamento não encontrado: " + id));
        
        log.info("Iniciando equipamento: {} ({})", equipment.getName(), equipment.getId());
        
        // Verificar se pode iniciar
        if (!equipment.canStart()) {
            throw new IllegalStateException(
                "Equipamento não pode ser iniciado. Status atual: " + equipment.getStatus()
            );
        }
        
        // Verificar interlock
        if (!interlockService.canStart(equipment)) {
            throw new IllegalStateException(
                "Interlock ativo: aguarde 5 segundos após o último start de motor"
            );
        }
        
        // Calcular corrente de inrush
        double inrushCurrent = equipment.calculateInrushCurrent();
        double inrushFactor = inrushCurrent / equipment.getNominalCurrent();
        log.info("Corrente de inrush calculada: {} A (nominal: {} A)", 
            inrushCurrent, equipment.getNominalCurrent());
        
        // Registrar log de inrush
        com.mattschutz.scada.dto.InrushLogDTO inrushLog = com.mattschutz.scada.dto.InrushLogDTO.builder()
                .timestamp(System.currentTimeMillis())
                .equipmentId(equipment.getId())
                .equipmentName(equipment.getName())
                .inrushCurrent(inrushCurrent)
                .nominalCurrent(equipment.getNominalCurrent())
                .inrushFactor(inrushFactor)
                .alarm(inrushCurrent > equipment.getNominalCurrent() * 12)
                .build();
        com.mattschutz.scada.controller.LogsController.addInrushLog(inrushLog);
        
        // Verificar se corrente de inrush é muito alta
        if (inrushCurrent > equipment.getNominalCurrent() * 12) {
            alarmService.createAlarm(
                equipment,
                AlarmSeverity.CRITICAL,
                AlarmType.INRUSH_HIGH,
                String.format("Corrente de inrush muito alta: %.2f A (%.1fx nominal)", 
                    inrushCurrent, inrushCurrent / equipment.getNominalCurrent())
            );
        }
        
        // Atualizar status
        equipment.setStatus(EquipmentStatus.STARTING);
        equipment = equipmentRepository.save(equipment);
        
        // Registrar no interlock
        interlockService.registerStart(equipment);
        
        // Simular partida
        simulateStartup(equipment);
        
        // Atualizar para RUNNING
        equipment.setStatus(EquipmentStatus.RUNNING);
        equipment.setCurrent(equipment.getNominalCurrent() * 0.8); // 80% da nominal
        equipment.setPower(equipment.getNominalCurrent() * equipment.getVoltage() * Math.sqrt(3) / 1000 * 0.8);
        equipment.updatePowerFactor();
        
        equipment = equipmentRepository.save(equipment);
        
        // Criar evento de sistema
        alarmService.createAlarm(
            equipment,
            AlarmSeverity.INFO,
            AlarmType.SYSTEM,
            "Equipamento iniciado com sucesso"
        );
        
        log.info("Equipamento iniciado: {}", equipment.getName());
        return equipment;
    }
    
    /**
     * Para um equipamento
     */
    public Equipment stopEquipment(String id) throws Exception {
        Equipment equipment = equipmentRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Equipamento não encontrado: " + id));
        
        log.info("Parando equipamento: {} ({})", equipment.getName(), equipment.getId());
        
        if (!equipment.canStop()) {
            throw new IllegalStateException(
                "Equipamento não pode ser parado. Status atual: " + equipment.getStatus()
            );
        }
        
        equipment.setStatus(EquipmentStatus.STOPPING);
        equipment = equipmentRepository.save(equipment);
        
        // Simular parada
        simulateShutdown(equipment);
        
        equipment.setStatus(EquipmentStatus.STOPPED);
        equipment.setCurrent(0.0);
        equipment.setPower(0.0);
        equipment.setActivePower(0.0);
        equipment.setReactivePower(0.0);
        
        if (equipment instanceof Motor) {
            ((Motor) equipment).setRpm(0.0);
            ((Motor) equipment).setTorque(0.0);
        }
        
        if (equipment instanceof Inverter) {
            ((Inverter) equipment).setOutputFrequency(0.0);
        }
        
        equipment = equipmentRepository.save(equipment);
        
        alarmService.createAlarm(
            equipment,
            AlarmSeverity.INFO,
            AlarmType.SYSTEM,
            "Equipamento parado"
        );
        
        log.info("Equipamento parado: {}", equipment.getName());
        return equipment;
    }
    
    /**
     * Parada de emergência
     */
    public void emergencyStopAll() {
        log.warn("PARADA DE EMERGÊNCIA ACIONADA");
        
        List<Equipment> runningEquipment = findByStatus(EquipmentStatus.RUNNING);
        
        for (Equipment equipment : runningEquipment) {
            equipment.setStatus(EquipmentStatus.STOPPED);
            equipment.setCurrent(0.0);
            equipment.setPower(0.0);
            
            alarmService.createAlarm(
                equipment,
                AlarmSeverity.CRITICAL,
                AlarmType.EMERGENCY_STOP,
                "Parada de emergência acionada"
            );
        }
        
        equipmentRepository.saveAll(runningEquipment);
    }
    
    /**
     * Atualiza parâmetros de um inversor
     */
    public Equipment updateInverterFrequency(String id, Double frequency) throws Exception {
        Equipment equipment = equipmentRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Equipamento não encontrado: " + id));
        
        if (!(equipment instanceof Inverter)) {
            throw new IllegalArgumentException("Equipamento não é um inversor");
        }
        
        Inverter inverter = (Inverter) equipment;
        
        if (!inverter.isFrequencyValid(frequency)) {
            throw new IllegalArgumentException(
                String.format("Frequência inválida: %.2f Hz. Deve estar entre %.2f e %.2f Hz",
                    frequency, inverter.getMinFrequency(), inverter.getMaxFrequency())
            );
        }
        
        inverter.adjustFrequency(frequency);
        
        // Simular mudança de frequência
        if (inverter.isRunning()) {
            inverter.setOutputFrequency(frequency);
            double motorSpeed = inverter.calculateMotorSpeed();
            
            // Se há motor conectado, atualizar RPM
            if (equipment instanceof Motor) {
                ((Motor) equipment).setRpm(motorSpeed);
            }
        }
        
        return equipmentRepository.save(inverter);
    }
    
    /**
     * Verifica e cria alarmes baseado em condições dos equipamentos
     */
    public void checkEquipmentAlarms() {
        List<Equipment> allEquipment = findAll();
        
        for (Equipment equipment : allEquipment) {
            // Verificar sobrecarga
            if (equipment.getCurrent() != null && equipment.getNominalCurrent() != null) {
                double loadPercent = (equipment.getCurrent() / equipment.getNominalCurrent()) * 100;
                
                if (loadPercent > 110) {
                    alarmService.createAlarm(
                        equipment,
                        AlarmSeverity.HIGH,
                        AlarmType.OVERLOAD,
                        String.format("Sobrecarga: %.1f%% da corrente nominal", loadPercent)
                    );
                }
            }
            
            // Verificar temperatura
            if (equipment.getTemperature() != null && equipment.getTemperature() > 80) {
                alarmService.createAlarm(
                    equipment,
                    AlarmSeverity.HIGH,
                    AlarmType.OVERTEMPERATURE,
                    String.format("Temperatura alta: %.1f°C", equipment.getTemperature())
                );
            }
            
            // Verificações específicas para motores
            if (equipment instanceof Motor) {
                Motor motor = (Motor) equipment;
                if (motor.isOverheating()) {
                    alarmService.createAlarm(
                        motor,
                        AlarmSeverity.CRITICAL,
                        AlarmType.OVERTEMPERATURE,
                        String.format("Motor superaquecido: %.1f°C (limite classe %s excedido)",
                            motor.getTemperature(), motor.getInsulationClass())
                    );
                }
                
                if (motor.hasExcessiveVibration()) {
                    alarmService.createAlarm(
                        motor,
                        AlarmSeverity.MEDIUM,
                        AlarmType.VIBRATION,
                        String.format("Vibração excessiva: %.2f mm/s", motor.getVibrationLevel())
                    );
                }
            }
            
            // Verificações específicas para transformadores
            if (equipment instanceof Transformer) {
                Transformer trafo = (Transformer) equipment;
                
                if (trafo.isOilLevelLow()) {
                    alarmService.createAlarm(
                        trafo,
                        AlarmSeverity.HIGH,
                        AlarmType.LOW_OIL_LEVEL,
                        String.format("Nível de óleo baixo: %.1f%%", trafo.getOilLevel() * 100)
                    );
                }
                
                if (trafo.isOilTemperatureHigh()) {
                    alarmService.createAlarm(
                        trafo,
                        AlarmSeverity.HIGH,
                        AlarmType.HIGH_OIL_TEMPERATURE,
                        String.format("Temperatura de óleo alta: %.1f°C", trafo.getOilTemperature())
                    );
                }
            }
        }
    }
    
    private void simulateStartup(Equipment equipment) {
        // Simular tempo de partida
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    private void simulateShutdown(Equipment equipment) {
        // Simular tempo de parada
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
