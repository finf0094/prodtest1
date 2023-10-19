package kz.moderation.server.controller;

import kz.moderation.server.dto.*;
import kz.moderation.server.entity.Role;
import kz.moderation.server.entity.User;
import kz.moderation.server.exception.AppError;
import kz.moderation.server.repository.UserRepository;
import kz.moderation.server.service.RoleService;
import kz.moderation.server.service.UserService;
import kz.moderation.server.utils.JwtTokenUtils;
import kz.moderation.server.dto.JwtRequest;
import kz.moderation.server.dto.JwtResponse;
import kz.moderation.server.dto.RegistrationUserDto;
import kz.moderation.server.dto.UserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final UserService userService;
    private final JwtTokenUtils jwtTokenUtils;
    private final AuthenticationManager authenticationManager;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> createAuthToken(@RequestBody JwtRequest authRequest) {
        System.out.println(authRequest);
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    authRequest.getIin(), authRequest.getPassword()
            ));
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(new AppError(HttpStatus.UNAUTHORIZED.value(), "Неправильный логин или пароль"), HttpStatus.UNAUTHORIZED);
        }

        UserDetails userDetails = userService.loadUserByUsername(authRequest.getIin());
        String token = jwtTokenUtils.generateToken(userDetails);

        List<String> roles = userDetails.getAuthorities()
                .stream()
                .map(authority -> authority.getAuthority())
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(token, authRequest.getIin(), userDetails.getUsername(), roles));
    }

    @PostMapping("/register")
    public ResponseEntity<?> createNewUser(@RequestBody RegistrationUserDto registrationUserDto) {
        User userFromDjango = userService.getUserFromDjangoApiByItin(registrationUserDto.getIin());

        // Check if the user data is retrieved successfully from Django
        if (userFromDjango == null) {
            return new ResponseEntity<>(
                    new AppError(HttpStatus.BAD_REQUEST.value(), "BAD REQUEST"), HttpStatus.BAD_REQUEST
            );
        }

        if (userService.findByItin(registrationUserDto.getIin()).isPresent()) {
            return new ResponseEntity<>(
                    new AppError(HttpStatus.BAD_REQUEST.value(), "Пользователь с таким ИИН уже существует"), HttpStatus.BAD_REQUEST
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


    @GetMapping("/user-info")
     public ResponseEntity<?> getUserData() {
         // получаем из фильтра данные ползователя
         Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

         // если не авторизован выкидываем ошибку
         if (!authentication.isAuthenticated()) {
             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
         }

         UserInfo userInfo = new UserInfo();

         // находим в базе пользователя
         User user = userService.findByEmail(authentication.getPrincipal().toString()).orElseThrow();

         userInfo.setIin(user.getItin());
         userInfo.setEmail(user.getEmail());
         userInfo.setFirstname(user.getFirstname());
         userInfo.setRoles(user.getRoles());
         userInfo.setPhone(user.getPhone());
         userInfo.setLastname(user.getLastname());
         userInfo.setPosition(user.getPosition());

         return ResponseEntity.ok(userInfo);
     }

}
