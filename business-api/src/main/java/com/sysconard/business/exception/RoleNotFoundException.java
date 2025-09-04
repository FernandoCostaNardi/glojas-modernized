package com.sysconard.business.exception;

public class RoleNotFoundException extends RuntimeException {
    
    private final String roleName;
    
    public RoleNotFoundException(String roleName) {
        super(String.format("Role '%s' não encontrada no sistema", roleName));
        this.roleName = roleName;
    }
    
    public String getRoleName() {
        return roleName;
    }
}
