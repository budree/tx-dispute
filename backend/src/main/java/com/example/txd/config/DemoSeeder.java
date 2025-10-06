// src/main/java/com/example/txd/config/DemoSeeder.java
package com.example.txd.config;

import com.example.txd.model.*;
import com.example.txd.repo.TransactionRepository;
import com.example.txd.repo.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

@Component
public class DemoSeeder implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(DemoSeeder.class);

    @Value("${app.seed.enabled:true}")
    private boolean enabled;

    private final UserRepository users;
    private final TransactionRepository txRepo;
    private final PasswordEncoder enc;

    public DemoSeeder(UserRepository users, TransactionRepository txRepo, PasswordEncoder enc) {
        this.users = users; this.txRepo = txRepo; this.enc = enc;
    }

    @Override
    public void run(ApplicationArguments args) {
        log.info("DemoSeeder starting. app.seed.enabled={}", enabled);
        if (!enabled) {
            log.info("Seeding disabled; skipping.");
            return;
        }

        // Users (idempotent)
        var admin = users.findByUsername("admin").orElseGet(() -> {
            var u = new AppUser();
            u.setUsername("admin");
            u.setPasswordHash(enc.encode("admin1234"));
            u.setRole(Role.ADMIN);
            u = users.save(u);
            log.info("Seeded admin user: admin/admin1234");
            return u;
        });

        var client = users.findByUsername("client").orElseGet(() -> {
            var u = new AppUser();
            u.setUsername("client");
            u.setPasswordHash(enc.encode("client1234"));
            u.setRole(Role.CLIENT);
            u = users.save(u);
            log.info("Seeded client user: client/client1234");
            return u;
        });

        // Transactions for client (idempotent by reference)
        createTxIfMissing(client, "TXN-20001", new BigDecimal("199.99"), "ZAR", "Monthly subscription");
        createTxIfMissing(client, "TXN-20002", new BigDecimal("85.50"),  "ZAR", "Grocery top-up");
        createTxIfMissing(client, "TXN-20003", new BigDecimal("1249.00"),"ZAR", "Mobile phone purchase");

        log.info("DemoSeeder finished.");
    }

    private void createTxIfMissing(AppUser owner, String ref, BigDecimal amount, String currency, String description) {
        txRepo.findByReference(ref).orElseGet(() -> {
            var t = new Transaction();
            t.setReference(ref);
            t.setAmount(amount);
            t.setCurrency(currency);
            t.setDescription(description);
            t.setUser(owner);
            return txRepo.save(t);
        });
    }
}
