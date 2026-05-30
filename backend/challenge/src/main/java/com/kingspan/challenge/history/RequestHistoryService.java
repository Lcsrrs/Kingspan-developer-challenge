package com.kingspan.challenge.history;

import com.kingspan.challenge.history.dto.HistoryResponseDTO;
import com.kingspan.challenge.requests.PurchaseRequest;
import com.kingspan.challenge.requests.RequestStatus;
import com.kingspan.challenge.users.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RequestHistoryService {

    private final RequestHistoryRepository historyRepository;

    public void record(PurchaseRequest request, User actor, RequestStatus fromStatus, RequestStatus toStatus, String comment) {
        RequestHistory history = RequestHistory.builder()
                .request(request)
                .actor(actor)
                .fromStatus(fromStatus)
                .toStatus(toStatus)
                .comment(comment)
                .build();

        historyRepository.save(history);
    }

    public List<HistoryResponseDTO> getByRequestId(UUID requestId) {
        return historyRepository.findByRequestIdOrderByCreatedAtAsc(requestId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    private HistoryResponseDTO toDTO(RequestHistory history) {
        return new HistoryResponseDTO(
                history.getId(),
                history.getActor().getName(),
                history.getActor().getRole().name(),
                history.getFromStatus(),
                history.getToStatus(),
                history.getComment(),
                history.getCreatedAt()
        );
    }

}
