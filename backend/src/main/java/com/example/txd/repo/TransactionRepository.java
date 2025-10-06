package com.example.txd.repo;

import com.example.txd.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Optional<Transaction> findByReference(String reference);
    List<Transaction> findByUser_Id(Long userId);
}
