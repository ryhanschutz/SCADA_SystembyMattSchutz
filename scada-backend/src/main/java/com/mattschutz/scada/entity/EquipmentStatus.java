package com.mattschutz.scada.entity;

public enum EquipmentStatus {
    STOPPED("Parado"),
    IDLE("Em espera"),
    STARTING("Iniciando"),
    RUNNING("Em operação"),
    STOPPING("Parando"),
    WARNING("Advertência"),
    ALARM("Alarme"),
    FAULT("Falha"),
    MAINTENANCE("Manutenção"),
    OFFLINE("Offline");
    
    private final String description;
    
    EquipmentStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    public boolean isOperational() {
        return this == RUNNING || this == STARTING || this == WARNING;
    }
    
    public boolean requiresAttention() {
        return this == WARNING || this == ALARM || this == FAULT;
    }
}
