package com.kingspan.challenge.requests;

import com.kingspan.challenge.history.RequestHistoryService;
import com.kingspan.challenge.history.dto.HistoryResponseDTO;
import com.kingspan.challenge.requests.dto.ActionRequestDTO;
import com.kingspan.challenge.requests.dto.CreateRequestDTO;
import com.kingspan.challenge.requests.dto.PageResponseDTO;
import com.kingspan.challenge.requests.dto.RequestResponseDTO;
import com.kingspan.challenge.users.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class PurchaseRequestController {

    private final PurchaseRequestService requestService;
    private final RequestHistoryService historyService;

    @PostMapping
    public ResponseEntity<RequestResponseDTO> createRequest (
            @Valid @RequestBody CreateRequestDTO createRequest,
            @AuthenticationPrincipal User currentUser) {

        RequestResponseDTO response = requestService.create(createRequest, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<PageResponseDTO<RequestResponseDTO>> listRequests (
            @RequestParam(required = false)RequestStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PageResponseDTO<RequestResponseDTO> response = requestService.getAll(status, page, size);
        return ResponseEntity.ok(response);

    }

    @GetMapping("/{requestId}")
    public ResponseEntity<RequestResponseDTO> getRequest (
            @PathVariable UUID requestId) {

        RequestResponseDTO response = requestService.getById(requestId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{requestId}/history")
    public ResponseEntity<List<HistoryResponseDTO>> getHistory (
            @PathVariable UUID requestId) {

        List<HistoryResponseDTO> response = historyService.getByRequestId(requestId);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{requestId}/approve")
    public ResponseEntity<RequestResponseDTO> approve (
            @PathVariable UUID requestId,
            @RequestBody(required = false)ActionRequestDTO dto,
            @AuthenticationPrincipal User currentUser) {

        String comment = dto != null ? dto.comment() : null;
        return ResponseEntity.ok(requestService.approve(requestId, currentUser, comment));
    }

    @PatchMapping("/{requestId}/reject")
    public ResponseEntity<RequestResponseDTO> reject (
            @PathVariable UUID requestId,
            @RequestBody(required = false)ActionRequestDTO dto,
            @AuthenticationPrincipal User currentUser) {

        String comment = dto != null ? dto.comment() : null;
        return ResponseEntity.ok(requestService.reject(requestId, currentUser, comment));
    }

    @PatchMapping("/{requestId}/cancell")
    public ResponseEntity<RequestResponseDTO> cancell (
            @PathVariable UUID requestId,
            @RequestBody(required = false)ActionRequestDTO dto,
            @AuthenticationPrincipal User currentUser) {

        String comment = dto != null ? dto.comment() : null;
        return ResponseEntity.ok(requestService.cancell(requestId, currentUser, comment));
    }

}
