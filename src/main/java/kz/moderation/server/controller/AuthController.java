package kz.moderation.server.controller;

import kz.moderation.server.config.CustomUserDetails;
import kz.moderation.server.dto.user.UserResponseAfterAuth;
import kz.moderation.server.entity.RefreshToken;
import kz.moderation.server.entity.User;
import kz.moderation.server.exception.AppError;
import kz.moderation.server.exception.Auth.AuthenticationException;
import kz.moderation.server.service.RefreshTokenService;
import kz.moderation.server.service.RoleService;
import kz.moderation.server.service.UserService;
import kz.moderation.server.utils.JwtTokenUtils;
import kz.moderation.server.dto.JWT.JwtRequest;
import kz.moderation.server.dto.JWT.JwtResponse;
import kz.moderation.server.dto.RegistrationUserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final UserService userService;
    private final RoleService roleService;
    private final JwtTokenUtils jwtTokenUtils;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;

    private final PasswordEncoder passwordEncoder;
    @PostMapping("/login")
    public ResponseEntity<?> createAuthToken(@RequestBody JwtRequest authRequest) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    authRequest.getIin(), authRequest.getPassword()
            ));
        }  catch (BadCredentialsException e) {
            throw new AuthenticationException("Пароль неправильный", HttpStatus.UNAUTHORIZED);
        }

        CustomUserDetails userDetails = userService.loadUserByUsername(authRequest.getIin());
        String accessToken = jwtTokenUtils.generateToken(userDetails);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(authRequest.getIin());

        List<String> roles = userDetails.getAuthorities()
                .stream()
                .map(authority -> authority.getAuthority())
                .collect(Collectors.toList());

        UserResponseAfterAuth userResponseAfterAuth = new UserResponseAfterAuth(authRequest.getIin(), userDetails.getEmail(), roles);

        JwtResponse jwtResponse = JwtResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .user(userResponseAfterAuth)
                .build();

        return ResponseEntity.ok(jwtResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<?> createNewUser(@RequestBody RegistrationUserDto registrationUserDto) {

        if (userService.findByItin(registrationUserDto.getIin()).isPresent()) {
            return new ResponseEntity<>(
                    new AppError(HttpStatus.BAD_REQUEST.value(), "Пользователь с таким ИИН уже существует в базе !"), HttpStatus.BAD_REQUEST
            );
        }

        User userFromDjango = userService.getUserFromDjangoApiByItin(registrationUserDto.getIin());

        // Check if the user data is retrieved successfully from Django
        if (userFromDjango == null) {
            throw new AuthenticationException("Пользователь не найден", HttpStatus.NOT_FOUND);
        }

        if (userService.findByItin(registrationUserDto.getIin()).isPresent()) {
            throw new AuthenticationException(
                    String.format("Пользователь с ИИН '%s' уже существует", registrationUserDto.getIin()),
                    HttpStatus.BAD_REQUEST
            );
        }

        if (userService.findByEmail(registrationUserDto.getEmail()).isPresent()) {
            throw new AuthenticationException(
                    String.format("Пользователь с почтой '%s' уже существует", registrationUserDto.getEmail()),
                    HttpStatus.BAD_REQUEST
            );
        }

        System.out.println(userFromDjango);

        // Create a new user and set its properties using data from Django
        User user = new User();
        user.setItin(registrationUserDto.getIin());
        user.setEmail(registrationUserDto.getEmail());
        user.setRoles(List.of(roleService.findByName("ROLE_USER")));
        user.setFirstname(userFromDjango.getFirstname());
        user.setLastname(userFromDjango.getLastname());
        user.setPhone(userFromDjango.getPhone());
        user.setPosition(userFromDjango.getPosition());

        // Set the user's password using passwordEncoder
        user.setPassword(passwordEncoder.encode(registrationUserDto.getPassword()));

        userService.save(user);

        return ResponseEntity.ok("User created");
    }

}
