package kz.moderation.server.dto.JWT;


import kz.moderation.server.dto.user.UserResponseAfterAuth;
import kz.moderation.server.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JwtResponse {
    private String accessToken;
    private String refreshToken;
    private UserResponseAfterAuth user;
}
