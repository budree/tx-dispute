package com.example.txd.dto;

import com.example.txd.model.DisputeStatus;
import jakarta.validation.constraints.NotNull;

public class DisputeAdvanceRequest {
    @NotNull
    private DisputeStatus nextStatus;

    public DisputeStatus getNextStatus() { return nextStatus; }
    public void setNextStatus(DisputeStatus nextStatus) { this.nextStatus = nextStatus; }
}
