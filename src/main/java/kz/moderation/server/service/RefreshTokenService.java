package kz.moderation.server.service;

import kz.moderation.server.entity.RefreshToken;
import kz.moderation.server.repository.RefreshTokenRepository;
import kz.moderation.server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    public RefreshToken createRefreshToken(String itin) {
        // Find the existing refresh token for the user
        Optional<RefreshToken> existingRefreshToken = refreshTokenRepository.findByUserInfoItin(itin);

        // If an existing refresh token is found, remove it
        existingRefreshToken.ifPresent(refreshTokenRepository::delete);

        // Create a new refresh token
        RefreshToken refreshToken = RefreshToken.builder()
                .userInfo(userRepository.findByItin(itin).get())
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(30L * 24 * 60 * 60 * 1000)) // 30 days
                .build();

        refreshTokenRepository.save(refreshToken);

        return refreshToken;
    }


    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if(token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException(token.getToken() + "Refresh token was expiration. Please make a new signin request");
        }
        return token;
    }

}
