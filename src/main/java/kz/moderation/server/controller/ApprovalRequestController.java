package kz.moderation.server.controller;

import jakarta.websocket.server.PathParam;
import kz.moderation.server.dto.ApprovalRequest.request.ApprovalRequestDto;
import kz.moderation.server.dto.ApprovalRequest.response.ApprovalRequestResponseDto;
import kz.moderation.server.dto.ResponseDto;
import kz.moderation.server.dto.user.UserDto;
import kz.moderation.server.entity.ApprovalRequest;
import kz.moderation.server.entity.User;
import kz.moderation.server.exception.AppError;
import kz.moderation.server.exception.NotFoundException;
import kz.moderation.server.service.ApprovalRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/approval-requests")
@RequiredArgsConstructor
public class ApprovalRequestController {

    private final ApprovalRequestService approvalRequestService;


    @PostMapping
    public ResponseEntity<?> createApprovalRequest(@RequestBody ApprovalRequestDto approvalRequestDto) {

        try {
            ApprovalRequest approvalRequest = approvalRequestService.createApprovalRequest(approvalRequestDto);
            UserDto userDto = UserDto.builder()
                    .itin(approvalRequest.getUser().getItin())
                    .firstName(approvalRequest.getUser().getFirstname())
                    .lastName(approvalRequest.getUser().getLastname())
                    .email(approvalRequest.getUser().getEmail())
                    .phone(approvalRequest.getUser().getPhone())
                    .position(approvalRequest.getUser().getPosition())
                    .build();
            ApprovalRequestResponseDto approvalRequestResponseDto =
                    ApprovalRequestResponseDto.builder()
                            .status(approvalRequest.getStatus())
                            .user(userDto).
                            build();

            return ResponseEntity.ok(approvalRequestResponseDto);
        }
        catch (NotFoundException notFoundException)
        {
            return ResponseEntity.ok(notFoundException.getMessage());
        }
        catch (Exception e)
        {
            return ResponseEntity.ok(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{requestId}/approve")
    public ApprovalRequest approveRequest(@PathVariable Long requestId) {
        return approvalRequestService.approveRequest(requestId);
    }

    @PutMapping("/{itin}/approved-with-itin")
    public ApprovalRequest approveRequestByItin(@PathVariable String itin) {
        return approvalRequestService.approveRequestByItin(itin);
    }

    @PutMapping("/{requestId}/reject")
    public ApprovalRequest rejectRequest(@PathVariable Long requestId) {
        return approvalRequestService.rejectRequest(requestId);
    }

    @PutMapping("/{itin}/rejected-with-itin")
    public ApprovalRequest rejectRequest(@PathVariable String itin) {
        return approvalRequestService.rejectRequest(itin);
    }

    @GetMapping("/status")
    public ResponseEntity<?> userStatus(@PathParam("itin") String itin)
    {
        Optional<ApprovalRequest> approvalRequest = approvalRequestService.getApprovalRequestByItin(itin);

        if (!approvalRequest.isPresent()) {
            return new ResponseEntity<>(
                    new AppError(HttpStatus.NOT_FOUND.value(), "Approval request does not exist"), HttpStatus.NOT_FOUND
            );
        }

        return ResponseEntity.ok(approvalRequest.get());
    }

    @GetMapping
    public ResponseEntity<?> getAllApprovalRequests() {
        return ResponseEntity.ok(approvalRequestService.getAllApprovalRequests());
    }
}

