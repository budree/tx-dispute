package com.example.txd.service;

import com.example.txd.dto.DisputeAdvanceRequest; // if you use it directly
import com.example.txd.dto.DisputeCreateRequest;
import com.example.txd.dto.DisputeResponse;
import com.example.txd.model.Dispute;
import com.example.txd.model.DisputeStatus;
import com.example.txd.model.Role;
import com.example.txd.repo.DisputeRepository;
import com.example.txd.repo.UserRepository;   // <-- inject this
import com.example.txd.security.Sec;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import static com.example.txd.security.Sec.role;
import static com.example.txd.security.Sec.userId;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class DisputeService {

    private final DisputeRepository repo;
    private final UserRepository userRepo;   // <-- inject this

    private static final Map<DisputeStatus, Set<DisputeStatus>> ALLOWED = Map.of(
            DisputeStatus.OPEN, Set.of(DisputeStatus.UNDER_REVIEW),
            DisputeStatus.UNDER_REVIEW, Set.of(DisputeStatus.RESOLVED, DisputeStatus.REJECTED),
            DisputeStatus.RESOLVED, Set.of(),
            DisputeStatus.REJECTED, Set.of()
    );

    public DisputeService(DisputeRepository repo, UserRepository userRepo) {
        this.repo = repo;
        this.userRepo = userRepo;
    }

    @Transactional
    public DisputeResponse create(DisputeCreateRequest req) {
        Dispute d = new Dispute();
        d.setTransactionRef(req.getTransactionRef());
        d.setReason(req.getReason());
        d.setStatus(DisputeStatus.OPEN);
        d.setOpenedAt(Instant.now());
        d.setUser(userRepo.findById(Sec.userId()).orElseThrow());  // attach owner
        d = repo.save(d);
        return toResponse(d);
    }

    @Transactional(readOnly = true)
    public DisputeResponse get(Long id) {
        Dispute d = repo.findById(id).orElseThrow();
        if (Role.ADMIN.name().equals(role())) {
            return toResponse(d);
        }
        if (d.getUser() != null && d.getUser().getId().equals(userId())) {
            return toResponse(d);
        }
        // Hide existence from other clients
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Dispute not found");
    }

    @Transactional(readOnly = true)
    public List<DisputeResponse> list() {
        if (Role.ADMIN.name().equals(role())) {
            return repo.findAll().stream().map(this::toResponse).toList();
        }
        return repo.findByUser_Id(userId()).stream().map(this::toResponse).toList();
    }

    @Transactional
    public DisputeResponse advance(Long id, DisputeStatus nextStatus) {
        Dispute d = repo.findById(id).orElseThrow();
        DisputeStatus current = d.getStatus();

        if (current == nextStatus)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Already in status " + current);
        if (!ALLOWED.getOrDefault(current, Set.of()).contains(nextStatus))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cannot transition from " + current + " to " + nextStatus);

        Instant now = Instant.now();
        if (nextStatus == DisputeStatus.UNDER_REVIEW && d.getUnderReviewAt() == null) d.setUnderReviewAt(now);
        if (nextStatus == DisputeStatus.RESOLVED && d.getResolvedAt() == null) d.setResolvedAt(now);
        if (nextStatus == DisputeStatus.REJECTED && d.getRejectedAt() == null) d.setRejectedAt(now);

        d.setStatus(nextStatus);
        d = repo.save(d);
        return toResponse(d);
    }

    private DisputeResponse toResponse(Dispute d) {
        return new DisputeResponse(
                d.getId(), d.getTransactionRef(), d.getReason(), d.getStatus(),
                d.getCreatedAt(), d.getUpdatedAt(),
                d.getOpenedAt(), d.getUnderReviewAt(), d.getResolvedAt(), d.getRejectedAt(),
                d.getUser() != null ? d.getUser().getId() : null,
                d.getUser() != null ? d.getUser().getUsername() : null
        );
    }
}
