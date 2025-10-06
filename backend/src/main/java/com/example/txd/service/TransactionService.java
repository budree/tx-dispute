package com.example.txd.service;

import com.example.txd.dto.TransactionCreateRequest;
import com.example.txd.dto.TransactionResponse;
import com.example.txd.model.Role;
import com.example.txd.model.Transaction;
import com.example.txd.repo.TransactionRepository;
import com.example.txd.repo.UserRepository;
import com.example.txd.security.Sec;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository repo;
    private final UserRepository userRepo;   // <-- inject this

    public TransactionService(TransactionRepository repo, UserRepository userRepo) {
        this.repo = repo;
        this.userRepo = userRepo;
    }

    @Transactional
    public TransactionResponse create(TransactionCreateRequest req) {
        Transaction t = new Transaction();
        t.setReference(req.getReference());
        t.setAmount(req.getAmount());
        if (req.getDescription()!=null) {
            t.setDescription(req.getDescription());
        }
        if (req.getCurrency() != null && !req.getCurrency().isBlank()) {
            t.setCurrency(req.getCurrency());
        }
        // attach current user
        Long uid = Sec.userId();
        t.setUser(userRepo.findById(uid).orElseThrow());
        t = repo.save(t);
        return toResponse(t);
    }

    @Transactional(readOnly = true)
    public TransactionResponse get(Long id) {
        return repo.findById(id).map(this::toResponse).orElse(null);
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> list() {
        if (Role.ADMIN.name().equals(Sec.role())) {
            return repo.findAll().stream().map(this::toResponse).toList();
        }
        Long uid = Sec.userId();
        return repo.findByUser_Id(uid).stream().map(this::toResponse).toList();
    }

    private TransactionResponse toResponse(Transaction t) {
        return new TransactionResponse(
                t.getId(),
                t.getReference(),
                t.getAmount(),
                t.getCurrency(),
                t.getDescription(),
                t.getCreatedAt()
        );
    }
}
