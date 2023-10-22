package kz.moderation.server.controller;

import kz.moderation.server.dto.JWT.JwtResponse;
import kz.moderation.server.dto.RefreshToken.request.RefreshTokenRequest;
import kz.moderation.server.dto.user.UserResponseAfterAuth;
import kz.moderation.server.entity.RefreshToken;
import kz.moderation.server.exception.AppError;
import kz.moderation.server.service.RefreshTokenService;
import kz.moderation.server.service.UserService;
import kz.moderation.server.utils.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RefreshTokenController {
    private final RefreshTokenService refreshTokenService;
    private final JwtTokenUtils jwtTokenUtils;
    private final UserService userService;

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        RefreshToken refreshToken = refreshTokenService.findByToken(refreshTokenRequest.getToken())
                .map(refreshTokenService::verifyExpiration)
                .orElse(null);

        if (refreshToken != null) {
            UserDetails userDetails = userService.loadUserByUsername(refreshToken.getUserInfo().getItin());
            List<String> roles = userDetails.getAuthorities()
                    .stream()
                    .map(authority -> authority.getAuthority())
                    .collect(Collectors.toList());



            UserResponseAfterAuth userResponseAfterAuth = new UserResponseAfterAuth(
                    refreshToken.getUserInfo().getItin(),
                    refreshToken.getUserInfo().getEmail(),
                    roles
            );

            String accessToken = jwtTokenUtils.generateToken(userDetails);

            JwtResponse jwtResponse = JwtResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken.getToken())
                    .user(userResponseAfterAuth)
                    .build();

            return ResponseEntity.ok(jwtResponse);
        } else {
            return new ResponseEntity(
                    new AppError(HttpStatus.NOT_FOUND.value(), "Refresh token does not exist!"),
                    HttpStatus.NOT_FOUND
            );
        }
    }


}


