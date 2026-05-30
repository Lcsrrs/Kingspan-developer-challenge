package com.kingspan.challenge.requests;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PurchaseRequestRepository extends JpaRepository<PurchaseRequest, UUID> {
    Page<PurchaseRequest> findByStatus(RequestStatus status, Pageable pageable);
    Page<PurchaseRequest> findByRequestorId(UUID requestorId, Pageable pageable);
}
