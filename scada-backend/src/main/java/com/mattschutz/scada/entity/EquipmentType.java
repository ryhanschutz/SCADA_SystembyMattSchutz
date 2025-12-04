package com.mattschutz.scada.entity;

public enum EquipmentType {
    MOTOR("Motor"),
    TRANSFORMER("Transformador"),
    CAPACITOR("Capacitor"),
    INVERTER("Inversor de Frequência"),
    PUMP("Bomba"),
    VALVE("Válvula"),
    SENSOR("Sensor"),
    GENERATOR("Gerador"),
    BREAKER("Disjuntor"),
    OTHER("Outro");
    
    private final String description;
    
    EquipmentType(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}
