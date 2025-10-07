// src/main/java/com/example/txd/config/DemoSeeder.java
package com.example.txd.config;

import com.example.txd.model.AppUser;
import com.example.txd.model.Role;
import com.example.txd.repo.TransactionRepository;
import com.example.txd.repo.UserRepository;
import com.example.txd.model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class DemoSeeder implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(DemoSeeder.class);

    // 50 household expenses + a few incomes
    private static final List<String> DESCRIPTIONS = List.of(
        "Supermarket groceries",
        "Electricity bill",
        "Water bill",
        "Internet subscription",
        "Mobile data bundle",
        "Mobile airtime top-up",
        "Fuel / petrol",
        "Ride-hailing / taxi",
        "Gym membership",
        "Streaming subscription",
        "School fees",
        "Stationery & supplies",
        "Home insurance premium",
        "Medical aid premium",
        "Pharmacy / medication",
        "Doctor visit copay",
        "Dining out / restaurant",
        "Coffee shop",
        "Takeaway / delivery",
        "Clothing purchase",
        "Shoes purchase",
        "Laundry & dry cleaning",
        "Household cleaning supplies",
        "Garden service",
        "Home maintenance & repairs",
        "Appliance purchase",
        "Furniture purchase",
        "Pet food & supplies",
        "Veterinary bill",
        "Childcare / nanny",
        "Toys & games",
        "Parking fees",
        "Toll fees",
        "Public transport fare",
        "Car service & parts",
        "Car insurance premium",
        "Vehicle license renewal",
        "Electricity prepaid token",
        "Municipal rates & taxes",
        "Cloud storage subscription",
        "Gift purchase",
        "Charity donation",
        "Holiday booking deposit",
        "Hotel accommodation",
        "Airline ticket",
        // Incomes ↓
        "Salary payment (income)",
        "Rental income (income)",
        "Interest income (income)",
        "Tax refund (income)",
        "Merchant refund (income)"
    );

    // Quick keywords to determine income vs expense (case-insensitive)
    private static final List<String> INCOME_KEYWORDS = List.of(
        "(income)", "salary", "rental income", "interest", "refund"
    );

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

        // --- Users (idempotent) ---
        ensureUser("admin", Role.ADMIN, "admin1234");
        var baseClient = ensureUser("client", Role.CLIENT, "client1234");

        // Additional demo clients client01..client10 (same password)
        List<AppUser> allClients = new ArrayList<>();
        allClients.add(baseClient);
        for (int i = 1; i <= 10; i++) {
            allClients.add(ensureUser(String.format("client%02d", i), Role.CLIENT, "client1234"));
        }

        // --- Keep your 3 example transactions for base "client" (sign auto-derived) ---
        createTxIfMissing(baseClient, "TXN-20001", new BigDecimal("199.99"), "ZAR", "Monthly subscription");
        createTxIfMissing(baseClient, "TXN-20002", new BigDecimal("85.50"),  "ZAR", "Grocery top-up");
        createTxIfMissing(baseClient, "TXN-20003", new BigDecimal("1249.00"),"ZAR", "Mobile phone purchase");

        // --- 100 transactions per client with unique refs and correct sign by type ---
        for (AppUser u : allClients) {
            int created = seedBatchTransactions(u, 100);
            log.info("Seeded {} transactions for user {}", created, u.getUsername());
        }

        log.info("DemoSeeder finished.");
    }

    private AppUser ensureUser(String username, Role role, String rawPassword) {
        return users.findByUsername(username).orElseGet(() -> {
            var u = new AppUser();
            u.setUsername(username);
            u.setPasswordHash(enc.encode(rawPassword));
            u.setRole(role);
            u = users.save(u);
            log.info("Seeded {} user: {}/{}", role, username, rawPassword);
            return u;
        });
    }

    private int seedBatchTransactions(AppUser owner, int count) {
        int created = 0;
        String prefix = "TXN-" + owner.getUsername().toUpperCase() + "-";

        for (int i = 1; i <= count; i++) {
            String ref = prefix + String.format("%04d", i);

            // Deterministic but varied absolute amount
            BigDecimal base = BigDecimal.valueOf((i % 500) + 0.99); // 0.99..500.99

            String description = randomDescription();
            String currency = "ZAR";

            // Sign is enforced inside createTxIfMissing based on description type
            boolean made = createTxIfMissing(owner, ref, base, currency, description);
            if (made) created++;
        }
        return created;
    }

    private String randomDescription() {
        int idx = ThreadLocalRandom.current().nextInt(DESCRIPTIONS.size());
        return DESCRIPTIONS.get(idx);
    }

    private boolean isIncome(String description) {
        if (description == null) return false;
        String d = description.toLowerCase(Locale.ROOT);
        for (String k : INCOME_KEYWORDS) {
            if (d.contains(k)) return true;
        }
        return false;
    }

    /**
     * Idempotent insert by reference; applies sign rule:
     *  - incomes => positive amount
     *  - expenses => negative amount
     *
     * @param amountAbs absolute amount (sign will be applied based on description)
     */
    private boolean createTxIfMissing(AppUser owner, String ref, BigDecimal amountAbs, String currency, String description) {
        return txRepo.findByReference(ref).map(existing -> false).orElseGet(() -> {
            var t = new Transaction();
            t.setReference(ref);

            BigDecimal abs = amountAbs.abs();
            t.setAmount(isIncome(description) ? abs : abs.negate());

            t.setCurrency(currency);
            t.setDescription(description);
            t.setUser(owner);
            t.setCreatedAt(Instant.now());
            txRepo.save(t);
            return true;
        });
    }
}
