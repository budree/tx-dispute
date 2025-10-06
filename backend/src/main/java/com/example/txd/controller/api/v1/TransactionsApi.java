package com.example.txd.controller.api.v1;

import com.example.txd.dto.TransactionCreateRequest;
import com.example.txd.dto.TransactionResponse;
import com.example.txd.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Tag(name = "Transactions", description = "Create and view transactions")
@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionsApi {

    private final TransactionService service;

    public TransactionsApi(TransactionService service) {
        this.service = service;
    }

    @Operation(summary = "Create a new transaction")
    @PostMapping
    public ResponseEntity<TransactionResponse> create(@Valid @RequestBody TransactionCreateRequest request) {
        TransactionResponse created = service.create(request);
        return ResponseEntity.created(URI.create("/api/v1/transactions/" + created.getId())).body(created);
    }

    @Operation(summary = "Get a transaction by ID")
    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> get(@PathVariable Long id) {
        TransactionResponse resp = service.get(id);
        return (resp == null) ? ResponseEntity.notFound().build() : ResponseEntity.ok(resp);
    }

    @Operation(summary = "List all transactions")
    @GetMapping
    public List<TransactionResponse> list() {
        return service.list();
    }
}
