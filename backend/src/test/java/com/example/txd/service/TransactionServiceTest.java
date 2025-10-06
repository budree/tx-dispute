package com.example.txd.service;

import com.example.txd.dto.TransactionCreateRequest;
import com.example.txd.dto.TransactionResponse;
import com.example.txd.model.AppUser;
import com.example.txd.model.Role;
import com.example.txd.model.Transaction;
import com.example.txd.repo.TransactionRepository;
import com.example.txd.repo.UserRepository;
import com.example.txd.security.Sec;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock TransactionRepository txRepo;
    @Mock UserRepository userRepo;

    TransactionService service;

    @BeforeEach
    void setUp() {
        service = new TransactionService(txRepo, userRepo);
    }

    @Test
    void create_attachesCurrentUser_andPersists() {
        try (var mocked = Mockito.mockStatic(Sec.class)) {
            mocked.when(Sec::userId).thenReturn(42L);

            var user = new AppUser(); user.setId(42L); user.setUsername("client");
            when(userRepo.findById(42L)).thenReturn(Optional.of(user));

            var req = new TransactionCreateRequest();
            req.setReference("TXN-1");
            req.setAmount(new BigDecimal("99.99"));
            req.setCurrency("ZAR");
            req.setDescription("Test tx");

            var saved = new Transaction();
            saved.setId(7L);
            saved.setReference("TXN-1");
            saved.setAmount(new BigDecimal("99.99"));
            saved.setCurrency("ZAR");
            saved.setDescription("Test tx");
            saved.setUser(user);
            saved.setCreatedAt(Instant.now());

            when(txRepo.save(any(Transaction.class))).thenReturn(saved);

            TransactionResponse resp = service.create(req);

            assertThat(resp.getId()).isEqualTo(7L);
            assertThat(resp.getReference()).isEqualTo("TXN-1");
            verify(txRepo).save(argThat(t -> t.getUser() != null && t.getUser().getId().equals(42L)));
        }
    }

    @Test
    void list_adminGetsAll_clientGetsOwn() {
        var a = t(1L, 100L, "A");   // owner 100
        var b = t(2L, 200L, "B");   // owner 200
        when(txRepo.findAll()).thenReturn(List.of(a, b));
        when(txRepo.findByUser_Id(100L)).thenReturn(List.of(a));

        try (var mocked = Mockito.mockStatic(Sec.class)) {
            // Admin branch
            mocked.when(Sec::role).thenReturn(Role.ADMIN.name());
            var all = service.list();
            assertThat(all).hasSize(2);

            // Client branch
            mocked.when(Sec::role).thenReturn(Role.CLIENT.name());
            mocked.when(Sec::userId).thenReturn(100L);
            var mine = service.list();
            assertThat(mine).extracting(TransactionResponse::getReference).containsExactly("A");
        }
    }

    private static Transaction t(Long id, Long ownerId, String ref) {
        var u = new AppUser(); u.setId(ownerId);
        var t = new Transaction();
        t.setId(id); t.setReference(ref);
        t.setAmount(new BigDecimal("1.00")); t.setCurrency("ZAR");
        t.setUser(u); t.setCreatedAt(Instant.now());
        return t;
    }
}
