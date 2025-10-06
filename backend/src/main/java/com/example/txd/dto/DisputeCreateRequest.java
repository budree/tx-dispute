package com.example.txd.dto;

import jakarta.validation.constraints.NotBlank;

public class DisputeCreateRequest {
    @NotBlank
    private String transactionRef;

    @NotBlank
    private String reason;

    public String getTransactionRef() { return transactionRef; }
    public void setTransactionRef(String transactionRef) { this.transactionRef = transactionRef; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
