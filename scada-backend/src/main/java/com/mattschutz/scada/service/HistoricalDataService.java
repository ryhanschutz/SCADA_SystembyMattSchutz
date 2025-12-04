package com.mattschutz.scada.service;

import com.mattschutz.scada.entity.Equipment;
import com.mattschutz.scada.entity.HistoricalData;
import com.mattschutz.scada.repository.HistoricalDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class HistoricalDataService {
    
    private final HistoricalDataRepository historicalDataRepository;
    private final EquipmentService equipmentService;
    
    public HistoricalData save(HistoricalData historicalData) {
        return historicalDataRepository.save(historicalData);
    }
    
    public List<HistoricalData> findByEquipmentId(String equipmentId) {
        return historicalDataRepository.findByEquipmentId(equipmentId);
    }
    
    public List<HistoricalData> findByEquipmentIdAndTimestampBetween(
            String equipmentId, 
            LocalDateTime startDate, 
            LocalDateTime endDate) {
        return historicalDataRepository.findByEquipmentIdAndTimestampBetween(
            equipmentId, startDate, endDate
        );
    }
    
    public List<Object[]> getHourlyAggregatedData(
            String equipmentId,
            LocalDateTime startDate,
            LocalDateTime endDate) {
        return historicalDataRepository.getHourlyAggregatedData(equipmentId, startDate, endDate);
    }
    
    public List<Object[]> getDailyAggregatedData(
            String equipmentId,
            LocalDateTime startDate,
            LocalDateTime endDate) {
        return historicalDataRepository.getDailyAggregatedData(equipmentId, startDate, endDate);
    }
    
    /**
     * Coleta dados históricos de todos os equipamentos
     * Executado automaticamente a cada 3 segundos
     */
    @Scheduled(fixedRate = 3000) // 3 segundos
    public void collectHistoricalData() {
        List<Equipment> allEquipment = equipmentService.findAll();
        
        for (Equipment equipment : allEquipment) {
            try {
                HistoricalData data = new HistoricalData(equipment);
                data.setSource("automatic");
                save(data);
            } catch (Exception e) {
                log.error("Erro ao coletar dados históricos do equipamento {}: {}", 
                    equipment.getId(), e.getMessage());
            }
        }
    }
    
    /**
     * Limpa dados históricos antigos
     * Executado diariamente à meia-noite
     */
    @Scheduled(cron = "0 0 0 * * *") // Meia-noite todos os dias
    public void cleanupOldData() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(90); // Manter 90 dias
        long deletedCount = historicalDataRepository.deleteByTimestampBefore(cutoffDate);
        log.info("Dados históricos limpos: {} registros deletados antes de {}", 
            deletedCount, cutoffDate);
    }
    
    /**
     * Cria dados históricos de amostra para demonstração
     */
    public void createSampleData(Equipment equipment, int count) {
        LocalDateTime now = LocalDateTime.now();
        
        for (int i = count; i >= 0; i--) {
            HistoricalData data = new HistoricalData(equipment);
            data.setTimestamp(now.minusMinutes(i * 2)); // A cada 2 minutos
            data.setSource("sample");
            save(data);
        }
        
        log.info("Criados {} pontos de dados históricos de amostra para {}", 
            count + 1, equipment.getName());
    }
}
