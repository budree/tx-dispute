package com.example.txd.repo;

import com.example.txd.model.Dispute;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DisputeRepository extends JpaRepository<Dispute, Long> {
    List<Dispute> findByUser_Id(Long userId);
}
