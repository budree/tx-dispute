package com.example.txd.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public class TransactionCreateRequest {
    @NotBlank
    private String reference;

    @NotNull @Positive
    private BigDecimal amount;

    // optional (defaults to "ZAR")
    private String currency;

    private String description;      // NEW (optional)

    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}

