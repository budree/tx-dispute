// src/main/java/com/example/txd/model/Dispute.java
package com.example.txd.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "disputes")
public class Dispute {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String transactionRef;

    @Column(nullable = false)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DisputeStatus status = DisputeStatus.OPEN;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    private Instant updatedAt;

    // per-status timestamps
    @Column(nullable = false, updatable = false)
    private Instant openedAt = Instant.now();
    private Instant underReviewAt;
    private Instant resolvedAt;
    private Instant rejectedAt;

    // NEW: owner
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private AppUser user;

    @PreUpdate
    void onUpdate() { this.updatedAt = Instant.now(); }

    // getters/setters (only relevant ones shown)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTransactionRef() { return transactionRef; }
    public void setTransactionRef(String transactionRef) { this.transactionRef = transactionRef; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public DisputeStatus getStatus() { return status; }
    public void setStatus(DisputeStatus status) { this.status = status; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public Instant getOpenedAt() { return openedAt; }
    public void setOpenedAt(Instant openedAt) { this.openedAt = openedAt; }
    public Instant getUnderReviewAt() { return underReviewAt; }
    public void setUnderReviewAt(Instant underReviewAt) { this.underReviewAt = underReviewAt; }
    public Instant getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(Instant resolvedAt) { this.resolvedAt = resolvedAt; }
    public Instant getRejectedAt() { return rejectedAt; }
    public void setRejectedAt(Instant rejectedAt) { this.rejectedAt = rejectedAt; }
    public AppUser getUser() { return user; }
    public void setUser(AppUser user) { this.user = user; }
}
