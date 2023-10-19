package kz.moderation.server.repository;


import kz.moderation.server.entity.ApprovalRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApprovalRequestRepository extends JpaRepository<ApprovalRequest, Long> {

    ApprovalRequest findByUser_Itin(String itin);
}