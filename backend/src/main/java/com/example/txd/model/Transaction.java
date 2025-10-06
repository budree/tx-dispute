package com.example.txd.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Unique reference for idempotency / external linkage
    @Column(nullable = false, unique = true)
    private String reference;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private String currency = "ZAR";

    @Column(length = 255)
    private String description;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @ManyToOne(optional=false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private AppUser user;

    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public Instant getCreatedAt() { return createdAt; }
    public AppUser getUser() { return user; }
    public void setUser(AppUser user) { this.user = user; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
