package com.kingspan.challenge.history;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RequestHistoryRepository extends JpaRepository<RequestHistory, UUID> {
    List<RequestHistory> findByRequestIdOrderByCreatedAtAsc(UUID requestId);
}
