package kz.moderation.server.service;

import kz.moderation.server.dto.ApprovalRequest.request.ApprovalRequestDto;
import kz.moderation.server.entity.ApprovalRequest;
import kz.moderation.server.entity.ApprovalRequestStatus;
import kz.moderation.server.entity.User;
import kz.moderation.server.exception.NotFoundException;
import kz.moderation.server.repository.ApprovalRequestRepository;
import kz.moderation.server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public List<ApprovalRequest> getAllApprovalRequests() {
        return approvalRequestRepository.findAll();
    }
}
