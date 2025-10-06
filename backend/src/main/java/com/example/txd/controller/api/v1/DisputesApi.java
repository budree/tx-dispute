package com.example.txd.controller.api.v1;

import com.example.txd.dto.DisputeAdvanceRequest;
import com.example.txd.dto.DisputeCreateRequest;
import com.example.txd.dto.DisputeResponse;
import com.example.txd.model.DisputeStatus;
import com.example.txd.service.DisputeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Tag(name = "Disputes", description = "Create, view, and advance transaction disputes")
@RestController
@RequestMapping("/api/v1/disputes")
public class DisputesApi {

    private final DisputeService service;

    public DisputesApi(DisputeService service) {
        this.service = service;
    }

    @Operation(summary = "Create a new dispute")
    @PostMapping
    public ResponseEntity<DisputeResponse> create(@Valid @RequestBody DisputeCreateRequest request) {
        DisputeResponse created = service.create(request);
        return ResponseEntity.created(URI.create("/api/v1/disputes/" + created.getId())).body(created);
    }

    @Operation(summary = "Get a dispute by ID")
    @GetMapping("/{id}")
    public ResponseEntity<DisputeResponse> get(@PathVariable Long id) {
        DisputeResponse resp = service.get(id);
        return (resp == null) ? ResponseEntity.notFound().build() : ResponseEntity.ok(resp);
    }

    @Operation(summary = "List all disputes")
    @GetMapping
    public List<DisputeResponse> list() {
        return service.list();
    }

    @Operation(summary = "Advance dispute status (OPEN → UNDER_REVIEW → RESOLVED/REJECTED)")
    @PostMapping("/{id}:advance")
    public ResponseEntity<DisputeResponse> advance(@PathVariable Long id,
                                                   @Valid @RequestBody DisputeAdvanceRequest request) {
        DisputeStatus next = request.getNextStatus();
        DisputeResponse updated = service.advance(id, next);
        return ResponseEntity.ok(updated);
    }
}
