package com.kingspan.challenge.requests;

import com.kingspan.challenge.history.RequestHistoryService;
import com.kingspan.challenge.requests.dto.CreateRequestDTO;
import com.kingspan.challenge.requests.dto.PageResponseDTO;
import com.kingspan.challenge.requests.dto.RequestResponseDTO;
import com.kingspan.challenge.requests.stateMachine.StateMachineService;
import com.kingspan.challenge.users.ApproverLevel;
import com.kingspan.challenge.users.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PurchaseRequestService {

    private final PurchaseRequestRepository requestRepository;
    private final RequestHistoryService historyService;
    private final StateMachineService stateMachineService;

    public RequestResponseDTO create(CreateRequestDTO dto, User requestor) {
        ApproverLevel requiredLevel = requiredLevelCalc(dto.amount());

        PurchaseRequest purchaseOrder = PurchaseRequest.builder()
                .title(dto.title())
                .description(dto.description())
                .amount(dto.amount())
                .category(dto.category())
                .requestor(requestor)
                .requiredLevel(requiredLevel)
                .build();

        PurchaseRequest saved = requestRepository.save(purchaseOrder);

        historyService.record(
                saved,
                requestor,
                null,
                RequestStatus.PENDING,
                "Solcitação criada"
        );


        return toDTO(saved);
    }

    public PageResponseDTO<RequestResponseDTO> getAll(RequestStatus status, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<PurchaseRequest> result = (status != null)
                ? requestRepository.findByStatus(status, pageable)
                : requestRepository.findAll(pageable);

        return PageResponseDTO.from(result.map(this::toDTO));
    }

    public RequestResponseDTO getById(UUID id) {
        PurchaseRequest response = findOrThrow(id);

        return toDTO(response);
    }

    public RequestResponseDTO toDTO(PurchaseRequest request) {
        return new RequestResponseDTO(
                request.getId(),
                request.getTitle(),
                request.getDescription(),
                request.getAmount(),
                request.getCategory(),
                request.getStatus(),
                request.getRequiredLevel(),
                request.getRequestor().getName(),
                request.getCreatedAt(),
                request.getUpdatedAt()
                );

    }

    public RequestResponseDTO approve(UUID requestId, User actor, String comment) {
        PurchaseRequest request = findOrThrow(requestId);

        stateMachineService.validateApproval(request, actor);

        RequestStatus previous = request.getStatus();
        request.setStatus(RequestStatus.APPROVED);
        requestRepository.save(request);

        historyService.record(
                request,
                actor,
                previous,
                RequestStatus.APPROVED,
                comment);

        return toDTO(request);
    }

    public RequestResponseDTO reject(UUID requestId, User actor, String comment) {
        PurchaseRequest request = findOrThrow(requestId);

        stateMachineService.validateReject(request, actor);

        RequestStatus previous = request.getStatus();
        request.setStatus(RequestStatus.REJECTED);
        requestRepository.save(request);

        historyService.record(
                request,
                actor,
                previous,
                RequestStatus.REJECTED,
                comment);

        return toDTO(request);
    }

    public RequestResponseDTO cancell(UUID requestId, User actor, String comment) {
        PurchaseRequest request = findOrThrow(requestId);

        stateMachineService.validateCancel(request, actor);

        RequestStatus previous = request.getStatus();
        request.setStatus(RequestStatus.CANCELLED);
        requestRepository.save(request);

        historyService.record(
                request,
                actor,
                previous,
                RequestStatus.CANCELLED,
                comment);

        return toDTO(request);
    }


    private ApproverLevel requiredLevelCalc(BigDecimal amount) {
        if (amount.compareTo(new BigDecimal("1000")) <= 0) return ApproverLevel.NIVEL_1;
        if (amount.compareTo(new BigDecimal("10000")) <= 0) return ApproverLevel.NIVEL_2;
        return ApproverLevel.NIVEL_3;
    }

    private PurchaseRequest findOrThrow(UUID id) {
        return requestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Requisição não encontrada"));
    }


}
