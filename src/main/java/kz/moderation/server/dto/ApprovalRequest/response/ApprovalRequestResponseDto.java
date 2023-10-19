package kz.moderation.server.dto.ApprovalRequest.response;


import kz.moderation.server.dto.user.UserDto;
import kz.moderation.server.entity.ApprovalRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApprovalRequestResponseDto {
    Long id;
    ApprovalRequestStatus status;
    UserDto user;


}
