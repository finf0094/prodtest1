package kz.moderation.server.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Entity
@Table(name = "approval_request")
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@Builder
public class ApprovalRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING) // Store the enum as a string in the database
    private ApprovalRequestStatus status;

    @ManyToOne
    private User user;
}
