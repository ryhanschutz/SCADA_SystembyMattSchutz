package com.mattschutz.scada.entity;

public enum UserRole {
    ADMIN("Administrador", 4),
    SUPERVISOR("Supervisor", 3),
    OPERATOR("Operador", 2),
    VISITOR("Visitante", 1);
    
    private final String description;
    private final int level;
    
    UserRole(String description, int level) {
        this.description = description;
        this.level = level;
    }
    
    public String getDescription() {
        return description;
    }
    
    public int getLevel() {
        return level;
    }
    
    public boolean canControl() {
        return level >= 2; // OPERATOR and above
    }
    
    public boolean canConfigure() {
        return level >= 3; // SUPERVISOR and above
    }
    
    public boolean canManageUsers() {
        return level >= 4; // ADMIN only
    }
}
