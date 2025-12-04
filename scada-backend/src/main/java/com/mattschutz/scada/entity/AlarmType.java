package com.mattschutz.scada.entity;

public enum AlarmType {
    OVERCURRENT("Sobrecorrente"),
    OVERVOLTAGE("Sobretensão"),
    UNDERVOLTAGE("Subtensão"),
    OVERTEMPERATURE("Sobretemperatura"),
    OVERLOAD("Sobrecarga"),
    VIBRATION("Vibração Excessiva"),
    LOW_OIL_LEVEL("Nível de Óleo Baixo"),
    HIGH_OIL_TEMPERATURE("Temperatura de Óleo Alta"),
    COMMUNICATION_FAILURE("Falha de Comunicação"),
    EMERGENCY_STOP("Parada de Emergência"),
    INRUSH_HIGH("Corrente de Inrush Alta"),
    POWER_FACTOR_LOW("Fator de Potência Baixo"),
    FREQUENCY_OUT_OF_RANGE("Frequência Fora da Faixa"),
    FAULT("Falha"),
    MAINTENANCE_DUE("Manutenção Necessária"),
    SYSTEM("Sistema"),
    OTHER("Outro");
    
    private final String description;
    
    AlarmType(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}
