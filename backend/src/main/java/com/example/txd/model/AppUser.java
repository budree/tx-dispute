package com.example.txd.model;

import jakarta.persistence.*;

@Entity @Table(name = "users")
public class AppUser {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, unique=true)
    private String username;

    @Column(nullable=false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private Role role = Role.CLIENT;

    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String u) { this.username = u; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String p) { this.passwordHash = p; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
}