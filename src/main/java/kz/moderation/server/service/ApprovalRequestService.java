package kz.moderation.server.service;

import kz.moderation.server.dto.ApprovalRequest.request.ApprovalRequestDto;
import kz.moderation.server.dto.ApprovalRequest.response.ApprovalRequestResponseDto;
import kz.moderation.server.dto.user.UserDto;
import kz.moderation.server.entity.ApprovalRequest;
import kz.moderation.server.entity.ApprovalRequestStatus;
import kz.moderation.server.entity.User;
import kz.moderation.server.exception.NotFoundException;
import kz.moderation.server.repository.ApprovalRequestRepository;
import kz.moderation.server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApprovalRequestService {

    private final ApprovalRequestRepository approvalRequestRepository;
    private final UserRepository userRepository;



    public ApprovalRequest createApprovalRequest(ApprovalRequestDto approvalRequestDto) {
        User user = userRepository.findByItin(approvalRequestDto.getUserItin()).orElseThrow(
                ()-> new NotFoundException("User not found")
        );
        ApprovalRequest newApprovalRequest = ApprovalRequest.builder().user(user).status(ApprovalRequestStatus.PENDING).build();
        return approvalRequestRepository.save(newApprovalRequest);
    }

    public ApprovalRequest approveRequest(Long requestId) {
        ApprovalRequest request = approvalRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Approval Request not found"));
        request.setStatus(ApprovalRequestStatus.APPROVED);
        return approvalRequestRepository.save(request);
    }

    public ApprovalRequest rejectRequest(Long requestId) {
        ApprovalRequest request = approvalRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Approval Request not found"));
        request.setStatus(ApprovalRequestStatus.REJECTED);
        return approvalRequestRepository.save(request);
    }

    public List<ApprovalRequestResponseDto> getAllApprovalRequests() {
        return  approvalRequestRepository.findAll().stream().map(approvalRequest -> (
                ApprovalRequestResponseDto.builder()
                        .id(approvalRequest.getId())
                        .user(
                                UserDto.builder()
                                        .itin(approvalRequest.getUser().getItin())
                                        .phone(approvalRequest.getUser().getPhone())
                                        .firstName(approvalRequest.getUser().getFirstname())
                                        .lastName(approvalRequest.getUser().getLastname())
                                        .email(approvalRequest.getUser().getEmail())
                                        .build())
                        .status(approvalRequest.getStatus())
                        .build()
                )).collect(Collectors.toList());
    }

    public ApprovalRequestStatus getStatusByItin(String itin)
    {
        ApprovalRequest  approvalRequest = approvalRequestRepository.findByUser_Itin(itin);
        return approvalRequest.getStatus();
    }

}
