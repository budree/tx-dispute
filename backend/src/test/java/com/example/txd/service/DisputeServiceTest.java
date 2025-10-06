package com.example.txd.service;

import com.example.txd.dto.DisputeCreateRequest;
import com.example.txd.dto.DisputeResponse;
import com.example.txd.model.*;
import com.example.txd.repo.DisputeRepository;
import com.example.txd.repo.UserRepository;
import com.example.txd.security.Sec;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DisputeServiceTest {

    @Mock DisputeRepository repo;
    @Mock UserRepository userRepo;

    @Test
    void create_setsOwner_andOpenStatus() {
        var service = new DisputeService(repo, userRepo);

        try (var mocked = Mockito.mockStatic(Sec.class)) {
            mocked.when(Sec::userId).thenReturn(5L);
            var owner = new AppUser(); owner.setId(5L);
            when(userRepo.findById(5L)).thenReturn(Optional.of(owner));

            var req = new DisputeCreateRequest();
            req.setTransactionRef("TXN-9");
            req.setReason("Wrong amount");

            when(repo.save(any(Dispute.class))).thenAnswer(inv -> {
                Dispute d = inv.getArgument(0);
                d.setId(10L);
                return d;
            });

            DisputeResponse resp = service.create(req);
            assertThat(resp.getId()).isEqualTo(10L);
            assertThat(resp.getStatus()).isEqualTo(DisputeStatus.OPEN);
        }
    }

    @Test
    void advance_openToUnderReview_setsTimestamp() {
        var service = new DisputeService(repo, userRepo);
        var d = new Dispute();
        d.setId(1L);
        d.setStatus(DisputeStatus.OPEN);
        d.setOpenedAt(Instant.now());

        when(repo.findById(1L)).thenReturn(Optional.of(d));
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        DisputeResponse resp = service.advance(1L, DisputeStatus.UNDER_REVIEW);
        assertThat(resp.getStatus()).isEqualTo(DisputeStatus.UNDER_REVIEW);
        assertThat(resp.getUnderReviewAt()).isNotNull();
    }

    @Test
    void advance_invalidTransition_throws409() {
        var service = new DisputeService(repo, userRepo);
        var d = new Dispute();
        d.setId(1L);
        d.setStatus(DisputeStatus.OPEN);

        when(repo.findById(1L)).thenReturn(Optional.of(d));

        assertThatThrownBy(() -> service.advance(1L, DisputeStatus.RESOLVED))
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining("Cannot transition");
    }

    @Test
    void get_clientCanOnlySeeOwn_otherwise404() {
        var service = new DisputeService(repo, userRepo);

        var owner = new AppUser(); owner.setId(100L);
        var other = new AppUser(); other.setId(200L);

        var d = new Dispute(); d.setId(9L); d.setUser(owner);

        when(repo.findById(9L)).thenReturn(Optional.of(d));

        try (var mocked = Mockito.mockStatic(Sec.class)) {
            mocked.when(Sec::role).thenReturn(Role.CLIENT.name());

            // Owner sees it
            mocked.when(Sec::userId).thenReturn(100L);
            assertThat(service.get(9L)).isNotNull();

            // Not owner -> 404
            mocked.when(Sec::userId).thenReturn(200L);
            assertThatThrownBy(() -> service.get(9L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("404");
        }
    }
}
