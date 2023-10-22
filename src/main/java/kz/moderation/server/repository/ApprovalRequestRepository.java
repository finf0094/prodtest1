package kz.moderation.server.repository;


import kz.moderation.server.entity.ApprovalRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ApprovalRequestRepository extends JpaRepository<ApprovalRequest, Long> {

    Optional<ApprovalRequest> findByUser_Itin(String itin);
}