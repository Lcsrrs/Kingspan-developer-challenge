package com.kingspan.challenge.history;

import com.kingspan.challenge.requests.PurchaseRequest;
import com.kingspan.challenge.requests.RequestStatus;
import com.kingspan.challenge.users.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "request_history")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false)
    private PurchaseRequest request;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_id", nullable = false)
    private User actor;

    @Column(name = "from_status")
    @Enumerated(EnumType.STRING)
    private RequestStatus fromStatus;

    @Column(name = "to_status")
    @Enumerated(EnumType.STRING)
    private RequestStatus toStatus;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

}
