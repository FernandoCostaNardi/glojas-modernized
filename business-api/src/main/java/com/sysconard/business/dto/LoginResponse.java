package com.sysconard.business.dto;

import java.util.Set;

public class LoginResponse {
    private String token;
    private String username;
    private String name;
    private Set<String> roles;
    private Set<String> permissions;
    
    // Construtores
    public LoginResponse() {}
    
    public LoginResponse(String token, String username, String name, Set<String> roles, Set<String> permissions) {
        this.token = token;
        this.username = username;
        this.name = name;
        this.roles = roles;
        this.permissions = permissions;
    }
    
    // Getters e Setters
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Set<String> getRoles() {
        return roles;
    }
    
    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }
    
    public Set<String> getPermissions() {
        return permissions;
    }
    
    public void setPermissions(Set<String> permissions) {
        this.permissions = permissions;
    }
}
