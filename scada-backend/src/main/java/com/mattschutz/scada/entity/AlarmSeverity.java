package com.mattschutz.scada.entity;

public enum AlarmSeverity {
    INFO("Informação", 1),
    LOW("Baixa", 2),
    MEDIUM("Média", 3),
    HIGH("Alta", 4),
    CRITICAL("Crítica", 5);
    
    private final String description;
    private final int priority;
    
    AlarmSeverity(String description, int priority) {
        this.description = description;
        this.priority = priority;
    }
    
    public String getDescription() {
        return description;
    }
    
    public int getPriority() {
        return priority;
    }
    
    public boolean isHighPriority() {
        return priority >= 4;
    }
}
