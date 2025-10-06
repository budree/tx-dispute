package com.example.txd.dto;

import java.math.BigDecimal;
import java.time.Instant;

public class TransactionResponse {
    private Long id;
    private String reference;
    private BigDecimal amount;
    private String currency;
    private String description; // NEW
    private Instant createdAt;

    public TransactionResponse(Long id, String reference, BigDecimal amount,
                               String currency, String description, Instant createdAt) {
        this.id = id;
        this.reference = reference;
        this.amount = amount;
        this.currency = currency;
        this.description = description;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public String getReference() { return reference; }
    public BigDecimal getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public String getDescription() { return description; }
    public Instant getCreatedAt() { return createdAt; }
}
