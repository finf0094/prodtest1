package kz.moderation.server.dto.RefreshToken.request;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RefreshTokenRequest {
    private String token;
}
