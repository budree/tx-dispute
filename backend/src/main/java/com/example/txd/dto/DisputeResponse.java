package com.example.txd.dto;

import com.example.txd.model.DisputeStatus;
import java.time.Instant;

public class DisputeResponse {
    private Long id;
    private String transactionRef;
    private String reason;
    private DisputeStatus status;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant openedAt;
    private Instant underReviewAt;
    private Instant resolvedAt;
    private Instant rejectedAt;
    private Long userId;
    private String username;

    public DisputeResponse(Long id, String transactionRef, String reason,
                           DisputeStatus status, Instant createdAt, Instant updatedAt,
                           Instant openedAt, Instant underReviewAt, Instant resolvedAt, Instant rejectedAt,
                           Long userId, String username) {
        this.id = id;
        this.transactionRef = transactionRef;
        this.reason = reason;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.openedAt = openedAt;
        this.underReviewAt = underReviewAt;
        this.resolvedAt = resolvedAt;
        this.rejectedAt = rejectedAt;
        this.userId = userId;
        this.username = username;
    }

    // getters only (or add setters if you prefer)
    public Long getId() { return id; }
    public String getTransactionRef() { return transactionRef; }
    public String getReason() { return reason; }
    public DisputeStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public Instant getOpenedAt() { return openedAt; }
    public Instant getUnderReviewAt() { return underReviewAt; }
    public Instant getResolvedAt() { return resolvedAt; }
    public Instant getRejectedAt() { return rejectedAt; }
    public Long getUserId() { return userId; }
    public String getUsername() { return username; }
}
