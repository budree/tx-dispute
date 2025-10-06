package com.example.txd.repository;

import com.example.txd.model.AppUser;
import com.example.txd.model.Role;
import com.example.txd.model.Transaction;
import com.example.txd.repo.TransactionRepository;
import com.example.txd.repo.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TransactionRepositoryTest {

    @Autowired
    TransactionRepository txRepo;
    @Autowired
    UserRepository userRepo;

    @Test
    void findByReference_and_findByUserId() {
        var u = new AppUser(); u.setUsername("client"); u.setPasswordHash("{noop}x"); u.setRole(Role.CLIENT);
        u = userRepo.save(u);

        var t1 = new Transaction();
        t1.setReference("TX-A"); t1.setAmount(new BigDecimal("1.00"));
        t1.setCurrency("ZAR"); t1.setUser(u); t1.setCreatedAt(Instant.now());
        txRepo.save(t1);

        var t2 = new Transaction();
        t2.setReference("TX-B"); t2.setAmount(new BigDecimal("2.00"));
        t2.setCurrency("ZAR"); t2.setUser(u); t2.setCreatedAt(Instant.now());
        txRepo.save(t2);

        assertThat(txRepo.findByReference("TX-A")).isPresent();
        List<Transaction> mine = txRepo.findByUser_Id(u.getId());
        assertThat(mine).hasSize(2);
    }
}
